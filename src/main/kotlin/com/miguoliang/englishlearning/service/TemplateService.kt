package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.model.Template
import com.miguoliang.englishlearning.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

/**
 * Manages template operations.
 */
@Service
class TemplateService(
    private val templateRepository: TemplateRepository,
) {
    /**
     * Get single template by code.
     *
     * @param code Template code identifier
     * @return Template or null if not found
     */
    suspend fun getTemplateByCode(code: String): Template? = templateRepository.findByCode(code)

    /**
     * List all templates.
     * Usage determined by relationships (card_type_template_rel).
     *
     * @return Flow of all templates
     */
    fun getAllTemplates(): Flow<Template> = templateRepository.findAll()
}
