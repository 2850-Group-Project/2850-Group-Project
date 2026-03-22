package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.sessions.*

import com.flightbooking.models.UserSession
import com.flightbooking.models.BookingSession

import com.flightbooking.models.FlightSearch
import com.flightbooking.models.PassengerInput

fun Route.paymentRoutes() {
    get("/payment") {
        val userSession = call.sessions.get<UserSession>()
        val bookingSession = call.sessions.get<BookingSession>()
        println("bookingSession = $bookingSession")
        println("bookingSession.search = ${bookingSession?.search}")
        println("bookingSession.passengers = ${bookingSession?.passengers}")

        if (userSession == null) {
            call.respondRedirect("/login")
            return@get
        }
        if (bookingSession == null) {
            call.respondRedirect("/home")
            return@get
        }

        call.respond(
            PebbleContent(
                "payment.peb",
                mapOf(
                    "userSession" to userSession,
                    "bookingSession" to bookingSession
                )
            )
        )
    }
    get("/test-payment") {
        val fakeSearch = FlightSearch(
            tripType = "oneway",
            origin = "London",
            destination = "Dubai",
            departureDate = "2026-08-12",
            returnDate = "",
            adults = "1",
            children = "0",
            infants = "0"
        )

        val fakeBooking = BookingSession(
            outboundFlightId = 123,
            outboundFareId = 456,
            search = fakeSearch,
            passengers = listOf(
                PassengerInput(
                    type = "adult",
                    title = "Mr",
                    firstName = "John",
                    lastName = "Doe",
                    dateOfBirth = "1990-01-01",
                    gender = "male",
                    email = "john@example.com",
                    nationality = "gb",
                    documentType = "passport",
                    documentNumber = "12345678",
                    documentCountry = "gb",
                    documentExpiry = "2030-01-01"
                )
            )
        )

        val fakeUser = UserSession(
            userEmail = "test@example.com",
            firstName = "Test"
        )


        call.respond(
            PebbleContent(
                "payment.peb",
                mapOf(
                    "userSession" to fakeUser,
                    "bookingSession" to fakeBooking
                )
            )
        )
    }

    post("/payment") {
        val userSession = call.sessions.get<UserSession>()
        val bookingSession = call.sessions.get<BookingSession>()

        if (userSession == null) {
            call.respondRedirect("/login")
            return@post
        }
        if (bookingSession == null) {
            call.respondRedirect("/home")
            return@post
        }

        val params = call.receiveParameters()
        val cardNumber = params["cardNumber"]
        val expiry = params["expiry"]
        val cvv = params["cvv"]

        println("Payment submitted:")
        println("Card: $cardNumber, Expiry: $expiry, CVV: $cvv")
        println("Passengers: ${bookingSession.passengers}")

        // TODO: process payment, create booking, etc.

        call.respondRedirect("/confirmation")
    }
    
}