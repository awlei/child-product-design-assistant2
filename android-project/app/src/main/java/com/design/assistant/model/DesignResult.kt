package com.design.assistant.model

import com.design.assistant.constants.StandardConstants

/**
 * 最终设计方案结果（专业儿童产品设计工程师版）
 * 严格遵循指定层级：📦→【适用标准】→📊基础适配→📏设计参数→⚖️测试要求→🧪标准测试项
 * 数据来源：对应标准专属数据库 + GPS028-2023数据库，所有参数可追溯
 */
data class DesignResult(
    // 基础标识（内部使用，不展示）
    val standardCode: String,
    val productType: String,
    val heightCm: Int,
    // 【适用标准】层级（醒目蓝色标签）
    val standardName: String,           // 标准名称（如ECE R129:2021 (欧盟i-Size)）
    val standardVersion: String,        // 标准版本（如2021版）
    val standardImplement: String,      // 实施要求（如强制实施/推荐）
    val standardKeyRequire: String,     // 标准核心要求（快速了解重点）
    // 📊 基础适配数据层级
    val dummyModel: String,             // 假人模型（如ECE R129 Q3假人）
    val dummyPercentile: String,        // 假人百分位（如50th百分位3岁儿童）
    val dummyStdCode: String,           // 假人对应国标编号（如ISO 13232-2:2021）
    val dummyHeightRange: String,       // 假人身高范围
    val dummyWeightRange: String,       // 假人体重范围
    val dummyBodyData: String,          // 假人人体测量参数（坐高/肩宽/头围）
    val installDirection: String,       // 安装方向
    val installStdClause: String,       // 安装方向对应标准条款
    val heightMatchTip: String,         // 用户身高匹配提示（如中值，适配性最优）
    // 📏 设计参数层级（GPS028数据库+标准要求）
    val gpsDesignParams: Gps028DesignParams,
    // ⚖️ 测试要求层级（量化阈值+标准条款+测试方法）
    val frontalCrash: String,           // 正面碰撞要求
    val frontalCrashClause: String,     // 正面碰撞标准条款
    val sideCrashChestCompress: String, // 侧撞胸部压缩要求
    val sideCrashClause: String,        // 侧撞对应标准条款
    val webbingStrength: String,        // 织带强度要求
    val webbingTestMethod: String,      // 织带测试标准编号
    // 🧪 标准测试项层级（测试设备+流程+合格判据，可直接对接实验室）
    val dynamicCrashFront: DynamicCrashItem,  // 动态碰撞-正碰
    val dynamicCrashBack: DynamicCrashItem,   // 动态碰撞-后碰
    val dynamicCrashSide: DynamicCrashItem,   // 动态碰撞-侧碰
    val flameRetardant: FlameRetardantItem     // 阻燃要求
)

/**
 * 动态碰撞测试项（专业版）：含测试设备/条件/合格判据，直接用于实验室测试方案
 */
data class DynamicCrashItem(
    val testDevice: String,     // 测试设备（如HYGE电动碰撞台）
    val testCondition: String,  // 测试条件（速度/姿态/约束系统）
    val qualifiedCriteria: String // 合格判据（量化阈值，无模糊表述）
)

/**
 * 阻燃测试项（专业版）：含适用材料/测试标准/合格判据，直接用于材料选型
 */
data class FlameRetardantItem(
    val applyMaterials: String, // 适用材料（如面料/泡沫/织带）
    val testStd: String,        // 测试标准编号（如ISO 3795:2019）
    val qualifiedCriteria: String // 合格判据（量化阈值）
)

/**
 * UI状态封装（加载/成功/失败/空闲）
 * 用于设计方案生成的状态管理，原有逻辑不变
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Idle : UiState<Nothing>()
}

/**
 * 扩展：标准代码转完整专业名称（带版本）
 * 给Repository层用，简化专业名称生成
 */
fun String.toProStdName(): String {
    return when (this) {
        StandardConstants.ECE_R129 -> "ECE R129:2021 (欧盟i-Size)"
        StandardConstants.GB_27887_2024 -> "GB 27887-2024 (中国儿童安全座椅新标)"
        StandardConstants.FMVSS_213 -> "FMVSS 213 (2022版，美国联邦机动车安全标准)"
        StandardConstants.AS_NZS_1754 -> "AS/NZS 1754:2020 (澳洲标准)"
        StandardConstants.CMVSS_213 -> "CMVSS 213 (加拿大机动车安全标准，第5版)"
        else -> StandardConstants.getStandardName(this)
    }
}
