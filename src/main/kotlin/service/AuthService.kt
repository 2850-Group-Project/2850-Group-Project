package service

import database.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import util.PasswordUtil

object AuthService {

    fun register(email: String, password: String): Boolean {
        return transaction {
            val exists = Users
                .select { Users.email eq email }
                .count() > 0

            if (exists) {
                false
            } else {
                Users.insert {
                    it[Users.email] = email
                    it[Users.passwordHash] = PasswordUtil.hash(password)
                }
                true
            }
        }
    }

    fun login(email: String, password: String): Boolean {
        return transaction {
            val user = Users
                .select { Users.email eq email }
                .singleOrNull()

            if (user != null) {
                PasswordUtil.verify(password, user[Users.passwordHash])
            } else {
                false
            }
        }
    }
}