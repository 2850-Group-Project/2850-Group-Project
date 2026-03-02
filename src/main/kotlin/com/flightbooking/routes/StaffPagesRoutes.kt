package com.flightbooking.routes

import com.flightbooking.models.StaffSession
import com.flightbooking.tables.*
import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun Route.staffPagesRoutes() {

    get("/staff/dashboard") {
        val session = call.sessions.get<StaffSession>()
        if (session == null) {
            call.respondRedirect("/staff/login")
            return@get
        }

        val staffEmail = session.staffEmail

        val model = transaction {

            val staffRow = StaffTable
                .select { StaffTable.email eq staffEmail }
                .limit(1)
                .firstOrNull()

            if (staffRow == null) {
                return@transaction mapOf<String, Any>(
                    "error" to "Staff not found, please login again."
                )
            }

            val staffName = listOfNotNull(
                staffRow[StaffTable.firstName],
                staffRow[StaffTable.lastName]
            ).joinToString(" ").ifBlank { "Staff" }

            val staffRole = staffRow[StaffTable.role] ?: "Staff"

            val todayPrefix = LocalDate.now().toString()

            val activeFlightsCount = FlightTable
                .select { FlightTable.status neq "cancelled" }
                .count()

            val departuresTodayCount = FlightTable
                .select { FlightTable.scheduledDepartureTime like "$todayPrefix%" }
                .count()

            val totalSeats = SeatTable.selectAll().count()
            val occupiedSeats = SeatTable.select { SeatTable.status neq "available" }.count()
            val loadFactor = if (totalSeats > 0) {
                val pct = (occupiedSeats.toDouble() / totalSeats.toDouble()) * 100.0
                String.format("%.1f%%", pct)
            } else {
                "N/A"
            }

            val systemAlertsCount = ComplaintTable
                .select { ComplaintTable.status eq "open" }
                .count()

            val origin = AirportTable.alias("origin")
            val dest = AirportTable.alias("dest")

            val flightList = (FlightTable
                .join(origin, JoinType.INNER, additionalConstraint = { FlightTable.originAirport eq origin[AirportTable.id] })
                .join(dest, JoinType.INNER, additionalConstraint = { FlightTable.destinationAirport eq dest[AirportTable.id] })
                .slice(
                    FlightTable.id,
                    FlightTable.flightNumber,
                    FlightTable.scheduledDepartureTime,
                    FlightTable.status,
                    FlightTable.capacity,
                    origin[AirportTable.iataCode],
                    dest[AirportTable.iataCode]
                )
                .select { FlightTable.status neq "cancelled" }
                .orderBy(FlightTable.id, SortOrder.DESC)
                .limit(8)
                .map { row ->
                    val no = row[FlightTable.flightNumber]?.let { "SB-$it" } ?: "SB-${row[FlightTable.id]}"
                    val destination = row[dest[AirportTable.iataCode]]
                    val departureTimeRaw = row[FlightTable.scheduledDepartureTime] ?: ""
                    val depTime = departureTimeRaw.takeLast(5).ifBlank { departureTimeRaw }
                    val status = row[FlightTable.status]

                    mapOf(
                        "no" to no,
                        "dest" to destination,
                        "dep" to depTime,
                        "status" to status,
                        "crew" to ""
                    )
                })

            mapOf(
                "staffEmail" to staffEmail,
                "staffName" to staffName,
                "staffRole" to staffRole,
                "activeFlights" to activeFlightsCount,
                "loadFactor" to loadFactor,
                "departuresToday" to departuresTodayCount,
                "systemAlerts" to systemAlertsCount,
                "flights" to flightList
            )
        }

        if (model.containsKey("error")) {
            call.respondText(model["error"].toString())
            return@get
        }

        call.respond(PebbleContent("staff_dashboard.peb", model))
    }

    get("/staff/logout") {
        call.sessions.clear<StaffSession>()
        call.respondRedirect("/staff/login")
    }
}