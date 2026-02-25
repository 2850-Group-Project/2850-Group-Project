package com.flightbooking

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.http.ContentType
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.http.content.*
import com.flightbooking.routes.authRoutes
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.ClasspathLoader
import java.io.StringWriter
import org.slf4j.event.Level

val PebbleEngineKey = AttributeKey<PebbleEngine>("PebbleEngine")

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val host = "0.0.0.0"
    embeddedServer(Netty, port = port, host = host) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(DefaultHeaders)
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
    install(CORS) {
        anyHost()
        allowHeader("*")
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
    }
    install(Sessions) {
        cookie<String>("COMP2850_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true
        }
    }

    // Pebble engine setup (explicit, matches your working project style)
    val pebbleEngine = PebbleEngine.Builder()
        .loader(ClasspathLoader().apply { prefix = "templates/" })
        .autoEscaping(true)
        .cacheActive(false)
        .build()
    attributes.put(PebbleEngineKey, pebbleEngine)

    routing {
        static("/static") {
            resources("static")
        }

        get("/") {
            call.respondText("<html><body><h1>Flight Booking (dev)</h1><p><a href= "/register\">Register</a ></p ></body></html>", ContentType.Text.Html)
        }

        get("/__health") {
            call.respondText("ok")
        }

        get("/__testpeb") {
            try {
                val writer = StringWriter()
                val template = pebbleEngine.getTemplate("register.peb")
                template.evaluate(writer, mapOf<String, Any>())
                call.respondText(writer.toString(), ContentType.Text.Html)
            } catch (e: Throwable) {
                e.printStackTrace()
                call.respondText("pebble error: ${e::class.simpleName}: ${e.message}", ContentType.Text.Plain)
            }
        }

        // mount auth routes if present; authRoutes file below is a minimal safe version
        try {
            authRoutes()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}