# io.bubble.core — Agent 指南

本目录复刻 OPPO Bubble（`com.oplus.ebadge`）的 **BLE 发现** 与 **经典蓝牙配对连接** 能力，仅依赖 Android 蓝牙 API 与自研协议层，**不调用** HeyTap DeviceManager / LinkService / OAF SDK。

更完整的逆向背景见仓库根目录 [`reverse.md`](../../../../../../../reverse.md)。

---

## 目录结构

| 包 / 文件    | 职责                                   |
| ------------ | -------------------------------------- |
| `scan/`      | BLE 扫描、厂商数据解析、权限辅助       |
| `connect/`   | 配对连接编排（`BubbleConnectManager`） |
| `bluetooth/` | RFCOMM 传输、`BondHelper` 系统配对     |
| `protocol/`  | Consult 握手、`LinkUuids`、帧 CRC      |
| `device/`    | 设备电量查询（sid=1 cid=8）            |
| `crypto/`    | 密钥、RSA、本地安全存储                |

---

## 推荐流程：先扫描，再配对连接

```text
1. 申请蓝牙权限（见下文）
2. DeviceScanController.loadBoundDevices() + requestScan()
3. 从 onNearbyDevicesChanged 取得真实 MAC（不要用 ScanResult.device.address）
4. BubbleConnectManager.connectDeviceByPair(ConnectParams(mac = …))
5. 监听 ConnectListener，CONNECTED 后可发 Consult / 业务消息
6. Activity onDestroy：scanController.release()，BubbleConnectManager.disconnect()
```

完整可参考 [`io.bubble.ui.DebugActivity`](../ui/DebugActivity.kt)。

---

## 一、扫描 API（`scan`）

### 核心类

| 类                         | 说明                                               |
| -------------------------- | -------------------------------------------------- |
| `DeviceScanController`     | 主入口，对齐官方 `DeviceScanController`            |
| `DeviceScanListener`       | 扫描/绑定列表回调                                  |
| `ScannedDevice`            | `mac` + `displayName`                              |
| `EbadgeMacParser`          | 从 `ScanResult` 解析真实 MAC（`jf.a1`）            |
| `BluetoothScanPermissions` | 运行时权限与蓝牙开关检查                           |
| `BondedDeviceRegistry`     | 已配对设备 MAC（`BluetoothAdapter.bondedDevices`） |
| `EbadgeScanConstants`      | 厂商 ID 1946、扫描时长 15s 等常量                  |

### 行为要点

- **过滤**：`ScanFilter` 厂商 ID `1946`，数据前缀 `{8,16}`，掩码 `{0xFF,0xFF}`。
- **MAC**：取厂商数据**最后 6 字节**，格式 `AA:BB:CC:DD:EE:FF`；与 `result.device.address` 可能不一致。
- **去重**：按 MAC；已出现在「已绑定」集合中的设备不会进入附近列表。
- **时长**：单次扫描 **15 秒** 后自动 `stopScan()`；也可主动 `stopScan()`。
- **已绑定列表**：默认来自系统已配对设备；可通过构造参数 `boundMacProvider` 自定义。

### 初始化与生命周期

```kotlin
val scanController = DeviceScanController(
    context = activity,
    listener = DeviceScanController.forwardingListener(
        isActive = { !activity.isFinishing && !activity.isDestroyed },
        onBoundDevicesLoaded = { /* List<ScannedDevice> */ },
        onNearbyDevicesChanged = { /* 增量全量列表 */ },
        onScanFailed = { errorCode -> },
        onRequestPermissions = { _, onResult ->
            // 宿主弹窗后必须调用 onResult(true/false)
            requestBluetoothPermissions { granted -> onResult(granted) }
        },
        onScanStarted = { },
        onScanStopped = { },
    ),
)

// Activity.onDestroy
scanController.release()
```

### 常用方法

```kotlin
// 刷新已绑定 MAC 集合并回调 onBoundDevicesLoaded
scanController.loadBoundDevices()

// 推荐：先走 onRequestPermissions，再扫描
scanController.requestScan()

// 权限已就绪时可直接扫
scanController.startScan()

scanController.stopScan()

// 当前附近设备快照（连接前读取）
val nearby: List<ScannedDevice> = scanController.getNearbyDevices()
```

### 权限（`BluetoothScanPermissions`）

| API 级别              | 需要权限                                               |
| --------------------- | ------------------------------------------------------ |
| Android 12+ (API 31+) | `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`                  |
| API 29–30             | `BLUETOOTH`, `BLUETOOTH_ADMIN`, `ACCESS_FINE_LOCATION` |

