package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.common.Pageable
import com.miguoliang.englishlearning.model.Knowledge
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Page
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class KnowledgeRepository : PanacheRepositoryBase<Knowledge, String> {

    suspend fun findAllOrderedByCode(pageable: Pageable): List<Knowledge> {
        return findAll()
            .page(Page.of(pageable.page, pageable.size)).list().awaitSuspending()
    }

    suspend fun findByCode(code: String): Knowledge? {
        return findById(code).awaitSuspending()
    }

    suspend fun findByCodeIn(codes: Collection<String>): List<Knowledge> {
        return find("code in ?1", codes).list().awaitSuspending()
    }

    suspend fun countAll(): Long {
        return count().awaitSuspending()
    }
}
