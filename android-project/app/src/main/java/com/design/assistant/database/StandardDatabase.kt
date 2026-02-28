package com.design.assistant.database

import com.design.assistant.constants.StandardConstants
import com.design.assistant.model.*

/**
 * 标准数据库
 * 包含不同标准体系的详细要求，用于生成设计方案
 */
object StandardDatabase {

    /**
     * 生成设计方案
     * 根据产品类型、标准体系和用户输入参数生成完整的设计方案
     */
    fun generateDesignResult(
        productType: ProductType,
        standardSystem: String,
        inputParameters: InputParameters
    ): DesignResult {
        return when (productType) {
            ProductType.CHILD_SEAT -> generateChildSeatDesign(standardSystem, inputParameters)
            ProductType.BABY_STROLLER -> generateBabyStrollerDesign(standardSystem, inputParameters)
            ProductType.HIGH_CHAIR -> generateHighChairDesign(standardSystem, inputParameters)
            ProductType.CHILD_BED -> generateChildBedDesign(standardSystem, inputParameters)
        }
    }

    /**
     * 生成儿童安全座椅设计方案
     */
    private fun generateChildSeatDesign(
        standardSystem: String,
        inputParameters: InputParameters
    ): DesignResult {
        return when (standardSystem) {
            StandardConstants.ECE_R129 -> generateECE_R129_ChildSeat(inputParameters)
            StandardConstants.FMVSS_213 -> generateFMVSS_213_ChildSeat(inputParameters)
            StandardConstants.CMVSS_213 -> generateCMVSS_213_ChildSeat(inputParameters)
            StandardConstants.AS_NZS_1754 -> generateAS_NZS_1754_ChildSeat(inputParameters)
            StandardConstants.GB_27887_2024 -> generateGB_27887_2024_ChildSeat(inputParameters)
            else -> generateDefaultChildSeat(inputParameters)
        }
    }

