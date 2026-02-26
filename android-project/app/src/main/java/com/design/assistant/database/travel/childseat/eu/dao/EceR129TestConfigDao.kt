package com.design.assistant.database.travel.childseat.eu.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.design.assistant.database.travel.childseat.eu.entity.EceR129TestConfigEntity
import kotlinx.coroutines.flow.Flow

/**
 * ECE R129 测试配置数据访问对象
 */
@Dao
interface EceR129TestConfigDao {
    @Query("SELECT * FROM ece_r129_test_config")
    fun getAllConfigs(): Flow<List<EceR129TestConfigEntity>>

    @Query("SELECT * FROM ece_r129_test_config WHERE configId = :configId")
    suspend fun getConfigById(configId: String): EceR129TestConfigEntity?

    @Query("SELECT * FROM ece_r129_test_config WHERE testType = :testType")
    fun getConfigsByTestType(testType: String): Flow<List<EceR129TestConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: EceR129TestConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllConfigs(configs: List<EceR129TestConfigEntity>)

    @Query("DELETE FROM ece_r129_test_config")
    suspend fun deleteAllConfigs()
}
