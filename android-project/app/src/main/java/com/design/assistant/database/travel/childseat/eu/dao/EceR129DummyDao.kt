package com.design.assistant.database.travel.childseat.eu.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.design.assistant.database.travel.childseat.eu.entity.EceR129DummyEntity
import kotlinx.coroutines.flow.Flow

/**
 * ECE R129 假人模型数据访问对象
 */
@Dao
interface EceR129DummyDao {
    @Query("SELECT * FROM ece_r129_dummy")
    fun getAllDummies(): Flow<List<EceR129DummyEntity>>

    @Query("SELECT * FROM ece_r129_dummy WHERE dummyId = :dummyId")
    suspend fun getDummyById(dummyId: String): EceR129DummyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDummy(dummy: EceR129DummyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDummies(dummies: List<EceR129DummyEntity>)

    @Query("DELETE FROM ece_r129_dummy")
    suspend fun deleteAllDummies()
}
