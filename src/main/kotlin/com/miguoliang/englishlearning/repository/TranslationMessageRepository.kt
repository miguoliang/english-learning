package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.TranslationMessage
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TranslationMessageRepository : PanacheRepositoryBase<TranslationMessage, String> {

    suspend fun findByTranslationKeyCode(translationKeyCode: String): List<TranslationMessage> {
        return find("translationKeyCode", translationKeyCode).list().awaitSuspending()
    }

    suspend fun findByTranslationKeyCodeAndLocaleCode(
        translationKeyCode: String,
        localeCode: String,
    ): TranslationMessage? {
        return find(
            "translationKeyCode = ?1 and localeCode = ?2",
            translationKeyCode,
            localeCode,
        ).firstResult().awaitSuspending()
    }

    suspend fun findByLocaleCode(localeCode: String): List<TranslationMessage> {
        return find("localeCode", localeCode).list().awaitSuspending()
    }
}
