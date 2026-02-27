package com.design.assistant.repository.gps028

import com.design.assistant.database.gps028.Gps028Database
import com.design.assistant.database.gps028.dao.Gps028DesignDao
import com.design.assistant.database.gps028.entity.Gps028DesignParamEntity
import com.design.assistant.model.Gps028DesignParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * GPS028-2023 标准仓库
 * 负责管理 GPS028-2023 设计参数的数据访问
 */
class Gps028Repository(private val database: Gps028Database) {
    private val dao: Gps028DesignDao = database.gps028DesignDao()

    /**
     * 初始化默认数据
     * GPS028-2023 标准的专业设计参数
     */
    suspend fun initDefaultData() = withContext(Dispatchers.IO) {
        // 检查是否已有数据
        if (dao.getCount() > 0) return@withContext

        // 插入 GPS028-2023 标准默认设计参数
        val defaultParams = listOf(
            Gps028DesignParamEntity(
                paramName = "儿童安全座椅-基础安全系数",
                paramValue = "1.5",
                paramUnit = "倍",
                description = "GPS028-2023 儿童安全座椅基础安全系数",
                category = "儿童安全座椅"
            ),
            Gps028DesignParamEntity(
                paramName = "儿童安全座椅-最大加速度",
                paramValue = "50",
                paramUnit = "g",
                description = "GPS028-2023 儿童安全座椅最大加速度",
                category = "儿童安全座椅"
            ),
            Gps028DesignParamEntity(
                paramName = "儿童安全座椅-头部伤害指标(HIC)",
                paramValue = "1000",
                paramUnit = "",
                description = "GPS028-2023 儿童安全座椅头部伤害指标",
                category = "儿童安全座椅"
            ),
            Gps028DesignParamEntity(
                paramName = "儿童安全座椅-胸部加速度",
                paramValue = "60",
                paramUnit = "g",
                description = "GPS028-2023 儿童安全座椅胸部加速度",
                category = "儿童安全座椅"
            ),
            Gps028DesignParamEntity(
                paramName = "婴儿推车-静态强度",
                paramValue = "50",
                paramUnit = "kg",
                description = "GPS028-2023 婴儿推车静态强度要求",
                category = "婴儿推车"
            ),
            Gps028DesignParamEntity(
                paramName = "儿童高脚椅-稳定性",
                paramValue = "10",
                paramUnit = "度",
                description = "GPS028-2023 儿童高脚椅稳定性要求",
                category = "儿童高脚椅"
            ),
            Gps028DesignParamEntity(
                paramName = "儿童床-围栏高度",
                paramValue = "600",
                paramUnit = "mm",
                description = "GPS028-2023 儿童床围栏高度要求",
                category = "儿童床"
            )
        )
        dao.insertAll(defaultParams)
    }

    /**
     * 获取所有设计参数
     */
    suspend fun getAllParams(): List<Gps028DesignParamEntity> = withContext(Dispatchers.IO) {
        dao.getAll()
    }

    /**
     * 根据类别获取设计参数
     */
    suspend fun getParamsByCategory(category: String): List<Gps028DesignParamEntity> = withContext(Dispatchers.IO) {
        dao.getByCategory(category)
    }

    /**
     * 插入新的设计参数
     */
    suspend fun insertParam(param: Gps028DesignParamEntity) = withContext(Dispatchers.IO) {
        dao.insert(param)
    }

    /**
     * 更新设计参数
     */
    suspend fun updateParam(param: Gps028DesignParamEntity) = withContext(Dispatchers.IO) {
        dao.update(param)
    }

    /**
     * 删除设计参数
     */
    suspend fun deleteParam(param: Gps028DesignParamEntity) = withContext(Dispatchers.IO) {
        dao.delete(param)
    }
}
