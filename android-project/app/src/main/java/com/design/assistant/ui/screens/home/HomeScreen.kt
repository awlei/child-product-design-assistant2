package com.design.assistant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // UI 状态
    var selectedProduct by remember { mutableStateOf<ProductType>(ProductType.CHILD_SEAT) }
    var selectedStandard by remember { mutableStateOf<String>(StandardConstants.ECE_R129) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showResultCard by remember { mutableStateOf(false) }
    var isQuickSuggestionMode by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showCalculationExplanation by remember { mutableStateOf(false) }
    var showStandardReferences by remember { mutableStateOf(false) }

    // 历史记录
    var designHistory by remember { mutableStateOf<MutableList<DesignResult>>(mutableListOf()) }

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

                // 添加到历史记录（最多保存 10 条）
                designHistory.add(0, designResult!!)
                if (designHistory.size > 10) {
                    designHistory.removeAt(designHistory.size - 1)
                }
            } else if (generateError != null) {
                // 生成失败，显示错误
                Log.e(TAG, "生成失败: $generateError")
                errorMessage = generateError ?: "未知错误"
                showErrorDialog = true
            }
        }
    }

    // 复制数据到剪贴板
    fun copyToClipboard(text: String, label: String) {
        val clipData = android.content.ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "已复制: $label", Toast.LENGTH_SHORT).show()
    }

    // 复制完整设计方案
    fun copyFullDesignResult(result: DesignResult) {
        val sb = StringBuilder()
        sb.append("【${result.productName}设计方案】\n")
        sb.append("适用标准：${result.standardName} (${result.applicableStandards.standardCode})\n\n")
        sb.append("【基础适配数据】\n")
        sb.append("假人类型：${result.basicAdaptationData.dummyInfo.dummyType}\n")
        sb.append("身高范围：${result.basicAdaptationData.dummyInfo.heightRange}\n")
        sb.append("体重范围：${result.basicAdaptationData.dummyInfo.weightRange}\n")
        sb.append("安装方向：${result.basicAdaptationData.dummyInfo.installationDirection}\n\n")
        sb.append("【设计参数】\n")
        sb.append("头枕高度：${result.designParameters.headrestHeight}\n")
        sb.append("座椅宽度：${result.designParameters.seatWidth}\n")
        sb.append("Envelope 尺寸：${result.designParameters.envelope.length} × ${result.designParameters.envelope.width} × ${result.designParameters.envelope.height}\n")
        sb.append("侧防面积：${result.designParameters.sideImpactArea}\n\n")
        sb.append("【测试要求】\n")
        sb.append("正面碰撞：${result.testRequirements.frontalImpact.speed}, ${result.testRequirements.frontalImpact.deceleration}\n")
        sb.append("侧撞测试：${result.testRequirements.sideImpactChestCompression.impactSpeed}, 最大压缩${result.testRequirements.sideImpactChestCompression.maxChestCompression}\n")
        sb.append("织带强度：${result.testRequirements.harnessStrength.testLoad}, ${result.testRequirements.harnessStrength.duration}\n")

        copyToClipboard(sb.toString(), "完整设计方案")
    }

    // 获取标准条款引用
    fun getStandardClauses(standard: String): List<String> {
        return when {
            standard.contains("ECE R129") -> listOf(
                "Annex 18 - ISOFIX 下拉装置尺寸要求",
                "Annex 19 - 假人规格（Q0, Q1, Q1.5, Q3, Q6, Q10）",
                "Annex 20 - 动态测试要求",
                "Regulation No.129 - 通用要求"
            )
            standard.contains("FMVSS 213") -> listOf(
                "S5 - 假人要求",
                "S7 - 动态测试程序",
                "S10 - 织带强度测试",
                "S18 - 安装说明要求"
            )
            standard.contains("GB 27887") -> listOf(
                "4.3 - 产品分类",
                "5.2 - 动态测试要求",
                "6.1 - 结构强度要求",
                "附录A - 假人规格"
            )
            else -> listOf(
                "标准通用要求条款",
                "测试方法条款",
                "安全性能条款"
            )
        }
    }

    // 确认生成
    fun onConfirmGenerate() {
        Log.d(TAG, "用户确认输入参数")

        // 设置为自定义参数模式
        isQuickSuggestionMode = false

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

    // 一键输出设计建议
    fun onQuickDesignSuggestion() {
        Log.d(TAG, "用户点击一键输出设计建议")

        // 设置为设计建议模式
        isQuickSuggestionMode = true

        // 根据产品和标准生成最佳实践参数
        val bestPracticeParams = when (selectedProduct) {
            ProductType.CHILD_SEAT -> generateBestPracticeChildSeatParams(selectedStandard)
            ProductType.BABY_STROLLER -> generateBestPracticeBabyStrollerParams(selectedStandard)
            ProductType.HIGH_CHAIR -> generateBestPracticeHighChairParams(selectedStandard)
            ProductType.CHILD_BED -> generateBestPracticeChildBedParams(selectedStandard)
        }

        Log.d(TAG, "最佳实践参数: $bestPracticeParams")

        // 清除之前的结果
        designVM.clearResult()
        showResultCard = false

        // 生成设计方案
        inputVM.setInputParameters(bestPracticeParams)
        designVM.generateDesign(
            productType = selectedProduct,
            standardSystem = selectedStandard,
            inputParameters = bestPracticeParams
        )
    }

    // 生成儿童安全座椅最佳实践参数
    private fun generateBestPracticeChildSeatParams(standard: String): InputParameters {
        return when {
            standard.contains("ECE R129") -> {
                // ECE R129 推荐使用最常用的 Q1 组（9-18个月）
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = standard,
                    minHeight = 40,
                    maxHeight = 87,
                    minWeight = null,
                    maxWeight = null,
                    ageRange = "9-18个月",
                    seatInstallationType = "ISOFIX",
                    vehicleType = "轿车/SUV"
                )
            }
            standard.contains("FMVSS") || standard.contains("CMVSS") -> {
                // FMVSS 213 推荐最常用的 1 组（9-18kg）
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = standard,
                    minHeight = null,
                    maxHeight = null,
                    minWeight = 9.0,
                    maxWeight = 18.0,
                    ageRange = "9-36个月",
                    seatInstallationType = "LATCH/ISOFIX",
                    vehicleType = "轿车/SUV"
                )
            }
            else -> {
                // GB 标准
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = standard,
                    minHeight = 40,
                    maxHeight = 105,
                    minWeight = 0.0,
                    maxWeight = 18.0,
                    ageRange = "0-4岁",
                    seatInstallationType = "ISOFIX + 安全带",
                    vehicleType = "通用"
                )
            }
        }
    }

    // 生成婴儿推车最佳实践参数
    private fun generateBestPracticeBabyStrollerParams(standard: String): InputParameters {
        return when {
            standard.contains("EN 1888") || standard.contains("GB 14748") -> {
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = standard,
                    minHeight = 45,
                    maxHeight = 100,
                    minWeight = 0.0,
                    maxWeight = 15.0,
                    ageRange = "0-3岁",
                    vehicleType = "适用于 1-3 名儿童"
                )
            }
            else -> {
                InputParameters(
                    productType = selectedProduct,
                    standardSystem = standard,
                    minHeight = 50,
                    maxHeight = 100,
                    minWeight = 0.0,
                    maxWeight = 13.6,
                    ageRange = "0-36个月",
                    vehicleType = "双座推车"
                )
            }
        }
    }

    // 生成儿童高脚椅最佳实践参数
    private fun generateBestPracticeHighChairParams(standard: String): InputParameters {
        return InputParameters(
            productType = selectedProduct,
            standardSystem = standard,
            minHeight = 70,
            maxHeight = 95,
            minWeight = 0.0,
            maxWeight = 15.0,
            ageRange = "6-36个月",
            seatInstallationType = "独立式/附着式",
            vehicleType = "家庭用餐场景"
        )
    }

    // 生成儿童床最佳实践参数
    private fun generateBestPracticeChildBedParams(standard: String): InputParameters {
        return InputParameters(
            productType = selectedProduct,
            standardSystem = standard,
            minHeight = 50,
            maxHeight = 140,
            minWeight = 0.0,
            maxWeight = 30.0,
            ageRange = "0-6岁",
            vehicleType = "家庭卧室"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "儿童产品设计助手",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "专业工程师版",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    // 计算说明按钮
                    IconButton(onClick = { showCalculationExplanation = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Calculate,
                            contentDescription = "计算说明"
                        )
                    }
                    // 标准引用按钮
                    IconButton(onClick = { showStandardReferences = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = "标准引用"
                        )
                    }
                    // 历史记录按钮
                    IconButton(onClick = { showHistoryDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "历史记录"
                        )
                    }
                    // 关于按钮
                    IconButton(onClick = { /* TODO: 显示关于信息 */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "关于"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
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

            // 快捷操作按钮组
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 一键输出设计建议按钮
                Button(
                    onClick = { onQuickDesignSuggestion() },
                    modifier = Modifier.weight(1f),
                    enabled = !isGenerating && !showResultCard,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("一键输出设计建议")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    isQuickSuggestion = isQuickSuggestionMode,
                    onSwitchToCustom = if (isQuickSuggestionMode) {
                        {
                            showResultCard = false
                            // 用户可以继续使用自定义参数
                        }
                    } else null,
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
    isQuickSuggestion: Boolean = false,
    onClose: () -> Unit,
    onSwitchToCustom: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isQuickSuggestion) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📦 ${result.productName}设计方案",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // 设计建议模式标识
                    if (isQuickSuggestion) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "✨ 最佳实践设计建议",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
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

            // 专业操作按钮区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 查看详细参数按钮
                OutlinedButton(
                    onClick = { /* TODO: 跳转到详细结果页面 */ },
                    modifier = Modifier.weight(1f),
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("查看详情", fontSize = 12.sp)
                }

                // 复制数据按钮
                OutlinedButton(
                    onClick = { /* TODO: 复制到剪贴板 */ },
                    modifier = Modifier.weight(1f),
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("复制数据", fontSize = 12.sp)
                }

                // 分享按钮
                OutlinedButton(
                    onClick = { /* TODO: 分享设计方案 */ },
                    modifier = Modifier.weight(1f),
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("分享", fontSize = 12.sp)
                }
            }

            // 设计建议模式提示
            if (isQuickSuggestion) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💡 最佳实践参数",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "此方案基于标准推荐的最佳实践参数生成，适合快速了解设计要求。如需针对特定参数进行设计，请使用"自定义参数"模式。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        // 切换到自定义参数按钮
                        if (onSwitchToCustom != null) {
                            Button(
                                onClick = onSwitchToCustom,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text("切换到自定义参数")
                            }
                        }
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
 * 计算说明对话框
 */
@Composable
fun CalculationExplanationDialog(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("参数计算说明")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "本应用基于以下方法计算设计参数：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Divider()
                CalculationItem(
                    title = "Envelope 尺寸",
                    description = "根据假人身高范围，加上安全余量计算得出。包括长度（带保护罩）、宽度（含肩宽）、高度（含头枕）三个维度。"
                )
                CalculationItem(
                    title = "头枕高度调节范围",
                    description = "基于假人身高范围和标准要求的头部保护区域计算，确保头枕能够覆盖假人头部位置。"
                )
                CalculationItem(
                    title = "侧防面积",
                    description = "根据侧撞测试要求的胸部压缩量和假人肩宽计算，确保侧防装置能够提供足够的保护。"
                )
                CalculationItem(
                    title = "织带强度测试载荷",
                    description = "基于标准要求的动态测试减速曲线和假人重量计算，确保织带能够承受测试载荷。"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("我知道了")
            }
        }
    )
}

/**
 * 单项计算说明
 */
@Composable
fun CalculationItem(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 标准引用对话框
 */
@Composable
fun StandardReferencesDialog(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("标准条款引用")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "各标准体系的关键条款：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Divider()

                StandardReferenceSection(
                    standardName = "ECE R129 (i-Size)",
                    clauses = listOf(
                        "Annex 18 - ISOFIX 下拉装置尺寸要求",
                        "Annex 19 - 假人规格（Q0, Q1, Q1.5, Q3, Q6, Q10）",
                        "Annex 20 - 动态测试要求",
                        "Regulation No.129 - 通用要求"
                    )
                )

                StandardReferenceSection(
                    standardName = "FMVSS 213 (美国)",
                    clauses = listOf(
                        "S5 - 假人要求",
                        "S7 - 动态测试程序",
                        "S10 - 织带强度测试",
                        "S18 - 安装说明要求"
                    )
                )

                StandardReferenceSection(
                    standardName = "GB 27887-2024 (中国)",
                    clauses = listOf(
                        "4.3 - 产品分类",
                        "5.2 - 动态测试要求",
                        "6.1 - 结构强度要求",
                        "附录A - 假人规格"
                    )
                )

                StandardReferenceSection(
                    standardName = "AS/NZS 1754 (澳大利亚/新西兰)",
                    clauses = listOf(
                        "Clause 2.1 - 产品分类",
                        "Clause 4.2 - 动态测试程序",
                        "Clause 5.1 - 织带强度要求",
                        "Annex AA - 假人规格"
                    )
                )

                StandardReferenceSection(
                    standardName = "CMVSS 213 (加拿大)",
                    clauses = listOf(
                        "S5 - 假人要求",
                        "S7 - 动态测试程序",
                        "S10 - 织带强度测试",
                        "S18 - 安装说明要求"
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("我知道了")
            }
        }
    )
}

/**
 * 标准引用章节
 */
@Composable
fun StandardReferenceSection(
    standardName: String,
    clauses: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = standardName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            clauses.forEach { clause ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = clause,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

/**
 * 历史记录对话框
 */
@Composable
fun HistoryDialog(
    visible: Boolean,
    history: List<DesignResult>,
    onDismiss: () -> Unit,
    onHistoryItemClicked: (DesignResult) -> Unit
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("历史记录")
        },
        text = {
            if (history.isEmpty()) {
                Text(
                    text = "暂无历史记录",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { result ->
                        HistoryItemCard(
                            result = result,
                            onClick = {
                                onHistoryItemClicked(result)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

/**
 * 历史记录项卡片
 */
@Composable
fun HistoryItemCard(
    result: DesignResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = result.standardName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "${result.basicAdaptationData.dummyInfo.dummyType} • ${result.designParameters.envelope.sizeClass}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
