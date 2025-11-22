package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Template
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TemplateRepository : PanacheRepositoryBase<Template, String> {

    fun streamAll(): Multi<Template> {
        return findAll().stream()
    }

    fun findByCode(code: String): Uni<Template?> {
        return findById(code)
    }

    fun findByName(name: String): Uni<Template?> {
        return find("name", name).firstResult()
    }
}
