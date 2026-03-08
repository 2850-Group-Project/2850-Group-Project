package com.flightbooking.service

import com.flightbooking.api.AviationStackClient
import com.flightbooking.access.AirportTableAccess
import com.flightbooking.models.Airport

class AirportImportService(
    private val client: AviationStackClient,
    private val access: AirportTableAccess
) {
    suspend fun importAllAirports() {
        val limit = 100 //max num you can import at once
        var offset = 0
        var total = Int.MAX_VALUE

        while (offset < total) {
            val response = client.getAirports(limit, offset)
            total = response.pagination.total

            response.data.forEach {ApiAirport ->
                val iata = ApiAirport.iata_Code
                if (iata.isNullOrBlank()) return@forEach //some don't have iata codes so skip them

                val airport = Airport( //convert API's model to ours
                    id = 0,
                    iataCode = iata,
                    name = ApiAirport.airport_name,
                    city = ApiAirport.city,
                    country = ApiAirport.country_name
                )
                access.upsertByIata(airport) //upsert it
            }

            offset += limit
        }
    }
}