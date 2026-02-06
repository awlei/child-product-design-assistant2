package com.design.assistant.model

/**
 * GPS028设计参数（专业工程师版）
 * 严格匹配输出层级「📏 设计参数」，补充基准点/公差/标准条款，可直接用于CAD建模
 * 数据来源：GPS028-2023数据库 + 对应标准强制要求
 */
data class Gps028DesignParams(
    val headrestHeight: String,         // 头枕高度（调节范围）
    val headrestBasePoint: String,      // 头枕高度基准点（如坐骨结节H点）
    val headrestTolerance: String,      // 头枕高度公差
    val seatWidth: String,              // 有效座宽（臀部支撑区）
    val seatWidthTotal: String,         // 总座宽（含侧防侧翼）
    val envelopSize: String,            // ISOFIX Envelop尺寸等级（如B1/B2）
    val envelopDetail: String,          // Envelop详细尺寸（长/宽/固定点间距）
    val envelopStdClause: String,       // Envelop对应标准条款
    val sideProtectionArea: String,     // 侧防面积要求（≥X㎡）
    val sideProtectionCover: String,    // 侧防覆盖区域（如T12胸部至P8头部）
    val sideProtectionStd: String       // 侧防测试标准编号
)
