package com.design.assistant.database.gps028.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * GPS028数据库实体（专业工程师版）
 * 补充基准点/公差/标准条款，与业务模型Gps028DesignParams严格映射
 * 物理隔离，仅存储儿童安全座椅设计参数，数据来源：GPS028-2023
 */
@Entity(tableName = "gps028_design_params")
data class Gps028DesignParamEntity(
    @PrimaryKey val dummyModel: String, // 关联假人模型（Q0/Q3/HIII-3YO/HIII-6YO）
    // 头枕相关
    val headrestHeight: String,         // 头枕高度调节范围
    val headrestBasePoint: String,      // 头枕高度基准点
    val headrestTolerance: String,      // 头枕高度公差
    // 座宽相关
    val seatWidth: String,              // 有效座宽（臀部支撑区）
    val seatWidthTotal: String,         // 总座宽（含侧防侧翼）
    // ISOFIX Envelop（盒子）相关
    val envelopSize: String,            // Envelop尺寸等级（B1/B2/FMVSS Class1）
    val envelopDetail: String,          // Envelop详细尺寸（长/宽/固定点间距）
    val envelopStdClause: String,       // Envelop对应标准条款
    // 侧防相关
    val sideProtectionArea: String,     // 侧防面积要求
    val sideProtectionCover: String,    // 侧防覆盖人体区域
    val sideProtectionStd: String       // 侧防测试标准编号
)
