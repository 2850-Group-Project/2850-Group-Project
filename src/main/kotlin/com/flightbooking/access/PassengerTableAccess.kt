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

class PassengerTableAccess {
    fun getAll(): List<Passenger> = transaction {
        PassengerTable.selectAll().map {
            constructPassengerRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Passenger> = transaction {
        PassengerTable.select { attribute eq value } 
            .map { constructPassengerRecord(it) } 
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
    fun constructPassengerRecord(it: ResultRow): Passenger {
        return Passenger (
                        id = it[PassengerTable.id],
                        bookingId = it[PassengerTable.bookingId],
                        email = it[PassengerTable.email],
                        checkedIn = it[PassengerTable.checkedIn],
                        title = it[PassengerTable.title],
                        firstName = it[PassengerTable.firstName],
                        lastName = it[PassengerTable.lastName],
                        dateOfBirth = it[PassengerTable.dateOfBirth],
                        gender = it[PassengerTable.gender],
                        nationality = it[PassengerTable.nationality],
                        documentType = it[PassengerTable.documentType],
                        documentNumber = it[PassengerTable.documentNumber],
                        documentCountry = it[PassengerTable.documentCountry],
                        documentExpiry = it[PassengerTable.documentExpiry]
                    )
    }
}