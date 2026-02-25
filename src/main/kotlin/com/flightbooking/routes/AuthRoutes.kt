package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*

fun Route.authRoutes() {
    get("/register") {
        call.respondText("<html><body><h2>Register (dev stub)</h2><form method=\"post\" action=\"/register\"><input name=\"email\"/><input name=\"password\" type=\"password\"/><button>Register</button></form></body></html>", io.ktor.http.ContentType.Text.Html)
    }

    post("/register") {
        val params = call.receiveParameters()
        val email = params["email"] ?: ""
        val password = params["password"] ?: ""
        // stub: just echo back
        call.respondText("registered: $email")
    }

    get("/login") {
        call.respondText("<html><body><h2>Login (dev stub)</h2><form method=\"post\" action=\"/login\"><input name=\"email\"/><input name=\"password\" type=\"password\"/><button>Login</button></form></body></html>", io.ktor.http.ContentType.Text.Html)
    }

    post("/login") {
        val params = call.receiveParameters()
        val email = params["email"] ?: ""
        val password = params["password"] ?: ""
        // stub success
        call.respondText("login ok for $email")
    }
}