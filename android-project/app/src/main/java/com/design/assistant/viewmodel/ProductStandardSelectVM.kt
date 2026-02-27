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
    private val _selectedStandard = MutableStateFlow<String>(StandardConstants.ECE_R129)
    val selectedStandard: StateFlow<String> = _selectedStandard.asStateFlow()

    /**
     * 设置选中的产品类型
     */
    fun selectProductType(productType: ProductType) {
        _selectedProductType.value = productType
        // 切换产品类型时，默认选中该产品的第一个标准
        when (productType) {
            ProductType.CHILD_SEAT -> _selectedStandard.value = StandardConstants.ECE_R129
            ProductType.BABY_STROLLER -> _selectedStandard.value = StandardConstants.GB_14748
            ProductType.HIGH_CHAIR -> _selectedStandard.value = StandardConstants.EN_14988
            ProductType.CHILD_BED -> _selectedStandard.value = StandardConstants.EN_716
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
                StandardConstants.ECE_R129,
                StandardConstants.CMVSS_213,
                StandardConstants.FMVSS_213,
                StandardConstants.AS_NZS_1754
            )
            ProductType.BABY_STROLLER -> listOf(
                StandardConstants.EN_1888,
                StandardConstants.GB_14748,
                StandardConstants.ASTM_F833,
                StandardConstants.CAN_CSA_D425
            )
            ProductType.HIGH_CHAIR -> listOf(
                StandardConstants.EN_14988,
                StandardConstants.GB_29281,
                StandardConstants.CAN_CSA_Z217_1
            )
            ProductType.CHILD_BED -> listOf(
                StandardConstants.EN_716,
                StandardConstants.GB_28007,
                StandardConstants.CAN_CSA_D1169
            )
        }
    }
}
