package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.model.Template
import com.miguoliang.englishlearning.repository.TemplateRepository
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

/**
 * Manages template operations.
 */
@ApplicationScoped
class TemplateService(
    private val templateRepository: TemplateRepository,
) {
    /**
     * Get single template by code.
     *
     * @param code Template code identifier
     * @return Uni<Template> or null if not found
     */
    fun getTemplateByCode(code: String): Uni<Template?> = templateRepository.findByCode(code)

    /**
     * List all templates.
     * Usage determined by relationships (card_type_template_rel).
     *
     * @return Multi of all templates
     */
    fun getAllTemplates(): Multi<Template> = templateRepository.streamAll()
}
