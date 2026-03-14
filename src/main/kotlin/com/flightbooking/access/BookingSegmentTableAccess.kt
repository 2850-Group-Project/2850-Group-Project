package com.flightbooking.access

import com.flightbooking.models.BookingSegment
import com.flightbooking.models.toBookingSegment
import com.flightbooking.tables.BookingSegmentTable
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

class BookingSegmentTableAccess {
    fun getAll(): List<BookingSegment> = transaction {
        BookingSegmentTable.selectAll().map {
            it.toBookingSegment()
        }}
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<BookingSegment> = transaction {
        BookingSegmentTable.select { attribute eq value } 
            .map { it.toBookingSegment() } }
    fun createBookingSegment(
        bookingId: Int, 
        flightId: Int, 
        flightFareId: Int
        ): Boolean = transaction { 
        BookingSegmentTable.insert { 
            it[BookingSegmentTable.bookingId] = bookingId 
            it[BookingSegmentTable.flightId] = flightId 
            it[BookingSegmentTable.flightFareId] = flightFareId 
        }
        true
    }
    fun deleteByID(id: Int) = transaction { 
        BookingSegmentTable.deleteWhere { BookingSegmentTable.id eq id } }
    fun <T> updateRecordByAttribute(id: Int, column: Column<T>, value: T): Boolean = transaction { 
        val rows = BookingSegmentTable.update({ BookingSegmentTable.id eq id }) { 
            stmt -> stmt[column] = value } 
        rows > 0 }
}
