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
            ResponseEntity.ok(statsService.getStats(accountId))
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StatsDto(0L, 0L, 0L, 0L, emptyMap()))
        }
    }
}
