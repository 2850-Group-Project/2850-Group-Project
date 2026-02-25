package access

import com.flightbooking.models.FareClass
import com.flightbooking.models.toFareClass
import com.flightbooking.tables.FareClassTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.FareClassTableAccess

class FareClassTableAccess {
    fun getAll(): List<FareClass> = transaction {
        FareClassTable.selectAll().map {
            constructFareClassRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<FareClass> = transaction {
        FareClassTable.select { attribute eq value } 
            .map { constructFareClassRecord(it) } 
    }
    fun constructFareClassRecord(it: ResultRow): FareClass {
        return FareClass (
                        id = it[FareClassTable.id],
                        classCode = it[FareClassTable.classCode],
                        cabinClass = it[FareClassTable.cabinClass],
                        displayName = it[FareClassTable.displayName],
                        refundable = it[FareClassTable.refundable],
                        cancelProtocol = it[FareClassTable.cancelProtocol],
                        advanceSeatSelection = it[FareClassTable.advanceSeatSelection],
                        priorityCheckin = it[FareClassTable.priorityCheckin],
                        priorityBoarding = it[FareClassTable.priorityBoarding],
                        loungeAccess = it[FareClassTable.loungeAccess],
                        carryOnAllowed = it[FareClassTable.carryOnAllowed],
                        carryOnWeightKg = it[FareClassTable.carryOnWeightKg],
                        checkedBaggagePieces = it[FareClassTable.checkedBaggagePieces],
                        checkedBaggageWeightKg = it[FareClassTable.checkedBaggageWeightKg],
                        milesEarnRate = it[FareClassTable.milesEarnRate],
                        minimumMilesForBooking = it[FareClassTable.minimumMilesForBooking],
                        description = it[FareClassTable.description],
                        createdAt = it[FareClassTable.createdAt],
                        updatedAt = it[FareClassTable.updatedAt]
                    )
    }
}