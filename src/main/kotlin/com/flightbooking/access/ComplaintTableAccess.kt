package access

import com.flightbooking.models.Complaint
import com.flightbooking.models.toComplaint
import com.flightbooking.tables.ComplaintTable

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

class ComplaintTableAccess {
    fun getAll(): List<Complaint> = transaction {
        ComplaintTable.selectAll().map {
            constructComplaintRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Complaint> = transaction {
        ComplaintTable.select { attribute eq value } 
            .map { constructComplaintRecord(it) } 
    }
    fun addComplaint(
        userId: Int?, 
        type: String?, 
        message: String?, 
        createdAt: String, 
        status: String, 
        handledByStaffId: Int?
        ): Complaint = transaction { 
        val id = ComplaintTable.insert { 
            it[ComplaintTable.userId] = userId 
            it[ComplaintTable.type] = type 
            it[ComplaintTable.message] = message 
            it[ComplaintTable.createdAt] = createdAt 
            it[ComplaintTable.status] = status 
            it[ComplaintTable.handledByStaffId] = handledByStaffId
        } get ComplaintTable.id 
        Complaint( 
            id = id!!, 
            userId = userId, 
            type = type, 
            message = message, 
            createdAt = createdAt, 
            status = status, 
            handledByStaffId = handledByStaffId
        ) }
    fun deleteByID(id: Int) = transaction { 
        ComplaintTable.deleteWhere { ComplaintTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = ComplaintTable.update({ ComplaintTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun constructComplaintRecord(it: ResultRow): Complaint {
        return Complaint (
                        id = it[ComplaintTable.id],
                        userId = it[ComplaintTable.userId],
                        type = it[ComplaintTable.type],
                        message = it[ComplaintTable.message],
                        createdAt = it[ComplaintTable.createdAt],
                        status = it[ComplaintTable.status],
                        handledByStaffId = it[ComplaintTable.handledByStaffId]
                    )
    }
}