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
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

class BookingTableAccess {
    fun getAll(): List<Booking> = transaction {
        BookingTable.selectAll().map {
            constructBookingRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Booking> = transaction {
        BookingTable.select { attribute eq value } 
            .map { constructBookingRecord(it) } 
    }
    fun addBooking(
        userId: Int?, 
        bookingReference: String, 
        paymentId: Int?, 
        createdAt: String, 
        bookingStatus: String, 
        cancelledAt: String?, 
        amendable: Int
        ): Booking = transaction { 
        val id = BookingTable.insert { 
            it[BookingTable.userId] = userId 
            it[BookingTable.bookingReference] = bookingReference 
            it[BookingTable.paymentId] = paymentId 
            it[BookingTable.createdAt] = createdAt 
            it[BookingTable.bookingStatus] = bookingStatus 
            it[BookingTable.cancelledAt] = cancelledAt 
            it[BookingTable.amendable] = amendable
        } get BookingTable.id 
        Booking( 
            id = id!!, 
            userId = userId, 
            bookingReference = bookingReference, 
            paymentId = paymentId, 
            createdAt = createdAt, 
            bookingStatus = bookingStatus, 
            cancelledAt = cancelledAt, 
            amendable = amendable
        ) }
    fun deleteByID(id: Int) = transaction { 
        BookingTable.deleteWhere { BookingTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = BookingTable.update({ BookingTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
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