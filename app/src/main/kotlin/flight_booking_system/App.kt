package flight_booking_system

import database.DBFactory

fun main() {

    // EXAMPLE usage of using the DBFactory to create a connection
    println("Initialising DB connection...")
    DBFactory.init()
    println("Connected to DB")

    println("Hello world")
}