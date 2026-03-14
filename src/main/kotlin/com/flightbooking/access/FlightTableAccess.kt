package com.flightbooking.access

import com.flightbooking.models.Flight
import com.flightbooking.models.toFlight
import com.flightbooking.tables.FlightTable

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
import org.jetbrains.exposed.sql.and

class FlightTableAccess {
    fun getAll(): List<Flight> = transaction {
        FlightTable.selectAll().map {
            it.toFlight()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Flight> = transaction {
        FlightTable.select { attribute eq value } 
            .map { it.toFlight() } 
    }
    fun createFlight(
        flightNumber: Int?, 
        originAirport: Int, 
        destinationAirport: Int, 
        scheduledDepartureTime: String?, 
        scheduledArrivalTime: String?, 
        status: String, 
        capacity: Int?
        ): Boolean = transaction { 
        FlightTable.insert { 
            it[FlightTable.flightNumber] = flightNumber 
            it[FlightTable.originAirport] = originAirport
            it[FlightTable.destinationAirport] = destinationAirport
            it[FlightTable.scheduledDepartureTime] = scheduledDepartureTime
            it[FlightTable.scheduledArrivalTime] = scheduledArrivalTime
            it[FlightTable.status] = status
            it[FlightTable.capacity] = capacity
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        FlightTable.deleteWhere { FlightTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = FlightTable.update({ FlightTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }

    fun getDomesticUKFlights(ukAirportIDs: List<Int>): List<Flight> = transaction {
        FlightTable.select {
            (FlightTable.originAirport inList ukAirportIDs) and
            (FlightTable.destinationAirport inList ukAirportIDs)
        }.map { it.toFlight() }
    }
}
