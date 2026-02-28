package com.design.assistant.viewmodel

import android.util.Log
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
    companion object {
        private const val TAG = "DesignGenerateVM"
    }

    // 生成状态
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // 生成结果
    private val _designResult = MutableStateFlow<DesignResult?>(null)
    val designResult: StateFlow<DesignResult?> = _designResult.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 防重复生成的保护标志
    private var isGeneratingInProgress = false

    /**
     * 生成设计方案
     * 根据产品类型、标准体系和用户输入参数生成完整的设计方案
     */
    fun generateDesign(
        productType: ProductType,
        standardSystem: String,
        inputParameters: InputParameters
    ) {
        // 防止重复生成
        if (isGeneratingInProgress) {
            Log.w(TAG, "设计方案正在生成中，忽略重复请求")
            return
        }

        viewModelScope.launch {
            try {
                isGeneratingInProgress = true
                _isGenerating.value = true
                _errorMessage.value = null
                _designResult.value = null

                Log.d(TAG, "========== 开始生成设计方案 ==========")
                Log.d(TAG, "产品类型: $productType")
                Log.d(TAG, "标准体系: $standardSystem")
                Log.d(TAG, "输入参数详情:")
                Log.d(TAG, "  - 产品类型参数: ${inputParameters.productType}")
                Log.d(TAG, "  - 标准体系参数: ${inputParameters.standardSystem}")
                Log.d(TAG, "  - 最小身高: ${inputParameters.minHeight} cm")
                Log.d(TAG, "  - 最大身高: ${inputParameters.maxHeight} cm")
                Log.d(TAG, "  - 最小体重: ${inputParameters.minWeight} kg")
                Log.d(TAG, "  - 最大体重: ${inputParameters.maxWeight} kg")
                Log.d(TAG, "  - 年龄范围: ${inputParameters.ageRange}")
                Log.d(TAG, "  - 安装方式: ${inputParameters.seatInstallationType}")
                Log.d(TAG, "  - 车辆类型: ${inputParameters.vehicleType}")
                Log.d(TAG, "  - 其他要求: ${inputParameters.additionalRequirements}")

                // 模拟生成延迟
                kotlinx.coroutines.delay(500)

                Log.d(TAG, "调用 StandardDatabase.generateDesignResult()...")
                // 调用标准数据库生成设计方案
                val result = StandardDatabase.generateDesignResult(
                    productType = productType,
                    standardSystem = standardSystem,
                    inputParameters = inputParameters
                )

                Log.d(TAG, "设计方案生成成功!")
                Log.d(TAG, "产品名称: ${result.productName}")
                Log.d(TAG, "标准名称: ${result.standardName}")
                Log.d(TAG, "假人类型: ${result.basicAdaptationData.dummyInfo.dummyType}")
                Log.d(TAG, "=========================================")

                _designResult.value = result
            } catch (e: Exception) {
                Log.e(TAG, "生成设计方案失败", e)
                Log.e(TAG, "错误类型: ${e.javaClass.simpleName}")
                Log.e(TAG, "错误信息: ${e.message}")
                Log.e(TAG, "堆栈跟踪:", e)

                _errorMessage.value = "生成设计方案失败: ${e.message}\n错误类型: ${e.javaClass.simpleName}"
            } finally {
                _isGenerating.value = false
                isGeneratingInProgress = false
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
