package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.common.Pageable
import com.miguoliang.englishlearning.model.Knowledge
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Page
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class KnowledgeRepository : PanacheRepositoryBase<Knowledge, String> {

    fun findAllOrderedByCode(pageable: Pageable): Multi<Knowledge> {
        return findAll().stream()
            .page(Page.of(pageable.page, pageable.size))
    }

    fun findByCode(code: String): Uni<Knowledge?> {
        return findById(code)
    }

    fun findByCodeIn(codes: Collection<String>): Multi<Knowledge> {
        return find("code in ?1", codes).stream()
    }
}
