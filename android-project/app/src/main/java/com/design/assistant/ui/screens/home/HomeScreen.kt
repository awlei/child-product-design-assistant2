package com.design.assistant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.util.Log
import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.DesignResult
import com.design.assistant.model.InputParameters
import com.design.assistant.model.ProductType
import com.design.assistant.ui.components.InputDialog
import com.design.assistant.viewmodel.InputParametersVM
import com.design.assistant.viewmodel.ProductStandardSelectVM

private const val TAG = "HomeScreen"

/**
 * 首页 Screen - 重新设计版
 * 简化流程，避免复杂的自动导航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    selectVM: ProductStandardSelectVM,
    inputVM: InputParametersVM,
    designVM: com.design.assistant.viewmodel.DesignGenerateVM
) {
    // UI 状态
    var selectedProduct by remember { mutableStateOf<ProductType>(ProductType.CHILD_SEAT) }
    var selectedStandard by remember { mutableStateOf<String>(StandardConstants.ECE_R129) }
    var showInputDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showResultCard by remember { mutableStateOf(false) }

    // ViewModel 状态
    val currentInputParameters by inputVM.inputParameters.collectAsState()
    val designResult by designVM.designResult.collectAsState()
    val isGenerating by designVM.isGenerating.collectAsState()
    val generateError by designVM.errorMessage.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // 监听生成状态变化
    LaunchedEffect(isGenerating, designResult, generateError) {
        Log.d(TAG, "状态变化: isGenerating=$isGenerating, designResult=$designResult, error=$generateError")

        if (!isGenerating) {
            if (designResult != null) {
                // 生成成功，显示结果
                Log.d(TAG, "生成成功，显示结果")
                showResultCard = true
            } else if (generateError != null) {
                // 生成失败，显示错误
                Log.e(TAG, "生成失败: $generateError")
                errorMessage = generateError ?: "未知错误"
                showErrorDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "儿童产品设计助手",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 产品选择
            ProductSelector(
                selectedProduct = selectedProduct,
                onProductSelected = { product ->
                    selectedProduct = product
                    selectedStandard = StandardConstants.getStandardsByProduct(product).first()
                    selectVM.selectProductType(product)
                }
            )

            // 标准选择
            StandardSelector(
                selectedStandard = selectedStandard,
                availableStandards = StandardConstants.getStandardsByProduct(selectedProduct),
                onStandardSelected = { standard ->
                    selectedStandard = standard
                    selectVM.selectStandard(standard)
                }
            )

            // 设计信息显示
            DesignInfoCard(
                product = selectedProduct,
                standard = selectedStandard
            )

            // 加载状态
            if (isGenerating) {
                LoadingCard()
            }

            // 结果卡片
            if (showResultCard && designResult != null && !isGenerating) {
                ResultCard(
                    result = designResult!!,
                    onClose = { showResultCard = false }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 功能按钮
            ActionButtons(
                isGenerating = isGenerating,
                showResultCard = showResultCard,
                onGenerateDesign = {
                    // 清除之前的结果
                    designVM.clearResult()
                    showResultCard = false

                    // 打开输入对话框
                    showInputDialog = true
                },
                onViewHistory = { /* TODO: 查看历史设计 */ }
            )

            // 底部安全区域
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 输入对话框
    InputDialog(
        productType = selectedProduct,
        standardSystem = selectedStandard,
        visible = showInputDialog,
        onDismiss = { showInputDialog = false },
        onConfirm = { params ->
            Log.d(TAG, "用户确认输入参数: $params")

            // 验证参数
            val validation = params.validate()
            if (validation is com.design.assistant.model.ValidationResult.Success) {
                Log.d(TAG, "参数验证成功，开始生成设计方案")
                inputVM.setInputParameters(params)
                showInputDialog = false

                // 生成设计方案
                designVM.generateDesign(
                    productType = selectedProduct,
                    standardSystem = selectedStandard,
                    inputParameters = params
                )
            } else if (validation is com.design.assistant.model.ValidationResult.Error) {
                Log.e(TAG, "参数验证失败: ${validation.message}")
                // 显示错误信息给用户
                errorMessage = "参数验证失败：${validation.message}"
                showErrorDialog = true
            }
        }
    )

    // 错误提示对话框
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                errorMessage = ""
                designVM.clearError()
            },
            title = { Text("错误") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                        errorMessage = ""
                        designVM.clearError()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

/**
 * 加载卡片
 */
@Composable
fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "正在生成设计方案...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 结果卡片
 */
@Composable
fun ResultCard(
    result: DesignResult,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "设计方案已生成",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭"
                    )
                }
            }

            Divider()

            // 基本信息
            Text(
                text = "产品：${result.productName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "标准：${result.standardName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "假人类型：${result.basicAdaptationData.dummyInfo.dummyType}",
                style = MaterialTheme.typography.bodyMedium
            )

            Divider()

            // 详细信息
            Text(
                text = "设计参数：",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "头枕高度：${result.designParameters.headrestHeight}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "座椅宽度：${result.designParameters.seatWidth}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "包装尺寸：${result.designParameters.envelope.length} × ${result.designParameters.envelope.width} × ${result.designParameters.envelope.height}",
                style = MaterialTheme.typography.bodySmall
            )

            // 查看详情按钮
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: 导航到详情页 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("查看完整详情")
            }
        }
    }
}
