package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.CardType
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CardTypeRepository : PanacheRepositoryBase<CardType, String> {

    fun streamAll(): Multi<CardType> {
        return findAll().stream()
    }

    fun findByCode(code: String): Uni<CardType?> {
        return findById(code)
    }

    fun findByName(name: String): Uni<CardType?> {
        return find("name", name).firstResult()
    }

    fun findByCodeIn(codes: Collection<String>): Multi<CardType> {
        return find("code in ?1", codes).stream()
    }
}
