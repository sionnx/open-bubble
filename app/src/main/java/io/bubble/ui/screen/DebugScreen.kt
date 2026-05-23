package io.bubble.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.bubble.ui.theme.BubbleTheme

private val DebugBackground = Color(0xFFE8E8E8)
private val DebugButtonContainer = Color(0xFF4A4A4A)
private val DebugButtonLabel = Color.White
private val DebugSectionLabel = Color(0xFF8A8A8A)

data class DebugScanUiState(
    val status: String = "未扫描",
    val boundDevices: List<String> = emptyList(),
    val nearbyDevices: List<String> = emptyList(),
)

data class DebugActions(
    val onStartScan: () -> Unit = {},
    val onStopScan: () -> Unit = {},
    val onConnect: () -> Unit = {},
    val onDisconnect: () -> Unit = {},
    val onUnbind: () -> Unit = {},
    val onGetDeviceInfo: () -> Unit = {},
    val onSendMessageTest: () -> Unit = {},
    val onCopyWallpaperResources: () -> Unit = {},
    val onSelectPhotoSendWallpaper: () -> Unit = {},
    val onSendSingleResourceWallpaper: () -> Unit = {},
    val onSendMultipleResourcesWallpaper: () -> Unit = {},
    val onTestVideoEdit: () -> Unit = {},
    val onRequestOtaVersion: () -> Unit = {},
    val onStartOta: () -> Unit = {},
    val onLogFeedback: () -> Unit = {},
    val onGetDeviceThemeList: () -> Unit = {},
    val onSwitchTheme: () -> Unit = {},
    val onRequestPreviewImage: () -> Unit = {},
    val onUploadHLog: () -> Unit = {},
    val onP2pConnect: () -> Unit = {},
    val onP2pDisconnect: () -> Unit = {},
    val onAppUpdateCheck: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    scanState: DebugScanUiState = DebugScanUiState(),
    actions: DebugActions = DebugActions(),
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DebugBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "调试工具",
                        fontSize = 18.sp,
                        color = Color.Black,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DebugSectionLabel("设备扫描")
            Text(
                text = scanState.status,
                color = DebugSectionLabel,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
            )
            if (scanState.boundDevices.isNotEmpty()) {
                Text(
                    text = "已绑定: ${scanState.boundDevices.joinToString()}",
                    color = DebugSectionLabel,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (scanState.nearbyDevices.isNotEmpty()) {
                Text(
                    text = "附近: ${scanState.nearbyDevices.joinToString()}",
                    color = DebugSectionLabel,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            DebugButton("开始扫描设备", onClick = actions.onStartScan)
            DebugButton("停止扫描", onClick = actions.onStopScan)

            DebugSectionLabel("连接测试")
            DebugButton("连接测试", onClick = actions.onConnect)
            DebugButton("断开测试", onClick = actions.onDisconnect)
            DebugButton("解绑测试", onClick = actions.onUnbind)
            DebugButton("获取设备信息", onClick = actions.onGetDeviceInfo)
            DebugButton(
                "发送消息测试(读取设备信息 sid=1 cid=7)",
                onClick = actions.onSendMessageTest,
            )

            DebugSectionLabel("壁纸测试")

            DebugButton("拷贝壁纸资源", onClick = actions.onCopyWallpaperResources)
            DebugButton("选择照片发送壁纸", onClick = actions.onSelectPhotoSendWallpaper)
            DebugButton("单资源发送壁纸", onClick = actions.onSendSingleResourceWallpaper)
            DebugButton("多资源发送壁纸", onClick = actions.onSendMultipleResourcesWallpaper)
            DebugButton("测试视频编辑", onClick = actions.onTestVideoEdit)

            DebugSectionLabel("OTA测试")

            DebugButton("请求OTA版本", onClick = actions.onRequestOtaVersion)
            DebugButton("开始OTA", onClick = actions.onStartOta)
            DebugButton("日志反馈", onClick = actions.onLogFeedback)
            DebugButton("获取设备主题列表", onClick = actions.onGetDeviceThemeList)
            DebugButton("切换主题", onClick = actions.onSwitchTheme)
            DebugButton("请求预览图文件", onClick = actions.onRequestPreviewImage)
            DebugButton("主动上报HLog", onClick = actions.onUploadHLog)
            DebugButton("P2P连接测试", onClick = actions.onP2pConnect)
            DebugButton("P2P断开测试", onClick = actions.onP2pDisconnect)
            DebugButton("APP更新检查", onClick = actions.onAppUpdateCheck)
        }
    }
}

@Composable
private fun DebugButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DebugButtonContainer,
            contentColor = DebugButtonLabel,
        ),
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun DebugSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = DebugSectionLabel,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
private fun DebugScreenPreview() {
    BubbleTheme {
        DebugScreen()
    }
}
