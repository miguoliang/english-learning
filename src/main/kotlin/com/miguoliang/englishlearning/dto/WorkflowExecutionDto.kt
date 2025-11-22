package com.miguoliang.englishlearning.dto

import java.time.Instant

/**
 * DTO for workflow execution creation responses.
 */
data class WorkflowExecutionDto(
    val workflowId: String,
    val workflowExecutionId: String,
    val runId: String,
    val status: String,
    val startedAt: Instant,
)
