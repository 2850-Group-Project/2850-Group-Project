package access

import com.flightbooking.models.SeatAssignment
import com.flightbooking.models.toSeatAssignment
import com.flightbooking.tables.SeatAssignmentTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow
import access.SeatAssignmentTableAccess
import org.jetbrains.exposed.sql.Column

class SeatAssignmentTableAccess {
    fun getAll(): List<SeatAssignment> = transaction {
        SeatAssignmentTable.selectAll().map {
            constructSeatAssignmentRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<SeatAssignment> = transaction {
        SeatAssignmentTable.select { attribute eq value } 
            .map { constructSeatAssignmentRecord(it) } 
    }
    fun constructSeatAssignmentRecord(it: ResultRow): SeatAssignment {
        return SeatAssignment (
                        id = it[SeatAssignmentTable.id],
                        passengerId = it[SeatAssignmentTable.passengerId],
                        bookingSegmentId = it[SeatAssignmentTable.bookingSegmentId],
                        seatId = it[SeatAssignmentTable.seatId],
                    )
    }
}