comments are in AirportTableAccess.kt, omitted for the rest
UserTableAccess.kt is incomplete/unchanged

Example for the functions (in tested and works in Application.kt)

import access.AirportTableAccess // just "import access" is what we'll use
import com.flightbooking.tables.AirportTable // similarly "import com.flightbooking.tables"

// AirportTableAccess connection
val airporttableaccess = AirportTableAccess()
val airports = airporttableaccess.getAll()
println("Found ${airports.size} airports in the database")

// insert
try {
    val inserted = airporttableaccess.addAirport(
        iataCode = "TST", 
        name = "Test Airport", 
        city = "TestTown", 
        country = null
    )
    println("Inserted airport with ID: ${inserted.id}")
} catch (e: Throwable) {
    println("FAILED TO INSERT, ALREADY EXISTS: ${e.message}")
}

// update
val updated = airporttableaccess.updateRecordByAttribute(
    id = 7, 
    column = AirportTable.name, 
    value = "Updated Test Airport"
)
println("Update successful on record with ID 7: $updated")
println("New name ID 7: ${airporttableaccess.getByAttribute(AirportTable.id, 7).firstOrNull()?.name}")

//deleting the test record
airporttableaccess.deleteByID(7)