package access

import com.flightbooking.models.FlightFare
import com.flightbooking.models.toFlightFare
import com.flightbooking.tables.FlightFareTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.FlightFareTableAccess

class FlightFareTableAccess {
    fun getAll(): List<FlightFare> = transaction {
        FlightFareTable.selectAll().map {
            constructFlightFareRecord(it)
        }
    }
    fun constructFlightFareRecord(it: ResultRow): FlightFare {
        return FlightFare (
                        id = it[FlightFareTable.id],
                        flightId = it[FlightFareTable.flightId],
                        fareClassId = it[FlightFareTable.fareClassId],
                        price = it[FlightFareTable.price],
                        currency = it[FlightFareTable.currency],
                        seatsAvailable = it[FlightFareTable.seatsAvailable]
                        saleStart = it[FlightFareTable.saleStart],
                        saleEnd = it[FlightFareTable.saleEnd]
                    )
    }
}