package com.design.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.design.assistant.database.StandardDatabase
import com.design.assistant.model.DesignResult
import com.design.assistant.model.InputParameters
import com.design.assistant.model.ProductType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 设计生成 ViewModel
 * 负责生成专业儿童产品设计方案
 */
class DesignGenerateVM : ViewModel() {
    // 生成状态
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // 生成结果
    private val _designResult = MutableStateFlow<DesignResult?>(null)
    val designResult: StateFlow<DesignResult?> = _designResult.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 生成设计方案
     * 根据产品类型、标准体系和用户输入参数生成完整的设计方案
     */
    fun generateDesign(
        productType: ProductType,
        standardSystem: String,
        inputParameters: InputParameters
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null

            try {
                println("开始生成设计方案...")
                println("产品类型: $productType")
                println("标准体系: $standardSystem")
                println("输入参数: $inputParameters")

                // 模拟生成延迟
                kotlinx.coroutines.delay(500)

                // 调用标准数据库生成设计方案
                val result = StandardDatabase.generateDesignResult(
                    productType = productType,
                    standardSystem = standardSystem,
                    inputParameters = inputParameters
                )

                println("设计方案生成成功!")
                println("产品名称: ${result.productName}")
                println("标准名称: ${result.standardName}")
                println("假人类型: ${result.basicAdaptationData.dummyInfo.dummyType}")

                _designResult.value = result
            } catch (e: Exception) {
                println("生成设计方案失败: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "生成设计方案失败: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * 清除设计结果
     */
    fun clearResult() {
        _designResult.value = null
    }
}
