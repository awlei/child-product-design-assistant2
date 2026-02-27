package com.design.assistant.viewmodel

import androidx.lifecycle.ViewModel
import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.ProductType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 产品与标准选择 ViewModel
 * 负责管理用户选择的产品类型和标准体系
 */
class ProductStandardSelectVM : ViewModel() {
    // 选中的产品类型
    private val _selectedProductType = MutableStateFlow<ProductType>(ProductType.CHILD_SEAT)
    val selectedProductType: StateFlow<ProductType> = _selectedProductType.asStateFlow()

    // 选中的标准体系
    private val _selectedStandard = MutableStateFlow<String>(StandardConstants.STANDARD_ECE_R129)
    val selectedStandard: StateFlow<String> = _selectedStandard.asStateFlow()

    /**
     * 设置选中的产品类型
     */
    fun selectProductType(productType: ProductType) {
        _selectedProductType.value = productType
        // 切换产品类型时，默认选中该产品的第一个标准
        when (productType) {
            ProductType.CHILD_SEAT -> _selectedStandard.value = StandardConstants.STANDARD_ECE_R129
            ProductType.BABY_STROLLER -> _selectedStandard.value = StandardConstants.STANDARD_GB_27887
            ProductType.HIGH_CHAIR -> _selectedStandard.value = StandardConstants.STANDARD_AS_NZS_1754
            ProductType.CHILD_BED -> _selectedStandard.value = StandardConstants.STANDARD_FMVSS_213
        }
    }

    /**
     * 设置选中的标准体系
     */
    fun selectStandard(standard: String) {
        _selectedStandard.value = standard
    }

    /**
     * 获取当前选中产品支持的标准列表
     */
    fun getSupportedStandards(): List<String> {
        return when (_selectedProductType.value) {
            ProductType.CHILD_SEAT -> listOf(
                StandardConstants.STANDARD_ECE_R129,
                StandardConstants.STANDARD_CMVSS_213,
                StandardConstants.STANDARD_FMVSS_213,
                StandardConstants.STANDARD_AS_NZS_1754
            )
            ProductType.BABY_STROLLER -> listOf(
                StandardConstants.STANDARD_GB_27887
            )
            ProductType.HIGH_CHAIR -> listOf(
                StandardConstants.STANDARD_AS_NZS_1754
            )
            ProductType.CHILD_BED -> listOf(
                StandardConstants.STANDARD_FMVSS_213
            )
        }
    }
}
