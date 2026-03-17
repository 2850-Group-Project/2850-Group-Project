package com.flightbooking.access

import com.flightbooking.models.Seat
import com.flightbooking.models.toSeat
import com.flightbooking.tables.SeatTable

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

import com.flightbooking.access.AirportTableAccess
import com.flightbooking.access.FlightTableAccess

class SeatTableAccess {
    fun getAll(): List<Seat> = transaction {
        SeatTable.selectAll().map {
            it.toSeat()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Seat> = transaction {
        SeatTable.select { attribute eq value } 
            .map { it.toSeat() } 
    }
    fun createSeat(
        flightId: Int,
        seatCode: String,
        cabinClass: String?,
        position: String?,
        extraLegroom: Int,
        exitRow: Int,
        reducedMobility: Int,
        status: String
        ):Boolean = transaction { 
        SeatTable.insert { 
            it[SeatTable.flightId] = flightId
            it[SeatTable.seatCode] = seatCode
            it[SeatTable.cabinClass] = cabinClass
            it[SeatTable.position] = position
            it[SeatTable.extraLegroom] = extraLegroom
            it[SeatTable.exitRow] = exitRow
            it[SeatTable.reducedMobility] = reducedMobility
            it[SeatTable.status] = status
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        SeatTable.deleteWhere { SeatTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = SeatTable.update({ SeatTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun generateUKDomesticSeats(
        airportAccess: AirportTableAccess = AirportTableAccess(),
        flightAccess: FlightTableAccess = FlightTableAccess()
    ) = transaction {
        println("generating UK domestic seats")
        val ukAirports = airportAccess.getByAttribute(com.flightbooking.tables.AirportTable.country,"United Kingdom")
        val ukIDs = ukAirports.map { it.id }
        val ukDomesticFlights = flightAccess.getDomesticUKFlights(ukIDs)
        val businessSeatLetters = listOf("A","C","D","F")
        val economySeatLetters = listOf("A","B","C","D","E","F")

        for (flight in ukDomesticFlights) {
            val capacity = flight.capacity ?: 150
            val businessRows = 1..5
            val businessSeats = businessRows.count() * businessSeatLetters.count()
            val remainingSeats = capacity - businessSeats
            val economyRowsNeeded = kotlin.math.ceil(remainingSeats / 6.0).toInt()
            val economyRows = 6 until (6 + economyRowsNeeded)

            for (row in businessRows) {
                for (letter in businessSeatLetters) {
                    val seatCode = "$row$letter"
                    createSeat(
                        flightId = flight.id,
                        seatCode = seatCode,
                        cabinClass = "Business",
                        position = when (letter) {
                            "A", "F" -> "window"
                            "C", "D" -> "aisle"
                            else -> null
                        },
                        extraLegroom = if (row == 1) 1 else 0,
                        exitRow = 0,
                        reducedMobility = if (row == 1 && (letter == "C" || letter == "D")) 1 else 0,
                        status = "available"
                    )
                }
            }
            for (row in economyRows) {
                for (letter in economySeatLetters) {
                    val seatCode = "$row$letter"
                    val exitRow = (row == 12 || row == 14)
                    val extraLegroom = exitRow || row == 6

                    createSeat(
                        flightId = flight.id,
                        seatCode = seatCode,
                        cabinClass = "Economy",
                        position = when (letter) {
                            "A", "F" -> "window"
                            "C", "D" -> "aisle"
                            else -> "middle"
                        },
                        extraLegroom = if (extraLegroom) 1 else 0,
                        exitRow = if (exitRow) 1 else 0,
                        reducedMobility = 0,
                        status = "available"
                    )
                }
            }
        }
        println("done generating")
    }
}
