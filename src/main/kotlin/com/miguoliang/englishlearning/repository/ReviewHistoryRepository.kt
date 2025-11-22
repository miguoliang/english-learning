package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.ReviewHistory
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ReviewHistoryRepository : PanacheRepositoryBase<ReviewHistory, Long> {

    suspend fun findByAccountCardId(accountCardId: Long): List<ReviewHistory> {
        return find("accountCardId", accountCardId).list().awaitSuspending()
    }
}
