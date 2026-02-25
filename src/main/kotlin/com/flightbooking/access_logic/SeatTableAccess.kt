package access

import com.flightbooking.models.Seat
import com.flightbooking.models.toSeat
import com.flightbooking.tables.SeatTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.SeatTableAccess

class SeatTableAccess {
    fun getAll(): List<Seat> = transaction {
        SeatTable.selectAll().map {
            constructSeatRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Seat> = transaction {
        SeatTable.select { attribute eq value } 
            .map { constructSeatRecord(it) } 
    }
    fun constructSeatRecord(it: ResultRow): Seat {
        return Seat (
                        id = it[SeatTable.id],
                        flightId = it[SeatTable.flightId],
                        seatCode = it[SeatTable.seatCode],
                        cabinClass = it[SeatTable.cabinClass],
                        position = it[SeatTable.position],
                        extraLegroom = it[SeatTable.extraLegroom],
                        exitRow = it[SeatTable.exitRow],
                        reducedMobility = it[SeatTable.reducedMobility],
                        status = it[SeatTable.status]
                    )
    }
}