package com.example

import database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.pebble.*
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.ktor.server.routing.*
import routes.authRoutes

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

fun Application.module() {

    DatabaseFactory.init()

    install(Pebble) {
        loader = ClasspathLoader().apply {
            prefix = "templates"
        }
    }

    routing {
        authRoutes()
    }
}