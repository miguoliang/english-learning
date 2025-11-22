package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.CardType
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CardTypeRepository : CoroutineCrudRepository<CardType, String> {
    suspend fun findByCode(code: String): CardType?

    suspend fun findByName(name: String): CardType?

    @Query("SELECT * FROM card_types WHERE code IN (:codes)")
    fun findByCodeIn(codes: Collection<String>): Flow<CardType>
}
