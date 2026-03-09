tried in Application.kt, none of the data had city names
"""
Application.kt:

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch

import com.flightbooking.api.AviationStackClient
import com.flightbooking.access.AirportTableAccess
import com.flightbooking.service.AirportImportService

    val httpClient = HttpClient(CIO) {
        install(ClientContentNegotiation) {
            json(
                kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            )
        }
    }
    val aviationStackClient = AviationStackClient(httpClient)
    val airportAccess = AirportTableAccess()
    val importer = AirportImportService(aviationStackClient, airportAccess)

    environment.monitor.subscribe(ApplicationStarted) {
        launch {
            println("Starting airport import...")

            importer.importAllAirports()

            println("Airport import complete.")
            println("Total airports in DB: ${airportAccess.getAll().size}")
        }
    }
"""

build.gradle.kts:
kotlin("plugin.serialization") version "1.9.0"
// Client-side ContentNegotiation
implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
implementation("io.ktor:ktor-client-json:2.3.7")
implementation("io.ktor:ktor-client-serialization:2.3.7")
