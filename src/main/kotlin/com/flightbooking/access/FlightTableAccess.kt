package com.flightbooking.access

import com.flightbooking.models.Flight
import com.flightbooking.models.toFlight
import com.flightbooking.models.FlightWithFares
import com.flightbooking.models.FareOption

import com.flightbooking.tables.FlightTable
import com.flightbooking.tables.AirportTable
import com.flightbooking.tables.FlightFareTable
import com.flightbooking.tables.FareClassTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.JoinType

import java.time.LocalDate

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

    // gets flights 10 days around the selected date, necessary for the flight search page 
    // (users should be able to choose from a range of dates around their selected date in case they see something cheaper)
    fun getFlightsAroundDate(
        originCode: String, 
        destinationCode: String, 
        date: LocalDate,
        ): List<FlightWithFares> {
        val dateFrom = date.minusDays(5).toString()
        val dateTo = date.plusDays(5).toString()

        // aliases for simplicity
        val OriginAirport = AirportTable.alias("origin")
        val DestinationAirport = AirportTable.alias("destination")

        return transaction {
            FlightTable
                .join(OriginAirport, JoinType.INNER, FlightTable.originAirport, OriginAirport[AirportTable.id])
                .join(DestinationAirport, JoinType.INNER, FlightTable.destinationAirport, DestinationAirport[AirportTable.id])
                .join(FlightFareTable, JoinType.LEFT, FlightTable.id, FlightFareTable.flightId)
                .join(FareClassTable, JoinType.LEFT, FlightFareTable.fareClassId, FareClassTable.id)
                .select {
                    (OriginAirport[AirportTable.iataCode] eq originCode) and
                    (DestinationAirport[AirportTable.iataCode] eq destinationCode) and
                    (FlightTable.scheduledDepartureTime greaterEq dateFrom) and
                    (FlightTable.scheduledDepartureTime lessEq dateTo)
                }
                .toList()
                .filter { it[FlightFareTable.id] != null }
                .groupBy { it[FlightTable.id] }
                .map { (_, rows) ->
                    val first = rows.first()
                    FlightWithFares(
                        flightId = first[FlightTable.id],
                        flightNumber = first[FlightTable.flightNumber],
                        departureTime = first[FlightTable.scheduledDepartureTime],
                        arrivalTime = first[FlightTable.scheduledArrivalTime],
                        status = first[FlightTable.status],
                        capacity = first[FlightTable.capacity],
                        originCode = first[OriginAirport[AirportTable.iataCode]],
                        destinationCode = first[DestinationAirport[AirportTable.iataCode]],
                        fares = rows.map { row ->
                            FareOption(
                                fareId = row[FlightFareTable.id],
                                fareClassId = row[FlightFareTable.fareClassId],
                                displayName = row[FareClassTable.displayName],
                                cabinClass = row[FareClassTable.cabinClass],
                                price = row[FlightFareTable.price],
                                currency = row[FlightFareTable.currency],
                                seatsAvailable = row[FlightFareTable.seatsAvailable]
                            )
                        }
                    )
                }
        }
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
}
