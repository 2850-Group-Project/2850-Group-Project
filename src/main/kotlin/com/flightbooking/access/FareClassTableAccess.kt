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
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

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
    fun addFareClass(
        classCode: String,
        cabinClass: String?, 
        displayName: String?,
        refundable: Int, 
        cancelProtocol: String?,
        advancedSeatSelection: Int,
        priorityCheckin: Int,
        priorityBoarding: Int,
        loungeAccess: Int,
        carryOnAllowed: Int,
        carryOnWeightKg: Int,
        checkedBaggagePieces: Int,
        checkedBaggageWeightKg: Int,
        milesEarnRate: Double,
        minimumMilesForBooking: Int?,
        description: String?,
        createdAt: String,
        updatedAt: String
        ):FareClass  = transaction { 
        val id = FareClassTable.insert { 
            it[FareClassTable.classCode] = classCode 
            it[FareClassTable.cabinClass] = cabinClass 
            it[FareClassTable.displayName] = displayName 
            it[FareClassTable.refundable] = refundable 
            it[FareClassTable.cancelProtocol] = cancelProtocol 
            it[FareClassTable.advanceSeatSelection] = advancedSeatSelection 
            it[FareClassTable.priorityCheckin] = priorityCheckin 
            it[FareClassTable.priorityBoarding] = priorityBoarding 
            it[FareClassTable.loungeAccess] = loungeAccess 
            it[FareClassTable.carryOnAllowed] = carryOnAllowed 
            it[FareClassTable.carryOnWeightKg] = carryOnWeightKg 
            it[FareClassTable.checkedBaggagePieces] = checkedBaggagePieces 
            it[FareClassTable.checkedBaggageWeightKg] = checkedBaggageWeightKg 
            it[FareClassTable.milesEarnRate] = milesEarnRate
            it[FareClassTable.minimumMilesForBooking] = minimumMilesForBooking
            it[FareClassTable.description] = description
            it[FareClassTable.createdAt] = createdAt
            it[FareClassTable.updatedAt] = updatedAt
        } get FareClassTable.id 
        FareClass( 
            id = id!!, 
            classCode = classCode,
            cabinClass = cabinClass,
            displayName = displayName,
            refundable = refundable,
            cancelProtocol = cancelProtocol,
            advanceSeatSelection = advancedSeatSelection,
            priorityCheckin = priorityCheckin,
            priorityBoarding = priorityBoarding,
            loungeAccess = loungeAccess,
            carryOnAllowed = carryOnAllowed,
            carryOnWeightKg = carryOnWeightKg,
            checkedBaggagePieces = checkedBaggagePieces,
            checkedBaggageWeightKg = checkedBaggageWeightKg,
            milesEarnRate = milesEarnRate,
            minimumMilesForBooking = minimumMilesForBooking,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        ) }
    fun deleteByID(id: Int) = transaction { 
        FareClassTable.deleteWhere { FareClassTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = FareClassTable.update({ FareClassTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
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