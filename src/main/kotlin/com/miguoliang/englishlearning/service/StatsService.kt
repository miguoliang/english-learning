package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.repository.AccountCardRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Calculates account statistics.
 */
@Service
class StatsService(
    private val accountCardRepository: AccountCardRepository,
) {
    /**
     * Get comprehensive learning statistics for an account.
     *
     * @param accountId Account ID
     * @return Statistics map
     */
    suspend fun getStats(accountId: Long): Map<String, Any> {
        val now = LocalDateTime.now()

        // Fetch all cards once and compute statistics in memory to avoid N+1
        val allCards =
            accountCardRepository
                .findByAccountId(accountId)
                .toList()

        if (allCards.isEmpty()) {
            return mapOf(
                "totalCards" to 0L,
                "newCards" to 0L,
                "learningCards" to 0L,
                "dueToday" to 0L,
                "byCardType" to emptyMap<String, Long>(),
            )
        }

        val totalCards = allCards.size.toLong()
        val newCards = allCards.count { it.repetitions == 0 }.toLong()
        val learningCards = allCards.count { it.repetitions > 0 && it.repetitions < 3 }.toLong()
        val dueToday = allCards.count { it.nextReviewDate <= now }.toLong()
        val byCardType =
            allCards
                .groupingBy { it.cardTypeCode }
                .eachCount()
                .mapValues { it.value.toLong() }

        return mapOf(
            "totalCards" to totalCards,
            "newCards" to newCards,
            "learningCards" to learningCards,
            "dueToday" to dueToday,
            "byCardType" to byCardType,
        )
    }
}
