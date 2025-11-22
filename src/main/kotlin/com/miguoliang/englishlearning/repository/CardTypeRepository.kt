package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.CardType
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CardTypeRepository : PanacheRepositoryBase<CardType, String> {

    suspend fun streamAll(): List<CardType> {
        return findAll().list().awaitSuspending()
    }

    suspend fun findByCode(code: String): CardType? {
        return findById(code).awaitSuspending()
    }

    suspend fun findByName(name: String): CardType? {
        return find("name", name).firstResult().awaitSuspending()
    }

    suspend fun findByCodeIn(codes: Collection<String>): List<CardType> {
        return find("code in ?1", codes).list().awaitSuspending()
    }
}
