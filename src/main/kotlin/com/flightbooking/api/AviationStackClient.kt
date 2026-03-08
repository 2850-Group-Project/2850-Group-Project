package com.flightbooking.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class AviationStackClient(
    val httpClient: HttpClient
) {
    private val accessKey = "af1d2c8c70f793e7f55d20864489b81c"
    private val url = "https://api.aviationstack.com/v1"

    suspend fun getAirports(limit: Int, offset: Int): AirportApiResponse {
        val response: HttpResponse = httpClient.get("$url/airports") {
            parameter("access_key", accessKey)
            parameter("limit", limit)
            parameter("offset", offset)
        }
        return response.body()
    }
}

data class AirportApiResponse(
    val pagination: Pagination,
    val data: List<ApiAirport>
)
data class Pagination(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val total: Int
)
data class ApiAirport(
    val iata_Code: String?,
    val airport_name: String?,
    val city: String?,
    val country_name: String?
)