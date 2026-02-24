package com.example

import database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.pebble.*
import io.ktor.server.routing.*
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.ClasspathLoader
import routes.authRoutes

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

fun Application.module() {

    DatabaseFactory.init()

    install(Pebble) {
        engine = PebbleEngine.Builder()
            .loader(
                ClasspathLoader().apply {
                    prefix = "templates"
                }
            )
            .build()
    }

    routing {
        authRoutes()
    }
}