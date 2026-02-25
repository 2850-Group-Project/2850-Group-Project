package com.flightbooking

import com.flightbooking.database.DBFactory
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.pebble.*
import io.ktor.server.routing.*
import com.flightbooking.routes.authRoutes

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val host = "0.0.0.0"
    embeddedServer(Netty, port = port, host = host) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    DBFactory.init()
    install(Pebble)
    routing {
        authRoutes()
    }
}