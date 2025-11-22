package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Template
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TemplateRepository : PanacheRepositoryBase<Template, String> {

    suspend fun streamAll(): List<Template> {
        return findAll().list().awaitSuspending()
    }

    suspend fun findByCode(code: String): Template? {
        return findById(code).awaitSuspending()
    }

    suspend fun findByName(name: String): Template? {
        return find("name", name).firstResult().awaitSuspending()
    }
}
