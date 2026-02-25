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

import access.ComplaintTableAccess

class ComplaintTableAccess {
    fun getAll(): List<Complaint> = transaction {
        ComplaintTable.selectAll().map {
            constructComplaintRecord(it)
        }
    }
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