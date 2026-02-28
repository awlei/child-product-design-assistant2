package com.design.assistant.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
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
                            val designResult by designVM.designResult.collectAsState()

                            if (designResult != null) {
                                DesignResultScreen(
                                    result = designResult,
                                    onBack = {
                                        try {
                                            navController.navigateUp()
                                            designVM.clearResult()
                                        } catch (e: Exception) {
                                            android.util.Log.e("MainActivity", "导航失败", e)
                                        }
                                    }
                                )
                            } else {
                                // 如果没有设计结果，显示加载或错误页面
                                Text(
                                    text = "未找到设计方案",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
