package com.flightbooking.access

import com.flightbooking.models.User
import com.flightbooking.models.toUser
import com.flightbooking.tables.UserTable

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.time.Instant

class UserTableAccess {
    fun getAll(): List<User> = transaction {
        UserTable.selectAll().map {
            it.toUser()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<User> = transaction {
        UserTable.select { attribute eq value } 
            .map { it.toUser() } 
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
    fun deleteByID(id: Int) = transaction { 
        UserTable.deleteWhere { UserTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = UserTable.update({ UserTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun findByEmail(email: String): User? = transaction {
        UserTable.select { UserTable.email eq email }
            .limit(1)
            .firstOrNull()
            ?.let { it.toUser() }
    }
}
