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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.ProductType
import com.design.assistant.viewmodel.ProductStandardSelectVM

/**
 * 首页 Screen - 优化版
 * 提供产品选择、标准选择、设计生成等功能入口
 */

/**
 * 产品选择器 - 优化版
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectVM: ProductStandardSelectVM = viewModel()
) {
    var selectedProduct by remember { mutableStateOf<ProductType>(ProductType.CHILD_SEAT) }
    var selectedStandard by remember { mutableStateOf<String>(StandardConstants.ECE_R129) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // 根据屏幕宽度决定按钮布局
    val isWideScreen = screenWidth > 400.dp

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

            Spacer(modifier = Modifier.height(8.dp))

            // 功能按钮
            ActionButtons(
                isWideScreen = isWideScreen,
                onGenerateDesign = { /* TODO: 实现设计生成逻辑 */ },
                onViewHistory = { /* TODO: 查看历史设计 */ }
            )

            // 底部安全区域
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 产品选择器 - 优化版
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelector(
    selectedProduct: ProductType,
    onProductSelected: (ProductType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "选择产品类型",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 使用 FilterChip 更适合触摸操作
            ProductType.values().forEach { product ->
                FilterChip(
                    selected = selectedProduct == product,
                    onClick = { onProductSelected(product) },
                    label = {
                        Text(
                            when (product) {
                                ProductType.CHILD_SEAT -> "儿童安全座椅"
                                ProductType.BABY_STROLLER -> "婴儿推车"
                                ProductType.HIGH_CHAIR -> "儿童高脚椅"
                                ProductType.CHILD_BED -> "儿童床"
                            },
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                if (product != ProductType.CHILD_BED) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * 标准选择器 - 优化版（限制高度）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardSelector(
    selectedStandard: String,
    availableStandards: List<String>,
    onStandardSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "选择标准体系",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 限制高度，避免占据过多空间
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableStandards.forEach { standard ->
                    FilterChip(
                        selected = selectedStandard == standard,
                        onClick = { onStandardSelected(standard) },
                        label = {
                            Text(
                                StandardConstants.getStandardName(standard),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

/**
 * 设计信息卡片 - 优化版
 */
@Composable
fun DesignInfoCard(
    product: ProductType,
    standard: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "设计参数",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(4.dp))

            InfoRow(
                label = "产品",
                value = when (product) {
                    ProductType.CHILD_SEAT -> "儿童安全座椅"
                    ProductType.BABY_STROLLER -> "婴儿推车"
                    ProductType.HIGH_CHAIR -> "儿童高脚椅"
                    ProductType.CHILD_BED -> "儿童床"
                }
            )
            InfoRow(
                label = "标准",
                value = StandardConstants.getStandardName(standard)
            )
        }
    }
}

/**
 * 信息行组件
 */
@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * 功能按钮 - 优化版（支持响应式布局）
 */
@Composable
fun ActionButtons(
    isWideScreen: Boolean,
    onGenerateDesign: () -> Unit,
    onViewHistory: () -> Unit
) {
    if (isWideScreen) {
        // 宽屏：横向排列
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onGenerateDesign,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "生成设计方案",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "查看历史",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    } else {
        // 窄屏：纵向排列
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onGenerateDesign,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "生成设计方案",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "查看历史",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
