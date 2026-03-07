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
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

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
    fun addSeat(
        flightId: Int,
        seatCode: String,
        cabinClass: String?,
        position: String?,
        extraLegroom: Int,
        exitRow: Int,
        reducedMobility: Int,
        status: String
        ): Seat = transaction { 
        val id = SeatTable.insert { 
            it[SeatTable.flightId] = flightId
            it[SeatTable.seatCode] = seatCode
            it[SeatTable.cabinClass] = cabinClass
            it[SeatTable.position] = position
            it[SeatTable.extraLegroom] = extraLegroom
            it[SeatTable.exitRow] = exitRow
            it[SeatTable.reducedMobility] = reducedMobility
            it[SeatTable.status] = status
        } get SeatTable.id 
        Seat( 
            id = id!!,
            flightId = flightId
            seatCode = seatCode
            cabinClass = cabinClass
            position = position
            extraLegroom = extraLegroom
            exitRow = exitRow
            reducedMobility = reducedMobility
            status = status
        ) }
    fun deleteByID(id: Int) = transaction { 
        SeatTable.deleteWhere { SeatTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = SeatTable.update({ SeatTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
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