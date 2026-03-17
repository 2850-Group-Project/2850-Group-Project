package com.flightbooking.access

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

import com.flightbooking.tables.PassengerTable
import com.flightbooking.tables.BookingSegmentTable
import com.flightbooking.tables.FlightFareTable
import com.flightbooking.tables.FlightTable
import com.flightbooking.tables.FareClassTable
import com.flightbooking.tables.SeatTable
import org.jetbrains.exposed.sql.and
class SeatAssignmentTableAccess {
    fun getAll(): List<SeatAssignment> = transaction {
        SeatAssignmentTable.selectAll().map {
            it.toSeatAssignment()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<SeatAssignment> = transaction {
        SeatAssignmentTable.select { attribute eq value } 
            .map { it.toSeatAssignment() } 
    }
    fun createSeatAssignment(
        passengerId: Int,
        bookingSegmentId: Int,
        seatId: Int?
        ):Boolean = transaction { 
        SeatAssignmentTable.insert { 
            it[SeatAssignmentTable.passengerId] = passengerId
            it[SeatAssignmentTable.bookingSegmentId] = bookingSegmentId
            it[SeatAssignmentTable.seatId] = seatId
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        SeatAssignmentTable.deleteWhere { SeatAssignmentTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = SeatAssignmentTable.update({ SeatAssignmentTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun generateSeatAssignments() = transaction {
        println("generating seat assignment")
        val ukDomesticFlightIds = listOf(
            2080, 4229, 4238, 4684, 4912,
            4924, 4955, 5754, 5791, 7388,
            8073, 8071, 7201, 8981, 4930
        )
        val segments = BookingSegmentTable
            .select { BookingSegmentTable.flightId inList ukDomesticFlightIds }
            .toList()
        segments.forEach { segment ->
            val bookingSegmentId = segment[BookingSegmentTable.id]
            val bookingId = segment[BookingSegmentTable.bookingId]
            val flightId = segment[BookingSegmentTable.flightId]
            val flightFareId = segment[BookingSegmentTable.flightFareId]
            val passenger = PassengerTable
                .select { PassengerTable.bookingId eq bookingId }
                .firstOrNull()
            if (passenger == null) {
                println("booking has no passenger skip")
                return@forEach
            }
            val passengerId = passenger[PassengerTable.id]

            val fareRow = FlightFareTable
                .select { FlightFareTable.id eq flightFareId }
                .first()
                
            val fareClassId = fareRow[FlightFareTable.fareClassId]
            val fareClassRow = FareClassTable
                .select { FareClassTable.id eq fareClassId }
                .first()
            val cabinClass = fareClassRow[FareClassTable.cabinClass]
            val availableSeats = SeatTable
                .select {
                    (SeatTable.flightId eq flightId) and
                    (SeatTable.cabinClass eq cabinClass) and
                    (SeatTable.status eq "available")
                }
                .toList()
            if (availableSeats.isEmpty()) {
                println("no seats left for flight $flightId in $cabinClass")
                return@forEach
            }
            val seatRow = availableSeats.random()
            val seatId = seatRow[SeatTable.id]
            SeatAssignmentTable.insert {
                it[SeatAssignmentTable.passengerId] = passengerId
                it[SeatAssignmentTable.bookingSegmentId] = bookingSegmentId
                it[SeatAssignmentTable.seatId] = seatId
            }
            SeatTable.update({ SeatTable.id eq seatId }) {
                it[SeatTable.status] = "taken"
            }
        }
        println("done generating")
    }

}
