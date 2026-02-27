package com.design.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.design.assistant.model.DesignResult
import com.design.assistant.repository.gps028.Gps028Repository
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

    // TODO: 后续注入 SelectVM 和 Repository
    // private val selectVM: ProductStandardSelectVM
    // private val multiStandardRepo: MultiStandardDesignRepository

    /**
     * 生成设计方案
     */
    fun generateDesign(
        productType: String,
        standard: String,
        customParams: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null

            try {
                // TODO: 实际实现中应该调用 multiStandardRepo.generateDesign()
                // 这里先使用模拟数据
                val result = DesignResult(
                    productName = "专业${
                        when (productType) {
                            "child_seat" -> "儿童安全座椅"
                            "baby_stroller" -> "婴儿推车"
                            "high_chair" -> "儿童高脚椅"
                            "child_bed" -> "儿童床"
                            else -> "儿童产品"
                        }
                    }设计",
                    standard = standard,
                    designParams = listOf(
                        "基础安全系数: 1.5",
                        "最大加速度: 50g",
                        "头部伤害指标(HIC): 1000",
                        "胸部加速度: 60g",
                        "围栏高度: 600mm",
                        "静态强度: 50kg",
                        "稳定性: 10度"
                    ),
                    compatibleTips = listOf(
                        "符合 GPS028-2023 标准要求",
                        "建议进行动态碰撞测试验证",
                        "建议进行耐久性测试",
                        "注意材料阻燃性要求"
                    ),
                    timestamp = System.currentTimeMillis()
                )

                _designResult.value = result
            } catch (e: Exception) {
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
