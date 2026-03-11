package com.flightbooking.routes

import com.flightbooking.models.StaffSession
import com.flightbooking.service.StaffAuthService
import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.staffAuthRoutes() {
    get("/staff/login") {
        call.respond(PebbleContent("staff_login.peb", mapOf<String, Any>()))
    }

    post("/staff/login") {
        val params = call.receiveParameters()
        val email = params["email"]?.trim().orEmpty()
        val password = params["password"].orEmpty()

        if (StaffAuthService.login(email, password)) {
            call.sessions.set(StaffSession(staffEmail = email))
            call.respondRedirect("/staff/dashboard")
        } else {
            call.respond(PebbleContent("staff_login.peb", mapOf("error" to "Invalid staff credentials")))
        }
    }

    get("/staff/register") {
        call.respond(PebbleContent("staff_register.peb", mapOf<String, Any>()))
    }

    post("/staff/register") {
        val params = call.receiveParameters()
        val firstName = params["firstName"]?.trim()
        val lastName = params["lastName"]?.trim()
        val email = params["email"]?.trim().orEmpty()
        val password = params["password"].orEmpty()
        val role = params["role"]?.trim()
        val inviteCode = params["inviteCode"]?.trim().orEmpty() 
        val expectedInvite = "STAFF-CHECK" 
        if (inviteCode != expectedInvite) { 
             call.respond(PebbleContent("staff_register.peb", mapOf("error" to "Invalid invite code"))) 
              return@post 
        } 
        }

        if (StaffAuthService.register(email, password, firstName, lastName, role)) {
            call.respondRedirect("/staff/login")
        } else {
            call.respond(PebbleContent("staff_register.peb", mapOf("error" to "Staff already exists")))
        }
    }
}