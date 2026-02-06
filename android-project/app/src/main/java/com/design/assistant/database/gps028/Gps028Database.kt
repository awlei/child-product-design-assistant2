package com.design.assistant.database.gps028

import androidx.room.Database
import androidx.room.RoomDatabase
import com.design.assistant.database.gps028.dao.Gps028DesignDao
import com.design.assistant.database.gps028.entity.Gps028DesignParamEntity

/**
 * GPS028专属数据库（物理隔离）
 * 仅存储儿童安全座椅设计参数
 */
@Database(
    entities = [Gps028DesignParamEntity::class],
    version = 1,
    exportSchema = true
)
abstract class Gps028Database : RoomDatabase() {
    abstract fun gps028DesignDao(): Gps028DesignDao

    companion object {
        const val DATABASE_NAME = "gps028_database.db" // 数据库文件名
    }
}
