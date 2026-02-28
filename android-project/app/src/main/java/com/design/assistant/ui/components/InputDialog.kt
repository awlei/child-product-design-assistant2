package com.design.assistant.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.InputParameters
import com.design.assistant.model.ProductType
import com.design.assistant.model.ValidationResult

/**
 * 参数输入对话框
 * 根据不同的标准体系动态显示不同的输入字段
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    productType: ProductType,
    standardSystem: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (InputParameters) -> Unit
) {
    if (!visible) return

    var minHeight by remember { mutableStateOf("") }
    var maxHeight by remember { mutableStateOf("") }
    var minWeight by remember { mutableStateOf("") }
    var maxWeight by remember { mutableStateOf("") }
    var ageRange by remember { mutableStateOf("") }
    var seatInstallationType by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var additionalRequirements by remember { mutableStateOf("") }

    // 根据标准体系确定需要显示的输入字段
    val needHeight = standardSystem in listOf(
        StandardConstants.ECE_R129,
        StandardConstants.GB_27887_2024,
        StandardConstants.AS_NZS_1754
    )

    val needWeight = standardSystem in listOf(
        StandardConstants.FMVSS_213,
        StandardConstants.CMVSS_213,
        StandardConstants.GB_27887_2024,
        StandardConstants.AS_NZS_1754
    )

    // 根据标准体系设置默认值
    LaunchedEffect(standardSystem) {
        when (standardSystem) {
            StandardConstants.ECE_R129 -> {
                minHeight = "40"
                maxHeight = "105"
            }
            StandardConstants.FMVSS_213,
            StandardConstants.CMVSS_213 -> {
                minWeight = "2.3"
                maxWeight = "18.0"
            }
            StandardConstants.GB_27887_2024,
            StandardConstants.AS_NZS_1754 -> {
                minHeight = "40"
                maxHeight = "150"
                minWeight = "0"
                maxWeight = "36"
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 标题
                Text(
                    text = "输入设计参数",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "标准：${StandardConstants.getStandardName(standardSystem)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // 输入字段（可滚动）
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 身高范围输入（欧标）
                    if (needHeight) {
                        Text(
                            text = "身高范围（cm）",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = minHeight,
                                onValueChange = { if (it.all { char -> char.isDigit() || char == '-' }) minHeight = it },
                                label = { Text("最小身高") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = maxHeight,
                                onValueChange = { if (it.all { char -> char.isDigit() || char == '-' }) maxHeight = it },
                                label = { Text("最大身高") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }

                    // 体重范围输入（美标）
                    if (needWeight) {
                        Text(
                            text = "体重范围（kg）",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = minWeight,
                                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) minWeight = it },
                                label = { Text("最小体重") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = maxWeight,
                                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) maxWeight = it },
                                label = { Text("最大体重") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                        }
                    }

                    // 可选字段
                    Text(
                        text = "其他参数（可选）",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = ageRange,
                        onValueChange = { ageRange = it },
                        label = { Text("年龄范围（如：0-6个月）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = seatInstallationType,
                        onValueChange = { seatInstallationType = it },
                        label = { Text("座椅安装方式（如：ISOFIX、安全带）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = vehicleType,
                        onValueChange = { vehicleType = it },
                        label = { Text("车辆类型（如：轿车、SUV）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = additionalRequirements,
                        onValueChange = { additionalRequirements = it },
                        label = { Text("其他特殊要求") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            try {
                                val params = InputParameters(
                                    productType = productType,
                                    standardSystem = standardSystem,
                                    minHeight = minHeight.toIntOrNull()?.takeIf { it > 0 },
                                    maxHeight = maxHeight.toIntOrNull()?.takeIf { it > 0 },
                                    minWeight = minWeight.toDoubleOrNull()?.takeIf { it > 0 },
                                    maxWeight = maxWeight.toDoubleOrNull()?.takeIf { it > 0 },
                                    ageRange = ageRange.ifEmpty { null },
                                    seatInstallationType = seatInstallationType.ifEmpty { null },
                                    vehicleType = vehicleType.ifEmpty { null },
                                    additionalRequirements = additionalRequirements.ifEmpty { null }
                                )
                                onConfirm(params)
                            } catch (e: Exception) {
                                android.util.Log.e("InputDialog", "创建参数失败", e)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }
}
