package access

import com.flightbooking.models.Notification
import com.flightbooking.models.toNotification
import com.flightbooking.tables.NotificationTable

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

class NotificationTableAccess {
    fun getAll(): List<Notification> = transaction {
        NotificationTable.selectAll().map {
            constructNotificationRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Notification> = transaction {
        NotificationTable.select { attribute eq value } 
            .map { constructNotificationRecord(it) } 
    }
    fun addNotification(
        userId: Int?, 
        type: String?, 
        message: String?, 
        createdAt: String, 
        readAt: String?
        ): Complaint = transaction { 
        val id = NotificationTable.insert { 
            it[NotificationTable.userId] = userId
            it[NotificationTable.type] = type
            it[NotificationTable.message] = message
            it[NotificationTable.createdAt] = createdAt
            it[NotificationTable.readAt] = readAt
        } get NotificationTable.id 
        Notification( 
            id = id!!,
            userId = userId,
            type = type,
            message = message,
            createdAt = createdAt,
            readAt = readAt
        ) }
    fun deleteByID(id: Int) = transaction { 
        NotificationTable.deleteWhere { NotificationTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = NotificationTable.update({ NotificationTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun constructNotificationRecord(it: ResultRow): Notification {
        return Notification (
                        id = it[NotificationTable.id],
                        userId = it[NotificationTable.userId],
                        type = it[NotificationTable.type],
                        message = it[NotificationTable.message],
                        createdAt = it[NotificationTable.createdAt],
                        readAt = it[NotificationTable.readAt]
                    )
    }
}