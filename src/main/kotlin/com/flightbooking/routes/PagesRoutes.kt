package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.pebble.PebbleContent
import io.ktor.server.sessions.*
import com.flightbooking.models.UserSession
import com.flightbooking.routes.authRoutes

fun Route.pagesRoutes() {
    get("/home") {
        // need to add check to make sure user is logged in before loading the home page
        val session = call.sessions.get<UserSession>()
        println(session)

        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }

        call.respond(PebbleContent("home.peb", mapOf(
            "userSession" to session
        )))
    }

        /**
     * Renders the user's profile dashboard page (left navigation + profile info panel).
     *
     * GET /profile
     * - Requires UserSession.
     * - On success: renders "my_profile.peb" with userSession in the model.
     * - On failure: redirects to /login.
     */
    get("/profile") {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }

        call.respond(
            PebbleContent(
                "my_profile.peb",
                mapOf("userSession" to session)
            )
        )
    }

    /**
     * Placeholder for complaints page (not implemented yet).
     *
     * GET /profile/complaints
     * - Always redirects to /404 (until implemented).
     */
    get("/profile/complaints") {
        call.respondRedirect("/404")
    }

    /**
     * Placeholder for notifications page (not implemented yet).
     *
     * GET /profile/notifications
     * - Always redirects to /404 (until implemented).
     */
    get("/profile/notifications") {
        call.respondRedirect("/404")
    }

    /**
     * Central 404 route used by pages that are not implemented yet.
     *
     * GET /404
     * - Renders "404.peb" with HTTP 404 status.
     */
    get("/404") {
        call.respond(
            HttpStatusCode.NotFound,
            PebbleContent("404.peb", mapOf<String, Any>())
        )
    }
}