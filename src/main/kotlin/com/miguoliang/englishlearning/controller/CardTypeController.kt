package com.miguoliang.englishlearning.controller

import com.miguoliang.englishlearning.dto.CardTypeDto
import com.miguoliang.englishlearning.dto.ErrorResponseFactory
import com.miguoliang.englishlearning.dto.PageDto
import com.miguoliang.englishlearning.dto.PageInfoDto
import com.miguoliang.englishlearning.dto.toDto
import com.miguoliang.englishlearning.service.CardTypeService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * REST controller for Card Type endpoints.
 * Access: Both operator and client roles (read-only).
 */
@Path("/api/v1/card-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CardTypeController(
    private val cardTypeService: CardTypeService,
) {
    /**
     * List all available card types.
     * GET /api/v1/card-types
     */
    @GET
    suspend fun listCardTypes(
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ): Response {
        return try {
            val cardTypes = cardTypeService.getAllCardTypes()

            val total = cardTypes.size.toLong()
            val start = page * size
            val end = minOf(start + size, cardTypes.size)
            val pagedContent =
                if (start < cardTypes.size) {
                    cardTypes.subList(start, end)
                } else {
                    emptyList()
                }

            val pageDto =
                PageDto(
                    content = pagedContent.map { it.toDto() },
                    page =
                        PageInfoDto(
                            number = page,
                            size = size,
                            totalElements = total,
                            totalPages = if (total > 0) ((total - 1) / size + 1).toInt() else 0,
                        ),
                )
            Response.ok(pageDto).build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(PageDto<CardTypeDto>(emptyList(), PageInfoDto(0, 0, 0, 0)))
                .build()
        }
    }

    /**
     * Get a specific card type.
     * GET /api/v1/card-types/{code}
     */
    @GET
    @Path("/{code}")
    suspend fun getCardType(
        @PathParam("code") code: String,
    ): Response {
        return try {
            val cardType = cardTypeService.getCardTypeByCode(code)

            when (cardType) {
                null -> Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponseFactory.notFound("CardType", code))
                    .build()
                else -> Response.ok(cardType.toDto()).build()
            }
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponseFactory.internalError(error.message ?: "Internal server error"))
                .build()
        }
    }
}
