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
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

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
    fun addSeatAssignment(
        passengerId: Int,
        bookingSegmentId: Int,
        seatId: Int?
        ): SeatAssignment = transaction { 
        val id = SeatAssignmentTable.insert { 
            it[SeatAssignmentTable.passengerId] = passengerId
            it[SeatAssignmentTable.bookingSegmentId] = bookingSegmentId
            it[SeatAssignmentTable.seatId] = seatId
        } get SeatAssignmentTable.id 
        SeatAssignment( 
            id = id!!,
            passengerId = passengerId,
            bookingSegmentId = bookingSegmentId,
            seatId = seatId
        ) }
    fun deleteByID(id: Int) = transaction { 
        SeatAssignmentTable.deleteWhere { SeatAssignmentTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = SeatAssignmentTable.update({ SeatAssignmentTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun constructSeatAssignmentRecord(it: ResultRow): SeatAssignment {
        return SeatAssignment (
                        id = it[SeatAssignmentTable.id],
                        passengerId = it[SeatAssignmentTable.passengerId],
                        bookingSegmentId = it[SeatAssignmentTable.bookingSegmentId],
                        seatId = it[SeatAssignmentTable.seatId],
                    )
    }
}