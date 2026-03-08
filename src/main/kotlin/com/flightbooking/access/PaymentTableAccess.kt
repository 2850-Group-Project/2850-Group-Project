package access

import com.flightbooking.models.Payment
import com.flightbooking.models.toPayment
import com.flightbooking.tables.PaymentTable

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

class PaymentTableAccess {
    fun getAll(): List<Payment> = transaction {
        PaymentTable.selectAll().map {
            constructPaymentRecord(it)
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Payment> = transaction {
        PaymentTable.select { attribute eq value } 
            .map { constructPaymentRecord(it) } 
    }
    fun addPayment(
        bookingId: Int, 
        amount: Double?, 
        paymentMethod: String?, 
        paymentStatus: String, 
        paidAt: String?,
        providerReference: String?,
        currency: String
        ): Payment = transaction { 
        val id = PaymentTable.insert { 
            it[PaymentTable.bookingId] = bookingId
            it[PaymentTable.amount] = amount
            it[PaymentTable.paymentMethod] = paymentMethod
            it[PaymentTable.paymentStatus] = paymentStatus
            it[PaymentTable.paidAt] = paidAt
            it[PaymentTable.providerReference] = providerReference
            it[PaymentTable.currency] = currency
        } get PaymentTable.id 
        Payment( 
            id = id!!,
            bookingId = bookingId,
            amount = amount,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            paidAt = paidAt,
            providerReference = providerReference,
            currency = currency
        ) }
    fun deleteByID(id: Int) = transaction { 
        PaymentTable.deleteWhere { PaymentTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = PaymentTable.update({ PaymentTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
    fun constructPaymentRecord(it: ResultRow): Payment {
        return Payment (
                        id = it[PaymentTable.id],
                        bookingId = it[PaymentTable.bookingId],
                        amount = it[PaymentTable.amount],
                        paymentMethod = it[PaymentTable.paymentMethod],
                        paymentStatus = it[PaymentTable.paymentStatus],
                        paidAt = it[PaymentTable.paidAt],
                        providerReference = it[PaymentTable.providerReference],
                        currency = it[PaymentTable.currency]
                    )
    }
}