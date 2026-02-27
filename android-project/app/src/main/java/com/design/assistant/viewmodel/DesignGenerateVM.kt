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

                // 创建 Gps028DesignParams
                val gpsParams = com.design.assistant.model.Gps028DesignParams(
                    headrestHeight = "450-650mm",
                    headrestBasePoint = "坐骨结节H点",
                    headrestTolerance = "±5mm",
                    seatWidth = "280mm",
                    seatWidthTotal = "380mm（含侧防侧翼）",
                    envelopSize = "B1",
                    envelopDetail = "长: 660mm, 宽: 380mm, 固定点间距: 280mm",
                    envelopStdClause = "ECE R129 Annex 7",
                    sideProtectionArea = "≥0.15㎡",
                    sideProtectionCover = "T12胸部至P8头部",
                    sideProtectionStd = "ECE R129 Annex 8"
                )

                // 创建 DynamicCrashItem
                val dynamicCrashFront = com.design.assistant.model.DynamicCrashItem(
                    testDevice = "HYGE电动碰撞台",
                    testCondition = "50km/h, 后向安装, 5点式安全带",
                    qualifiedCriteria = "头部HIC ≤ 1000, 胸部加速度 ≤ 60g, 头部位移 ≤ 550mm"
                )

                val dynamicCrashBack = com.design.assistant.model.DynamicCrashItem(
                    testDevice = "HYGE电动碰撞台",
                    testCondition = "30km/h, 后向安装, 5点式安全带",
                    qualifiedCriteria = "头部HIC ≤ 1000, 胸部加速度 ≤ 60g, 头部位移 ≤ 550mm"
                )

                val dynamicCrashSide = com.design.assistant.model.DynamicCrashItem(
                    testDevice = "侧碰滑车",
                    testCondition = "24km/h, 90°侧向撞击",
                    qualifiedCriteria = "胸部压缩 ≤ 44mm, 腹部力 ≤ 2.5kN"
                )

                // 创建 FlameRetardantItem
                val flameRetardant = com.design.assistant.model.FlameRetardantItem(
                    applyMaterials = "面料、泡沫、织带",
                    testStd = "ISO 3795:2019",
                    qualifiedCriteria = "燃烧速度 ≤ 100mm/min"
                )

                val result = DesignResult(
                    standardCode = standard,
                    productType = productType,
                    heightCm = 100,
                    standardName = com.design.assistant.constants.StandardConstants.getStandardName(standard),
                    standardVersion = "最新版",
                    standardImplement = "强制实施",
                    standardKeyRequire = "符合儿童安全座椅安全要求",
                    dummyModel = "Q3",
                    dummyPercentile = "50th百分位3岁儿童",
                    dummyStdCode = "ISO 13232-2:2021",
                    dummyHeightRange = "83-97cm",
                    dummyWeightRange = "9-18kg",
                    dummyBodyData = "坐高: 460mm, 肩宽: 230mm, 头围: 500mm",
                    installDirection = "后向",
                    installStdClause = "ECE R129 Annex 4",
                    heightMatchTip = "中值，适配性最优",
                    gpsDesignParams = gpsParams,
                    frontalCrash = "50km/h, 头部HIC ≤ 1000, 胸部加速度 ≤ 60g",
                    frontalCrashClause = "ECE R129 Annex 6",
                    sideCrashChestCompress = "≤ 44mm",
                    sideCrashClause = "ECE R129 Annex 8",
                    webbingStrength = "≥ 2750N",
                    webbingTestMethod = "ISO 6613:2020",
                    dynamicCrashFront = dynamicCrashFront,
                    dynamicCrashBack = dynamicCrashBack,
                    dynamicCrashSide = dynamicCrashSide,
                    flameRetardant = flameRetardant
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
