package com.design.assistant.model

/**
 * 产品类型枚举（关联所属品类）
 * 新增产品 → 加枚举项 + 指定品类即可
 */
enum class ProductType(val typeName: String, val category: ProductCategory) {
    // 出行类
    CHILD_SEAT("儿童安全座椅", ProductCategory.TRAVEL),
    BABY_STROLLER("婴儿推车", ProductCategory.TRAVEL),
    // 家居类
    HIGH_CHAIR("儿童高脚椅", ProductCategory.HOME),
    CHILD_BED("儿童床", ProductCategory.HOME);

    /** 产品大品类（出行/家居） */
    enum class ProductCategory(val categoryName: String) {
        TRAVEL("出行类"),
        HOME("家居类")
    }
}
