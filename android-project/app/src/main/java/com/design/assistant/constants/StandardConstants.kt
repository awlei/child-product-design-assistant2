package com.design.assistant.constants

import com.design.assistant.model.ProductType

/**
 * 全局标准常量：代码定义+显示名称+产品-标准映射
 * 新增标准/产品仅需修改此类，无其他侵入性修改
 */
object StandardConstants {
    // ============= 儿童安全座椅（出行类） =============
    const val ECE_R129 = "ECE_R129"          // 欧盟i-Size
    const val GB_27887_2024 = "GB_27887_2024"// 中国新标
    const val FMVSS_213 = "FMVSS_213"        // 美国标准
    const val AS_NZS_1754 = "AS_NZS_1754"    // 澳洲标准
    const val CMVSS_213 = "CMVSS_213"        // 加拿大标准

    // ============= 婴儿推车（出行类） =============
    const val EN_1888 = "EN_1888"            // 欧盟标准
    const val GB_14748 = "GB_14748"          // 中国标准
    const val ASTM_F833 = "ASTM_F833"        // 美国标准
    const val CAN_CSA_D425 = "CAN_CSA_D425"  // 加拿大标准

    // ============= 儿童高脚椅（家居类） =============
    const val EN_14988 = "EN_14988"          // 欧盟标准
    const val GB_29281 = "GB_29281"          // 中国标准
    const val CAN_CSA_Z217_1 = "CAN_CSA_Z217_1"// 加拿大标准

    // ============= 儿童床（家居类） =============
    const val EN_716 = "EN_716"              // 欧盟标准
    const val GB_28007 = "GB_28007"          // 中国标准
    const val CAN_CSA_D1169 = "CAN_CSA_D1169"// 加拿大标准

    /** 标准代码 → 中文显示名称（含地区） */
    fun getStandardName(standardCode: String): String {
        return when (standardCode) {
            // 儿童安全座椅
            ECE_R129 -> "ECE R129 (欧盟i-Size)"
            GB_27887_2024 -> "GB 27887-2024 (中国新标)"
            FMVSS_213 -> "FMVSS 213 (美国标准)"
            AS_NZS_1754 -> "AS/NZS 1754 (澳洲标准)"
            CMVSS_213 -> "CMVSS 213 (加拿大标准)"
            // 婴儿推车
            EN_1888 -> "EN 1888 (欧盟标准)"
            GB_14748 -> "GB 14748 (中国标准)"
            ASTM_F833 -> "ASTM F833 (美国标准)"
            CAN_CSA_D425 -> "CAN/CSA D425 (加拿大标准)"
            // 儿童高脚椅
            EN_14988 -> "EN 14988 (欧盟标准)"
            GB_29281 -> "GB 29281 (中国标准)"
            CAN_CSA_Z217_1 -> "CAN/CSA Z217.1 (加拿大标准)"
            // 儿童床
            EN_716 -> "EN 716 (欧盟标准)"
            GB_28007 -> "GB 28007 (中国标准)"
            CAN_CSA_D1169 -> "CAN/CSA D1169 (加拿大标准)"
            else -> "未知标准"
        }
    }

    /** 产品类型 → 对应支持的标准列表（自动关联） */
    fun getStandardsByProduct(productType: ProductType): List<String> {
        return when (productType) {
            ProductType.CHILD_SEAT -> listOf(ECE_R129, GB_27887_2024, FMVSS_213, AS_NZS_1754, CMVSS_213)
            ProductType.BABY_STROLLER -> listOf(EN_1888, GB_14748, ASTM_F833, CAN_CSA_D425)
            ProductType.HIGH_CHAIR -> listOf(EN_14988, GB_29281, CAN_CSA_Z217_1)
            ProductType.CHILD_BED -> listOf(EN_716, GB_28007, CAN_CSA_D1169)
        }
    }

    /** 标准代码 → 关联的数据库名称（用于日志/调试） */
    fun getDbNameByStandard(standardCode: String): String {
        return getStandardName(standardCode).split("(")[0].trim() + "_db"
    }
}
