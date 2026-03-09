package com.flightbooking.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
@Serializable
data class AirportApiResponse(
    val pagination: Pagination? = null,
    val data: List<ApiAirport> = emptyList()
)
@Serializable
data class Pagination(
    val limit: Int? = null,
    val offset: Int? = null,
    val count: Int? = null,
    val total: Int? = null
)
@Serializable
data class ApiAirport(
    @SerialName("iata_code")
    val iataCode: String? = null,

    @SerialName("airport_name")
    val airportName: String? = null,

    @SerialName("city")
    val city: String? = null,

    @SerialName("country_name")
    val countryName: String? = null
)