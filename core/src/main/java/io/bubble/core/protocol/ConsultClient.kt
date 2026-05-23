package io.bubble.core.protocol

import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import io.bubble.core.crypto.BitInvert
import io.bubble.core.crypto.CryptoHelper
import io.bubble.core.crypto.RsaHelper
import io.bubble.core.crypto.SecurityStore
import io.bubble.core.protocol.consult.proto.ConsultProto
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Arrays
import kotlin.random.Random
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * 对应 LinkService {@code pk.a ClientConsultHelper}（Consult 报文使用原版 {@link ConsultProto}）。
 */
class ConsultClient(
    private val mac: String,
    private val pairSecret: ByteArray,
    private val connectionType: Int = 2,
    private val isConsultKeyModule: Boolean = true,
    private val needBond: Boolean = true,
    private val send: (sid: Int, cid: Int, body: ByteArray) -> Unit,
    private val onSuccess: (TransferConfigResult) -> Unit,
    private val onError: (Int) -> Unit,
) {
    data class TransferConfigResult(
        val mtu: Int,
        val maxFrameSize: Int,
        val interval: Int,
        val supportEncrypt: Boolean,
    )

    companion object {
        private const val RSA_BITS = 2048
        private const val CONSULT_SID = 1
    }

    private var consulting = false
    private var transportProcessed = false
    private var supportEncrypt = false
    private var consultKey: ByteArray? = pairSecret
    private var identityKey: ByteArray? = null
    private var localRandomData: ByteArray? = null
    private var shakeHandRandom: ByteArray? = null
    private var localTimeRandom: Long = 0L
    private var remoteTimeRandom: Long = 0L
    private var rsaPrivate: RSAPrivateKey? = null
    private var rsaPublic: RSAPublicKey? = null
    private var aesKey: ByteArray? = null
    private var aesIv: ByteArray? = null
    private var transferMtu = 20
    private var transferMaxFrame = 20
    private var transferInterval = 10

    fun start() {
        if (consulting) return
        consulting = true
        transportProcessed = false
        val config = defaultTransferConfig(5)
        val transportConsult = ConsultProto.TransportConsult.newBuilder()
            .setMaxFrameSize(config.maxFrameSize)
            .setMaxTransimissionUnit(config.mtu)
            .setInterval(config.interval)
            .setProtocolVersion(5)
            .setSupportProtocol(3)
            .build()
        logcat(LogPriority.DEBUG) { "startConsult: local protocol version 5" }
        sendCommand(CONSULT_SID, 1, transportConsult.toByteArray())
    }

    /**
     * @param frame 已去掉传输层外壳后的 Consult 载荷（sid + cid + protobuf），与 {@code pk.d#d} 入参一致。
     */
    fun onFrame(frame: ByteArray) {
        if (frame.size < 2) return
        val sid = frame[0].toInt() and 0xFF
        val cid = frame[1].toInt() and 0xFF
        if (sid != CONSULT_SID) return
        val body = frame
        when (cid and 0x7F) {
            1 -> processTransportConsult(body)
            24 -> processIdentityConsult(body)
            25 -> processIdentityCheck(body)
            22 -> processKeyConsult(body)
            21 -> processShakeHand(body)
            // 原版 pk.a#d：仅 cid=37 走 processAESConsultRequest；36 为手机 outbound RSA
            37 -> processAesConsult(body)
            else -> logcat(LogPriority.DEBUG) { "receiveData: ignore cid=$cid" }
        }
    }

    /** 兼容 TransportLayer 已拆出 sid/cid 的回调。 */
    fun onPacket(payload: ByteArray) {
        if (payload.isEmpty()) return
        onFrame(payload)
    }

    private fun processTransportConsult(frame: ByteArray) {
        logcat(LogPriority.DEBUG) { "processTransportConsult" }
        if (transportProcessed) {
            logcat(LogPriority.DEBUG) { "processTransportConsult: already process" }
            return
        }
        transportProcessed = true
        try {
            val from = ConsultProto.TransportConsult.parseFrom(
                Arrays.copyOfRange(frame, 2, frame.size),
            )
            val protocolVersion = from.protocolVersion
            val config = defaultTransferConfig(protocolVersion)
            transferMaxFrame = from.maxFrameSize
            transferMtu = from.maxTransimissionUnit
            transferInterval = from.interval
            supportEncrypt = from.supportProtocol and 0x3 > 0
            logcat(LogPriority.DEBUG) {
                "processTransportConsult: protocolVersion=$protocolVersion,frame=$transferMaxFrame," +
                    "mtu=$transferMtu,interval=$transferInterval,supportEncrypt=$supportEncrypt"
            }
            when {
                protocolVersion >= 3 -> afterTransportConsultV3()
                protocolVersion == 2 -> startKeyConsultWithShake()
                protocolVersion == 1 -> startKeyConsultV1()
            }
        } catch (e: InvalidProtocolBufferException) {
            logcat(LogPriority.ERROR) { "processTransportConsult\n${e.asLog()}" }
            fail(4)
        }
    }

    private fun afterTransportConsultV3() {
        when {
            ConnectionTypeHelper.isIdentityConsultType(connectionType) &&
                !ConnectionTypeHelper.isBleType(connectionType) -> sendIdentityConsultRequest()
            !isConsultKeyModule -> startKeyConsultWithShake()
            ConnectionTypeHelper.isAlternateIdentityType(connectionType) ->
                sendIdentityConsultRequest()
            else -> startKeyConsultWithShake()
        }
    }

    private fun processIdentityConsult(frame: ByteArray) {
        logcat(LogPriority.DEBUG) { "processIdentityConsult" }
        try {
            remoteTimeRandom = ConsultProto.IdentityConsult.parseFrom(
                Arrays.copyOfRange(frame, 2, frame.size),
            ).randomNumber
            logcat(LogPriority.DEBUG) { "processIdentityConsult: $remoteTimeRandom" }
            identityKey = CryptoHelper.deriveIdentityKey(localTimeRandom, remoteTimeRandom)
            if (identityKey == null) {
                logcat(LogPriority.DEBUG) { "processIdentityConsult: key empty" }
                fail(9)
            } else {
                sendIdentityCheckRequest()
            }
        } catch (e: InvalidProtocolBufferException) {
            logcat(LogPriority.ERROR) { "processIdentityConsult\n${e.asLog()}" }
            fail(4)
        }
    }

    private fun processIdentityCheck(frame: ByteArray) {
        try {
            logcat(LogPriority.DEBUG) { "processIdentityCheck" }
            val from = ConsultProto.IdentityCheck.parseFrom(
                Arrays.copyOfRange(frame, 2, frame.size),
            )
            val source = from.getData()?.toByteArray()
            val encrypt = from.getEncryptData()?.toByteArray()
            logcat(LogPriority.DEBUG) {
                "processIdentityCheck: source=${source?.contentToString()}, encrypt=${encrypt?.contentToString()}"
            }
            val key = identityKey
            if (key == null) {
                fail(9)
                return
            }
            val ok = if (encrypt == null || localRandomData == null) {
                false
            } else {
                val decrypted = CryptoHelper.decryptAes128(encrypt, key)
                Arrays.equals(localRandomData, BitInvert.invert(decrypted))
            }
            if (!ok) {
                logcat(LogPriority.DEBUG) { "IdentityCheck: failed" }
                fail(11)
                return
            }
            when {
                ConnectionTypeHelper.isKeyConsultType(connectionType) -> startKeyConsultWithShake()
                supportEncrypt -> sendRsaConsultRequest()
                else -> finishSuccess()
            }
        } catch (e: InvalidProtocolBufferException) {
            logcat(LogPriority.ERROR) { "IdentityCheck\n${e.asLog()}" }
            fail(4)
        }
    }

    private fun processKeyConsult(frame: ByteArray) {
        try {
            val state = ConsultProto.KeyConsult.parseFrom(
                Arrays.copyOfRange(frame, 2, frame.size),
            ).state
            logcat(LogPriority.DEBUG) { "processKeyConsult: state=$state" }
            when (state) {
                1 -> {
                    if (needBond) {
                        SecurityStore.saveKey(mac, consultKey)
                    }
                    finishSuccess()
                }
                5 -> {
                    logcat(LogPriority.DEBUG) { "processKeyConsult: ERROR_KEY_NOT_EXIST" }
                    sendKeyConsultRequest(needSendKey = true)
                }
                else -> fail(state)
            }
        } catch (e: InvalidProtocolBufferException) {
            logcat(LogPriority.ERROR) { "processKeyConsult\n${e.asLog()}" }
            fail(4)
        }
    }

    private fun processShakeHand(frame: ByteArray) {
        try {
            logcat(LogPriority.DEBUG) { "processShakeHand: mNeedBond=$needBond" }
            val from = ConsultProto.ShakeHand.parseFrom(
                Arrays.copyOfRange(frame, 2, frame.size),
            )
            val source = intListToBytes(from.dataList)
            val encrypt = intListToBytes(from.encryptDataList)
            logcat(LogPriority.DEBUG) {
                "processShakeHand: source=${Arrays.toString(source)}, encrypt=${Arrays.toString(encrypt)}"
            }
            val key = consultKey ?: SecurityStore.getKey(mac)
            val matched = if (encrypt == null || shakeHandRandom == null || key == null) {
                false
            } else {
                val decrypted = CryptoHelper.decryptAes128NoIv(encrypt, key)
                Arrays.equals(shakeHandRandom, BitInvert.invert(decrypted))
            }
            if (!matched) {
                logcat(LogPriority.DEBUG) { "processShakeHand: key not match" }
                if (isConsultKeyModule) {
                    sendKeyConsultRequest(needSendKey = true)
                } else {
                    fail(8)
                }
                return
            }
            if (needBond) {
                SecurityStore.saveKey(mac, key)
            }
            if (supportEncrypt) {
                sendRsaConsultRequest()
            } else {
                finishSuccess()
            }
        } catch (e: InvalidProtocolBufferException) {
            logcat(LogPriority.ERROR) { "processShakeHand\n${e.asLog()}" }
            fail(4)
        }
    }

    private fun processAesConsult(frame: ByteArray) {
        logcat(LogPriority.DEBUG) { "processAESConsult" }
        try {
            val from = ConsultProto.AESConsult.parseFrom(
                Arrays.copyOfRange(frame, 2, frame.size),
            )
            val aesKeyEncrypted = from.getAESKeyEncrypted()
            if (aesKeyEncrypted == null || aesKeyEncrypted.isEmpty) {
                val state = from.state
                if (state == 1) {
                    finishSuccess()
                } else if (state != 0) {
                    fail(state)
                }
                return
            }
            val privateKey = rsaPrivate
            if (privateKey == null) {
                fail(12)
                return
            }
            aesIv = from.getAESIv()?.toByteArray()
            aesKey = RsaHelper.decryptWithPrivate(aesKeyEncrypted.toByteArray(), privateKey)
            if (aesKey == null) {
                fail(13)
                return
            }
            val remoteCipher = from.getEncryptData()?.toByteArray()
            val plain = from.getData()?.toByteArray()
            if (remoteCipher != null && plain != null) {
                val decrypted = CryptoHelper.decryptAes256(remoteCipher, aesKey!!, aesIv)
                if (decrypted == null || !decrypted.contentEquals(plain)) {
                    fail(4)
                    return
                }
            }
            sendAesConsultResponse()
        } catch (e: InvalidProtocolBufferException) {
            logcat(LogPriority.ERROR) { "processAESConsult\n${e.asLog()}" }
            fail(4)
        }
    }

    private fun sendIdentityConsultRequest() {
        logcat(LogPriority.DEBUG) { "sendIdentityConsultRequest" }
        localTimeRandom = System.currentTimeMillis()
        val body = ConsultProto.IdentityConsult.newBuilder()
            .setRandomNumber(localTimeRandom)
            .build()
            .toByteArray()
        sendCommand(CONSULT_SID, 24, body)
    }

    private fun sendIdentityCheckRequest() {
        logcat(LogPriority.DEBUG) { "sendIdentityCheckRequest" }
        val data = randomBytes()
        localRandomData = data
        val key = identityKey ?: run { fail(9); return }
        val encrypted = CryptoHelper.encryptAes128(data, key) ?: run { fail(10); return }
        val body = ConsultProto.IdentityCheck.newBuilder()
            .setData(ByteString.copyFrom(data))
            .setEncryptData(ByteString.copyFrom(encrypted))
            .build()
            .toByteArray()
        sendCommand(CONSULT_SID, 25, body)
    }

    private fun sendKeyConsultRequest(needSendKey: Boolean) {
        logcat(LogPriority.DEBUG) { "sendKeyConsultRequest: needSendKey=$needSendKey, mNeedBond=$needBond" }
        val builder = ConsultProto.KeyConsult.newBuilder()
        if (needSendKey) {
            val key = consultKey ?: SecurityStore.getKey(mac) ?: run { fail(5); return }
            consultKey = key
            builder.setKey(ByteString.copyFrom(key)).setIsConsultKey(true)
        } else {
            val key = SecurityStore.getKey(mac) ?: consultKey
            if (key == null) {
                fail(5)
                return
            }
            builder.setIsConsultKey(false)
        }
        sendCommand(CONSULT_SID, 22, builder.build().toByteArray())
    }

    private fun sendShakeHandRequest() {
        logcat(LogPriority.DEBUG) { "sendShakeHandRequest" }
        val key = consultKey ?: SecurityStore.getKey(mac) ?: run { fail(5); return }
        consultKey = key
        shakeHandRandom = randomBytes()
        val encrypted = CryptoHelper.encryptAes128NoIv(shakeHandRandom!!, key) ?: run { fail(10); return }
        val builder = ConsultProto.ShakeHand.newBuilder()
        shakeHandRandom!!.forEach { builder.addData(it.toInt() and 0xFF) }
        encrypted.forEach { builder.addEncryptData(it.toInt() and 0xFF) }
        sendCommand(CONSULT_SID, 21, builder.build().toByteArray())
    }

    private fun sendRsaConsultRequest() {
        logcat(LogPriority.DEBUG) { "sendRSAConsultRequest" }
        ensureRsaKeyPair()
        var modulus = rsaPublic!!.modulus.toByteArray()
        val exponent = rsaPublic!!.publicExponent.toByteArray()
        if (modulus.size > 256) {
            modulus = modulus.copyOfRange(1, 257)
        }
        val body = ConsultProto.RSAConsult.newBuilder()
            .setRsaBits(RSA_BITS)
            .setRsaKeyModulus(ByteString.copyFrom(modulus))
            .setRsaKeyExponent(ByteString.copyFrom(exponent))
            .build()
            .toByteArray()
        sendCommand(CONSULT_SID, 36, body)
    }

    private fun sendAesConsultResponse() {
        logcat(LogPriority.DEBUG) { "sendAESConsultResponse" }
        val body = ConsultProto.AESConsult.newBuilder()
            .setState(1)
            .build()
            .toByteArray()
        sendCommand(CONSULT_SID, 37, body)
    }

    private fun startKeyConsultWithShake() {
        logcat(LogPriority.DEBUG) { "startKeyConsultWithShake: connectionType=$connectionType, mNeedBond=$needBond" }
        if (!isConsultKeyModule) {
            sendShakeHandRequest()
            return
        }
        if (ConnectionTypeHelper.isKeyConsultType(connectionType)) {
            if (needBond) {
                sendKeyConsultRequest(needSendKey = true)
            } else {
                sendShakeHandRequest()
            }
        } else if (SecurityStore.getKey(mac) == null) {
            fail(5)
        } else {
            finishSuccess()
        }
    }

    private fun startKeyConsultV1() {
        if (!isConsultKeyModule) {
            if (SecurityStore.getKey(mac) != null) {
                finishSuccess()
            } else {
                fail(5)
            }
            return
        }
        if (ConnectionTypeHelper.isKeyConsultType(connectionType)) {
            sendKeyConsultRequest(needSendKey = needBond)
        } else if (SecurityStore.getKey(mac) == null) {
            fail(5)
        } else {
            finishSuccess()
        }
    }

    private fun sendCommand(sid: Int, cid: Int, body: ByteArray) {
        send(sid, cid, body)
    }

    private fun defaultTransferConfig(protocolVersion: Int): TransferConfigResult {
        return if (connectionType == 1) {
            TransferConfigResult(5000, 5000, 0, supportEncrypt)
        } else {
            TransferConfigResult(20, 20, 10, supportEncrypt)
        }
    }

    private fun ensureRsaKeyPair() {
        if (rsaPublic == null || rsaPrivate == null) {
            val pair = RsaHelper.generateKeyPair()
            rsaPublic = pair.publicKey
            rsaPrivate = pair.privateKey
        }
    }

    private fun intListToBytes(list: List<Int>): ByteArray? {
        if (list.isEmpty()) return null
        return ByteArray(list.size) { list[it].toByte() }
    }

    private fun randomBytes(): ByteArray {
        val data = ByteArray(4)
        Random.nextBytes(data)
        return data
    }

    private fun finishSuccess() {
        consulting = false
        if (needBond) {
            SecurityStore.saveKey(mac, consultKey)
        }
        onSuccess(
            TransferConfigResult(
                mtu = transferMtu,
                maxFrameSize = transferMaxFrame,
                interval = transferInterval,
                supportEncrypt = supportEncrypt,
            ),
        )
    }

    private fun fail(code: Int) {
        consulting = false
        logcat(LogPriority.ERROR) { "consult failed code=$code" }
        onError(code)
    }
}
