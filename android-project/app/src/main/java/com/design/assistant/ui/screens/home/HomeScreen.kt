package com.design.assistant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.util.Log
import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.DesignResult
import com.design.assistant.model.InputParameters
import com.design.assistant.model.ProductType
import com.design.assistant.viewmodel.InputParametersVM
import com.design.assistant.viewmodel.ProductStandardSelectVM

private const val TAG = "HomeScreen"

/**
 * 首页 Screen - 重新设计版（输入表单模式）
 * 先用户输入参数，后生成并显示结果
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
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showResultCard by remember { mutableStateOf(false) }

    // 输入参数状态
    var minWeight by remember { mutableStateOf("") }
    var maxWeight by remember { mutableStateOf("") }
    var minHeight by remember { mutableStateOf("") }
    var maxHeight by remember { mutableStateOf("") }

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

    // 确认生成
    fun onConfirmGenerate() {
        Log.d(TAG, "用户确认输入参数")

        // 根据标准体系创建参数
        val params = when {
            selectedStandard.contains("ECE R129") -> {
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = selectedStandard,
                    minHeight = minHeight.toIntOrNull(),
                    maxHeight = maxHeight.toIntOrNull(),
                    minWeight = null,
                    maxWeight = null
                )
            }
            selectedStandard.contains("FMVSS") || selectedStandard.contains("CMVSS") -> {
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = selectedStandard,
                    minHeight = null,
                    maxHeight = null,
                    minWeight = minWeight.toDoubleOrNull(),
                    maxWeight = maxWeight.toDoubleOrNull()
                )
            }
            else -> {
                // GB 标准，身高和体重都需要
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = selectedStandard,
                    minHeight = minHeight.toIntOrNull(),
                    maxHeight = maxHeight.toIntOrNull(),
                    minWeight = minWeight.toDoubleOrNull(),
                    maxWeight = maxWeight.toDoubleOrNull()
                )
            }
        }

        // 验证参数
        val validation = params.validate()
        if (validation is com.design.assistant.model.ValidationResult.Success) {
            Log.d(TAG, "参数验证成功，开始生成设计方案")
            inputVM.setInputParameters(params)

            // 清除之前的结果
            designVM.clearResult()
            showResultCard = false

            // 生成设计方案
            designVM.generateDesign(
                productType = selectedProduct,
                standardSystem = selectedStandard,
                inputParameters = params
            )
        } else if (validation is com.design.assistant.model.ValidationResult.Error) {
            Log.e(TAG, "参数验证失败: ${validation.message}")
            errorMessage = "参数验证失败：${validation.message}"
            showErrorDialog = true
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

            // 输入参数表单
            InputParametersForm(
                standard = selectedStandard,
                minHeight = minHeight,
                maxHeight = maxHeight,
                minWeight = minWeight,
                maxWeight = maxWeight,
                onMinHeightChange = { minHeight = it },
                onMaxHeightChange = { maxHeight = it },
                onMinWeightChange = { minWeight = it },
                onMaxWeightChange = { maxWeight = it },
                onConfirm = { onConfirmGenerate() },
                isGenerating = isGenerating
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

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
 * 输入参数表单
 * 根据标准体系显示不同的输入字段
 */
@Composable
fun InputParametersForm(
    standard: String,
    minHeight: String,
    maxHeight: String,
    minWeight: String,
    maxWeight: String,
    onMinHeightChange: (String) -> Unit,
    onMaxHeightChange: (String) -> Unit,
    onMinWeightChange: (String) -> Unit,
    onMaxWeightChange: (String) -> Unit,
    onConfirm: () -> Unit,
    isGenerating: Boolean
) {
    val needHeight = standard.contains("ECE R129") || standard.contains("GB") || standard.contains("AS/NZS")
    val needWeight = standard.contains("FMVSS") || standard.contains("CMVSS") || standard.contains("GB") || standard.contains("AS/NZS")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题
            Text(
                text = "输入参数",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Divider()

            // 身高输入
            if (needHeight) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "身高范围 (cm)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = minHeight,
                            onValueChange = onMinHeightChange,
                            label = { Text("最小身高") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        OutlinedTextField(
                            value = maxHeight,
                            onValueChange = onMaxHeightChange,
                            label = { Text("最大身高") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }
                }
                Divider()
            }

            // 体重输入
            if (needWeight) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "体重范围 (kg)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = minWeight,
                            onValueChange = onMinWeightChange,
                            label = { Text("最小体重") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                            )
                        )
                        OutlinedTextField(
                            value = maxWeight,
                            onValueChange = onMaxWeightChange,
                            label = { Text("最大体重") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                            )
                        )
                    }
                }
                Divider()
            }

            // 确认按钮
            Button(
                onClick = onConfirm,
                enabled = !isGenerating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "生成",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isGenerating) "生成中..." else "生成设计方案")
            }
        }
    }
}

/**
 * 加载卡片
 */

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
 * 结果卡片（复用 DesignResultScreen 的展示逻辑）
 */
@Composable
fun ResultCard(
    result: DesignResult,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📦 ${result.productName}设计方案",
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

            // 适用标准标签（醒目蓝色）
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "【适用标准】",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = result.standardName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                    Text(
                        text = result.applicableStandards.standardCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            }

            // 基础适配数据
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 基础适配数据",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "🔽 假人",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "  • 类型：${result.basicAdaptationData.dummyInfo.dummyType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  • 身高：${result.basicAdaptationData.dummyInfo.heightRange}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  • 体重：${result.basicAdaptationData.dummyInfo.weightRange}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  • 安装：${result.basicAdaptationData.dummyInfo.installationDirection}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 设计参数摘要
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📏 设计参数（摘要）",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "  • 头枕高度：${result.designParameters.headrestHeight}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  • 座椅宽度：${result.designParameters.seatWidth}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  • Envelope：${result.designParameters.envelope.sizeClass}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * 产品选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelector(
    selectedProduct: ProductType,
    onProductSelected: (ProductType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedProduct.typeName,
            onValueChange = {},
            readOnly = true,
            label = { Text("选择产品类型") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ProductType.values().forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.typeName) },
                    onClick = {
                        onProductSelected(product)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 标准选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardSelector(
    selectedStandard: String,
    availableStandards: List<String>,
    onStandardSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedStandard,
            onValueChange = {},
            readOnly = true,
            label = { Text("选择标准体系") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableStandards.forEach { standard ->
                DropdownMenuItem(
                    text = { Text(standard) },
                    onClick = {
                        onStandardSelected(standard)
                        expanded = false
                    }
                )
            }
        }
    }
}
