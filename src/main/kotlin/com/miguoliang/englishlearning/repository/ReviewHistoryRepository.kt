package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.ReviewHistory
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Multi
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ReviewHistoryRepository : PanacheRepository<ReviewHistory> {

    fun findByAccountCardId(accountCardId: Long): Multi<ReviewHistory> {
        return find("accountCardId", accountCardId).stream()
    }
}
