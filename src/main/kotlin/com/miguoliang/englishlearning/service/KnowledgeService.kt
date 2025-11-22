package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.common.Page
import com.miguoliang.englishlearning.common.Pageable
import com.miguoliang.englishlearning.model.Knowledge
import com.miguoliang.englishlearning.repository.KnowledgeRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

/**
 * Manages knowledge operations.
 */
@ApplicationScoped
class KnowledgeService(
    private val knowledgeRepository: KnowledgeRepository,
    private val paginationHelper: PaginationHelper,
) {
    /**
     * List knowledge items with pagination.
     *
     * @param pageable Pagination parameters
     * @param filter Optional filter expression (not implemented in MVP)
     * @return Uni<Page> of Knowledge items
     */
    fun getKnowledge(
        pageable: Pageable,
        filter: String? = null,
    ): Uni<Page<Knowledge>> {
        // TODO: Implement filter support for metadata queries
        return paginationHelper.paginate(
            knowledgeRepository.findAllOrderedByCode(pageable),
            knowledgeRepository.count(),
            pageable,
        )
    }

    /**
     * Get single knowledge item by code.
     *
     * @param code Knowledge code identifier
     * @return Uni<Knowledge> or null if not found
     */
    fun getKnowledgeByCode(code: String): Uni<Knowledge?> = knowledgeRepository.findByCode(code)

    /**
     * Batch load knowledge items by codes.
     *
     * @param codes Collection of knowledge codes
     * @return Uni<Map> of code to Knowledge
     */
    fun getKnowledgeByCodes(codes: Collection<String>): Uni<Map<String, Knowledge>> =
        if (codes.isEmpty()) {
            Uni.createFrom().item(emptyMap())
        } else {
            knowledgeRepository
                .findByCodeIn(codes)
                .collect().asList()
                .map { list ->
                    list.associateBy { knowledge -> knowledge.code }
                }
        }
}
