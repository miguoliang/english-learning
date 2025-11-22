package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.CardTypeTemplateRel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CardTypeTemplateRelRepository : CoroutineCrudRepository<CardTypeTemplateRel, Long> {
    fun findByCardTypeCode(cardTypeCode: String): Flow<CardTypeTemplateRel>

    fun findByTemplateCode(templateCode: String): Flow<CardTypeTemplateRel>

    suspend fun findByCardTypeCodeAndRole(
        cardTypeCode: String,
        role: String,
    ): CardTypeTemplateRel?
}
