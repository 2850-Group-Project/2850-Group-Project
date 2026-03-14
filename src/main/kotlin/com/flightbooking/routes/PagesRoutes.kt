package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.sessions.*

import com.flightbooking.access.FlightTableAccess
import com.flightbooking.access.AirportTableAccess

import com.flightbooking.models.UserSession
import com.flightbooking.models.FlightSearch

import com.flightbooking.routes.authRoutes

import io.ktor.http.HttpStatusCode

import java.time.LocalDate
import org.jetbrains.exposed.sql.compoundAnd

fun Route.pagesRoutes() {
    get("/home") {
        // need to add check to make sure user is logged in before loading the home page
        val session = call.sessions.get<UserSession>()
        // println(session)

        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }

        val airports = AirportTableAccess().getAll()

        call.respond(PebbleContent("home.peb", mapOf(
            "userSession" to session,
            "airports" to airports,
        )))
    }

    get("/flights/search") {
        // need to add check to make sure user is logged in before loading the flight search page
        // we also need to check that all the required data is provided
        val session = call.sessions.get<UserSession>()
        
        // println("SESSION DATA VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV")
        // println(session)
        // println("SESSION DATA ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
        
        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }
        
        // packaging all search data into one class
        val search = FlightSearch(
            tripType = call.request.queryParameters["trip_type"],
            origin = AirportTableAccess().getCityByOrigin(call.request.queryParameters["origin"] ?: ""),
            destination = AirportTableAccess().getCityByOrigin(call.request.queryParameters["destination"] ?: ""),
            departureDate = call.request.queryParameters["departure_date"],
            returnDate = call.request.queryParameters["return_date"],
            adults = call.request.queryParameters["adults"],
            children = call.request.queryParameters["children"],
            infants = call.request.queryParameters["infants"],
        )

        if (search.origin == null || search.destination == null) {
            return@get
        }

        val airports = AirportTableAccess()

        // TODO: properly handle error when the inputted airport is not found, just isnt great
        // this should be checked beforehand however, on the home page, since we should only ever pass error free
        // form inputs to stages further up
        val originAirportCode = airports.getAirportCodeByOrigin(search.origin) ?: ""
        val destinationAirportCode = airports.getAirportCodeByOrigin(search.destination) ?: ""

        println(originAirportCode)
        println(destinationAirportCode)
        
        // println("FLIGHT SEARCH DATA VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV")
        // println(search)
        // println("FLIGHT SEARCH DATA ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")

        // get outbound flight data
        val flightTable = FlightTableAccess()
        val outboundFlights = flightTable.getFlightsAroundDate(originAirportCode, destinationAirportCode, LocalDate.parse(search.departureDate))

        // get inbound flight data (for trip type = return)
        val inboundFlights = flightTable.getFlightsAroundDate(destinationAirportCode, originAirportCode, LocalDate.parse(search.returnDate))

        call.respond(PebbleContent("flight_search.peb", mapOf(
            "userSession" to session,
            "isLoggedIn" to true,
            "search" to search,
            "outboundFlights" to outboundFlights,
            "returnFlights" to inboundFlights,
        )))
    }
}