package com.miguoliang.englishlearning.dto

import java.time.Instant

/**
 * Response DTO for workflow cancellation.
 */
data class WorkflowCancelResponseDto(
    val workflowId: String,
    val canceled: Boolean,
    val timestamp: Instant,
)
