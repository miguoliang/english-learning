package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.TranslationMessage
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TranslationMessageRepository : CoroutineCrudRepository<TranslationMessage, String> {
    fun findByTranslationKeyCode(translationKeyCode: String): Flow<TranslationMessage>

    suspend fun findByTranslationKeyCodeAndLocaleCode(
        translationKeyCode: String,
        localeCode: String,
    ): TranslationMessage?

    fun findByLocaleCode(localeCode: String): Flow<TranslationMessage>
}
