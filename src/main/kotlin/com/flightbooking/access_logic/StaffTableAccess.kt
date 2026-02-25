package access

import com.flightbooking.models.Staff
import com.flightbooking.models.toStaff
import com.flightbooking.tables.StaffTable

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow

import access.StaffTableAccess

class StaffTableAccess {
    fun getAll(): List<Staff> = transaction {
        StaffTable.selectAll().map {
            constructStaffRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Staff> = transaction {
        StaffTable.select { attribute eq value } 
            .map { constructStaffRecord(it) } 
    }
    fun constructStaffRecord(it: ResultRow): Staff {
        return Staff (
                        id = it[StaffTable.id],
                        email = it[StaffTable.email],
                        passwordHash = it[StaffTable.passwordHash],
                        firstName = it[StaffTable.firstName],
                        lastName = it[StaffTable.lastName],
                        phoneNumber = it[StaffTable.phoneNumber],
                        role = it[StaffTable.role],
                        createdAt = it[StaffTable.createdAt],
                    )
    }
}