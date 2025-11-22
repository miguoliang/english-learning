package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.dto.StatsDto
import com.miguoliang.englishlearning.repository.AccountCardRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime

/**
 * Calculates account statistics.
 */
@ApplicationScoped
class StatsService(
    private val accountCardRepository: AccountCardRepository,
) {
    /**
     * Get comprehensive learning statistics for an account.
     *
     * @param accountId Account ID
     * @return StatsDto with statistics
     */
    suspend fun getStats(accountId: Long): StatsDto {
        val now = LocalDateTime.now()

        // Fetch all cards once and compute statistics in memory to avoid N+1
        val allCards = accountCardRepository.findByAccountId(accountId)

        return if (allCards.isEmpty()) {
            StatsDto(
                totalCards = 0L,
                newCards = 0L,
                learningCards = 0L,
                dueToday = 0L,
                byCardType = emptyMap(),
            )
        } else {
            StatsDto(
                totalCards = allCards.size.toLong(),
                newCards = allCards.count { it.repetitions == 0 }.toLong(),
                learningCards = allCards.count { it.repetitions > 0 && it.repetitions < 3 }.toLong(),
                dueToday = allCards.count { it.nextReviewDate <= now }.toLong(),
                byCardType = allCards
                    .groupingBy { it.cardTypeCode }
                    .eachCount()
                    .mapValues { it.value.toLong() },
            )
        }
    }
}
