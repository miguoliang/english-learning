package com.miguoliang.englishlearning.controller

import com.miguoliang.englishlearning.common.PageRequest
import com.miguoliang.englishlearning.dto.*
import com.miguoliang.englishlearning.dto.PageInfoDto
import com.miguoliang.englishlearning.service.AccountCardService
import com.miguoliang.englishlearning.service.CardTemplateService
import com.miguoliang.englishlearning.service.CardTypeService
import com.miguoliang.englishlearning.service.KnowledgeService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * REST controller for Account Card endpoints.
 * Access: client role (for /me endpoints) or operator role (for {accountId} endpoints).
 *
 * Note: JWT authentication is not yet implemented. For MVP, accountId is passed as path parameter.
 * TODO: Extract accountId from JWT token 'sub' claim for /me endpoints.
 */
@Path("/api/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    @GET
    @Path("/me/cards")
    suspend fun listMyCards(
        @QueryParam("card_type_code") card_type_code: String?,
        @QueryParam("status") status: String?,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ): Response {
        return try {
            // TODO: Extract accountId from JWT token
            val accountId = 1L // Placeholder

            val pageable = PageRequest.of(page, size)
            val pageResult = accountCardService.getAccountCards(accountId, pageable, card_type_code, status)

            // Batch load knowledge and card types to avoid N+1
            val knowledgeCodes = pageResult.content.map { it.knowledgeCode }.distinct()
            val cardTypeCodes = pageResult.content.map { it.cardTypeCode }.distinct()

            val knowledgeMap = knowledgeService.getKnowledgeByCodes(knowledgeCodes)
            val cardTypeMap = cardTypeService.getCardTypesByCodes(cardTypeCodes)

            val dtos = pageResult.content.mapNotNull { card ->
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

            val pageDto = PageDto(
                content = dtos,
                page = PageInfoDto(
                    number = pageResult.number,
                    size = pageResult.size,
                    totalElements = pageResult.totalElements,
                    totalPages = pageResult.totalPages,
                ),
            )
            Response.ok(pageDto).build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(PageDto(emptyList<AccountCardDto>(), PageInfoDto(0, 0, 0, 0)))
                .build()
        }
    }

    /**
     * List specific account's cards (operator access).
     * GET /api/v1/accounts/{accountId}/cards
     * TODO: Validate operator role from JWT token
     */
    @GET
    @Path("/{accountId}/cards")
    suspend fun listAccountCards(
        @PathParam("accountId") accountId: Long,
        @QueryParam("card_type_code") card_type_code: String?,
        @QueryParam("status") status: String?,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ): Response {
        return try {
            val pageable = PageRequest.of(page, size)
            val pageResult = accountCardService.getAccountCards(accountId, pageable, card_type_code, status)

            // Batch load knowledge and card types to avoid N+1
            val knowledgeCodes = pageResult.content.map { it.knowledgeCode }.distinct()
            val cardTypeCodes = pageResult.content.map { it.cardTypeCode }.distinct()

            val knowledgeMap = knowledgeService.getKnowledgeByCodes(knowledgeCodes)
            val cardTypeMap = cardTypeService.getCardTypesByCodes(cardTypeCodes)

            val dtos = pageResult.content.mapNotNull { card ->
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

            val pageDto = PageDto(
                content = dtos,
                page = PageInfoDto(
                    number = pageResult.number,
                    size = pageResult.size,
                    totalElements = pageResult.totalElements,
                    totalPages = pageResult.totalPages,
                ),
            )
            Response.ok(pageDto).build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(PageDto(emptyList<AccountCardDto>(), PageInfoDto(0, 0, 0, 0)))
                .build()
        }
    }

    /**
     * Get a specific card for current account.
     * GET /api/v1/accounts/me/cards/{cardId}
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @GET
    @Path("/me/cards/{cardId}")
    suspend fun getMyCard(
        @PathParam("cardId") cardId: Long,
    ): Response {
        return try {
            // TODO: Extract accountId from JWT token
            val accountId = 1L // Placeholder

            val card = accountCardService.getCardById(accountId, cardId)
            if (card == null) {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponseFactory.notFound("Card", cardId.toString()))
                    .build()
            } else {
                val knowledge = knowledgeService.getKnowledgeByCode(card.knowledgeCode)
                val cardType = cardTypeService.getCardTypeByCode(card.cardTypeCode)

                if (knowledge == null || cardType == null) {
                    Response.status(Response.Status.NOT_FOUND)
                        .entity(ErrorResponseFactory.notFound("Knowledge or CardType", card.knowledgeCode))
                        .build()
                } else {
                    Response.ok(
                        card.toDto(
                            knowledge = knowledge.toDto(),
                            cardType = cardType.toDto(),
                        ),
                    ).build()
                }
            }
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponseFactory.internalError(error.message ?: "Internal server error"))
                .build()
        }
    }

    /**
     * Get cards due for review for current account.
     * GET /api/v1/accounts/me/cards:due
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @GET
    @Path("/me/cards:due")
    suspend fun getDueCards(
        @QueryParam("card_type_code") card_type_code: String?,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ): Response {
        return try {
            // TODO: Extract accountId from JWT token
            val accountId = 1L // Placeholder

            val pageable = PageRequest.of(page, size)
            val pageResult = accountCardService.getDueCards(accountId, pageable, card_type_code)

            // Batch load knowledge and card types to avoid N+1
            val knowledgeCodes = pageResult.content.map { it.knowledgeCode }.distinct()
            val cardTypeCodes = pageResult.content.map { it.cardTypeCode }.distinct()

            val knowledgeMap = knowledgeService.getKnowledgeByCodes(knowledgeCodes)
            val cardTypeMap = cardTypeService.getCardTypesByCodes(cardTypeCodes)

            // Render templates for each card
            val dtos = pageResult.content.mapNotNull { card ->
                val knowledge = knowledgeMap[card.knowledgeCode]
                val cardType = cardTypeMap[card.cardTypeCode]
                if (knowledge != null && cardType != null) {
                    val front = cardTemplateService.renderByRole(cardType, knowledge, "front")
                    val back = cardTemplateService.renderByRole(cardType, knowledge, "back")
                    card.toDto(
                        knowledge = knowledge.toDto(),
                        cardType = cardType.toDto(),
                        front = front,
                        back = back,
                    )
                } else {
                    null
                }
            }

            val pageDto = PageDto(
                content = dtos,
                page = PageInfoDto(
                    number = pageResult.number,
                    size = pageResult.size,
                    totalElements = pageResult.totalElements,
                    totalPages = pageResult.totalPages,
                ),
            )
            Response.ok(pageDto).build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(PageDto<AccountCardDto>(emptyList(), PageInfoDto(0, 0, 0, 0)))
                .build()
        }
    }

    /**
     * Initialize cards for current account.
     * POST /api/v1/accounts/me/cards:initialize
     * TODO: Extract accountId from JWT token 'sub' claim, trigger Temporal workflow
     */
    @POST
    @Path("/me/cards:initialize")
    suspend fun initializeCards(
        request: InitializeCardsRequestDto?,
    ): Response {
        return try {
            // TODO: Extract accountId from JWT token
            val accountId = 1L // Placeholder

            val created = accountCardService.initializeCards(accountId, request?.cardTypeCodes)
            Response.ok(mapOf("created" to created, "skipped" to 0)).build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("created" to 0, "skipped" to 0))
                .build()
        }
    }

    /**
     * Submit a review result and update SM-2 algorithm state.
     * POST /api/v1/accounts/me/cards/{cardId}:review
     * TODO: Extract accountId from JWT token 'sub' claim
     */
    @POST
    @Path("/me/cards/{cardId}:review")
    suspend fun reviewCard(
        @PathParam("cardId") cardId: Long,
        @Valid request: ReviewRequestDto,
    ): Response {
        return try {
            // TODO: Extract accountId from JWT token
            val accountId = 1L // Placeholder

            // Validate quality range
            if (request.quality !in 0..5) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponseFactory.badRequest("Quality must be between 0 and 5"))
                    .build()
            }

            val card = accountCardService.reviewCard(accountId, cardId, request.quality)

            val knowledge = knowledgeService.getKnowledgeByCode(card.knowledgeCode)
            val cardType = cardTypeService.getCardTypeByCode(card.cardTypeCode)

            if (knowledge == null || cardType == null) {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponseFactory.notFound("Knowledge or CardType", card.knowledgeCode))
                    .build()
            } else {
                Response.ok(
                    card.toDto(
                        knowledge = knowledge.toDto(),
                        cardType = cardType.toDto(),
                    ),
                ).build()
            }
        } catch (error: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponseFactory.badRequest(error.message ?: "Invalid request"))
                .build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponseFactory.internalError(error.message ?: "Internal server error"))
                .build()
        }
    }
}
