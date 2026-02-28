package com.design.assistant.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
                    // 监听设计结果变化，自动导航到结果页
                    val designResult by designVM.designResult.collectAsState()
                    val isGenerating by designVM.isGenerating.collectAsState()
                    var hasNavigatedToResult by remember { mutableStateOf(false) }

                    // 当设计结果生成成功时，自动导航到结果页
                    LaunchedEffect(designResult, isGenerating) {
                        if (designResult != null && !isGenerating && !hasNavigatedToResult) {
                            try {
                                android.util.Log.d("MainActivity", "设计方案生成成功，导航到结果页")
                                navController.navigate("designResult")
                                hasNavigatedToResult = true
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "导航失败", e)
                            }
                        } else if (isGenerating) {
                            // 重置导航标志，允许下次导航
                            hasNavigatedToResult = false
                        }
                    }

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
                            val resultDesignResult by designVM.designResult.collectAsState()

                            if (resultDesignResult != null) {
                                val result = resultDesignResult!!
                                DesignResultScreen(
                                    result = result,
                                    onBack = {
                                        try {
                                            navController.navigateUp()
                                            designVM.clearResult()
                                            hasNavigatedToResult = false  // 重置导航标志
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
