package access // we want to be able to access all access_logic files from a single package "access"

import com.flightbooking.models.Airport
import com.flightbooking.models.toAirport
import com.flightbooking.tables.AirportTable

import org.jetbrains.exposed.sql.selectAll

// other function imports that will likely be used
// there is a bunch more information here: 
// https://www.jetbrains.com/help/exposed/dsl-statement-builder.html
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.ResultRow
import access.AirportTableAccess

// class instance/reference of the airport table
class AirportTableAccess {
    // specific search functions for each table (pretty much copy and pasted for most)
    fun getAll(): List<Airport> = transaction {
        AirportTable.selectAll().map {
            constructAirportRecord(it)
        }
    }
    // need to add more functions!
    fun <T> getByAttribute(attribute: Column<T>, value: T): List<Airport> = transaction {
        AirportTable.select { attribute eq value } 
            .map { constructAirportRecord(it) } 
    }

    // take record (row) and transforms into airport object which we can use like a normal object
    fun constructAirportRecord(it: ResultRow): Airport {
        return Airport (
                        id = it[AirportTable.id],
                        iataCode = it[AirportTable.iataCode],
                        name = it[AirportTable.name],
                        city = it[AirportTable.city],
                        country = it[AirportTable.country]
                    )
    }
}