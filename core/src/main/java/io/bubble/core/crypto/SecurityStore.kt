package io.bubble.core.crypto

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.bubble.core.CorePreferenceKeys
import io.bubble.core.bubbleCorePreferencesDataStore
import io.bubble.core.scan.EbadgeMacParser
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 按设备 MAC 持久化 Consult 握手密钥的本地仓库。
 *
 * 对应 OPPO LinkService 中的 {@code fk.a}。
 *
 * ## Consult 密钥是什么？
 *
 * RFCOMM 连接建立后，手机与徽章会走 **Consult 协议**（sid=1）完成能力协商与身份校验。
 * 其中 KeyConsult / ShakeHand 等步骤使用一把 **对称密钥**（16 字节，通常来自 [PairSecretGenerator] 生成的
 * 16 位随机字符串）做 AES-128 加解密，这把密钥在 [ConsultClient] 里称为 {@code consultKey}。
 *
 * ## 为什么要按 MAC 缓存？
 *
 * 每台徽章有唯一 MAC（经典蓝牙地址）。首次配对成功后，双方已确认共享同一把 consultKey；
 * 手机将其以 MAC 为键写入本仓库，下次连接同一台设备时可直接 [getKey] 取出，跳过重新协商，
 * 实现「已绑定设备快速重连」。若连接多台徽章，则各自 MAC 对应各自的密钥，互不覆盖。
 *
 * 存储策略：内存 [ConcurrentHashMap] 供同步读取；Jetpack DataStore 异步落盘（Base64 编码）。
 */
object SecurityStore {
    /** 内存中的 MAC → Consult 密钥映射，供 [ConsultClient] 同步 [getKey] 使用。 */
    private val keys = ConcurrentHashMap<String, ByteArray>()

    /** 后台协程，负责 DataStore 读写，避免阻塞 Consult 握手线程。 */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var dataStore: DataStore<Preferences>? = null

    /**
     * 初始化本地持久化，并从 DataStore 预加载已保存的 Consult 密钥到内存。
     *
     * 应在应用启动时调用一次（例如 [io.bubble.core.connect.BubbleConnectManager.init]）。
     * 若 [ConsultClient] 在预加载完成前发起连接，[getKey] 可能暂时返回 null，握手会走首次配对流程。
     *
     * @param context 任意 [Context]；内部使用 [Context.getApplicationContext]。
     */
    fun init(context: Context) {
        if (dataStore != null) return
        val store = context.applicationContext.bubbleCorePreferencesDataStore
        dataStore = store
        scope.launch {
            hydrateFromStore(store.data.first())
        }
    }

    /**
     * 读取指定 MAC 对应的 Consult 密钥。
     *
     * 供 [ConsultClient] 在 ShakeHand、KeyConsult 等步骤校验或发送密钥时使用。
     * MAC 会先经 [EbadgeMacParser.normalizeMac] 规范化（大写、去空白）。
     *
     * @param mac 目标设备的经典蓝牙 MAC，须与扫描/连接时使用的地址一致。
     * @return 已缓存的密钥；若该设备从未成功完成 Consult 绑定则返回 null。
     */
    fun getKey(mac: String): ByteArray? = keys[EbadgeMacParser.normalizeMac(mac)]

    /**
     * 保存或清除指定 MAC 的 Consult 密钥。
     *
     * Consult 握手成功（KeyConsult state=1、ShakeHand 校验通过、[ConsultClient.finishSuccess]）
     * 且 {@code needBond=true} 时由 [ConsultClient] 调用，将当前 {@code consultKey} 持久化。
     * 传入 null 等价于删除该 MAC 的密钥。
     *
     * 先更新内存以保证后续 [getKey] 立即可见，再异步写入 DataStore。
     *
     * @param mac 设备 MAC。
     * @param key Consult 对称密钥；null 表示清除。
     */
    fun saveKey(mac: String, key: ByteArray?) {
        val normalized = EbadgeMacParser.normalizeMac(mac)
        if (key == null) {
            keys.remove(normalized)
        } else {
            keys[normalized] = key
        }
        val store = dataStore ?: return
        scope.launch {
            store.edit { prefs ->
                val prefKey = CorePreferenceKeys.consultKey(normalized)
                if (key == null) {
                    prefs.remove(prefKey)
                } else {
                    prefs[prefKey] = Base64.encodeToString(key, Base64.NO_WRAP)
                }
            }
        }
    }

    /**
     * 删除指定 MAC 的 Consult 密钥（内存 + DataStore）。
     *
     * 解绑或需要强制重新配对时可调用；内部委托 [saveKey] 传入 null。
     *
     * @param mac 设备 MAC。
     */
    fun clearKey(mac: String) {
        saveKey(mac, null)
    }

    /**
     * 从 DataStore 快照恢复全部 Consult 密钥到内存。
     *
     * 仅处理键名以 {@code consult_key_} 开头的偏好项，值为 Base64 编码的字节数组。
     * 由 [init] 在后台调用，全量替换当前 [keys] 内容。
     */
    private fun hydrateFromStore(prefs: Preferences) {
        val loaded = ConcurrentHashMap<String, ByteArray>()
        prefs.asMap().forEach { (key, value) ->
            val name = key.name
            if (!name.startsWith(CONSULT_KEY_PREFIX) || value !is String) return@forEach
            val mac = name.removePrefix(CONSULT_KEY_PREFIX)
            runCatching {
                loaded[mac] = Base64.decode(value, Base64.NO_WRAP)
            }
        }
        keys.clear()
        keys.putAll(loaded)
    }

    private const val CONSULT_KEY_PREFIX = "consult_key_"
}
