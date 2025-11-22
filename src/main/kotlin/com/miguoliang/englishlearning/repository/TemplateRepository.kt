package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Template
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : CoroutineCrudRepository<Template, String> {
    suspend fun findByCode(code: String): Template?

    suspend fun findByName(name: String): Template?
}
