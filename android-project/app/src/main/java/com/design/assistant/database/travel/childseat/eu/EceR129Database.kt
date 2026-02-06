package com.design.assistant.database.travel.childseat.eu

import androidx.room.Database
import androidx.room.RoomDatabase
import com.design.assistant.database.travel.childseat.eu.dao.EceR129DummyDao
import com.design.assistant.database.travel.childseat.eu.dao.EceR129TestConfigDao
import com.design.assistant.database.travel.childseat.eu.entity.EceR129DummyEntity
import com.design.assistant.database.travel.childseat.eu.entity.EceR129TestConfigEntity

/**
 * 欧盟ECE R129专属数据库（物理隔离）
 */
@Database(
    entities = [EceR129DummyEntity::class, EceR129TestConfigEntity::class],
    version = 1,
    exportSchema = true
)
abstract class EceR129Database : RoomDatabase() {
    abstract fun eceR129DummyDao(): EceR129DummyDao
    abstract fun eceR129TestConfigDao(): EceR129TestConfigDao

    companion object {
        const val DATABASE_NAME = "ece_r129_database.db"
    }
}
