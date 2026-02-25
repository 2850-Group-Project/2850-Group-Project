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

import access.NotificationTableAccess

class NotificationTableAccess {
    fun getAll(): List<Notification> = transaction {
        NotificationTable.selectAll().map {
            constructNotificationRecord(it)
        }
    }
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