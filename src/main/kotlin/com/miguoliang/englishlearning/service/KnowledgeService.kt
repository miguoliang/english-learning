package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.model.Knowledge
import com.miguoliang.englishlearning.repository.KnowledgeRepository
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Manages knowledge operations.
 */
@Service
class KnowledgeService(
    private val knowledgeRepository: KnowledgeRepository,
    private val paginationHelper: PaginationHelper,
) {
    /**
     * List knowledge items with pagination.
     *
     * @param pageable Pagination parameters
     * @param filter Optional filter expression (not implemented in MVP)
     * @return Page of Knowledge items
     */
    suspend fun getKnowledge(
        pageable: Pageable,
        filter: String? = null,
    ): Page<Knowledge> {
        // TODO: Implement filter support for metadata queries
        return paginationHelper.paginate(
            knowledgeRepository.findAllOrderedByCode(pageable),
            { knowledgeRepository.count() },
            pageable,
        )
    }

    /**
     * Get single knowledge item by code.
     *
     * @param code Knowledge code identifier
     * @return Knowledge or null if not found
     */
    suspend fun getKnowledgeByCode(code: String): Knowledge? = knowledgeRepository.findByCode(code)

    /**
     * Batch load knowledge items by codes.
     *
     * @param codes Collection of knowledge codes
     * @return Map of code to Knowledge
     */
    suspend fun getKnowledgeByCodes(codes: Collection<String>): Map<String, Knowledge> =
        codes.takeIf { it.isNotEmpty() }
            ?.let {
                knowledgeRepository
                    .findByCodeIn(it)
                    .toList()
                    .associateBy { knowledge -> knowledge.code }
            }
            ?: emptyMap()
}
