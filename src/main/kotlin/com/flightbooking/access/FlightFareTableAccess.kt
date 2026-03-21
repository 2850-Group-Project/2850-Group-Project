package com.flightbooking.access

import com.flightbooking.models.FlightFare
import com.flightbooking.models.toFlightFare
import com.flightbooking.tables.FlightFareTable

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
import com.flightbooking.access.FareClassTableAccess

class FlightFareTableAccess {
    fun getAll(): List<FlightFare> = transaction {
        FlightFareTable.selectAll().map {
            it.toFlightFare()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<FlightFare> = transaction {
        FlightFareTable.select { attribute eq value } 
            .map { it.toFlightFare() } 
    }
    
    @Suppress("LongParameterList")
    fun createFlightFare(
        flightId: Int, 
        fareClassId: Int, 
        price: Double, 
        currency: String, 
        seatsAvailable: Int, 
        saleStart: String?, 
        saleEnd: String?
        ): Boolean = transaction { 
        FlightFareTable.insert { 
            it[FlightFareTable.flightId] = flightId 
            it[FlightFareTable.fareClassId] = fareClassId 
            it[FlightFareTable.price] = price 
            it[FlightFareTable.currency] = currency 
            it[FlightFareTable.seatsAvailable] = seatsAvailable 
            it[FlightFareTable.saleStart] = saleStart 
            it[FlightFareTable.saleEnd] = saleEnd 
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        FlightFareTable.deleteWhere { FlightFareTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = FlightFareTable.update({ FlightFareTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    
    fun generateUKDomesticFares(
        airportAccess: AirportTableAccess = AirportTableAccess(),
        flightAccess: FlightTableAccess = FlightTableAccess(),
        fareClassAccess: FareClassTableAccess = FareClassTableAccess()
    ) = transaction {
        println("generating UK fares")
        val ukAirports = airportAccess.getByAttribute(com.flightbooking.tables.AirportTable.country,"United Kingdom")
        val ukIDs = ukAirports.map { it.id }
        val ukDomesticFlights = flightAccess.getDomesticUKFlights(ukIDs)

        val fareClasses = fareClassAccess.getAll()

        for (flight in ukDomesticFlights) {
            for (fareClass in fareClasses) {
                val existing = getByAttribute(
                    com.flightbooking.tables.FlightFareTable.flightId, flight.id
                ).any { it.fareClassId == fareClass.id }
                if (existing) continue
                val multiplier = when (fareClass.id) {
                    1 -> 1.0
                    2 -> 1.2
                    3 -> 2.0
                    4 -> 1.5
                    5 -> 1.8
                    else -> 1.0
                }
                val basePrice = 40 + (flight.id % 50) * 1.2
                val capacity = flight.capacity ?: 100
                val seatsAvailable = (capacity / (fareClass.id + 2)).coerceAtLeast(5)

                createFlightFare(
                    flightId = flight.id,
                    fareClassId = fareClass.id,
                    price = basePrice * multiplier,
                    currency = "GBP",
                    seatsAvailable = seatsAvailable,
                    saleStart = null,
                    saleEnd = null
                )
            }
        }
        println("generated")
    }
}