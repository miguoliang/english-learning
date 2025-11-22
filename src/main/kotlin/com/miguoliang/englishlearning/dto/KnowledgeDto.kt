package com.miguoliang.englishlearning.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.miguoliang.englishlearning.model.Metadata

/**
 * DTO for Knowledge API responses.
 */
data class KnowledgeDto(
    val code: String,
    val name: String,
    val description: String?,
    val metadata: Map<String, Any>?
)

/**
 * Converts Knowledge entity to DTO.
 */
fun com.miguoliang.englishlearning.model.Knowledge.toDto(): KnowledgeDto {
    return KnowledgeDto(
        code = this.code,
        name = this.name,
        description = this.description,
        metadata = this.metadata?.let { metadata ->
            val map = mutableMapOf<String, Any>()
            metadata.level?.let { map["level"] = it }
            map
        }
    )
}

