package io.bubble.core.protocol

import java.nio.ByteBuffer
import java.util.ArrayList

/**
 * 对应 LinkService {@code pj.g} 未加密 Consult 帧封包/解包（SOF=0xAA）。
 */
class TransportLayer(
    private val maxMtu: Int = 20,
) {
    private val buffer = ByteBuffer.allocate(8192)

    fun packConsultPayload(sid: Int, cid: Int, body: ByteArray): List<ByteArray> {
        val payload = ByteArray(body.size + 2)
        payload[0] = (sid and 0xFF).toByte()
        payload[1] = (cid and 0xFF).toByte()
        System.arraycopy(body, 0, payload, 2, body.size)
        return buildFrames(payload, encrypt = false, compressed = false)
    }

    fun feed(bytes: ByteArray): List<ByteArray> {
        buffer.put(bytes)
        buffer.flip()
        val remaining = ByteArray(buffer.remaining())
        buffer.get(remaining)
        buffer.clear()
        return parseFrames(remaining)
    }

    private fun buildFrames(
        data: ByteArray,
        encrypt: Boolean,
        compressed: Boolean,
    ): List<ByteArray> {
        val frames = ArrayList<ByteArray>()
        var offset = 0
        var seq = 0
        var remaining = data.size
        while (remaining > 0) {
            val headerLen = lengthHeaderSize(remaining)
            val maxPayload = maxMtu - headerLen - 1 - 2 - 2
            val chunk: Int
            val flags: Byte
            if (seq == 0) {
                if (remaining <= maxPayload) {
                    chunk = remaining
                    flags = 0
                } else {
                    chunk = maxPayload - 1
                    flags = 1
                }
            } else if (remaining <= maxPayload - 1) {
                chunk = remaining
                flags = 3
            } else {
                chunk = maxPayload - 1
                flags = 2
            }
            val frameLen = if (seq > 0) chunk + 3 else chunk + 2
            val total = frameLen + 1 + headerLen + 2
            val frame = ByteArray(total)
            frame[0] = 0xAA.toByte()
            writeLength(frame, 1, frameLen)
            var metaIndex = 1 + headerLen
            var meta = 0
            if (encrypt) meta = meta or 0x40
            if (compressed) meta = meta or 0x08
            meta = meta or (flags.toInt() and 0x03)
            frame[metaIndex] = meta.toByte()
            frame[metaIndex + 1] = (meta shr 8).toByte()
            metaIndex += 2
            if (seq > 0) {
                frame[metaIndex] = (seq and 0xFF).toByte()
                metaIndex++
            }
            System.arraycopy(data, offset, frame, metaIndex, chunk)
            val crcIndex = total - 2
            val crc = FrameCrc.compute(frame, 0, crcIndex)
            frame[crcIndex] = (crc and 0xFF).toByte()
            frame[crcIndex + 1] = ((crc shr 8) and 0xFF).toByte()
            frames.add(frame)
            offset += chunk
            remaining -= chunk
            seq++
        }
        return frames
    }

    private fun parseFrames(input: ByteArray): List<ByteArray> {
        val packets = ArrayList<ByteArray>()
        var index = 0
        while (index < input.size) {
            if (input[index] != 0xAA.toByte()) {
                index++
                continue
            }
            if (index + 6 > input.size) break
            var cursor = index + 1
            val lenByte = input[cursor].toInt()
            var frameLen = lenByte and 0x7F
            cursor++
            if (lenByte and 0x80 != 0) {
                if (cursor >= input.size) break
                frameLen = frameLen or ((input[cursor].toInt() and 0x7F) shl 7)
                cursor++
            }
            val end = cursor + frameLen + 2
            if (end > input.size) break
            val payload = unwrapFrame(input, cursor, frameLen)
            if (payload != null) {
                packets.add(payload)
            }
            index = end
        }
        return packets
    }

    private fun unwrapFrame(data: ByteArray, metaStart: Int, frameLen: Int): ByteArray? {
        if (metaStart + frameLen > data.size) return null
        val metaLow = data[metaStart].toInt()
        val hasSeq = (metaLow and 0x03) != 0
        val payloadStart = metaStart + 2 + if (hasSeq) 1 else 0
        val payloadLen = frameLen - 2 - if (hasSeq) 1 else 0
        if (payloadStart + payloadLen > data.size) return null
        return data.copyOfRange(payloadStart, payloadStart + payloadLen)
    }

    private fun lengthHeaderSize(remaining: Int): Int {
        val frameLenEstimate = remaining + 4
        return if (frameLenEstimate <= 127) 1 else 2
    }

    private fun writeLength(frame: ByteArray, offset: Int, length: Int) {
        if (length <= 127) {
            frame[offset] = (length and 0x7F).toByte()
        } else {
            frame[offset] = ((length and 0x7F) or 0x80).toByte()
            frame[offset + 1] = ((length ushr 7) and 0x7F).toByte()
        }
    }
}