    /**
     * ECE R129 儿童安全座椅设计方案
     */
    private fun generateECE_R129_ChildSeat(input: InputParameters): DesignResult {
        val minHeight = input.minHeight ?: 40
        val maxHeight = input.maxHeight ?: 105

        // 根据身高范围确定分组
        val (group, dummyType) = when {
            maxHeight <= 60 -> "Q0" to "Q0 假人（出生至 10 个月）"
            maxHeight <= 76 -> "Q0+" to "Q0+ 假人（出生至 12 个月）"
            maxHeight <= 87 -> "Q1" to "Q1 假人（9 个月至 18 个月）"
            maxHeight <= 100 -> "Q1.5" to "Q1.5 假人（12 个月至 36 个月）"
            maxHeight <= 125 -> "Q3" to "Q3 假人（3 岁至 6 岁）"
            maxHeight <= 150 -> "Q6" to "Q6 假人（6 岁至 10 岁）"
            else -> "Q10" to "Q10 假人（10 岁至 12 岁）"
        }

        val installationDirection = when {
            maxHeight <= 87 -> "反向安装（强制性）"
            maxHeight <= 105 -> "反向或正向（根据座椅类型）"
            else -> "正向安装"
        }

        return DesignResult(
            productType = ProductType.CHILD_SEAT,
            productName = "儿童安全座椅",
            standardSystem = StandardConstants.ECE_R129,
            standardName = "ECE R129（i-Size）",
            inputParameters = input,

            // 适用标准
            applicableStandards = ApplicableStandards(
                standardCode = "ECE R129",
                standardName = "ECE R129 - 儿童约束系统",
                version = "Rev. 05",
                effectiveDate = "2023-09-01",
                issuingBody = "联合国欧洲经济委员会（UNECE）"
            ),

            // 基础适配数据
            basicAdaptationData = BasicAdaptationData(
                dummyInfo = DummyInfo(
                    dummyType = dummyType,
                    heightRange = "${minHeight}cm - ${maxHeight}cm",
                    weightRange = "根据身高转换：${(minHeight * 0.5).toInt()}kg - ${(maxHeight * 0.5).toInt()}kg",
                    installationDirection = installationDirection,
                    ageGroup = getAgeGroupByHeight(maxHeight)
                )
            ),

            // 设计参数
            designParameters = DesignParameters(
                headrestHeight = getHeadrestHeightByGroup(group),
                seatWidth = getSeatWidthByGroup(group),
                envelope = EnvelopeInfo(
                    sizeClass = getISOFIXSizeClass(group),
                    length = getEnvelopeLength(group),
                    width = getEnvelopeWidth(group),
                    height = getEnvelopeHeight(group),
                    description = "ISOFIX 安装空间要求，符合 ECE R129 Annex 18"
                ),
                sideImpactArea = getSideImpactArea(group)
            ),

            // 测试要求
            testRequirements = TestRequirements(
                frontalImpact = FrontalImpactRequirement(
                    testName = "ECE R129 正面碰撞测试",
                    speed = "50 km/h",
                    deceleration = "28g - 32g",
                    criteria = "假人头部伤害指标 HIC < 1000\n胸部加速度 < 55g\n颈部载荷符合限定值",
                    notes = "使用 Q 系列假人，测试座椅结构完整性"
                ),
                sideImpactChestCompression = SideImpactRequirement(
                    testName = "ECE R129 侧碰测试",
                    impactSpeed = "24.1 km/h",
                    maxChestCompression = "< 44mm",
                    maxChestDeflection = "< 44mm",
                    criteria = "假人胸部压缩量、挠度在限值内\n头部位移符合标准要求"
                ),
                harnessStrength = HarnessStrengthRequirement(
                    testName = "织带强度测试",
                    testLoad = "11kN",
                    duration = "30 秒",
                    elongationLimit = "< 5%",
                    criteria = "织带不断裂，伸长率在限值内\n卡扣功能正常"
                )
            ),

            // 标准测试项
            standardTestItems = StandardTestItems(
                dynamicTests = listOf(
                    DynamicTestItem(
                        testId = "ECER129-001",
                        testName = "动态碰撞：正碰",
                        testType = DynamicTestType.FRONTAL_IMPACT,
                        testDescription = "车辆以 50km/h 撞击刚性障碍物，评估假人伤害指标",
                        testConditions = "环境温度：20°C ± 5°C\n假人：${dummyType}\n座椅安装：按照制造商说明",
                        acceptanceCriteria = "HIC < 1000\n胸部加速度 < 55g\n颈部载荷：Fc < 1150N, Nz < 1150N",
                        isMandatory = true
                    ),
                    DynamicTestItem(
                        testId = "ECER129-002",
                        testName = "动态碰撞：侧碰",
                        testType = DynamicTestType.SIDE_IMPACT,
                        testDescription = "使用移动变形屏障进行侧撞测试",
                        testConditions = "撞击速度：24.1 km/h\n屏障：ECE R129 移动变形屏障\n假人：${dummyType}",
                        acceptanceCriteria = "胸部压缩量 < 44mm\n胸部挠度 < 44mm\n头部位移 < 550mm",
                        isMandatory = true
                    ),
                    DynamicTestItem(
                        testId = "ECER129-003",
                        testName = "动态碰撞：后碰",
                        testType = DynamicTestType.REAR_IMPACT,
                        testDescription = "评估后向座椅在后碰中的表现",
                        testConditions = "撞击速度：30 km/h\n假人：${dummyType}",
                        acceptanceCriteria = "假人位移在安全范围内\n座椅安装点无松动",
                        isMandatory = true
                    )
                )
            )
        )
    }

