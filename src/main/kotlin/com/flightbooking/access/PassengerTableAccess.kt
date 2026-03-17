package com.flightbooking.access

import com.flightbooking.models.Passenger
import com.flightbooking.models.toPassenger
import com.flightbooking.tables.PassengerTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

import com.flightbooking.tables.BookingTable
import com.flightbooking.tables.UserTable

class PassengerTableAccess {
    fun getAll(): List<Passenger> = transaction {
        PassengerTable.selectAll().map {
            it.toPassenger()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Passenger> = transaction {
        PassengerTable.select { attribute eq value } 
            .map { it.toPassenger() } 
    }
    fun createPassenger(
        bookingId: Int?,
        email: String?,
        checkedIn: Int,
        title: String?,
        firstName: String?,
        lastName: String?,
        dateOfBirth: String?,
        gender: String?,
        nationality: String?,
        documentType: String?,
        documentNumber: String?,
        documentCountry: String?,
        documentExpiry: String?
        ): Boolean = transaction { 
        PassengerTable.insert { 
            it[PassengerTable.bookingId] = bookingId
            it[PassengerTable.email] = email
            it[PassengerTable.checkedIn] = checkedIn
            it[PassengerTable.title] = title
            it[PassengerTable.firstName] = firstName
            it[PassengerTable.lastName] = lastName
            it[PassengerTable.dateOfBirth] = dateOfBirth
            it[PassengerTable.gender] = gender
            it[PassengerTable.nationality] = nationality
            it[PassengerTable.documentType] = documentType
            it[PassengerTable.documentNumber] = documentNumber
            it[PassengerTable.documentCountry] = documentCountry
            it[PassengerTable.documentExpiry] = documentExpiry
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        PassengerTable.deleteWhere { PassengerTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = PassengerTable.update({ PassengerTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun generatePassengers() = transaction {
        println("generating passengers")
        val bookings = BookingTable.selectAll().toList()
        val maleNames = listOf("James", "Tom", "Daniel", "Michael", "Ethan", "Liam")
        val femaleNames = listOf("Priya", "Sarah", "Emily", "Aisha", "Sophie", "Laura")
        val lastNames = listOf("Walker", "Sharma", "Nguyen", "Smith", "Brown", "Patel")

        fun randomDOB(): String {
            val year = (1950..2010).random()
            val month = (1..12).random()
            val day = (1..28).random()
            return "%04d-%02d-%02d".format(year, month, day)
        }
        fun randomExpiry(): String {
            val year = (2026..2035).random()
            val month = (1..12).random()
            val day = (1..28).random()
            return "%04d-%02d-%02d".format(year, month, day)
        }
        fun randomPassport(country: String): String = 
            country + (100000000..999999999).random()
        bookings.forEach { bookingRow ->
            val bookingId = bookingRow[BookingTable.id]
            val userId = bookingRow[BookingTable.userId]
            if (userId != null) {
                val user = UserTable
                    .select { UserTable.id eq userId }
                    .first()
                val firstName = user[UserTable.firstName] ?: "Unknown"
                val lastName = user[UserTable.lastName] ?: "Passenger"
                val email = user[UserTable.email]
                val dob = user[UserTable.dateOfBirth] ?: randomDOB()
                val nationality = listOf("GB", "IN", "US", "FR", "DE", "CN", "SP", "GE").random()
                val gender = listOf("M","F").random()
                PassengerTable.insert {
                    it[PassengerTable.bookingId] = bookingId
                    it[PassengerTable.email] = email
                    it[PassengerTable.checkedIn] = 0
                    it[PassengerTable.title] = if (gender == "M") "Mr" else "Ms"
                    it[PassengerTable.firstName] = firstName
                    it[PassengerTable.lastName] = lastName
                    it[PassengerTable.dateOfBirth] = dob
                    it[PassengerTable.gender] = gender
                    it[PassengerTable.nationality] = nationality
                    it[PassengerTable.documentType] = "passport"
                    it[PassengerTable.documentNumber] = randomPassport(nationality)
                    it[PassengerTable.documentCountry] = nationality
                    it[PassengerTable.documentExpiry] = randomExpiry()
                }
            }
            val companionCount = (0..2).random()
            repeat(companionCount) {
                val isMale = (0..1).random() == 0
                val firstName = if (isMale) maleNames.random() else femaleNames.random()
                val lastName = lastNames.random()
                val email = "${firstName.lowercase()}.${lastName.lowercase()}@email.com"
                val nationality = listOf("GB", "IN", "US", "FR", "DE", "CN", "SP", "GE").random()

                PassengerTable.insert {
                    it[PassengerTable.bookingId] = bookingId
                    it[PassengerTable.email] = email
                    it[PassengerTable.checkedIn] = (0..1).random()
                    it[PassengerTable.title] = if (isMale) "Mr" else "Ms"
                    it[PassengerTable.firstName] = firstName
                    it[PassengerTable.lastName] = lastName
                    it[PassengerTable.dateOfBirth] = randomDOB()
                    it[PassengerTable.gender] = if (isMale) "M" else "F"
                    it[PassengerTable.nationality] = nationality
                    it[PassengerTable.documentType] = "passport"
                    it[PassengerTable.documentNumber] = randomPassport(nationality)
                    it[PassengerTable.documentCountry] = nationality
                    it[PassengerTable.documentExpiry] = randomExpiry()
                }
            }
        }
        println("done generating")
    }
}
