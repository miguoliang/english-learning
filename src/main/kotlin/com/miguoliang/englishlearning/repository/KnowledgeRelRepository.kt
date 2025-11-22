package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.KnowledgeRel
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface KnowledgeRelRepository : CoroutineCrudRepository<KnowledgeRel, Long> {
    fun findBySourceKnowledgeCode(sourceKnowledgeCode: String): Flow<KnowledgeRel>

    fun findByTargetKnowledgeCode(targetKnowledgeCode: String): Flow<KnowledgeRel>

    suspend fun findBySourceKnowledgeCodeAndTargetKnowledgeCode(
        sourceKnowledgeCode: String,
        targetKnowledgeCode: String,
    ): KnowledgeRel?
}
