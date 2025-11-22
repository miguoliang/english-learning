package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.AccountCard
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AccountCardRepository : CoroutineCrudRepository<AccountCard, Long> {
    fun findByAccountId(accountId: Long): Flow<AccountCard>

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND next_review_date <= :date ORDER BY next_review_date OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findDueCardsByAccountId(
        accountId: Long,
        date: LocalDateTime,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query("SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND next_review_date <= :date")
    suspend fun countDueCardsByAccountId(
        accountId: Long,
        date: LocalDateTime,
    ): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND next_review_date <= :date AND card_type_code = :cardTypeCode ORDER BY next_review_date OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findDueCardsByAccountIdAndCardTypeCode(
        accountId: Long,
        date: LocalDateTime,
        cardTypeCode: String,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query(
        "SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND next_review_date <= :date AND card_type_code = :cardTypeCode",
    )
    suspend fun countDueCardsByAccountIdAndCardTypeCode(
        accountId: Long,
        date: LocalDateTime,
        cardTypeCode: String,
    ): Long

    suspend fun findByAccountIdAndKnowledgeCodeAndCardTypeCode(
        accountId: Long,
        knowledgeCode: String,
        cardTypeCode: String,
    ): AccountCard?

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountId(
        accountId: Long,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query("SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId")
    suspend fun countByAccountId(accountId: Long): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndCardTypeCode(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query("SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode")
    suspend fun countByAccountIdAndCardTypeCode(
        accountId: Long,
        cardTypeCode: String,
    ): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND repetitions = 0 ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndStatusNew(
        accountId: Long,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query("SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND repetitions = 0")
    suspend fun countByAccountIdAndStatusNew(accountId: Long): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND repetitions > 0 AND repetitions < 3 ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndStatusLearning(
        accountId: Long,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query("SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND repetitions > 0 AND repetitions < 3")
    suspend fun countByAccountIdAndStatusLearning(accountId: Long): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND next_review_date <= :date ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndStatusReview(
        accountId: Long,
        date: LocalDateTime,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query("SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND next_review_date <= :date")
    suspend fun countByAccountIdAndStatusReview(
        accountId: Long,
        date: LocalDateTime,
    ): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode AND repetitions = 0 ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndCardTypeCodeAndStatusNew(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query(
        "SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode AND repetitions = 0",
    )
    suspend fun countByAccountIdAndCardTypeCodeAndStatusNew(
        accountId: Long,
        cardTypeCode: String,
    ): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode AND repetitions > 0 AND repetitions < 3 ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndCardTypeCodeAndStatusLearning(
        accountId: Long,
        cardTypeCode: String,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query(
        "SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode AND repetitions > 0 AND repetitions < 3",
    )
    suspend fun countByAccountIdAndCardTypeCodeAndStatusLearning(
        accountId: Long,
        cardTypeCode: String,
    ): Long

    @Query(
        "SELECT * FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode AND next_review_date <= :date ORDER BY id OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}",
    )
    fun findByAccountIdAndCardTypeCodeAndStatusReview(
        accountId: Long,
        cardTypeCode: String,
        date: LocalDateTime,
        pageable: Pageable,
    ): Flow<AccountCard>

    @Query(
        "SELECT COUNT(*) FROM account_cards WHERE account_id = :accountId AND card_type_code = :cardTypeCode AND next_review_date <= :date",
    )
    suspend fun countByAccountIdAndCardTypeCodeAndStatusReview(
        accountId: Long,
        cardTypeCode: String,
        date: LocalDateTime,
    ): Long
}
