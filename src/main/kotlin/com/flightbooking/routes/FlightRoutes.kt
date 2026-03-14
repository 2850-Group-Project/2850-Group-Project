package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.sessions.*

import com.flightbooking.models.UserSession

fun Route.flightRoutes() {
    post("/flights/select") {
        val session = call.sessions.get<UserSession>()
        println(session)

        if (session == null) {
            call.respondRedirect("/login")
            return@post
        }

        
    }
}