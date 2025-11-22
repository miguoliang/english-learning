package com.miguoliang.englishlearning.service

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CodeGenerationService(
    private val pgPool: PgPool,
) {
    fun generateCode(prefix: String): Uni<String> {
        require(prefix in listOf("ST", "CS")) { "Invalid prefix: $prefix. Must be ST or CS" }
        require(prefix.length == 2) { "Prefix must be exactly 2 characters" }

        val sequenceName = "code_seq_${prefix.lowercase()}"

        return pgPool
            .query("SELECT nextval('$sequenceName')")
            .execute()
            .map { rowSet ->
                val row: Row = rowSet.iterator().next()
                row.getLong(0)
            }
            .map { nextValue ->
                val number = nextValue.toString().padStart(7, '0')
                require(number.length <= 7) { "Sequence value exceeds 7 digits" }

                val code = "$prefix-$number"
                require(code.length == 10) { "Generated code must be exactly 10 characters" }

                code
            }
    }

    fun validateCode(code: String): Boolean {
        val pattern = Regex("^(ST|CS)-\\d{7}$")
        return pattern.matches(code) && code.length == 10
    }
}
