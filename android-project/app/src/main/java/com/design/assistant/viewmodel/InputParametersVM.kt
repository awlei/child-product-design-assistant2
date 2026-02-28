package com.design.assistant.viewmodel

import androidx.lifecycle.ViewModel
import com.design.assistant.model.InputParameters
import com.design.assistant.model.ProductType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 输入参数管理 ViewModel
 * 负责管理用户输入的设计参数
 */
class InputParametersVM : ViewModel() {
    // 当前输入参数
    private val _inputParameters = MutableStateFlow<InputParameters?>(null)
    val inputParameters: StateFlow<InputParameters?> = _inputParameters.asStateFlow()

    // 上一次的产品类型（用于检测产品变化）
    private val _lastProductType = MutableStateFlow<ProductType?>(null)
    val lastProductType: StateFlow<ProductType?> = _lastProductType.asStateFlow()

    /**
     * 设置输入参数
     */
    fun setInputParameters(parameters: InputParameters) {
        _inputParameters.value = parameters
        _lastProductType.value = parameters.productType
    }

    /**
     * 清除输入参数
     */
    fun clearInputParameters() {
        _inputParameters.value = null
        _lastProductType.value = null
    }

    /**
     * 获取当前参数摘要
     */
    fun getParametersSummary(): String {
        return _inputParameters.value?.getSummary() ?: ""
    }

    /**
     * 验证当前参数
     */
    fun validateParameters(): Boolean {
        return _inputParameters.value?.validate() is com.design.assistant.model.ValidationResult.Success
    }

    /**
     * 检查是否需要更新参数（当产品或标准变化时）
     */
    fun shouldUpdateParameters(productType: ProductType, standardSystem: String): Boolean {
        val current = _inputParameters.value ?: return true
        return current.productType != productType || current.standardSystem != standardSystem
    }
}