    /**
     * FMVSS 213 儿童安全座椅设计方案
     */
    private fun generateFMVSS_213_ChildSeat(input: InputParameters): DesignResult {
        val minWeight = input.minWeight ?: 2.3
        val maxWeight = input.maxWeight ?: 18.0

        val (group, dummyType) = when {
            maxWeight <= 9.1 -> "Group 1" to "CRABI 6 个月假人"
            maxWeight <= 18.0 -> "Group 2" to "Hybrid III 3 岁假人"
            maxWeight <= 22.7 -> "Group 3" to "Hybrid III 6 岁假人"
            else -> "Group 4" to "Hybrid III 10 岁假人"
        }

        val installationDirection = when {
            maxWeight <= 9.1 -> "反向安装（推荐）"
            maxWeight <= 13.6 -> "反向或正向（根据体重）"
            else -> "正向安装"
        }

        return DesignResult(
            productType = ProductType.CHILD_SEAT,
            productName = "儿童安全座椅",
            standardSystem = StandardConstants.FMVSS_213,
            standardName = "FMVSS 213 - 儿童约束系统",
            inputParameters = input,

            // 适用标准
            applicableStandards = ApplicableStandards(
                standardCode = "FMVSS 213",
                standardName = "FMVSS 213 - 儿童约束系统",
                version = "Rev. 09",
                effectiveDate = "2019-09-01",
                issuingBody = "美国国家公路交通安全管理局（NHTSA）"
            ),

            // 基础适配数据
            basicAdaptationData = BasicAdaptationData(
                dummyInfo = DummyInfo(
                    dummyType = dummyType,
                    heightRange = "根据体重估算：${(minWeight * 2).toInt()}cm - ${(maxWeight * 2).toInt()}cm",
                    weightRange = "${minWeight}kg - ${maxWeight}kg",
                    installationDirection = installationDirection,
                    ageGroup = getAgeGroupByWeight(maxWeight)
                )
            ),

            // 设计参数
            designParameters = DesignParameters(
                headrestHeight = getHeadrestHeightByGroup(group),
                seatWidth = getSeatWidthByGroup(group),
                envelope = EnvelopeInfo(
                    sizeClass = getLATCHSizeClass(group),
                    length = getEnvelopeLength(group),
                    width = getEnvelopeWidth(group),
                    height = getEnvelopeHeight(group),
                    description = "LATCH 安装空间要求，符合 FMVSS 225"
                ),
                sideImpactArea = getSideImpactArea(group)
            ),

            // 测试要求
            testRequirements = TestRequirements(
                frontalImpact = FrontalImpactRequirement(
                    testName = "FMVSS 213 正面碰撞测试",
                    speed = "48 km/h",
                    deceleration = "30g - 35g",
                    criteria = "假人头部伤害指标 HIC < 1000\n胸部加速度 < 60g",
                    notes = "使用 Hybrid III 系列假人"
                ),
                sideImpactChestCompression = SideImpactRequirement(
                    testName = "FMVSS 213 侧碰测试（新标准）",
                    impactSpeed = "32 km/h",
                    maxChestCompression = "< 50mm",
                    maxChestDeflection = "< 50mm",
                    criteria = "符合新版 FMVSS 213 侧碰要求"
                ),
                harnessStrength = HarnessStrengthRequirement(
                    testName = "织带强度测试",
                    testLoad = "11kN",
                    duration = "30 秒",
                    elongationLimit = "< 5%",
                    criteria = "织带不断裂，伸长率在限值内"
                )
            ),

            // 标准测试项
            standardTestItems = StandardTestItems(
                dynamicTests = listOf(
                    DynamicTestItem(
                        testId = "FMVSS213-001",
                        testName = "动态碰撞：正碰",
                        testType = DynamicTestType.FRONTAL_IMPACT,
                        testDescription = "车辆以 48km/h 撞击刚性障碍物",
                        testConditions = "环境温度：20°C ± 5°C\n假人：${dummyType}\n座椅安装：按照制造商说明",
                        acceptanceCriteria = "HIC < 1000\n胸部加速度 < 60g",
                        isMandatory = true
                    ),
                    DynamicTestItem(
                        testId = "FMVSS213-002",
                        testName = "动态碰撞：侧碰",
                        testType = DynamicTestType.SIDE_IMPACT,
                        testDescription = "新 FMVSS 213 侧碰测试要求",
                        testConditions = "撞击速度：32 km/h\n假人：${dummyType}",
                        acceptanceCriteria = "胸部压缩量 < 50mm\n胸部挠度 < 50mm",
                        isMandatory = true
                    ),
                    DynamicTestItem(
                        testId = "FMVSS213-003",
                        testName = "动态碰撞：后碰",
                        testType = DynamicTestType.REAR_IMPACT,
                        testDescription = "评估后向座椅在后碰中的表现",
                        testConditions = "撞击速度：30 km/h\n假人：${dummyType}",
                        acceptanceCriteria = "假人位移在安全范围内\n座椅结构完整",
                        isMandatory = true
                    )
                )
            )
        )
    }

    /**
     * CMVSS 213 儿童安全座椅设计方案（加拿大标准，类似 FMVSS 213）
     */
    private fun generateCMVSS_213_ChildSeat(input: InputParameters): DesignResult {
        val result = generateFMVSS_213_ChildSeat(input)
        return result.copy(
            standardSystem = StandardConstants.CMVSS_213,
            standardName = "CMVSS 213 - 儿童约束系统",
            applicableStandards = result.applicableStandards.copy(
                standardCode = "CMVSS 213",
                standardName = "CMVSS 213 - 儿童约束系统",
                version = "Rev. 07",
                effectiveDate = "2020-01-01",
                issuingBody = "加拿大交通部（Transport Canada）"
            )
        )
    }

