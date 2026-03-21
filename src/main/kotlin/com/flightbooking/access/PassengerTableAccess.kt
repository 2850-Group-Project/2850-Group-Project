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
import org.jetbrains.exposed.sql.insertAndGetId


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
    fun generatePassengers(): Map<Int, List<Int>> = transaction {
        println("generating passengers")
        val passengersByBooking = mutableMapOf<Int, MutableList<Int>>()
        val bookings = BookingTable.selectAll().toList()
        val maleNames = listOf("James", "Tom", "Daniel", "Michael", "Ethan", "Liam")
        val femaleNames = listOf("Priya", "Sarah", "Emily", "Aisha", "Sophie", "Laura")
        val lastNames = listOf("Walker", "Sharma", "Nguyen", "Smith", "Brown", "Patel")
        fun randomDOB() = "%04d-%02d-%02d".format(
            (1950..2010).random(),
            (1..12).random(),
            (1..28).random()
        )
        fun randomExpiry() = "%04d-%02d-%02d".format(
            (2026..2035).random(),
            (1..12).random(),
            (1..28).random()
        )
        fun randomPassport(country: String) =
            country + (100000000..999999999).random()
        bookings.forEach { bookingRow ->
            val bookingId = bookingRow[BookingTable.id]
            passengersByBooking[bookingId] = mutableListOf()
            // 1–3 passengers per booking
            val passengerCount = (1..3).random()
            repeat(passengerCount) {
                val isMale = (0..1).random() == 0
                val firstName = if (isMale) maleNames.random() else femaleNames.random()
                val lastName = lastNames.random()
                val email = "${firstName.lowercase()}.${lastName.lowercase()}@email.com"
                val nationality = listOf("GB", "IN", "US", "FR", "DE", "CN", "SP", "GE").random()
                val id = PassengerTable.insert { row ->
                    row[PassengerTable.bookingId] = bookingId
                    row[PassengerTable.email] = email
                    row[PassengerTable.checkedIn] = 0
                    row[PassengerTable.title] = if (isMale) "Mr" else "Ms"
                    row[PassengerTable.firstName] = firstName
                    row[PassengerTable.lastName] = lastName
                    row[PassengerTable.dateOfBirth] = randomDOB()
                    row[PassengerTable.gender] = if (isMale) "M" else "F"
                    row[PassengerTable.nationality] = nationality
                    row[PassengerTable.documentType] = "passport"
                    row[PassengerTable.documentNumber] = randomPassport(nationality)
                    row[PassengerTable.documentCountry] = nationality
                    row[PassengerTable.documentExpiry] = randomExpiry()
                }[PassengerTable.id]
                passengersByBooking[bookingId]!!.add(id)
            }
        }
        println("done generating passengers")
        passengersByBooking
    }
}
