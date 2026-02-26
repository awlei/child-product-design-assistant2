package com.design.assistant.database.travel.childseat.eu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ECE R129 测试配置实体
 * 存储测试条件和配置信息
 */
@Entity(tableName = "ece_r129_test_config")
data class EceR129TestConfigEntity(
    @PrimaryKey val configId: String,     // 配置ID
    val configName: String,               // 配置名称
    val testType: String,                 // 测试类型（前向碰撞/后向碰撞/侧向碰撞）
    val crashSpeed: String,               // 碰撞速度（km/h）
    val deceleration: String,             // 减速度（g）
    val description: String              // 描述
)
