package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.TranslationKey
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TranslationKeyRepository : CoroutineCrudRepository<TranslationKey, String> {
    suspend fun findByKey(key: String): TranslationKey?
}
