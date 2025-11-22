package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.ReviewHistory
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewHistoryRepository : CoroutineCrudRepository<ReviewHistory, Long> {
    fun findByAccountCardId(accountCardId: Long): Flow<ReviewHistory>
}
