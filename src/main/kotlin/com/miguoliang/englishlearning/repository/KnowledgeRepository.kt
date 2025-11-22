package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Knowledge
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface KnowledgeRepository : CoroutineCrudRepository<Knowledge, String> {
    @Query("SELECT * FROM knowledge ORDER BY code OFFSET :#{#pageable.offset} LIMIT :#{#pageable.pageSize}")
    fun findAllOrderedByCode(pageable: Pageable): Flow<Knowledge>

    suspend fun findByCode(code: String): Knowledge?

    @Query("SELECT * FROM knowledge WHERE code IN (:codes)")
    fun findByCodeIn(codes: Collection<String>): Flow<Knowledge>
}
