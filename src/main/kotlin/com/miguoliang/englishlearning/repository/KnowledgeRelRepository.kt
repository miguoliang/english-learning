package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.KnowledgeRel
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class KnowledgeRelRepository : PanacheRepository<KnowledgeRel> {

    fun findBySourceKnowledgeCode(sourceKnowledgeCode: String): Multi<KnowledgeRel> {
        return find("sourceKnowledgeCode", sourceKnowledgeCode).stream()
    }

    fun findByTargetKnowledgeCode(targetKnowledgeCode: String): Multi<KnowledgeRel> {
        return find("targetKnowledgeCode", targetKnowledgeCode).stream()
    }

    fun findBySourceKnowledgeCodeAndTargetKnowledgeCode(
        sourceKnowledgeCode: String,
        targetKnowledgeCode: String,
    ): Uni<KnowledgeRel?> {
        return find(
            "sourceKnowledgeCode = ?1 and targetKnowledgeCode = ?2",
            sourceKnowledgeCode,
            targetKnowledgeCode,
        ).firstResult()
    }
}
