package com.flightbooking

import com.flightbooking.database.DBFactory
import com.flightbooking.routes.authRoutes
import com.flightbooking.routes.staffAuthRoutes
import com.flightbooking.routes.staffPagesRoutes
import com.flightbooking.models.StaffSession
import com.flightbooking.routes.pagesRoutes
import com.flightbooking.routes.staffBookingsRoutes
import com.flightbooking.models.UserSession
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.EngineMain
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.pebble.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.http.HttpStatusCode
import org.slf4j.event.Level

/**
 * Entry point for the Ktor application.
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * Main application module. Installs all plugins, configures sessions,
 * initialises the database, and registers all routes.
 */
fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
    }

    install(StatusPages) {
        // check for 404 error (page not found)
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(PebbleContent("404.peb", emptyMap()))
        }
        // any other 500 error (server error)
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
        }
    }

    install(ContentNegotiation)

    install(Pebble) {
        // Sets prefix for pebble templates so we don't need to repeat it
        loader(ClasspathLoader().apply {
            prefix = "templates" 
        })
        cacheActive(false) 
    }

    install(Sessions) {
        // Create user/staff session data as cookie to be used later
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true
        }
        cookie<StaffSession>("STAFF_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true
        }
    }

    // Start up DB abstraction instance
    
    try {
        DBFactory.init()
    } catch (e: Throwable) {
        log.error("Failed to init DBFactory", e)
        dispose() // gracefull exit instead of abrupt error thrown
    }

    // Prepare and load the routes
    routing {
        staticResources("/static", "static") // allows easy stylesheet reference (like pebble "templates" prefix)
        
        // TODO: WE NEED TO MOVE THESE AWAY
        get("/") {
            call.respondRedirect("/login")
        }
        get("/__health") {
            call.respondText("ok")
        }
        authRoutes()
        staffAuthRoutes()
        pagesRoutes()
        staffPagesRoutes()
        staffBookingsRoutes()
    }
}