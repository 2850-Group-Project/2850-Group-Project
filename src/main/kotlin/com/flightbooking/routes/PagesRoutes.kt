package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.sessions.*
import com.flightbooking.models.UserSession
import com.flightbooking.routes.authRoutes

fun Route.pagesRoutes() {
    get("/home") {
        // need to add check to make sure user is logged in before loading the home page
        val session = call.sessions.get<UserSession>()
        println(session)

        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }

        call.respond(PebbleContent("home.peb", mapOf(
            "userSession" to session
        )))
    }

    get("/flights/search") {
        // need to add check to make sure user is logged in before loading the flight search page
        // we also need to check that all the required data is provided
        val session = call.sessions.get<UserSession>()
        println(session)
        println("hit the flight search page digga")

        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }

        call.respond(PebbleContent("flight_search.peb", mapOf(
            "userSession" to session,
            "isLoggedIn" to true
        )))
    }
}