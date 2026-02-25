 package com.flightbooking

import com.flightbooking.database.DBFactory
import com.flightbooking.routes.authRoutes
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.pebble.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import org.slf4j.event.Level

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val host = "0.0.0.0"
    embeddedServer(Netty, port = port, host = host) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respondText("Internal Server Error: ${cause::class.simpleName}: ${cause.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation)
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates" 
        })
        cacheActive(false) 
    }
    install(Sessions) {
        cookie<String>("COMP2850_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true
        }
    }
    try {
        DBFactory.init()
    } catch (e: Throwable) {
        e.printStackTrace()
        throw e
    }
    routing {
        get("/") {
            call.respondText("root ok")
        }
        get("/__health") {
            call.respondText("ok")
        }
        authRoutes()
    }
}