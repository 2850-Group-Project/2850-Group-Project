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

import access.PaymentTableAccess

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