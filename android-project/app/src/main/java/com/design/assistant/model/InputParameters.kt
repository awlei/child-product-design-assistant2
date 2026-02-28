package com.design.assistant.model

/**
 * 用户输入参数数据模型
 * 根据不同的标准体系，包含不同的输入字段
 */
data class InputParameters(
    // 产品类型
    val productType: ProductType,
    // 标准体系
    val standardSystem: String,

    // 欧标（ECE R129）相关：身高范围（cm）
    val minHeight: Int? = null,      // 最小身高（cm）
    val maxHeight: Int? = null,      // 最大身高（cm）

    // 美标（FMVSS 213、CMVSS 213）相关：体重范围（kg）
    val minWeight: Double? = null,   // 最小体重（kg）
    val maxWeight: Double? = null,   // 最大体重（kg）

    // 其他可选参数
    val ageRange: String? = null,    // 年龄范围（如：0-6个月）
    val additionalRequirements: String? = null, // 其他特殊要求
    val seatInstallationType: String? = null, // 座椅安装方式（如：ISOFIX、安全带）
    val vehicleType: String? = null  // 车辆类型（如：轿车、SUV）
) {
    /**
     * 验证输入参数是否有效
     */
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()

        try {
            // 根据标准体系验证必填字段
            when (standardSystem) {
                "ECE R129" -> {
                    // 欧标需要身高范围
                    if (minHeight == null || maxHeight == null) {
                        errors.add("欧标需要输入身高范围")
                    } else {
                        val min = minHeight!!
                        val max = maxHeight!!
                        if (min <= 0 || max <= 0) {
                            errors.add("身高必须大于 0")
                        } else if (min >= max) {
                            errors.add("最小身高必须小于最大身高")
                        } else if (max - min > 50) {
                            errors.add("身高范围过大，建议不超过 50cm")
                        }
                    }
                }
                "FMVSS 213", "CMVSS 213" -> {
                    // 美标和加标需要体重范围
                    if (minWeight == null || maxWeight == null) {
                        errors.add("美标需要输入体重范围")
                    } else {
                        val min = minWeight!!
                        val max = maxWeight!!
                        if (min <= 0 || max <= 0) {
                            errors.add("体重必须大于 0")
                        } else if (min >= max) {
                            errors.add("最小体重必须小于最大体重")
                        } else if (max - min > 20) {
                            errors.add("体重范围过大，建议不超过 20kg")
                        }
                    }
                }
                "GB 27887-2024", "AS/NZS 1754" -> {
                    // 国标和澳标可能需要身高和体重
                    if (minHeight == null && maxHeight == null && minWeight == null && maxWeight == null) {
                        errors.add("需要输入身高范围或体重范围")
                    }
                }
            }
        } catch (e: Exception) {
            errors.add("参数验证异常: ${e.message}")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors.joinToString("\n"))
        }
    }

    /**
     * 获取参数摘要文本
     */
    fun getSummary(): String {
        val summary = StringBuilder()

        when (standardSystem) {
            "ECE R129" -> {
                if (minHeight != null && maxHeight != null) {
                    summary.append("身高范围：${minHeight}cm - ${maxHeight}cm\n")
                }
            }
            "FMVSS 213", "CMVSS 213" -> {
                if (minWeight != null && maxWeight != null) {
                    summary.append("体重范围：${minWeight}kg - ${maxWeight}kg\n")
                }
            }
            else -> {
                if (minHeight != null && maxHeight != null) {
                    summary.append("身高范围：${minHeight}cm - ${maxHeight}cm\n")
                }
                if (minWeight != null && maxWeight != null) {
                    summary.append("体重范围：${minWeight}kg - ${maxWeight}kg\n")
                }
            }
        }

        if (!ageRange.isNullOrEmpty()) {
            summary.append("年龄范围：$ageRange\n")
        }

        if (!seatInstallationType.isNullOrEmpty()) {
            summary.append("安装方式：$seatInstallationType\n")
        }

        if (!vehicleType.isNullOrEmpty()) {
            summary.append("车辆类型：$vehicleType\n")
        }

        if (!additionalRequirements.isNullOrEmpty()) {
            summary.append("其他要求：$additionalRequirements\n")
        }

        return summary.toString().trim()
    }
}

/**
 * 验证结果
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
