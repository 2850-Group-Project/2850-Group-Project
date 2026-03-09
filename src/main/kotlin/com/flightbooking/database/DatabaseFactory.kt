package com.flightbooking.database

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils

import com.flightbooking.tables.*

/**
 * Class responsible for creating/mainting connection to database.
 * 
 * On init, connects to SQLite database and makes sure all required
 * tables exist, creating any that are missing via tables in Tables.kt
 */
object DBFactory {
    /**
     * Initialised connection to database and checks for/creates missing tables
     * 
     * @throws SQLException/ExposedSQLException if the database connection/table connect fails
     */

    fun init() {
        Database.connect(
            url = "jdbc:sqlite:data/flight_booking_DB.db",
            driver = "org.sqlite.JDBC"
        )
        println("Checking if all tables exist...")
        transaction {
            SchemaUtils.create(
                AirportTable,
                FlightTable,
                FareClassTable,
                FlightFareTable,
                UserTable,
                BookingTable,
                PaymentTable,
                PassengerTable,
                BookingSegmentTable,
                SeatTable,
                SeatAssignmentTable,
                StaffTable,
                ComplaintTable,
                NotificationTable
            )
        }
        println("All tables present")
    }
}
