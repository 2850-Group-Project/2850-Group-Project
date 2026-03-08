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
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

class FlightFareTableAccess {
    fun getAll(): List<FlightFare> = transaction {
        FlightFareTable.selectAll().map {
            constructFlightFareRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<FlightFare> = transaction {
        FlightFareTable.select { attribute eq value } 
            .map { constructFlightFareRecord(it) } 
    }
    fun createFlightFare(
        flightId: Int, 
        fareClassId: Int, 
        price: Double, 
        currency: String, 
        seatsAvailable: Int, 
        saleStart: String?, 
        saleEnd: String?
        ): Boolean = transaction { 
        FlightFareTable.insert { 
            it[FlightFareTable.flightId] = flightId 
            it[FlightFareTable.fareClassId] = fareClassId 
            it[FlightFareTable.price] = price 
            it[FlightFareTable.currency] = currency 
            it[FlightFareTable.seatsAvailable] = seatsAvailable 
            it[FlightFareTable.saleStart] = saleStart 
            it[FlightFareTable.saleEnd] = saleEnd 
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        FlightFareTable.deleteWhere { FlightFareTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = FlightFareTable.update({ FlightFareTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun constructFlightFareRecord(it: ResultRow): FlightFare {
        return FlightFare (
                        id = it[FlightFareTable.id],
                        flightId = it[FlightFareTable.flightId],
                        fareClassId = it[FlightFareTable.fareClassId],
                        price = it[FlightFareTable.price],
                        currency = it[FlightFareTable.currency],
                        seatsAvailable = it[FlightFareTable.seatsAvailable],
                        saleStart = it[FlightFareTable.saleStart],
                        saleEnd = it[FlightFareTable.saleEnd]
                    )
    }
}