package io.bubble.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bubble.preferences.AppPreferencesRepository
import io.bubble.ui.component.DeviceScanBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var showScanSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferencesRepository = remember { AppPreferencesRepository.getInstance(context) }
    val lastConnectedLabel by preferencesRepository.lastConnectedLabel
        .collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Bubble")
                },
                actions = {
                    IconButton(onClick = { showScanSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("Welcome to Bubble!")
            if (lastConnectedLabel != null) {
                Text("上次连接：$lastConnectedLabel")
            }
        }
    }

    DeviceScanBottomSheet(
        visible = showScanSheet,
        onDismiss = { showScanSheet = false },
    )
}