    /**
     * GB 27887-2024 儿童安全座椅设计方案（中国新国标）
     */
    private fun generateGB_27887_2024_ChildSeat(input: InputParameters): DesignResult {
        val minHeight = input.minHeight ?: 40
        val maxHeight = input.maxHeight ?: 150
        val minWeight = input.minWeight ?: 0.0
        val maxWeight = input.maxWeight ?: 36.0

        val (group, dummyType) = when {
            maxWeight <= 9.0 -> "I 组" to "Q1 假人"
            maxWeight <= 18.0 -> "II 组" to "Q1.5 假人"
            maxWeight <= 25.0 -> "III 组" to "Q3 假人"
            maxWeight <= 36.0 -> "IV 组" to "Q6 假人"
            else -> "V 组" to "Q10 假人"
        }

        return DesignResult(
            productType = ProductType.CHILD_SEAT,
            productName = "儿童安全座椅",
            standardSystem = StandardConstants.GB_27887_2024,
            standardName = "GB 27887-2024 - 机动车儿童乘员用约束系统",
            inputParameters = input,

            // 适用标准
            applicableStandards = ApplicableStandards(
                standardCode = "GB 27887",
                standardName = "GB 27887-2024 - 机动车儿童乘员用约束系统",
                version = "2024 版",
                effectiveDate = "2024-05-01",
                issuingBody = "中国国家市场监督管理总局"
            ),

            // 基础适配数据
            basicAdaptationData = BasicAdaptationData(
                dummyInfo = DummyInfo(
                    dummyType = dummyType,
                    heightRange = "${minHeight}cm - ${maxHeight}cm",
                    weightRange = "${minWeight}kg - ${maxWeight}kg",
                    installationDirection = if (maxWeight <= 9.0) "反向安装（强制性）" else "正向安装",
                    ageGroup = getAgeGroupByWeight(maxWeight)
                )
            ),

            // 设计参数
            designParameters = DesignParameters(
                headrestHeight = getHeadrestHeightByGroup(group),
                seatWidth = getSeatWidthByGroup(group),
                envelope = EnvelopeInfo(
                    sizeClass = getISOFIXSizeClass(group),
                    length = getEnvelopeLength(group),
                    width = getEnvelopeWidth(group),
                    height = getEnvelopeHeight(group),
                    description = "ISOFIX 安装空间要求，符合 GB 27887-2024"
                ),
                sideImpactArea = getSideImpactArea(group)
            ),

            // 测试要求
            testRequirements = TestRequirements(
                frontalImpact = FrontalImpactRequirement(
                    testName = "GB 27887 正面碰撞测试",
                    speed = "50 km/h",
                    deceleration = "28g - 32g",
                    criteria = "HIC < 1000\n胸部加速度 < 55g",
                    notes = "符合中国国标要求"
                ),
                sideImpactChestCompression = SideImpactRequirement(
                    testName = "GB 27887 侧碰测试",
                    impactSpeed = "24.1 km/h",
                    maxChestCompression = "< 44mm",
                    maxChestDeflection = "< 44mm",
                    criteria = "胸部压缩量、挠度在限值内"
                ),
                harnessStrength = HarnessStrengthRequirement(
                    testName = "织带强度测试",
                    testLoad = "11kN",
                    duration = "30 秒",
                    elongationLimit = "< 5%",
                    criteria = "织带不断裂，伸长率在限值内"
                )
            ),

            // 标准测试项
            standardTestItems = StandardTestItems(
                dynamicTests = listOf(
                    DynamicTestItem(
                        testId = "GB27887-001",
                        testName = "动态碰撞：正碰",
                        testType = DynamicTestType.FRONTAL_IMPACT,
                        testDescription = "车辆以 50km/h 撞击刚性障碍物",
                        testConditions = "环境温度：20°C ± 5°C\n假人：${dummyType}",
                        acceptanceCriteria = "HIC < 1000\n胸部加速度 < 55g",
                        isMandatory = true
                    ),
                    DynamicTestItem(
                        testId = "GB27887-002",
                        testName = "动态碰撞：侧碰",
                        testType = DynamicTestType.SIDE_IMPACT,
                        testDescription = "使用移动变形屏障进行侧撞测试",
                        testConditions = "撞击速度：24.1 km/h",
                        acceptanceCriteria = "胸部压缩量 < 44mm\n胸部挠度 < 44mm",
                        isMandatory = true
                    ),
                    DynamicTestItem(
                        testId = "GB27887-003",
                        testName = "动态碰撞：后碰",
                        testType = DynamicTestType.REAR_IMPACT,
                        testDescription = "评估后向座椅在后碰中的表现",
                        testConditions = "撞击速度：30 km/h",
                        acceptanceCriteria = "假人位移在安全范围内",
                        isMandatory = true
                    )
                )
            )
        )
    }

