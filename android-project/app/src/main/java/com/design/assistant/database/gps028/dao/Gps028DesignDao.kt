package com.design.assistant.database.gps028.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.design.assistant.database.gps028.entity.Gps028DesignParamEntity

/**
 * GPS028设计参数DAO
 * 支持通用参数查询和管理
 */
@Dao
interface Gps028DesignDao {
    /** 根据假人模型查询GPS设计参数 */
    @Query("SELECT * FROM gps028_design_params WHERE dummyModel = :dummyModel")
    suspend fun getParamsByDummy(dummyModel: String): List<Gps028DesignParamEntity>

    /** 插入/覆盖默认参数（初始化用） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(params: List<Gps028DesignParamEntity>)

    /** 插入单个参数 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(param: Gps028DesignParamEntity)

    /** 更新参数 */
    @androidx.room.Update
    suspend fun update(param: Gps028DesignParamEntity)

    /** 删除参数 */
    @androidx.room.Delete
    suspend fun delete(param: Gps028DesignParamEntity)

    /** 获取所有参数 */
    @Query("SELECT * FROM gps028_design_params")
    suspend fun getAll(): List<Gps028DesignParamEntity>

    /** 根据类别获取参数 */
    @Query("SELECT * FROM gps028_design_params WHERE category = :category")
    suspend fun getByCategory(category: String): List<Gps028DesignParamEntity>

    /** 根据参数名称获取参数 */
    @Query("SELECT * FROM gps028_design_params WHERE paramName = :paramName LIMIT 1")
    suspend fun getByName(paramName: String): Gps028DesignParamEntity?

    /** 获取参数数量 */
    @Query("SELECT COUNT(*) FROM gps028_design_params")
    suspend fun getCount(): Int
}
