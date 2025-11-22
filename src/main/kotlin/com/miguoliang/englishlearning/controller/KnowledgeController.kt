package com.miguoliang.englishlearning.controller

import com.miguoliang.englishlearning.common.PageRequest
import com.miguoliang.englishlearning.dto.ErrorResponseFactory
import com.miguoliang.englishlearning.dto.KnowledgeDto
import com.miguoliang.englishlearning.dto.PageDto
import com.miguoliang.englishlearning.dto.PageInfoDto
import com.miguoliang.englishlearning.dto.toDto
import com.miguoliang.englishlearning.service.KnowledgeService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * REST controller for Knowledge endpoints.
 * Access: Both operator and client roles (read-only for clients, full CRUD for operators).
 */
@Path("/api/v1/knowledge")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class KnowledgeController(
    private val knowledgeService: KnowledgeService,
) {
    /**
     * List knowledge items with optional filtering.
     * GET /api/v1/knowledge
     */
    @GET
    suspend fun listKnowledge(
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ): Response {
        return try {
            val pageable = PageRequest.of(page, size)
            val pageResult = knowledgeService.getKnowledge(pageable)

            val content = pageResult.content.map { it.toDto() }
            val pageDto =
                PageDto(
                    content = content,
                    page =
                        PageInfoDto(
                            number = pageResult.number,
                            size = pageResult.size,
                            totalElements = pageResult.totalElements,
                            totalPages = pageResult.totalPages,
                        ),
                )
            Response.ok(pageDto).build()
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(PageDto<KnowledgeDto>(emptyList(), PageInfoDto(0, 0, 0, 0)))
                .build()
        }
    }

    /**
     * Get a specific knowledge item.
     * GET /api/v1/knowledge/{code}
     */
    @GET
    @Path("/{code}")
    suspend fun getKnowledge(
        @PathParam("code") code: String,
    ): Response {
        return try {
            val knowledge = knowledgeService.getKnowledgeByCode(code)

            when (knowledge) {
                null -> Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponseFactory.notFound("Knowledge", code))
                    .build()
                else -> Response.ok(knowledge.toDto()).build()
            }
        } catch (error: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponseFactory.internalError(error.message ?: "Internal server error"))
                .build()
        }
    }
}
