package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.model.CardType
import com.miguoliang.englishlearning.repository.CardTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

/**
 * Manages card type operations.
 */
@Service
class CardTypeService(
    private val cardTypeRepository: CardTypeRepository,
) {
    /**
     * List all card types.
     * Note: Template references are not loaded here. Use CardTemplateService to render templates for specific roles.
     *
     * @return Flow of all card types
     */
    fun getAllCardTypes(): Flow<CardType> = cardTypeRepository.findAll()

    /**
     * Get single card type by code.
     * Note: Template references are not loaded here. Use CardTemplateService to render templates for specific roles.
     *
     * @param code Card type code identifier
     * @return CardType or null if not found
     */
    suspend fun getCardTypeByCode(code: String): CardType? = cardTypeRepository.findByCode(code)

    /**
     * Batch load card types by codes.
     *
     * @param codes Collection of card type codes
     * @return Map of code to CardType
     */
    suspend fun getCardTypesByCodes(codes: Collection<String>): Map<String, CardType> =
        codes.takeIf { it.isNotEmpty() }
            ?.let {
                cardTypeRepository
                    .findByCodeIn(it)
                    .toList()
                    .associateBy { cardType -> cardType.code }
            }
            ?: emptyMap()
}
