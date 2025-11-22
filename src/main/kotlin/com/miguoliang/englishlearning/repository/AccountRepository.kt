package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Account
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class AccountRepository : PanacheRepository<Account> {

    fun findByUsername(username: String): Uni<Account?> {
        return find("username", username).firstResult()
    }
}
