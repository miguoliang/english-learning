package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.model.CardType
import com.miguoliang.englishlearning.repository.CardTypeRepository
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

/**
 * Manages card type operations.
 */
@ApplicationScoped
class CardTypeService(
    private val cardTypeRepository: CardTypeRepository,
) {
    /**
     * List all card types.
     * Note: Template references are not loaded here. Use CardTemplateService to render templates for specific roles.
     *
     * @return Multi of all card types
     */
    fun getAllCardTypes(): Multi<CardType> = cardTypeRepository.streamAll()

    /**
     * Get single card type by code.
     * Note: Template references are not loaded here. Use CardTemplateService to render templates for specific roles.
     *
     * @param code Card type code identifier
     * @return Uni<CardType> or null if not found
     */
    fun getCardTypeByCode(code: String): Uni<CardType?> = cardTypeRepository.findByCode(code)

    /**
     * Batch load card types by codes.
     *
     * @param codes Collection of card type codes
     * @return Uni<Map> of code to CardType
     */
    fun getCardTypesByCodes(codes: Collection<String>): Uni<Map<String, CardType>> =
        if (codes.isEmpty()) {
            Uni.createFrom().item(emptyMap())
        } else {
            cardTypeRepository
                .findByCodeIn(codes)
                .collect().asList()
                .map { list ->
                    list.associateBy { cardType -> cardType.code }
                }
        }
}
