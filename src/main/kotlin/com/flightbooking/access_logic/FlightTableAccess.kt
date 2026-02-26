package access

import com.flightbooking.models.Flight
import com.flightbooking.models.toFlight
import com.flightbooking.tables.FlightTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow
import access.FlightTableAccess
import org.jetbrains.exposed.sql.Column

class FlightTableAccess {
    fun getAll(): List<Flight> = transaction {
        FlightTable.selectAll().map {
            constructFlightRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Flight> = transaction {
        FlightTable.select { attribute eq value } 
            .map { constructFlightRecord(it) } 
    }
    fun constructFlightRecord(it: ResultRow): Flight {
        return Flight (
                        id = it[FlightTable.id],
                        flightNumber = it[FlightTable.flightNumber],
                        originAirport = it[FlightTable.originAirport],
                        destinationAirport = it[FlightTable.destinationAirport],
                        scheduledDepartureTime = it[FlightTable.scheduledDepartureTime],
                        scheduledArrivalTime = it[FlightTable.scheduledArrivalTime],
                        status = it[FlightTable.status],
                        capacity = it[FlightTable.capacity]
                    )
    }
}