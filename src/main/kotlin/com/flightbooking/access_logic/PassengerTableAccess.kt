package access

import com.flightbooking.models.Passenger
import com.flightbooking.models.toPassenger
import com.flightbooking.tables.PassengerTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.PassengerTableAccess

class PassengerTableAccess {
    fun getAll(): List<Passenger> = transaction {
        PassengerTable.selectAll().map {
            constructPassengerRecord(it)
        }
    }
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