    /**
     * AS/NZS 1754 儿童安全座椅设计方案（澳大利亚/新西兰标准）
     */
    private fun generateAS_NZS_1754_ChildSeat(input: InputParameters): DesignResult {
        val result = generateECE_R129_ChildSeat(input)
        return result.copy(
            standardSystem = StandardConstants.AS_NZS_1754,
            standardName = "AS/NZS 1754 - 儿童约束系统",
            applicableStandards = result.applicableStandards.copy(
                standardCode = "AS/NZS 1754",
                standardName = "AS/NZS 1754 - 儿童约束系统",
                version = "2019 版",
                effectiveDate = "2019-12-01",
                issuingBody = "澳大利亚/新西兰标准协会"
            ),
            testRequirements = result.testRequirements.copy(
                frontalImpact = result.testRequirements.frontalImpact.copy(
                    speed = "48-50 km/h",
                    deceleration = "28g - 32g"
                )
            )
        )
    }

    /**
     * 默认儿童安全座椅设计方案
     */
    private fun generateDefaultChildSeat(input: InputParameters): DesignResult {
        return generateECE_R129_ChildSeat(input)
    }

    // 生成其他产品的设计方案（简化版本）
    private fun generateBabyStrollerDesign(standardSystem: String, input: InputParameters): DesignResult {
        // TODO: 实现婴儿推车设计方案
        return DesignResult(
            productType = ProductType.BABY_STROLLER,
            productName = "婴儿推车",
            standardSystem = standardSystem,
            standardName = StandardConstants.getStandardName(standardSystem),
            inputParameters = input,
            applicableStandards = ApplicableStandards(
                standardCode = standardSystem,
                standardName = StandardConstants.getStandardName(standardSystem),
                version = "待完善",
                effectiveDate = "待完善",
                issuingBody = "待完善"
            ),
            basicAdaptationData = BasicAdaptationData(
                dummyInfo = DummyInfo(
                    dummyType = "N/A",
                    heightRange = "N/A",
                    weightRange = "${input.minWeight ?: 0}kg - ${input.maxWeight ?: 15}kg",
                    installationDirection = "N/A"
                )
            ),
            designParameters = DesignParameters(
                headrestHeight = "N/A",
                seatWidth = "N/A",
                envelope = EnvelopeInfo("N/A", "N/A", "N/A", "N/A", "N/A"),
                sideImpactArea = "N/A"
            ),
            testRequirements = TestRequirements(
                frontalImpact = FrontalImpactRequirement("N/A", "N/A", "N/A", "N/A"),
                sideImpactChestCompression = SideImpactRequirement("N/A", "N/A", "N/A", "N/A", "N/A"),
                harnessStrength = HarnessStrengthRequirement("N/A", "N/A", "N/A", "N/A", "N/A")
            ),
            standardTestItems = StandardTestItems(emptyList())
        )
    }

    private fun generateHighChairDesign(standardSystem: String, input: InputParameters): DesignResult {
        // TODO: 实现高脚椅设计方案
        return generateBabyStrollerDesign(standardSystem, input).copy(
            productType = ProductType.HIGH_CHAIR,
            productName = "儿童高脚椅"
        )
    }

    private fun generateChildBedDesign(standardSystem: String, input: InputParameters): DesignResult {
        // TODO: 实现儿童床设计方案
        return generateBabyStrollerDesign(standardSystem, input).copy(
            productType = ProductType.CHILD_BED,
            productName = "儿童床"
        )
    }

    // 辅助方法：根据分组获取设计参数
    private fun getHeadrestHeightByGroup(group: String): String {
        return when {
            group.contains("Q0") -> "35cm - 50cm"
            group.contains("Q1") -> "45cm - 65cm"
            group.contains("Q1.5") -> "55cm - 75cm"
            group.contains("Q3") -> "65cm - 85cm"
            group.contains("Q6") -> "75cm - 95cm"
            group.contains("Q10") -> "85cm - 110cm"
            group.contains("I") -> "45cm - 65cm"
            group.contains("II") -> "55cm - 75cm"
            group.contains("III") -> "65cm - 85cm"
            group.contains("IV") -> "75cm - 95cm"
            else -> "N/A"
        }
    }

