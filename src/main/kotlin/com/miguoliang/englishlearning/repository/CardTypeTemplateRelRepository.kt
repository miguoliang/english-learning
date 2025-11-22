package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.CardTypeTemplateRel
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CardTypeTemplateRelRepository : PanacheRepositoryBase<CardTypeTemplateRel, Long> {

    suspend fun findByCardTypeCode(cardTypeCode: String): List<CardTypeTemplateRel> {
        return find("cardTypeCode", cardTypeCode).list().awaitSuspending()
    }

    suspend fun findByTemplateCode(templateCode: String): List<CardTypeTemplateRel> {
        return find("templateCode", templateCode).list().awaitSuspending()
    }

    suspend fun findByCardTypeCodeAndRole(
        cardTypeCode: String,
        role: String,
    ): CardTypeTemplateRel? {
        return find("cardTypeCode = ?1 and role = ?2", cardTypeCode, role).firstResult().awaitSuspending()
    }
}
