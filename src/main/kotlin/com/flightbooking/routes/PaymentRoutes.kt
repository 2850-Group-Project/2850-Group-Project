package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.sessions.*

import com.flightbooking.models.UserSession
import com.flightbooking.models.BookingSession

fun Route.paymentRoutes() {
    get("/payment") {
        val userSession = call.sessions.get<UserSession>()
        val bookingSession = call.sessions.get<BookingSession>()
        println("DEBUG bookingSession = $bookingSession")
        println("DEBUG bookingSession.search = ${bookingSession?.search}")
        println("DEBUG bookingSession.passengers = ${bookingSession?.passengers}")

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
}