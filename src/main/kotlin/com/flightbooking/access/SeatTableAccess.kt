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
}
