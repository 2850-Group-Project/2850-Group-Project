package com.flightbooking.access

import com.flightbooking.models.User
import com.flightbooking.tables.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserTableAccess {
    fun findByEmail(email: String): User? = transaction {
        UserTable.select { UserTable.email eq email }
            .limit(1)
            .firstOrNull()
            ?.let { toUser(it) }
    }

    fun createUser(
    email: String,
    passwordHash: String,
    firstName: String?,
    lastName: String?
): Boolean = transaction {
    val exists = UserTable.select { UserTable.email eq email }.count() > 0
    if (exists) return@transaction false

    UserTable.insert {
        it[UserTable.email] = email
        it[UserTable.passwordHash] = passwordHash
        it[UserTable.firstName] = firstName
        it[UserTable.lastName] = lastName
        it[UserTable.phoneNumber] = null
        it[UserTable.dateOfBirth] = null
        it[UserTable.createdAt] = java.time.Instant.now().toString()
        it[UserTable.accountStatus] = "active"
    }
    true
}

    private fun toUser(row: ResultRow): User {
        return User(
            id = row[UserTable.id],
            email = row[UserTable.email],
            passwordHash = row[UserTable.passwordHash],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            phoneNumber = row[UserTable.phoneNumber],
            dateOfBirth = row[UserTable.dateOfBirth],
            createdAt = row[UserTable.createdAt],
            accountStatus = row[UserTable.accountStatus]
        )
    }
}