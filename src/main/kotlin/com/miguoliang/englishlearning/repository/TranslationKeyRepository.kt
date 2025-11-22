package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.TranslationKey
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TranslationKeyRepository : PanacheRepositoryBase<TranslationKey, String> {

    fun findByKey(key: String): Uni<TranslationKey?> {
        return find("key", key).firstResult()
    }
}
