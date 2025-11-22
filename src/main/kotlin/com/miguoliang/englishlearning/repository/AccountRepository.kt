package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Account
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class AccountRepository : PanacheRepositoryBase<Account, Long> {

    suspend fun findByUsername(username: String): Account? {
        return find("username", username).firstResult().awaitSuspending()
    }
}
