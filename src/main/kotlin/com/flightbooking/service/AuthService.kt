package com.flightbooking.service

import com.flightbooking.tables.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.flightbooking.util.PasswordUtil

object AuthService {
    fun register(email: String, password: String): Boolean {
        return transaction {
            val exists = UserTable.select { UserTable.email eq email }.count() > 0
            if (exists) {
                false
            } else {
                UserTable.insert {
                    it[UserTable.email] = email
                    it[UserTable.passwordHash] = PasswordUtil.hash(password)
                    it[UserTable.createdAt] = ""
                }
                true
            }
        }
    }
    fun login(email: String, password: String): Boolean {
        return transaction {
            val user = UserTable.select { UserTable.email eq email }.singleOrNull()
            if (user != null) {
                PasswordUtil.verify(password, user[UserTable.passwordHash] ?: "")
            } else {
                false
            }
        }
    }
}