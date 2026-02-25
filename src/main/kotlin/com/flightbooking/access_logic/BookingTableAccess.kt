package access

import com.flightbooking.models.Booking
import com.flightbooking.models.toBooking
import com.flightbooking.tables.BookingTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.BookingTableAccess

class BookingTableAccess {
    fun getAll(): List<Booking> = transaction {
        BookingTable.selectAll().map {
            constructBookingRecord(it)
        }
    }
    fun constructBookingRecord(it: ResultRow): Booking {
        return Booking (
                        id = it[BookingTable.id],
                        userId = it[BookingTable.userId],
                        bookingReference = it[BookingTable.bookingReference],
                        paymentId = it[BookingTable.paymentId],
                        createdAt = it[BookingTable.createdAt],
                        bookingStatus = it[BookingTable.bookingStatus],
                        cancelledAt = it[BookingTable.cancelledAt],
                        amendable = it[BookingTable.amendable]
                    )
    }
}