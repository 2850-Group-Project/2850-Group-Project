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
import access.AirportTableAccess

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

    """
    TESTING ACCESS FUNCTIONS pls ignore or comment it out
    """
    // AirportTableAccess connection test
    val airporttableaccess = AirportTableAccess()
    val airports = airporttableaccess.getAll()
    println("Found ${airports.size} airports in the database")

    // testing insert
    try {
        val inserted = airporttableaccess.addAirport(
            iataCode = "TST", 
            name = "Test Airport", 
            city = "TestTown", 
            country = "Testland"
        )
        println("Inserted airport with ID: ${inserted.id}")
    } catch (e: Throwable) {
        println("FAILED TO INSERT, ALREADY EXISTS: ${e.message}")
    }

    "
    val updated = airporttableaccess.updateRecordByAttribute(
        id = 7, 
        column = Column<String>("name"), 
        value = "Updated Test Airport"
    )
    println("Update successful on record with ID: ${updated.id}")
    "
    
    airporttableaccess.deleteByID(7) // cleanup
    """
    end of testing access functions
    """

    routing {
        get("/") {
            call.respondRedirect("/login")
        }
        get("/__health") {
            call.respondText("ok")
        }
        authRoutes()
    }
}