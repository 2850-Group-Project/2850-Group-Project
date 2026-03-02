package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*

fun Route.pagesRoutes() {
    get("/home") {
        call.respond(PebbleContent("home.peb", mapOf()))
    }
}