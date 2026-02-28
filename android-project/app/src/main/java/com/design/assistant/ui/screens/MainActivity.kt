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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.design.assistant.ui.screens.home.HomeScreen
import com.design.assistant.ui.screens.result.DesignResultScreen
import com.design.assistant.ui.theme.DesignAssistantTheme
import com.design.assistant.viewmodel.DesignGenerateVM
import com.design.assistant.viewmodel.InputParametersVM
import com.design.assistant.viewmodel.ProductStandardSelectVM

/**
 * 主 Activity
 * 应用入口，使用 Jetpack Compose 构建UI
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesignAssistantTheme {
                // 在 Activity 级别创建 ViewModel 实例，确保所有 Screen 共享同一个实例
                val designVM: DesignGenerateVM = viewModel()
                val inputVM: InputParametersVM = viewModel()
                val selectVM: ProductStandardSelectVM = viewModel()
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
                            HomeScreen(
                                navController = navController,
                                selectVM = selectVM,
                                inputVM = inputVM,
                                designVM = designVM
                            )
                        }
                        composable("designResult") {
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
