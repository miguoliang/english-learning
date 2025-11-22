package com.miguoliang.englishlearning.controller

import com.miguoliang.englishlearning.dto.ErrorResponseFactory
import com.miguoliang.englishlearning.dto.KnowledgeDto
import com.miguoliang.englishlearning.dto.PageDto
import com.miguoliang.englishlearning.dto.PageInfoDto
import com.miguoliang.englishlearning.dto.toDto
import com.miguoliang.englishlearning.service.KnowledgeService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Knowledge endpoints.
 * Access: Both operator and client roles (read-only for clients, full CRUD for operators).
 */
@RestController
@RequestMapping("/api/v1/knowledge")
class KnowledgeController(
    private val knowledgeService: KnowledgeService,
) {
    /**
     * List knowledge items with optional filtering.
     * GET /api/v1/knowledge
     */
    @GetMapping
    suspend fun listKnowledge(pageable: Pageable): ResponseEntity<PageDto<KnowledgeDto>> =
        try {
            val page = knowledgeService.getKnowledge(pageable)
            val content = page.content.map { it.toDto() }
            val pageDto =
                PageDto(
                    content = content,
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
                .body(PageDto<KnowledgeDto>(emptyList(), PageInfoDto(0, 0, 0, 0)))
        }

    /**
     * Get a specific knowledge item.
     * GET /api/v1/knowledge/{code}
     */
    @GetMapping("/{code}")
    suspend fun getKnowledge(
        @PathVariable code: String,
    ): ResponseEntity<Any> =
        try {
            val knowledge = knowledgeService.getKnowledgeByCode(code)
            if (knowledge == null) {
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponseFactory.notFound("Knowledge", code))
            } else {
                ResponseEntity.ok<Any>(knowledge.toDto())
            }
        } catch (error: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body<Any>(ErrorResponseFactory.internalError(error.message ?: "Internal server error"))
        }
}
