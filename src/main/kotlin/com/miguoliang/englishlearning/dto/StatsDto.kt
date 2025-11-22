package com.miguoliang.englishlearning.dto

/**
 * DTO for Statistics API responses.
 */
data class StatsDto(
    val totalCards: Long,
    val newCards: Long,
    val learningCards: Long,
    val dueToday: Long,
    val byCardType: Map<String, Long>,
)
