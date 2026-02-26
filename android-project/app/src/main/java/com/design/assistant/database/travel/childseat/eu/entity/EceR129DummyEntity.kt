package com.design.assistant.database.travel.childseat.eu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ECE R129 假人模型实体
 * 存储不同假人模型的规格参数
 */
@Entity(tableName = "ece_r129_dummy")
data class EceR129DummyEntity(
    @PrimaryKey val dummyId: String,      // 假人ID（如 "Q0", "Q1", "Q1.5", "Q3", "Q6", "Q10"）
    val dummyName: String,                // 假人名称
    val weight: String,                   // 重量（kg）
    val height: String,                   // 身高（mm）
    val age: String,                      // 适用年龄
    val description: String              // 描述
)
