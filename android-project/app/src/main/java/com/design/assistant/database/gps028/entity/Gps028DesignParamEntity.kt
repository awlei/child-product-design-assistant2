package com.design.assistant.database.gps028.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * GPS028数据库实体（通用参数版本）
 * 支持存储各类设计参数，便于初始化和管理
 */
@Entity(tableName = "gps028_design_params")
data class Gps028DesignParamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val paramName: String,              // 参数名称
    val paramValue: String,             // 参数值
    val paramUnit: String,              // 参数单位
    val description: String,            // 参数描述
    val category: String,               // 参数类别（儿童安全座椅/婴儿推车/儿童高脚椅/儿童床）
    val dummyModel: String? = null      // 关联假人模型（可选，用于特定场景）
)
