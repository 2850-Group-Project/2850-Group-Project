package com.flightbooking.access

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

import com.flightbooking.tables.BookingTable

class PaymentTableAccess {
    fun getAll(): List<Payment> = transaction {
        PaymentTable.selectAll().map {
            it.toPayment()
        }
    }
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Payment> = transaction {
        PaymentTable.select { attribute eq value } 
            .map { it.toPayment() } 
    }
    fun createPayment(
        bookingId: Int, 
        amount: Double?, 
        paymentMethod: String?, 
        paymentStatus: String, 
        paidAt: String?,
        providerReference: String?,
        currency: String
        ): Boolean = transaction { 
        PaymentTable.insert { 
            it[PaymentTable.bookingId] = bookingId
            it[PaymentTable.amount] = amount
            it[PaymentTable.paymentMethod] = paymentMethod
            it[PaymentTable.paymentStatus] = paymentStatus
            it[PaymentTable.paidAt] = paidAt
            it[PaymentTable.providerReference] = providerReference
            it[PaymentTable.currency] = currency
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        PaymentTable.deleteWhere { PaymentTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = PaymentTable.update({ PaymentTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
}