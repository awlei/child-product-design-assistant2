package com.design.assistant.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.ProductType
import com.design.assistant.viewmodel.ProductStandardSelectVM

/**
 * 首页 Screen
 * 提供产品选择、标准选择、设计生成等功能入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectVM: ProductStandardSelectVM = viewModel()
) {
    var selectedProduct by remember { mutableStateOf<ProductType>(ProductType.CHILD_SEAT) }
    var selectedStandard by remember { mutableStateOf<String>(StandardConstants.ECE_R129) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("儿童产品设计助手") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            // 功能按钮
            ActionButtons(
                onGenerateDesign = { /* TODO: 实现设计生成逻辑 */ }
            )
        }
    }
}

/**
 * 产品选择器
 */
@Composable
fun ProductSelector(
    selectedProduct: ProductType,
    onProductSelected: (ProductType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "选择产品类型",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            ProductType.values().forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedProduct == product,
                        onClick = { onProductSelected(product) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (product) {
                            ProductType.CHILD_SEAT -> "儿童安全座椅"
                            ProductType.BABY_STROLLER -> "婴儿推车"
                            ProductType.HIGH_CHAIR -> "儿童高脚椅"
                            ProductType.CHILD_BED -> "儿童床"
                        }
                    )
                }
            }
        }
    }
}

/**
 * 标准选择器
 */
@Composable
fun StandardSelector(
    selectedStandard: String,
    availableStandards: List<String>,
    onStandardSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "选择标准体系",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(availableStandards) { standard ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStandard == standard,
                            onClick = { onStandardSelected(standard) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = StandardConstants.getStandardName(standard))
                    }
                }
            }
        }
    }
}

/**
 * 设计信息卡片
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
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "设计参数",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "产品: ${when (product) {
                    ProductType.CHILD_SEAT -> "儿童安全座椅"
                    ProductType.BABY_STROLLER -> "婴儿推车"
                    ProductType.HIGH_CHAIR -> "儿童高脚椅"
                    ProductType.CHILD_BED -> "儿童床"
                }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "标准: ${StandardConstants.getStandardName(standard)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * 功能按钮
 */
@Composable
fun ActionButtons(
    onGenerateDesign: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onGenerateDesign,
            modifier = Modifier.weight(1f)
        ) {
            Text("生成设计方案")
        }
        OutlinedButton(
            onClick = { /* TODO: 查看历史设计 */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("查看历史")
        }
    }
}
