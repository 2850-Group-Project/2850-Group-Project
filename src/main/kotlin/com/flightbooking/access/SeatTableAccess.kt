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
import com.flightbooking.tables.FlightTable

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
        activeFlights: List<Int>
    ) = transaction {
        println("generating UK domestic seats")
        val ukDomesticFlights = FlightTable
            .select { FlightTable.id inList activeFlights }
            .toList()
        val businessSeatLetters = listOf("A","C","D","F")   // 2-2 layout
        val premiumSeatLetters  = listOf("A","B","C","D","E","F")
        val economySeatLetters  = listOf("A","B","C","D","E","F")
        for (flight in ukDomesticFlights) {
            val flightId = flight[FlightTable.id]
            val capacity = flight[FlightTable.capacity] ?: 150
            val businessRows = 1..5
            val businessSeats = businessRows.count() * businessSeatLetters.count()
            val premiumRows = 6..9
            val premiumSeats = premiumRows.count() * premiumSeatLetters.count()
            val remainingSeats = capacity - (businessSeats + premiumSeats)
            val economyRowsNeeded = kotlin.math.ceil(remainingSeats / 6.0).toInt()
            val economyStart = premiumRows.last + 1
            val economyRows = economyStart until (economyStart + economyRowsNeeded)
            for (row in businessRows) {
                for (letter in businessSeatLetters) {
                    createSeat(
                        flightId = flightId,
                        seatCode = "$row$letter",
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
            for (row in premiumRows) {
                for (letter in premiumSeatLetters) {
                    val extraLegroom = (row == premiumRows.first)
                    createSeat(
                        flightId = flightId,
                        seatCode = "$row$letter",
                        cabinClass = "Premium Economy",
                        position = when (letter) {
                            "A", "F" -> "window"
                            "C", "D" -> "aisle"
                            else -> "middle"
                        },
                        extraLegroom = if (extraLegroom) 1 else 0,
                        exitRow = 0,
                        reducedMobility = 0,
                        status = "available"
                    )
                }
            }
            for (row in economyRows) {
                val isExitRow = (row == economyRows.first + 4) || (row == economyRows.first + 5)
                val extraLegroom = isExitRow || row == economyRows.first
                for (letter in economySeatLetters) {
                    createSeat(
                        flightId = flightId,
                        seatCode = "$row$letter",
                        cabinClass = "Economy",
                        position = when (letter) {
                            "A", "F" -> "window"
                            "C", "D" -> "aisle"
                            else -> "middle"
                        },
                        extraLegroom = if (extraLegroom) 1 else 0,
                        exitRow = if (isExitRow) 1 else 0,
                        reducedMobility = 0,
                        status = "available"
                    )
                }
            }
        }
        println("done generating")
    }
}
