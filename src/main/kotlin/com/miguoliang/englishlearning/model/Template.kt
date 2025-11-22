package com.miguoliang.englishlearning.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "templates")
data class Template(
    @Id
    @Column(name = "code")
    val code: String,
    @Column(name = "name")
    val name: String,
    @Column(name = "description")
    val description: String?,
    @Column(name = "format")
    val format: String?,
    @Column(name = "content")
    val content: ByteArray,
    @Column(name = "created_at")
    val createdAt: Instant,
    @Column(name = "updated_at")
    val updatedAt: Instant,
    @Column(name = "created_by")
    val createdBy: String?,
    @Column(name = "updated_by")
    val updatedBy: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Template

        if (code != other.code) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (format != other.format) return false
        if (!content.contentEquals(other.content)) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (createdBy != other.createdBy) return false
        if (updatedBy != other.updatedBy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (format?.hashCode() ?: 0)
        result = 31 * result + content.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + (createdBy?.hashCode() ?: 0)
        result = 31 * result + (updatedBy?.hashCode() ?: 0)
        return result
    }
}
