package com.miguoliang.englishlearning.dto

import java.time.Instant

/**
 * DTO for workflow status responses.
 */
data class WorkflowStatusDto(
    val workflowId: String,
    val workflowType: String,
    val status: String,
    val startedAt: Instant?,
    val completedAt: Instant?,
    val result: Map<String, Any>? = null,
    val error: String? = null,
)
