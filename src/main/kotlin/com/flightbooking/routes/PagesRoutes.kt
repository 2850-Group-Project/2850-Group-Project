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

        call.respond(PebbleContent("home.peb", mapOf()))
    }
}