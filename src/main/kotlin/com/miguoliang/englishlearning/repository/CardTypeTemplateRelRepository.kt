package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.CardTypeTemplateRel
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CardTypeTemplateRelRepository : PanacheRepository<CardTypeTemplateRel> {

    fun findByCardTypeCode(cardTypeCode: String): Multi<CardTypeTemplateRel> {
        return find("cardTypeCode", cardTypeCode).stream()
    }

    fun findByTemplateCode(templateCode: String): Multi<CardTypeTemplateRel> {
        return find("templateCode", templateCode).stream()
    }

    fun findByCardTypeCodeAndRole(
        cardTypeCode: String,
        role: String,
    ): Uni<CardTypeTemplateRel?> {
        return find("cardTypeCode = ?1 and role = ?2", cardTypeCode, role).firstResult()
    }
}
