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
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder

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
    fun addStaff(
        email: String,
        passwordHash: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        role: String?,
        createdAt: String
        ): Staff = transaction { 
        val id = StaffTable.insert { 
            it[StaffTable.email] = email
            it[StaffTable.passwordHash] = passwordHash
            it[StaffTable.firstName] = firstName
            it[StaffTable.lastName] = lastName
            it[StaffTable.phoneNumber] = phoneNumber
            it[StaffTable.role] = role
            it[StaffTable.createdAt] = createdAt
        } get StaffTable.id 
        Staff( 
            id = id!!,
            email = email,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            role = role,
            createdAt = createdAt
        ) }
    fun deleteByID(id: Int) = transaction { 
        StaffTable.deleteWhere { StaffTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = StaffTable.update({ StaffTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
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