```kotlin
val permissions = BluetoothScanPermissions.requiredPermissions()
val ready = BluetoothScanPermissions.hasScanPermissions(context)
val btOn = BluetoothScanPermissions.isBluetoothEnabled()
```

Manifest 已声明 `BLUETOOTH_SCAN` 且带 `neverForLocation`（仅按厂商 ID 扫描，不依赖定位）。

### 单独解析 MAC（无控制器）

```kotlin
val mac: String? = EbadgeMacParser.parseRealMac(scanResult)
val normalized = mac?.let { EbadgeMacParser.normalizeMac(it) }
```

---

## 二、配对连接 API（`connect` + `bluetooth`）

### 核心类

| 类                     | 说明                                                            |
| ---------------------- | --------------------------------------------------------------- |
| `BubbleConnectManager` | 单例，对齐 `HDM2Connect.connectDeviceByPair`                    |
| `ConnectParams`        | MAC、型号、reason、可选 `deviceSecret`                          |
| `ConnectListener`      | 状态 / 成功 / 失败回调                                          |
| `ConnectState`         | `IDLE` → `CONNECTING` → `CONNECTED` / `FAILED` / `DISCONNECTED` |
| `TransferConfig`       | Consult 成功后的 mtu、帧长、间隔、是否加密                      |
| `BondHelper`           | `createBond` + 等待 `BOND_BONDED`（连接流程内部调用）           |
| `RfcommTransport`      | RFCOMM + Consult 帧读写                                         |

### 初始化

```kotlin
// Application 或 Activity.onCreate 一次即可
BubbleConnectManager.init(applicationContext)
BubbleConnectManager.addListener(connectListener)
// onDestroy
BubbleConnectManager.removeListener(connectListener)
```

### 配对连接

```kotlin
BubbleConnectManager.connectDeviceByPair(
    ConnectParams(
        mac = "AA:BB:CC:DD:EE:FF",  // 必须来自扫描解析的 MAC
        model = ConnectParams.DEFAULT_MODEL,  // 默认 "OB19B1"
        reason = "connect",
        deviceSecret = null,  // null 则自动生成 pair secret
    ),
)
```

内部顺序（IO 协程，勿在主线程阻塞等待）：

1. 检查蓝牙已开启
2. `BondHelper.ensureBonded` — 系统配对弹窗
3. `RfcommSocket` 连接 `LinkUuids.CONSULT_MAIN`
4. `ConsultClient.start()` — Consult 握手
5. `onConnected` + `TransferConfig`

### 断开

```kotlin
BubbleConnectManager.disconnect(mac, reason = "disconnect")
```

### 监听示例

```kotlin
val listener = object : ConnectListener {
    override fun onStateChanged(mac: String, state: ConnectState, detail: String?) {
        when (state) {
            ConnectState.CONNECTING -> { }
            ConnectState.CONNECTED -> { }
            ConnectState.FAILED -> { /* detail 含错误信息 */ }
            ConnectState.DISCONNECTED -> { }
            ConnectState.IDLE -> { }
        }
    }

    override fun onConnected(mac: String, config: TransferConfig) {
        // config.mtu, maxFrameSize, interval, supportEncrypt
    }

    override fun onFailed(mac: String, code: Int, detail: String) {
        // 常见：-1 未 init，103 蓝牙/配对/连接失败，301 socket 关闭
    }
}
```

### 查询状态

```kotlin
BubbleConnectManager.getState(): ConnectState
BubbleConnectManager.getActiveMac(): String?
BubbleConnectManager.getLastTransferConfig(): TransferConfig?
```

### 仅系统配对（不跑完整 Consult）

```kotlin
// 挂起函数，需在协程中调用
BondHelper.ensureBonded(context, BondHelper.remoteDevice(mac))
```

一般应优先使用 `BubbleConnectManager`，其已包含配对 + RFCOMM + Consult。

---

## 三、设备电量 API（`device`）

对齐官方 `hb.a.a`（**sid=1, cid=8**）与 `mb.c` 应答解析。

### 核心类

| 类 | 说明 |
| --- | --- |
| `DeviceBatteryClient` | 对外入口：拉取、缓存、监听 |
| `DeviceBatteryInfo` | `mac` / `percent`（0–100，未知为 -1）/ `isCharging` |
| `DeviceBatteryListener` | 电量更新回调（查询结果或设备上报） |
| `BatteryException` / `BatteryError` | 未连接、超时、解析失败、IO 错误 |

### 前置条件