    private fun getSeatWidthByGroup(group: String): String {
        return when {
            group.contains("Q0") || group.contains("I") -> "30cm - 35cm"
            group.contains("Q1") || group.contains("II") -> "35cm - 40cm"
            group.contains("Q1.5") -> "38cm - 43cm"
            group.contains("Q3") || group.contains("III") -> "40cm - 45cm"
            group.contains("Q6") || group.contains("IV") -> "42cm - 48cm"
            group.contains("Q10") -> "45cm - 52cm"
            else -> "N/A"
        }
    }

    private fun getISOFIXSizeClass(group: String): String {
        return when {
            group.contains("Q0") || group.contains("I") -> "ISOFIX SIZE CLASS D (新生儿)"
            group.contains("Q1") || group.contains("II") -> "ISOFIX SIZE CLASS C (0-13kg)"
            group.contains("Q1.5") -> "ISOFIX SIZE CLASS B1 (9-18kg)"
            group.contains("Q3") || group.contains("III") -> "ISOFIX SIZE CLASS B2 (15-25kg)"
            group.contains("Q6") || group.contains("IV") -> "ISOFIX SIZE CLASS A (22-36kg)"
            group.contains("Q10") -> "ISOFIX SIZE CLASS A+ (30-45kg)"
            else -> "ISOFIX SIZE CLASS 通用"
        }
    }

    private fun getLATCHSizeClass(group: String): String {
        return when {
            group.contains("Group 1") || group.contains("I") -> "LATCH Class 1 (0-10kg)"
            group.contains("Group 2") || group.contains("II") -> "LATCH Class 2 (10-18kg)"
            group.contains("Group 3") || group.contains("III") -> "LATCH Class 3 (18-29kg)"
            group.contains("Group 4") || group.contains("IV") -> "LATCH Class 4 (29-45kg)"
            else -> "LATCH Class 通用"
        }
    }

    private fun getEnvelopeLength(group: String): String {
        return when {
            group.contains("Q0") -> "60cm - 70cm"
            group.contains("Q1") -> "65cm - 75cm"
            group.contains("Q1.5") -> "70cm - 80cm"
            group.contains("Q3") -> "75cm - 85cm"
            group.contains("Q6") -> "80cm - 90cm"
            group.contains("Q10") -> "85cm - 95cm"
            else -> "N/A"
        }
    }

    private fun getEnvelopeWidth(group: String): String {
        return when {
            group.contains("Q0") -> "44cm - 50cm"
            group.contains("Q1") -> "48cm - 54cm"
            group.contains("Q1.5") -> "50cm - 56cm"
            group.contains("Q3") -> "52cm - 58cm"
            group.contains("Q6") -> "54cm - 60cm"
            group.contains("Q10") -> "56cm - 62cm"
            else -> "N/A"
        }
    }

    private fun getEnvelopeHeight(group: String): String {
        return when {
            group.contains("Q0") -> "55cm - 65cm"
            group.contains("Q1") -> "60cm - 70cm"
            group.contains("Q1.5") -> "65cm - 75cm"
            group.contains("Q3") -> "70cm - 80cm"
            group.contains("Q6") -> "75cm - 85cm"
            group.contains("Q10") -> "80cm - 90cm"
            else -> "N/A"
        }
    }

    private fun getSideImpactArea(group: String): String {
        return when {
            group.contains("Q0") -> "≥ 600cm²"
            group.contains("Q1") -> "≥ 650cm²"
            group.contains("Q1.5") -> "≥ 700cm²"
            group.contains("Q3") -> "≥ 750cm²"
            group.contains("Q6") -> "≥ 800cm²"
            group.contains("Q10") -> "≥ 850cm²"
            else -> "N/A"
        }
    }

    private fun getAgeGroupByHeight(height: Int): String {
        return when {
            height <= 60 -> "新生儿 - 10 个月"
            height <= 76 -> "新生儿 - 12 个月"
            height <= 87 -> "9 个月 - 18 个月"
            height <= 100 -> "12 个月 - 36 个月"
            height <= 125 -> "3 岁 - 6 岁"
            height <= 150 -> "6 岁 - 10 岁"
            else -> "10 岁 - 12 岁"
        }
    }

    private fun getAgeGroupByWeight(weight: Double): String {
        return when {
            weight <= 9.0 -> "新生儿 - 18 个月"
            weight <= 18.0 -> "1.5 岁 - 4 岁"
            weight <= 25.0 -> "4 岁 - 7 岁"
            weight <= 36.0 -> "7 岁 - 12 岁"
            else -> "10 岁 - 12 岁"
        }
    }
}
