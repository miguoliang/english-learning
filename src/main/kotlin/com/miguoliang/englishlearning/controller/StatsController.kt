package com.miguoliang.englishlearning.controller

import com.miguoliang.englishlearning.dto.StatsDto
import com.miguoliang.englishlearning.service.StatsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Statistics endpoints.
 * Access: client role (for /me endpoints).
 *
 * Note: JWT authentication is not yet implemented. For MVP, accountId is extracted from path.
 * TODO: Extract accountId from JWT token 'sub' claim for /me endpoints.
 */
@RestController
@RequestMapping("/api/v1/accounts")
class StatsController(
    private val statsService: StatsService,
) {
    /**
     * Get learning statistics for current account.
     * GET /api/v1/accounts/me/stats
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @GetMapping("/me/stats")
    suspend fun getStats(): ResponseEntity<StatsDto> {
        // TODO: Extract accountId from JWT token
        val accountId = 1L // Placeholder

        return try {
            val statsMap = statsService.getStats(accountId)

            @Suppress("UNCHECKED_CAST")
            val statsDto =
                StatsDto(
                    totalCards = (statsMap["totalCards"] as? Number)?.toLong() ?: 0L,
                    newCards = (statsMap["newCards"] as? Number)?.toLong() ?: 0L,
                    learningCards = (statsMap["learningCards"] as? Number)?.toLong() ?: 0L,
                    dueToday = (statsMap["dueToday"] as? Number)?.toLong() ?: 0L,
                    byCardType = (statsMap["byCardType"] as? Map<String, Long>) ?: emptyMap(),
                )
            ResponseEntity.ok(statsDto)
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StatsDto(0L, 0L, 0L, 0L, emptyMap()))
        }
    }
}
