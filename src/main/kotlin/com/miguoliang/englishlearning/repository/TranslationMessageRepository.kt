package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.TranslationMessage
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TranslationMessageRepository : PanacheRepositoryBase<TranslationMessage, String> {

    fun findByTranslationKeyCode(translationKeyCode: String): Multi<TranslationMessage> {
        return find("translationKeyCode", translationKeyCode).stream()
    }

    fun findByTranslationKeyCodeAndLocaleCode(
        translationKeyCode: String,
        localeCode: String,
    ): Uni<TranslationMessage?> {
        return find(
            "translationKeyCode = ?1 and localeCode = ?2",
            translationKeyCode,
            localeCode,
        ).firstResult()
    }

    fun findByLocaleCode(localeCode: String): Multi<TranslationMessage> {
        return find("localeCode", localeCode).stream()
    }
}
