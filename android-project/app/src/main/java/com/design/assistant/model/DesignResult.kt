package com.design.assistant.model

/**
 * 设计方案结果数据模型
 * 根据用户选择的产品类型、标准体系和输入参数生成完整的设计方案
 */
data class DesignResult(
    // 产品信息
    val productType: ProductType,
    val productName: String,
    val standardSystem: String,
    val standardName: String,

    // 用户输入参数
    val inputParameters: InputParameters,

    // 适用标准
    val applicableStandards: ApplicableStandards,

    // 基础适配数据
    val basicAdaptationData: BasicAdaptationData,

    // 设计参数（GPS数据库）
    val designParameters: DesignParameters,

    // 测试要求
    val testRequirements: TestRequirements,

    // 标准测试项
    val standardTestItems: StandardTestItems,

    // 生成时间
    val generatedAt: Long = System.currentTimeMillis()
)

/**
 * 适用标准信息
 */
data class ApplicableStandards(
    val standardCode: String,
    val standardName: String,
    val version: String,
    val effectiveDate: String,
    val issuingBody: String
)

/**
 * 基础适配数据
 */
data class BasicAdaptationData(
    val dummyInfo: DummyInfo
)

/**
 * 假人信息
 */
data class DummyInfo(
    val dummyType: String,           // 假人类型（如：Q系列假人、P系列假人）
    val heightRange: String,        // 身高范围
    val weightRange: String,        // 体重范围
    val installationDirection: String, // 安装方向（如：反向、正向、双向）
    val ageGroup: String? = null    // 年龄组（可选）
)

/**
 * 设计参数（GPS数据库）
 */
data class DesignParameters(
    val headrestHeight: String,     // 头枕高度
    val seatWidth: String,          // 座宽
    val envelope: EnvelopeInfo,     // 盒子Envelope（如：ISOFIX SIZE CLASS B1）
    val sideImpactArea: String      // 侧防面积
)

/**
 * Envelope信息（安装空间要求）
 */
data class EnvelopeInfo(
    val sizeClass: String,          // 尺寸等级（如：ISOFIX SIZE CLASS B1）
    val length: String,             // 长度
    val width: String,              // 宽度
    val height: String,             // 高度
    val description: String? = null // 描述
)

/**
 * 测试要求
 */
data class TestRequirements(
    val frontalImpact: FrontalImpactRequirement,
    val sideImpactChestCompression: SideImpactRequirement,
    val harnessStrength: HarnessStrengthRequirement
)

/**
 * 正面碰撞要求
 */
data class FrontalImpactRequirement(
    val testName: String,
    val speed: String,              // 测试速度
    deceleration: String,          // 减速度
    val criteria: String,          // 通过标准
    val notes: String? = null      // 备注
)

/**
 * 侧撞胸部压缩要求
 */
data class SideImpactRequirement(
    val testName: String,
    val impactSpeed: String,        // 撞击速度
    maxChestCompression: String,    // 最大胸部压缩量
    maxChestDeflection: String,    // 最大胸部挠度
    val criteria: String
)

/**
 * 织带强度要求
 */
data class HarnessStrengthRequirement(
    val testName: String,
    val testLoad: String,           // 测试载荷
    val duration: String,           // 持续时间
    val elongationLimit: String,    // 伸长率限制
    val criteria: String
)

/**
 * 标准测试项
 */
data class StandardTestItems(
    val dynamicTests: List<DynamicTestItem>
)

/**
 * 动态测试项
 */
data class DynamicTestItem(
    val testId: String,
    val testName: String,
    val testType: DynamicTestType,
    val testDescription: String,
    val testConditions: String,
    val acceptanceCriteria: String,
    val isMandatory: Boolean
)

/**
 * 动态测试类型
 */
enum class DynamicTestType {
    FRONTAL_IMPACT,     // 正碰
    REAR_IMPACT,        // 后碰
    SIDE_IMPACT,        // 侧碰
    ROLL_OVER,          // 翻滚
    OTHER
}

/**
 * 动态测试类型转字符串
 */
fun DynamicTestType.getDisplayName(): String {
    return when (this) {
        DynamicTestType.FRONTAL_IMPACT -> "正面碰撞"
        DynamicTestType.REAR_IMPACT -> "后碰"
        DynamicTestType.SIDE_IMPACT -> "侧碰"
        DynamicTestType.ROLL_OVER -> "翻滚测试"
        DynamicTestType.OTHER -> "其他测试"
    }
}
