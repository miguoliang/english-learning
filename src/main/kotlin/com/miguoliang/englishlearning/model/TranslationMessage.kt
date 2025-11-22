package com.miguoliang.englishlearning.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "translation_messages")
data class TranslationMessage(
    @Id
    @Column(name = "code")
    val code: String,
    @Column(name = "translation_key_code")
    val translationKeyCode: String,
    @Column(name = "locale_code")
    val localeCode: String,
    @Column(name = "message")
    val message: String,
    @Column(name = "created_at")
    val createdAt: Instant,
    @Column(name = "updated_at")
    val updatedAt: Instant,
    @Column(name = "created_by")
    val createdBy: String?,
    @Column(name = "updated_by")
    val updatedBy: String?,
)
