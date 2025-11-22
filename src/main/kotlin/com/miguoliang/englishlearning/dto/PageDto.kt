package com.miguoliang.englishlearning.dto

/**
 * DTO wrapper for paginated responses.
 * Provides consistent pagination structure matching API specification.
 */
data class PageDto<T>(
    val content: List<T>,
    val page: PageInfoDto,
)

/**
 * Page information DTO.
 */
data class PageInfoDto(
    val number: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
