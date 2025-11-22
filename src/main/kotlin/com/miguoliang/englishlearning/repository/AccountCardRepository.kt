package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.common.Pageable
import com.miguoliang.englishlearning.model.AccountCard
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Page
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime

@ApplicationScoped
class AccountCardRepository : PanacheRepositoryBase<AccountCard, Long> {

    suspend fun findByAccountId(accountId: Long): List<AccountCard> {
        return find("accountId", accountId).list<AccountCard>().awaitSuspending()
    }

    suspend fun findDueCardsByAccountId(
        accountId: Long,
        date: LocalDateTime,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and nextReviewDate <= ?2 order by nextReviewDate",
            accountId,
            date,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countDueCardsByAccountId(
        accountId: Long,
        date: LocalDateTime,
    ): Long {
        return count("accountId = ?1 and nextReviewDate <= ?2", accountId, date).awaitSuspending()
    }

    suspend fun findDueCardsByAccountIdAndCardTypeCode(
        accountId: Long,
        date: LocalDateTime,
        cardTypeCode: String,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and nextReviewDate <= ?2 and cardTypeCode = ?3 order by nextReviewDate",
            accountId,
            date,
            cardTypeCode,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countDueCardsByAccountIdAndCardTypeCode(
        accountId: Long,
        date: LocalDateTime,
        cardTypeCode: String,
    ): Long {
        return count(
            "accountId = ?1 and nextReviewDate <= ?2 and cardTypeCode = ?3",
            accountId,
            date,
            cardTypeCode,
        ).awaitSuspending()
    }

    suspend fun findByAccountIdAndKnowledgeCodeAndCardTypeCode(
        accountId: Long,
        knowledgeCode: String,
        cardTypeCode: String,
    ): AccountCard? {
        return find(
            "accountId = ?1 and knowledgeCode = ?2 and cardTypeCode = ?3",
            accountId,
            knowledgeCode,
            cardTypeCode,
        ).firstResult<AccountCard>().awaitSuspending()
    }

    suspend fun findByAccountId(
        accountId: Long,
        pageable: Pageable,
    ): List<AccountCard> {
        return find("accountId = ?1 order by id", accountId)
            .page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountId(accountId: Long): Long {
        return count("accountId", accountId).awaitSuspending()
    }

    suspend fun findByAccountIdAndCardTypeCode(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 order by id",
            accountId,
            cardTypeCode,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndCardTypeCode(
        accountId: Long,
        cardTypeCode: String,
    ): Long {
        return count("accountId = ?1 and cardTypeCode = ?2", accountId, cardTypeCode).awaitSuspending()
    }

    suspend fun findByAccountIdAndStatusNew(
        accountId: Long,
        pageable: Pageable,
    ): List<AccountCard> {
        return find("accountId = ?1 and repetitions = 0 order by id", accountId)
            .page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndStatusNew(accountId: Long): Long {
        return count("accountId = ?1 and repetitions = 0", accountId).awaitSuspending()
    }

    suspend fun findByAccountIdAndStatusLearning(
        accountId: Long,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and repetitions > 0 and repetitions < 3 order by id",
            accountId,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndStatusLearning(accountId: Long): Long {
        return count("accountId = ?1 and repetitions > 0 and repetitions < 3", accountId).awaitSuspending()
    }

    suspend fun findByAccountIdAndStatusReview(
        accountId: Long,
        date: LocalDateTime,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and nextReviewDate <= ?2 order by id",
            accountId,
            date,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndStatusReview(
        accountId: Long,
        date: LocalDateTime,
    ): Long {
        return count("accountId = ?1 and nextReviewDate <= ?2", accountId, date).awaitSuspending()
    }

    suspend fun findByAccountIdAndCardTypeCodeAndStatusNew(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions = 0 order by id",
            accountId,
            cardTypeCode,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndCardTypeCodeAndStatusNew(
        accountId: Long,
        cardTypeCode: String,
    ): Long {
        return count(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions = 0",
            accountId,
            cardTypeCode,
        ).awaitSuspending()
    }

    suspend fun findByAccountIdAndCardTypeCodeAndStatusLearning(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions > 0 and repetitions < 3 order by id",
            accountId,
            cardTypeCode,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndCardTypeCodeAndStatusLearning(
        accountId: Long,
        cardTypeCode: String,
    ): Long {
        return count(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions > 0 and repetitions < 3",
            accountId,
            cardTypeCode,
        ).awaitSuspending()
    }

    suspend fun findByAccountIdAndCardTypeCodeAndStatusReview(
        accountId: Long,
        cardTypeCode: String,
        date: LocalDateTime,
        pageable: Pageable,
    ): List<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 and nextReviewDate <= ?3 order by id",
            accountId,
            cardTypeCode,
            date,
        ).page<AccountCard>(Page.of(pageable.page, pageable.size)).list<AccountCard>().awaitSuspending()
    }

    suspend fun countByAccountIdAndCardTypeCodeAndStatusReview(
        accountId: Long,
        cardTypeCode: String,
        date: LocalDateTime,
    ): Long {
        return count(
            "accountId = ?1 and cardTypeCode = ?2 and nextReviewDate <= ?3",
            accountId,
            cardTypeCode,
            date,
        ).awaitSuspending()
    }
}