1. `BubbleConnectManager.init(context)`（内部会注册电量报文监听）
2. `connectDeviceByPair` 且 `ConnectState.CONNECTED`

### 拉取电量（挂起）

```kotlin
// 默认 activeMac，5s 超时
val info: DeviceBatteryInfo = DeviceBatteryClient.fetchBattery()

// 指定 MAC / 超时
val info2 = DeviceBatteryClient.fetchBattery(
    mac = "AA:BB:CC:DD:EE:FF",
    timeoutMs = 8_000L,
)
// info.percent in 0..100；info.isKnown == true
// info.isCharging
```

### 读缓存（不发蓝牙）

```kotlin
val cached = DeviceBatteryClient.getCachedBattery() // 最近一次成功值
```

### 监听变更

```kotlin
DeviceBatteryClient.addListener { info ->
    Log.d("battery", "${info.mac} ${info.percent}% charging=${info.isCharging}")
}
// onDestroy: DeviceBatteryClient.removeListener(listener)
```

### 错误处理

```kotlin
try {
    DeviceBatteryClient.fetchBattery()
} catch (e: BatteryException) {
    when (e.error) {
        BatteryError.NOT_CONNECTED -> { }
        BatteryError.TIMEOUT -> { }
        BatteryError.PARSE_ERROR -> { }
        BatteryError.IO_ERROR -> { }
    }
}
```

断开连接后 `BubbleConnectManager.disconnect` 会清除该 MAC 的电量缓存。

---

## 四、扫描 + 连接串联示例

```kotlin
class MyActivity : ComponentActivity() {
    private lateinit var scanController: DeviceScanController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BubbleConnectManager.init(applicationContext)

        scanController = DeviceScanController(
            context = this,
            listener = DeviceScanController.forwardingListener(
                isActive = { !isFinishing && !isDestroyed },
                onBoundDevicesLoaded = { },
                onNearbyDevicesChanged = { devices ->
                    // 可选：自动连接第一台
                    // devices.firstOrNull()?.let { connect(it.mac) }
                },
                onScanFailed = { },
                onRequestPermissions = { _, onResult ->
                    ensureBtPermissions { onResult(it) }
                },
            ),
        )
    }

    fun onScanClick() {
        if (!BluetoothScanPermissions.isBluetoothEnabled()) return
        scanController.loadBoundDevices()
        scanController.requestScan()
    }

    fun connect(mac: String) {
        BubbleConnectManager.connectDeviceByPair(
            ConnectParams(mac = mac, reason = "connect"),
        )
    }

    override fun onDestroy() {
        scanController.release()
        BubbleConnectManager.getActiveMac()?.let {
            BubbleConnectManager.disconnect(it)
        }
        super.onDestroy()
    }
}
```

---

## 五、Agent 注意事项

1. **MAC 来源**：连接参数必须使用 `EbadgeMacParser` / `DeviceScanController` 得到的 MAC，不要使用 BLE 外设地址。
2. **线程**：`BubbleConnectManager` 回调来自后台协程；更新 UI 请切主线程。
3. **单连接**：`BubbleConnectManager` 同时只维护一条活跃连接；新 `connectDeviceByPair` 会取消上一次任务。
4. **无 OEM 设备库**：`loadBoundDevices` 不等于官方 `UserDeviceInfo` 云同步列表，仅为本机已配对蓝牙设备。
5. **扩展业务**：Consult 成功后的消息经 `BubbleConnectManager.sendBusinessMessage` 发送；电量见 `device/DeviceBatteryClient`。其他 sid/cid 继续对照 `reverse.md` 与 `tmp/jadx-out`。
6. **不要** 在本目录引入 HeyTap / Oplus wearable / DeviceManager 依赖。

---

## 六、错误码速查

| 场景                          | code / 常量                                                |
| ----------------------------- | ---------------------------------------------------------- |
| 扫描器不可用                  | `EbadgeScanConstants.SCAN_FAILED_SCANNER_UNAVAILABLE` (-1) |
| ConnectManager 未 init        | -1                                                         |
| 蓝牙未开 / 配对或 socket 失败 | 103                                                        |
| Consult 失败 / socket 断开    | 301                                                        |

---

## 七、相关文件索引

- 扫描实现：`scan/DeviceScanController.kt`
- 连接实现：`connect/BubbleConnectManager.kt`
- 电量实现：`device/DeviceBatteryClient.kt`、`device/BatteryMessageCodec.kt`
- 集成示例：`ui/DebugActivity.kt`
- 逆向文档：仓库根 `reverse.md` §5（BLE 发现）、§4.1（连接测试）
