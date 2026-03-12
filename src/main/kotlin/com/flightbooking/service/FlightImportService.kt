package com.flightbooking.service

import com.flightbooking.api.AviationStackClient
import com.flightbooking.access.AirportTableAccess
import com.flightbooking.access.FlightTableAccess
import com.flightbooking.models.Flight
import com.flightbooking.tables.FlightTable
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class FlightImportService(
    private val client: AviationStackClient,
    private val airportAccess: AirportTableAccess,
    private val flightAccess: FlightTableAccess
) {
    suspend fun importAllFlights() {
        val airports = airportAccess.getAll()
        val iataToID = airports.associate { it.iataCode!! to it.id!! }

        var fetched = 0
        var skippedMissingIata = 0
        var skippedUnknownAirport = 0
        var inserted = 0

        transaction { // wiping existing flights
            FlightTable.deleteAll()
        }
        val limit = 100 //max num you can import at once
        var offset = 0
        var total = Int.MAX_VALUE

        while (offset < total) {
            val response = client.getFlights(limit, offset)
            val pagination = response.pagination ?: break
            total = pagination.total ?: break

            response.data.forEach { apiFlight ->
                fetched++
                val originIata = apiFlight.departure.iata
                val destIata = apiFlight.arrival.iata

                if (originIata == null || destIata == null) {
                    skippedMissingIata++
                    return@forEach
                }

                val originID = iataToID[originIata]
                val destID = iataToID[destIata]

                if (originID == null || destID == null) {
                    skippedUnknownAirport++
                    return@forEach
                }

                val flightNumber = apiFlight.flight.number
                val departureTime = apiFlight.departure.scheduled
                val arrivalTime = apiFlight.arrival.scheduled
                val status = apiFlight.flightStatus ?: "scheduled"

                flightAccess.createFlight(
                    flightNumber = flightNumber,
                    originAirport = originID,
                    destinationAirport = destID,
                    scheduledDepartureTime = departureTime,
                    scheduledArrivalTime = arrivalTime,
                    status = status,
                    capacity = null
                )
                inserted++
            }
            offset += limit
        }
        println("fetched: $fetched")
        println("skipped missing iata: $skippedMissingIata")
        println("skipped unknown airport: $skippedUnknownAirport")
        println("insertedf: $inserted")
    }
}