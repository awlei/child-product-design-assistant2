package com.design.assistant.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
 * 简化版：移除复杂的自动导航逻辑
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesignAssistantTheme {
                // 创建 ViewModel 实例
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
                        // 首页
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                selectVM = selectVM,
                                inputVM = inputVM,
                                designVM = designVM
                            )
                        }

                        // 结果详情页（备用，当前主要在首页显示结果）
                        composable("designResult") {
                            val designResult by designVM.designResult.collectAsState()

                            if (designResult != null) {
                                DesignResultScreen(
                                    result = designResult!!,
                                    onBack = {
                                        try {
                                            navController.navigateUp()
                                            // 不清除结果，允许返回后继续查看
                                        } catch (e: Exception) {
                                            android.util.Log.e("MainActivity", "导航失败", e)
                                        }
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
