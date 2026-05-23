package io.bubble.core.connect

/** 对齐 Debug / HDM2Connect 连接状态语义。 */
enum class ConnectState {
    IDLE,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    FAILED,
}

data class ConnectParams(
    val mac: String,
    val model: String = DEFAULT_MODEL,
    val reason: String = "connect",
    val deviceSecret: String? = null,
) {
    companion object {
        const val DEFAULT_MAC = "00:0F:45:36:DD:EE"
        const val DEFAULT_MODEL = "OB19B1"
    }
}

data class TransferConfig(
    val mtu: Int,
    val maxFrameSize: Int,
    val interval: Int,
    val supportEncrypt: Boolean,
)

interface ConnectListener {
    fun onStateChanged(mac: String, state: ConnectState, detail: String? = null)
    fun onConnected(mac: String, config: TransferConfig)
    fun onFailed(mac: String, code: Int, detail: String)
}
