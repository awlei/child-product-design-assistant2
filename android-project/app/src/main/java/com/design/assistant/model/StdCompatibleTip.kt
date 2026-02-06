package com.design.assistant.model

/**
 * 多标准兼容设计建议（专业工程师版）
 * 同时选择多个标准时自动生成，解决跨标准设计的差异点/通用点，直接用于方案评审
 */
data class StdCompatibleTip(
    val stdCombination: String,         // 选中的标准组合（如ECE R129+GB 27887-2024）
    val commonPoints: List<String>,     // 标准通用点（无需额外调整，直接兼容）
    val diffPoints: List<String>,       // 标准差异点（需针对性设计，满足最严要求）
    val designSuggest: List<String>     // 兼容设计建议（工程师可直接采纳）
)
