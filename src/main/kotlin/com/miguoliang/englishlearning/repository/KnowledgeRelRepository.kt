package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.KnowledgeRel
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class KnowledgeRelRepository : PanacheRepositoryBase<KnowledgeRel, Long> {

    suspend fun findBySourceKnowledgeCode(sourceKnowledgeCode: String): List<KnowledgeRel> {
        return find("sourceKnowledgeCode", sourceKnowledgeCode).list().awaitSuspending()
    }

    suspend fun findByTargetKnowledgeCode(targetKnowledgeCode: String): List<KnowledgeRel> {
        return find("targetKnowledgeCode", targetKnowledgeCode).list().awaitSuspending()
    }

    suspend fun findBySourceKnowledgeCodeAndTargetKnowledgeCode(
        sourceKnowledgeCode: String,
        targetKnowledgeCode: String,
    ): KnowledgeRel? {
        return find(
            "sourceKnowledgeCode = ?1 and targetKnowledgeCode = ?2",
            sourceKnowledgeCode,
            targetKnowledgeCode,
        ).firstResult().awaitSuspending()
    }
}
