package com.design.assistant.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.design.assistant.model.DesignResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * 设计方案展示页面
 * 以树形结构展示完整的设计方案
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignResultScreen(
    result: DesignResult,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = result.productName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = result.standardName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 适用标准标签（醒目蓝色）
            StandardTagCard(
                standard = result.applicableStandards
            )

            // 基础适配数据
            SectionCard(
                icon = "📊",
                title = "基础适配数据"
            ) {
                DummyInfoSection(result.basicAdaptationData.dummyInfo)
            }

            // 设计参数
            SectionCard(
                icon = "📏",
                title = "设计参数"
            ) {
                DesignParametersSection(result.designParameters)
            }

            // 测试要求
            SectionCard(
                icon = "⚖️",
                title = "测试要求"
            ) {
                TestRequirementsSection(result.testRequirements)
            }

            // 标准测试项
            SectionCard(
                icon = "🧪",
                title = "标准测试项"
            ) {
                StandardTestItemsSection(result.standardTestItems)
            }

            // 生成时间
            Text(
                text = "生成时间：${formatTimestamp(result.generatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 适用标准标签卡片（醒目蓝色）
 */
@Composable
fun StandardTagCard(standard: com.design.assistant.model.ApplicableStandards) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "【适用标准】",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Divider(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )
            InfoRow(
                label = "标准编号",
                value = standard.standardCode,
                labelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                valueColor = MaterialTheme.colorScheme.onPrimary
            )
            InfoRow(
                label = "标准名称",
                value = standard.standardName,
                labelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                valueColor = MaterialTheme.colorScheme.onPrimary
            )
            InfoRow(
                label = "版本",
                value = standard.version,
                labelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                valueColor = MaterialTheme.colorScheme.onPrimary
            )
            InfoRow(
                label = "生效日期",
                value = standard.effectiveDate,
                labelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                valueColor = MaterialTheme.colorScheme.onPrimary
            )
            InfoRow(
                label = "发布机构",
                value = standard.issuingBody,
                labelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                valueColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/**
 * 区块卡片组件
 */
@Composable
fun SectionCard(
    icon: String,
    title: String,
    content: @Composable ColumnScope.() -> Unit
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
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            // 内容
            content()
        }
    }
}

/**
 * 假人信息区块
 */
@Composable
fun DummyInfoSection(dummyInfo: com.design.assistant.model.DummyInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TreeNode(
            icon = "🔽",
            title = "假人"
        ) {
            InfoRow("假人类型", dummyInfo.dummyType)
            InfoRow("身高范围", dummyInfo.heightRange)
            InfoRow("体重范围", dummyInfo.weightRange)
            InfoRow("安装方向", dummyInfo.installationDirection)
            if (dummyInfo.ageGroup != null) {
                InfoRow("年龄组", dummyInfo.ageGroup)
            }
        }
    }
}

/**
 * 设计参数区块
 */
@Composable
fun DesignParametersSection(params: com.design.assistant.model.DesignParameters) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoRow("头枕高度", params.headrestHeight)
        InfoRow("座宽", params.seatWidth)

        // Envelope 信息
        TreeNode(
            icon = "📦",
            title = "盒子 Envelope"
        ) {
            InfoRow("尺寸等级", params.envelope.sizeClass)
            InfoRow("长度", params.envelope.length)
            InfoRow("宽度", params.envelope.width)
            InfoRow("高度", params.envelope.height)
            if (params.envelope.description != null) {
                InfoRow("描述", params.envelope.description)
            }
        }

        InfoRow("侧防面积", params.sideImpactArea)
    }
}

/**
 * 测试要求区块
 */
@Composable
fun TestRequirementsSection(requirements: com.design.assistant.model.TestRequirements) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TreeNode(
            icon = "▶️",
            title = "正面"
        ) {
            TestRequirementItem(requirements.frontalImpact)
        }

        TreeNode(
            icon = "▶️",
            title = "侧撞胸部压缩"
        ) {
            TestRequirementItem(requirements.sideImpactChestCompression)
        }

        TreeNode(
            icon = "▶️",
            title = "织带强度"
        ) {
            TestRequirementItem(requirements.harnessStrength)
        }
    }
}

/**
 * 测试要求项
 */
@Composable
fun TestRequirementItem(requirement: com.design.assistant.model.FrontalImpactRequirement) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        InfoRow("测试名称", requirement.testName)
        InfoRow("速度", requirement.speed)
        InfoRow("减速度", requirement.deceleration)
        InfoRow("通过标准", requirement.criteria)
        if (requirement.notes != null) {
            InfoRow("备注", requirement.notes)
        }
    }
}

@Composable
fun TestRequirementItem(requirement: com.design.assistant.model.SideImpactRequirement) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        InfoRow("测试名称", requirement.testName)
        InfoRow("撞击速度", requirement.impactSpeed)
        InfoRow("最大胸部压缩量", requirement.maxChestCompression)
        InfoRow("最大胸部挠度", requirement.maxChestDeflection)
        InfoRow("通过标准", requirement.criteria)
    }
}

@Composable
fun TestRequirementItem(requirement: com.design.assistant.model.HarnessStrengthRequirement) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        InfoRow("测试名称", requirement.testName)
        InfoRow("测试载荷", requirement.testLoad)
        InfoRow("持续时间", requirement.duration)
        InfoRow("伸长率限制", requirement.elongationLimit)
        InfoRow("通过标准", requirement.criteria)
    }
}

/**
 * 标准测试项区块
 */
@Composable
fun StandardTestItemsSection(items: com.design.assistant.model.StandardTestItems) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.dynamicTests.forEach { test ->
            TreeNode(
                icon = if (test.isMandatory) Icons.Default.CheckCircle else "📋",
                title = "${test.testName}${if (test.isMandatory) " (强制性)" else ""}"
            ) {
                TestDetailItem(test)
            }
        }
    }
}

/**
 * 测试详情项
 */
@Composable
fun TestDetailItem(test: com.design.assistant.model.DynamicTestItem) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        InfoRow("测试ID", test.testId)
        InfoRow("测试描述", test.testDescription)
        InfoRow("测试条件", test.testConditions)
        InfoRow("验收标准", test.acceptanceCriteria)
    }
}

/**
 * 树节点组件
 */
@Composable
fun TreeNode(
    icon: Any,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 树节点标题
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (icon) {
                is androidx.compose.ui.graphics.vector.ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                else -> {
                    Text(
                        text = icon.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        // 子内容
        Box(
            modifier = Modifier.padding(start = 14.dp)
        ) {
            // 左侧竖线
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    .align(alignment = Alignment.CenterStart)
            )
            // 内容区域
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * 信息行组件
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label：",
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            modifier = Modifier.weight(1.5f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 格式化时间戳
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
