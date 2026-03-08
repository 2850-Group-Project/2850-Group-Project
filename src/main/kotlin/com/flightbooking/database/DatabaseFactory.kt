package com.flightbooking.database

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils

import com.flightbooking.tables.*

// DB factory is an object that creates a connection to DB
// it makes sure that all the tables exist, and adds them if they don't
// (using the Tables.kt file as a reference for making those tables)
object DBFactory {
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
