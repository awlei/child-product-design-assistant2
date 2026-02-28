package com.design.assistant.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.design.assistant.ui.screens.home.HomeScreen
import com.design.assistant.ui.screens.result.DesignResultScreen
import com.design.assistant.ui.theme.DesignAssistantTheme
import com.design.assistant.viewmodel.DesignGenerateVM
import com.design.assistant.viewmodel.InputParametersVM
import com.design.assistant.viewmodel.ProductStandardSelectVM
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 主 Activity
 * 应用入口，使用 Jetpack Compose 构建UI
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesignAssistantTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("designResult") {
                            val designVM: DesignGenerateVM = viewModel()
                            val designResult = designVM.designResult.collectAsState().value

                            if (designResult != null) {
                                DesignResultScreen(
                                    result = designResult,
                                    onBack = {
                                        navController.navigateUp()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
