package com.flightbooking

import com.flightbooking.database.DBFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.pebble.*
import io.ktor.server.routing.*
import com.flightbooking.routes.authRoutes

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

fun Application.module() {
    DBFactory.init()
    install(Pebble)
    routing {
        authRoutes()
    }
}