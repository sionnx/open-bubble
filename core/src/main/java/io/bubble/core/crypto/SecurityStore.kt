package io.bubble.core.crypto

import java.util.concurrent.ConcurrentHashMap

/** 对应 LinkService {@code fk.a}：按 MAC 缓存 Consult 密钥。 */
object SecurityStore {
    private val keys = ConcurrentHashMap<String, ByteArray>()

    fun getKey(mac: String): ByteArray? = keys[mac]

    fun saveKey(mac: String, key: ByteArray?) {
        if (key == null) {
            keys.remove(mac)
        } else {
            keys[mac] = key
        }
    }

    fun clearKey(mac: String) {
        keys.remove(mac)
    }
}
