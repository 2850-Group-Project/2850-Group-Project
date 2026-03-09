package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.sessions.*
import com.flightbooking.service.AuthService
import com.flightbooking.routes.pagesRoutes
import com.flightbooking.models.UserSession
import com.flightbooking.models.User
import com.flightbooking.access.UserTableAccess

fun Route.authRoutes() {
    get("/register") {
        call.respond(PebbleContent("register.peb", mapOf()))
    }
    post("/register") {
    val params = call.receiveParameters()
    val email = params["email"]?.trim().orEmpty()
    val password = params["password"].orEmpty()
    val firstName = params["firstName"]?.trim()
    val lastName = params["lastName"]?.trim()

    if (AuthService.register(email, password, firstName, lastName)) {
        call.respondRedirect("/login")
    } else {
        call.respond(PebbleContent("register.peb", mapOf("error" to "User already exists")))
    }
}
    get("/login") {
        call.respond(PebbleContent("login.peb", mapOf()))
    }
    post("/login") {
        val params = call.receiveParameters()
        val email = params["email"] ?: ""
        val password = params["password"] ?: ""
        if (AuthService.login(email, password)) {
            val userTable = UserTableAccess()
            val userData = userTable.findByEmail(email)

            print(userData)

            if (userData == null) {
                call.respondRedirect("/login")
            }

            call.sessions.set(UserSession(
                userEmail = email,
                firstName = userData?.firstName ?: "UNKNOWN"
            ))
            call.respondRedirect("/home")
        } else {
            call.respond(PebbleContent("login.peb", mapOf("error" to "Invalid credentials")))
        }
    }

    get("/logout") {
        call.sessions.clear<UserSession>()
        println("logged out")
        call.respondRedirect("/")
    }
}