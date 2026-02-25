package access

import com.flightbooking.models.BookingSegment
import com.flightbooking.models.toBookingSegment
import com.flightbooking.tables.BookingSegmentTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.BookingSegmentTableAccess // ?

class BookingSegmentTableAccess {
    fun getAll(): List<BookingSegment> = transaction {
        BookingSegmentTable.selectAll().map {
            constructBookingSegmentRecord(it)
        }
    }
    fun constructBookingSegmentRecord(it: ResultRow): BookingSegment {
        return BookingSegment (
                        id = it[BookingSegmentTable.id],
                        bookingId = it[BookingSegmentTable.bookingId],
                        flightId = it[BookingSegmentTable.flightId],
                        flightFareId = it[BookingSegmentTable.flightFareId],
                    )
    }
}