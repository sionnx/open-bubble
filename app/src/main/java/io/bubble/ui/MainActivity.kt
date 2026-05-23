package io.bubble.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.bubble.ui.component.BluetoothPermissionGate
import io.bubble.ui.screen.HomeScreen
import io.bubble.ui.theme.BubbleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BubbleTheme {
                BluetoothPermissionGate {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                    ) {
                        composable("home") {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}
