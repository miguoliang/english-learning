package com.miguoliang.englishlearning.controller

import com.miguoliang.englishlearning.dto.*
import com.miguoliang.englishlearning.dto.PageInfoDto
import com.miguoliang.englishlearning.service.AccountCardService
import com.miguoliang.englishlearning.service.CardTemplateService
import com.miguoliang.englishlearning.service.CardTypeService
import com.miguoliang.englishlearning.service.KnowledgeService
import jakarta.validation.Valid
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Account Card endpoints.
 * Access: client role (for /me endpoints) or operator role (for {accountId} endpoints).
 *
 * Note: JWT authentication is not yet implemented. For MVP, accountId is passed as path parameter.
 * TODO: Extract accountId from JWT token 'sub' claim for /me endpoints.
 */
@RestController
@RequestMapping("/api/v1/accounts")
class AccountCardController(
    private val accountCardService: AccountCardService,
    private val knowledgeService: KnowledgeService,
    private val cardTypeService: CardTypeService,
    private val cardTemplateService: CardTemplateService,
) {
    /**
     * List current account's cards with optional filtering.
     * GET /api/v1/accounts/me/cards
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @GetMapping("/me/cards")
    suspend fun listMyCards(
        @RequestParam(required = false) card_type_code: String?,
        @RequestParam(required = false) status: String?,
        pageable: Pageable,
    ): ResponseEntity<PageDto<AccountCardDto>> {
        // TODO: Extract accountId from JWT token
        val accountId = 1L // Placeholder

        return try {
            val page = accountCardService.getAccountCards(accountId, pageable, card_type_code, status)

            // Batch load knowledge and card types to avoid N+1
            val knowledgeCodes = page.content.map { it.knowledgeCode }.distinct()
            val cardTypeCodes = page.content.map { it.cardTypeCode }.distinct()

            val (knowledgeMap, cardTypeMap) =
                coroutineScope {
                    val knowledgeDeferred = async { knowledgeService.getKnowledgeByCodes(knowledgeCodes) }
                    val cardTypeDeferred = async { cardTypeService.getCardTypesByCodes(cardTypeCodes) }
                    Pair(knowledgeDeferred.await(), cardTypeDeferred.await())
                }

            val dtos =
                page.content.mapNotNull { card ->
                    val knowledge = knowledgeMap[card.knowledgeCode]
                    val cardType = cardTypeMap[card.cardTypeCode]
                    if (knowledge != null && cardType != null) {
                        card.toDto(
                            knowledge = knowledge.toDto(),
                            cardType = cardType.toDto(),
                        )
                    } else {
                        null
                    }
                }

            val pageDto =
                PageDto(
                    content = dtos,
                    page =
                        PageInfoDto(
                            number = page.number,
                            size = page.size,
                            totalElements = page.totalElements,
                            totalPages = page.totalPages,
                        ),
                )
            ResponseEntity.ok(pageDto)
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PageDto(emptyList(), PageInfoDto(0, 0, 0, 0)))
        }
    }

    /**
     * List specific account's cards (operator access).
     * GET /api/v1/accounts/{accountId}/cards
     * TODO: Validate operator role from JWT token
     */
    @GetMapping("/{accountId}/cards")
    suspend fun listAccountCards(
        @PathVariable accountId: Long,
        @RequestParam(required = false) card_type_code: String?,
        @RequestParam(required = false) status: String?,
        pageable: Pageable,
    ): ResponseEntity<PageDto<AccountCardDto>> =
        try {
            val page = accountCardService.getAccountCards(accountId, pageable, card_type_code, status)

            // Batch load knowledge and card types to avoid N+1
            val knowledgeCodes = page.content.map { it.knowledgeCode }.distinct()
            val cardTypeCodes = page.content.map { it.cardTypeCode }.distinct()

            val (knowledgeMap, cardTypeMap) =
                coroutineScope {
                    val knowledgeDeferred = async { knowledgeService.getKnowledgeByCodes(knowledgeCodes) }
                    val cardTypeDeferred = async { cardTypeService.getCardTypesByCodes(cardTypeCodes) }
                    Pair(knowledgeDeferred.await(), cardTypeDeferred.await())
                }

            val dtos =
                page.content.mapNotNull { card ->
                    val knowledge = knowledgeMap[card.knowledgeCode]
                    val cardType = cardTypeMap[card.cardTypeCode]
                    if (knowledge != null && cardType != null) {
                        card.toDto(
                            knowledge = knowledge.toDto(),
                            cardType = cardType.toDto(),
                        )
                    } else {
                        null
                    }
                }

            val pageDto =
                PageDto(
                    content = dtos,
                    page =
                        PageInfoDto(
                            number = page.number,
                            size = page.size,
                            totalElements = page.totalElements,
                            totalPages = page.totalPages,
                        ),
                )
            ResponseEntity.ok(pageDto)
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PageDto(emptyList(), PageInfoDto(0, 0, 0, 0)))
        }

    /**
     * Get a specific card for current account.
     * GET /api/v1/accounts/me/cards/{cardId}
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @GetMapping("/me/cards/{cardId}")
    suspend fun getMyCard(
        @PathVariable cardId: Long,
    ): ResponseEntity<Any> {
        // TODO: Extract accountId from JWT token
        val accountId = 1L // Placeholder

        return try {
            val card = accountCardService.getCardById(accountId, cardId)

            if (card == null) {
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponseFactory.notFound("Card", cardId.toString()))
            } else {
                val (knowledge, cardType) =
                    coroutineScope {
                        val knowledgeDeferred = async { knowledgeService.getKnowledgeByCode(card.knowledgeCode) }
                        val cardTypeDeferred = async { cardTypeService.getCardTypeByCode(card.cardTypeCode) }
                        Pair(knowledgeDeferred.await(), cardTypeDeferred.await())
                    }

                if (knowledge == null || cardType == null) {
                    ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body<Any>(ErrorResponseFactory.notFound("Knowledge or CardType", card.knowledgeCode))
                } else {
                    ResponseEntity.ok<Any>(
                        card.toDto(
                            knowledge = knowledge.toDto(),
                            cardType = cardType.toDto(),
                        ),
                    )
                }
            }
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PageDto<AccountCardDto>(emptyList(), PageInfoDto(0, 0, 0, 0)))
        }
    }

    /**
     * Get cards due for review for current account.
     * GET /api/v1/accounts/me/cards:due
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @GetMapping("/me/cards:due")
    suspend fun getDueCards(
        @RequestParam(required = false) card_type_code: String?,
        pageable: Pageable,
    ): ResponseEntity<PageDto<AccountCardDto>> {
        // TODO: Extract accountId from JWT token
        val accountId = 1L // Placeholder

        return try {
            val page = accountCardService.getDueCards(accountId, pageable, card_type_code)

            // Batch load knowledge and card types to avoid N+1
            val knowledgeCodes = page.content.map { it.knowledgeCode }.distinct()
            val cardTypeCodes = page.content.map { it.cardTypeCode }.distinct()

            val (knowledgeMap, cardTypeMap) =
                coroutineScope {
                    val knowledgeDeferred = async { knowledgeService.getKnowledgeByCodes(knowledgeCodes) }
                    val cardTypeDeferred = async { cardTypeService.getCardTypesByCodes(cardTypeCodes) }
                    Pair(knowledgeDeferred.await(), cardTypeDeferred.await())
                }

            // Render templates for each card (template rendering is cached internally)
            val dtos =
                coroutineScope {
                    page.content
                        .mapNotNull { card ->
                            val knowledge = knowledgeMap[card.knowledgeCode]
                            val cardType = cardTypeMap[card.cardTypeCode]
                            if (knowledge != null && cardType != null) {
                                async {
                                    val (front, back) =
                                        coroutineScope {
                                            val frontDeferred =
                                                async { cardTemplateService.renderByRole(cardType, knowledge, "front") }
                                            val backDeferred =
                                                async { cardTemplateService.renderByRole(cardType, knowledge, "back") }
                                            Pair(frontDeferred.await(), backDeferred.await())
                                        }
                                    card.toDto(
                                        knowledge = knowledge.toDto(),
                                        cardType = cardType.toDto(),
                                        front = front,
                                        back = back,
                                    )
                                }
                            } else {
                                null
                            }
                        }.awaitAll()
                }

            val pageDto =
                PageDto<AccountCardDto>(
                    content = dtos,
                    page =
                        PageInfoDto(
                            number = page.number,
                            size = page.size,
                            totalElements = page.totalElements,
                            totalPages = page.totalPages,
                        ),
                )
            ResponseEntity.ok(pageDto)
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PageDto<AccountCardDto>(emptyList(), PageInfoDto(0, 0, 0, 0)))
        }
    }

    /**
     * Initialize cards for current account.
     * POST /api/v1/accounts/me/cards:initialize
     * TODO: Extract accountId from JWT token 'sub' claim, trigger Temporal workflow
     */
    @PostMapping("/me/cards:initialize")
    suspend fun initializeCards(
        @RequestBody(required = false) request: InitializeCardsRequestDto?,
    ): ResponseEntity<Map<String, Int>> {
        // TODO: Extract accountId from JWT token
        val accountId = 1L // Placeholder

        return try {
            val created = accountCardService.initializeCards(accountId, request?.cardTypeCodes)
            ResponseEntity.ok(mapOf("created" to created, "skipped" to 0))
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("created" to 0, "skipped" to 0))
        }
    }

    /**
     * Submit a review result and update SM-2 algorithm state.
     * POST /api/v1/accounts/me/cards/{cardId}:review
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @PostMapping("/me/cards/{cardId}:review")
    suspend fun reviewCard(
        @PathVariable cardId: Long,
        @Valid @RequestBody request: ReviewRequestDto,
    ): ResponseEntity<Any> {
        // TODO: Extract accountId from JWT token
        val accountId = 1L // Placeholder

        // Validate quality range
        if (request.quality < 0 || request.quality > 5) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseFactory.badRequest("Quality must be between 0 and 5"))
        }

        return try {
            val card = accountCardService.reviewCard(accountId, cardId, request.quality)

            val (knowledge, cardType) =
                coroutineScope {
                    val knowledgeDeferred = async { knowledgeService.getKnowledgeByCode(card.knowledgeCode) }
                    val cardTypeDeferred = async { cardTypeService.getCardTypeByCode(card.cardTypeCode) }
                    Pair(knowledgeDeferred.await(), cardTypeDeferred.await())
                }

            if (knowledge == null || cardType == null) {
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponseFactory.notFound("Knowledge or CardType", card.knowledgeCode))
            } else {
                ResponseEntity.ok<Any>(
                    card.toDto(
                        knowledge = knowledge.toDto(),
                        cardType = cardType.toDto(),
                    ),
                )
            }
        } catch (error: IllegalArgumentException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body<Any>(ErrorResponseFactory.badRequest(error.message ?: "Invalid request"))
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body<Any>(
                    ErrorResponseFactory.internalError(error.message ?: "Internal server error"),
                )
        }
    }
}
