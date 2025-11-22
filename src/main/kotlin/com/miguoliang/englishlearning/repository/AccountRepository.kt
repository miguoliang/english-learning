package com.miguoliang.englishlearning.repository

import com.miguoliang.englishlearning.model.Account
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CoroutineCrudRepository<Account, Long> {
    suspend fun findByUsername(username: String): Account?
}
