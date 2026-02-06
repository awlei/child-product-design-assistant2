package com.design.assistant.database.gps028.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.design.assistant.database.gps028.entity.Gps028DesignParamEntity

/**
 * GPS028设计参数DAO
 * 核心：按假人模型查询参数
 */
@Dao
interface Gps028DesignDao {
    /** 根据假人模型查询GPS设计参数 */
    @Query("SELECT * FROM gps028_design_params WHERE dummyModel = :dummyModel LIMIT 1")
    suspend fun getParamsByDummy(dummyModel: String): Gps028DesignParamEntity?

    /** 插入/覆盖默认参数（初始化用） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(params: List<Gps028DesignParamEntity>)
}
