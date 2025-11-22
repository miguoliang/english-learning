package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.common.Pageable
import com.miguoliang.englishlearning.model.AccountCard
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.quarkus.panache.common.Page
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime

@ApplicationScoped
class AccountCardRepository : PanacheRepository<AccountCard> {

    fun findByAccountId(accountId: Long): Multi<AccountCard> {
        return find("accountId", accountId).stream()
    }

    fun findDueCardsByAccountId(
        accountId: Long,
        date: LocalDateTime,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and nextReviewDate <= ?2 order by nextReviewDate",
            accountId,
            date,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countDueCardsByAccountId(
        accountId: Long,
        date: LocalDateTime,
    ): Uni<Long> {
        return count("accountId = ?1 and nextReviewDate <= ?2", accountId, date)
    }

    fun findDueCardsByAccountIdAndCardTypeCode(
        accountId: Long,
        date: LocalDateTime,
        cardTypeCode: String,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and nextReviewDate <= ?2 and cardTypeCode = ?3 order by nextReviewDate",
            accountId,
            date,
            cardTypeCode,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countDueCardsByAccountIdAndCardTypeCode(
        accountId: Long,
        date: LocalDateTime,
        cardTypeCode: String,
    ): Uni<Long> {
        return count(
            "accountId = ?1 and nextReviewDate <= ?2 and cardTypeCode = ?3",
            accountId,
            date,
            cardTypeCode,
        )
    }

    fun findByAccountIdAndKnowledgeCodeAndCardTypeCode(
        accountId: Long,
        knowledgeCode: String,
        cardTypeCode: String,
    ): Uni<AccountCard?> {
        return find(
            "accountId = ?1 and knowledgeCode = ?2 and cardTypeCode = ?3",
            accountId,
            knowledgeCode,
            cardTypeCode,
        ).firstResult()
    }

    fun findByAccountId(
        accountId: Long,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find("accountId = ?1 order by id", accountId)
            .page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountId(accountId: Long): Uni<Long> {
        return count("accountId", accountId)
    }

    fun findByAccountIdAndCardTypeCode(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 order by id",
            accountId,
            cardTypeCode,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndCardTypeCode(
        accountId: Long,
        cardTypeCode: String,
    ): Uni<Long> {
        return count("accountId = ?1 and cardTypeCode = ?2", accountId, cardTypeCode)
    }

    fun findByAccountIdAndStatusNew(
        accountId: Long,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find("accountId = ?1 and repetitions = 0 order by id", accountId)
            .page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndStatusNew(accountId: Long): Uni<Long> {
        return count("accountId = ?1 and repetitions = 0", accountId)
    }

    fun findByAccountIdAndStatusLearning(
        accountId: Long,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and repetitions > 0 and repetitions < 3 order by id",
            accountId,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndStatusLearning(accountId: Long): Uni<Long> {
        return count("accountId = ?1 and repetitions > 0 and repetitions < 3", accountId)
    }

    fun findByAccountIdAndStatusReview(
        accountId: Long,
        date: LocalDateTime,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and nextReviewDate <= ?2 order by id",
            accountId,
            date,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndStatusReview(
        accountId: Long,
        date: LocalDateTime,
    ): Uni<Long> {
        return count("accountId = ?1 and nextReviewDate <= ?2", accountId, date)
    }

    fun findByAccountIdAndCardTypeCodeAndStatusNew(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions = 0 order by id",
            accountId,
            cardTypeCode,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndCardTypeCodeAndStatusNew(
        accountId: Long,
        cardTypeCode: String,
    ): Uni<Long> {
        return count(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions = 0",
            accountId,
            cardTypeCode,
        )
    }

    fun findByAccountIdAndCardTypeCodeAndStatusLearning(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions > 0 and repetitions < 3 order by id",
            accountId,
            cardTypeCode,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndCardTypeCodeAndStatusLearning(
        accountId: Long,
        cardTypeCode: String,
    ): Uni<Long> {
        return count(
            "accountId = ?1 and cardTypeCode = ?2 and repetitions > 0 and repetitions < 3",
            accountId,
            cardTypeCode,
        )
    }

    fun findByAccountIdAndCardTypeCodeAndStatusReview(
        accountId: Long,
        cardTypeCode: String,
        date: LocalDateTime,
        pageable: Pageable,
    ): Multi<AccountCard> {
        return find(
            "accountId = ?1 and cardTypeCode = ?2 and nextReviewDate <= ?3 order by id",
            accountId,
            cardTypeCode,
            date,
        ).stream().page(Page.of(pageable.page, pageable.size))
    }

    fun countByAccountIdAndCardTypeCodeAndStatusReview(
        accountId: Long,
        cardTypeCode: String,
        date: LocalDateTime,
    ): Uni<Long> {
        return count(
            "accountId = ?1 and cardTypeCode = ?2 and nextReviewDate <= ?3",
            accountId,
            cardTypeCode,
            date,
        )
    }
}
