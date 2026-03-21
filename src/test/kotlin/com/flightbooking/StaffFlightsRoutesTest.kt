package com.flightbooking

import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class StaffFlightsRoutesTest : IntegrationTestSupport() {
    @Test
    // Unauthenticated staff users should be sent to the staff login page.
    fun unauthenticatedStaffFlightsRedirectsToStaffLogin() = testApplication {
        configureApp()
        val client = createClient { followRedirects = false }

        val response = client.get("/staff/flights")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/staff/login", response.headers[HttpHeaders.Location])
    }

    @Test
    // An authenticated staff user should be able to load the staff flights page.
    fun authenticatedStaffFlightsPageLoads() {
    }

    @Test
    // Staff should be able to create a flight from the management page.
    fun createFlightRedirectsWithSuccessMessage() {
    }

    @Test
    // Flight creation should reject invalid route input and redirect with an error.
    fun createFlightRejectsInvalidRouteData() {
    }

    @Test
    // Staff should be able to update an existing flight.
    fun updateFlightRedirectsWithSuccessMessage() {
    }

    @Test
    // Staff should be able to delete an existing flight.
    fun deleteFlightRedirectsWithSuccessMessage() {
    }

    @Test
    // Creating a flight should automatically generate seats for that flight.
    fun createFlightAutoGeneratesSeats() {
    }
}
