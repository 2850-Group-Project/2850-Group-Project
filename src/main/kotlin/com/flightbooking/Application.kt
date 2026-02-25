package com.flightbooking

import com.flightbooking.database.DBFactory
import com.flightbooking.routes.authRoutes
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

    // create Pebble engine similar to your previous working project
    val pebbleEngine = PebbleEngine.Builder()
        .loader(ClasspathLoader().apply { prefix = "templates/" })
        .autoEscaping(true)
        .cacheActive(false)
        .build()
    attributes.put(PebbleEngineKey, pebbleEngine)
    environment.monitor.subscribe(ApplicationStarted) {
        log.info("✓ Pebble templates loaded from resources/templates/")
        log.info("✓ Server running on port ${environment.connectors.firstOrNull()?.port ?: "unknown"}")
    }

    // initialize DB but don't crash the whole app if DB init fails
    try {
        DBFactory.init()
    } catch (e: Throwable) {
        e.printStackTrace()
        log.warn("DB init failed (starting app anyway) — check DB configuration")
    }

    routing {
        // static files from resources/static
        static("/static") {
            resources("static")
        }

        get("/") {
            call.respondText(renderTemplate(this.call, "index.peb", mapOf("title" to "Flight Booking")), ContentType.Text.Html)
        }

        get("/__health") {
            call.respondText("ok")
        }

        get("/__testpeb") {
            try {
                call.respondText(renderTemplate(this.call, "register.peb", mapOf("error" to null)), ContentType.Text.Html)
            } catch (e: Throwable) {
                e.printStackTrace()
                call.respondText("pebble error: ${e::class.simpleName}: ${e.message}", ContentType.Text.Plain)
            }
        }

        // mount auth routes (your existing file)
        try {
            authRoutes()
        } catch (e: Throwable) {
            e.printStackTrace()
            log.warn("authRoutes failed to mount: ${e.message}")
        }

        // a minimal tasks page (safe fallback if your taskRoutes isn't present)
        get("/tasks") {
            val model = mapOf("title" to "Tasks (demo)", "tasks" to listOf<String>())
            call.respondText(renderTemplate(this.call, "tasks/index.peb", model), ContentType.Text.Html)
        }
    }
}

/**
 * Render a Pebble template to string using the engine stored on application attributes.
 * templateName is path relative to resources/templates/, e.g. "tasks/index.peb" or "register.peb"
 */
fun renderTemplate(call: ApplicationCall, templateName: String, context: Map<String, Any?> = emptyMap()): String {
    val app = call.application
    val engine = app.attributes.getOrNull(PebbleEngineKey)
        ?: throw IllegalStateException("PebbleEngine not found in application attributes")
    val writer = StringWriter()
    val template = engine.getTemplate(templateName)
    val enriched = context.mapValues { it.value ?: "" }
    template.evaluate(writer, enriched)
    return writer.toString()
}