package com.flightbooking.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.pebble.*
import io.ktor.server.pebble.PebbleContent
import io.ktor.server.sessions.*
import com.flightbooking.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
    /**
    * Renders the "My Bookings" page for the logged-in user.
    *
    * GET "/profile/bookings"
    * Query params:
    * - q (optional): booking id filter (numeric)
    * - status (optional): booking status filter (pending/confirmed/cancelled)
    *
    * Behaviour:
    * - Requires UserSession. If missing -> redirect /login
    * - If user cannot be resolved -> redirect /404
    * - Loads bookings belonging to the user, including segment flight + airport + seat info
    * - Renders my_bookings.peb
    */
    get("/profile/bookings") {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respondRedirect("/login")
            return@get
        }

        val q = call.request.queryParameters["q"]?.trim().orEmpty()
        val qId = q.toIntOrNull()
        val statusFilter = call.request.queryParameters["status"]?.trim()?.lowercase().orEmpty()

        val model = transaction {
            val userRow = UserTable
                .select { UserTable.email eq session.userEmail }
                .limit(1)
                .firstOrNull()

            if (userRow == null) {
                return@transaction mapOf<String, Any>("__notFound" to true)
            }

            val userId = userRow[UserTable.id]

            val origin = AirportTable.alias("origin")
            val dest = AirportTable.alias("dest")
            var cond: Op<Boolean> = (BookingTable.userId eq userId)

            if (statusFilter.isNotBlank()) {
                cond = cond and (BookingTable.bookingStatus.lowerCase() eq statusFilter)
            }

            if (q.isNotBlank()) {
                if (qId == null) {
                    val emptyBookings = emptyList<Map<String, Any>>()
                    return@transaction mapOf(
                        "userSession" to session,
                        "q" to q,
                        "statusFilter" to statusFilter,
                        "bookings" to emptyBookings
                    )
                }
                cond = cond and (BookingTable.id eq qId)
            }

            val rows = (BookingTable
                .join(BookingSegmentTable, JoinType.INNER, additionalConstraint = { BookingSegmentTable.bookingId eq BookingTable.id })
                .join(FlightTable, JoinType.LEFT, additionalConstraint = { FlightTable.id eq BookingSegmentTable.flightId })
                .join(origin, JoinType.LEFT, additionalConstraint = { FlightTable.originAirport eq origin[AirportTable.id] })
                .join(dest, JoinType.LEFT, additionalConstraint = { FlightTable.destinationAirport eq dest[AirportTable.id] })
                .join(SeatAssignmentTable, JoinType.LEFT, additionalConstraint = { SeatAssignmentTable.bookingSegmentId eq BookingSegmentTable.id })
                .join(SeatTable, JoinType.LEFT, additionalConstraint = { SeatTable.id eq SeatAssignmentTable.seatId })
                .slice(
                    BookingTable.id,
                    BookingTable.bookingReference,
                    BookingTable.bookingStatus,
                    BookingTable.createdAt,

                    BookingSegmentTable.id,

                    FlightTable.id,
                    FlightTable.flightNumber,
                    FlightTable.status,
                    FlightTable.scheduledDepartureTime,
                    FlightTable.scheduledArrivalTime,

                    origin[AirportTable.iataCode],
                    origin[AirportTable.name],
                    dest[AirportTable.iataCode],
                    dest[AirportTable.name],

                    SeatTable.seatCode
                )
                .select { cond }
                .orderBy(BookingTable.id, SortOrder.DESC)
                .map { r ->
                    mapOf(
                        "bookingId" to r[BookingTable.id],
                        "bookingReference" to r[BookingTable.bookingReference],
                        "bookingStatus" to r[BookingTable.bookingStatus],
                        "createdAt" to r[BookingTable.createdAt],

                        "segmentId" to r[BookingSegmentTable.id],
   
                        "flightNumber" to (r.getOrNull(FlightTable.flightNumber)?.toString() ?: ""),
                        "flightStatus" to (r.getOrNull(FlightTable.status) ?: ""),
                        "dep" to (r.getOrNull(FlightTable.scheduledDepartureTime) ?: ""),
                        "arr" to (r.getOrNull(FlightTable.scheduledArrivalTime) ?: ""),

                        "originIata" to r.getOrNull(origin[AirportTable.iataCode]),
                        "originName" to r.getOrNull(origin[AirportTable.name]),
                        "destIata" to r.getOrNull(dest[AirportTable.iataCode]),
                        "destName" to r.getOrNull(dest[AirportTable.name]),

                        "seatCode" to r.getOrNull(SeatTable.seatCode)
                    )              
                }
            )

            val grouped = rows.groupBy { it["bookingId"] as Int }
   
            val bookings = grouped.map { (bid, items) ->
                val first = items.first()
 
                val segments = items.map { itRow ->
                    mapOf(
                        "segmentId" to itRow["segmentId"],
                        "flightNumber" to (itRow["flightNumber"] ?: ""),
                        "flightStatus" to (itRow["flightStatus"] ?: ""),
                        "dep" to (itRow["dep"] ?: ""),
                        "arr" to (itRow["arr"] ?: ""),
                        "originIata" to itRow["originIata"],
                        "originName" to itRow["originName"],
                        "destIata" to itRow["destIata"],
                        "destName" to itRow["destName"],
                        "seatCode" to itRow["seatCode"]
                    )
                }

                mapOf(
                    "bookingId" to bid,
                    "bookingReference" to (first["bookingReference"] ?: ""),
                    "bookingStatus" to (first["bookingStatus"] ?: ""),
                    "createdAt" to (first["createdAt"] ?: ""),
                    "segments" to segments
                )
            }

            mapOf(
                "userSession" to session,
                "q" to q,
                "statusFilter" to statusFilter,
                "bookings" to bookings
            )
        }

        if (model.containsKey("__notFound")) {
            call.respondRedirect("/404")
            return@get
        }

        call.respond(PebbleContent("my_bookings.peb", model))
    }

    /**
    * Cancels a booking for the logged-in user.
    *
    * POST "/profile/bookings/cancel"
    * - Requires an existing [UserSession].
    * - Reads `bookingId` from form parameters.
    * - Verifies the booking belongs to the current user.
    * - Updates `booking_status` to "cancelled" and sets `cancelled_at` to now.
    * - Redirects back to `/profile/bookings`.
    * - If session is missing -> redirect to `/login`.
    * - If user or booking cannot be resolved -> redirect to `/404`.
    */
    post("/profile/bookings/cancel") {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respondRedirect("/login")
            return@post
        }

        val params = call.receiveParameters()
        val bookingId = params["bookingId"]?.toIntOrNull()
        if (bookingId == null) {
            call.respondRedirect("/404")
            return@post
        }

        val ok = transaction {
            val userRow = UserTable
                .select { UserTable.email eq session.userEmail }
                .limit(1)
                .firstOrNull() ?: return@transaction false

            val userId = userRow[UserTable.id]

            val owned = BookingTable
                .select { (BookingTable.id eq bookingId) and (BookingTable.userId eq userId) }
                .limit(1)
                .any()

            if (!owned) return@transaction false

            BookingTable.update({ (BookingTable.id eq bookingId) and (BookingTable.userId eq userId) }) {
                it[bookingStatus] = "cancelled"
                it[cancelledAt] = java.time.Instant.now().toString()
            }

            true
        }

        if (!ok) {
            call.respondRedirect("/404")
            return@post
        }

        call.respondRedirect("/profile/bookings")
    }
}