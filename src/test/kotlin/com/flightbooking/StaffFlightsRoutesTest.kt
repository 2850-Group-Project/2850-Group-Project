package com.flightbooking

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun authenticatedStaffFlightsPageLoads() = testApplication {
        configureApp()
        val client = createClient {
            followRedirects = false
            install(HttpCookies)
        }

        client.registerStaff()
        val loginResponse = client.loginStaff()
        assertEquals(HttpStatusCode.Found, loginResponse.status)
        assertEquals("/staff/dashboard", loginResponse.headers[HttpHeaders.Location])

        val response = client.get("/staff/flights")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Flight Scheduler"))
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

    // Submit a valid staff registration form for staff flights tests.
    private suspend fun HttpClient.registerStaff(
        email: String = "staff@example.com",
        password: String = "StrongPass123!"
    ) = submitForm(
        url = "/staff/register",
        formParameters = parameters {
            append("firstName", "Alex")
            append("lastName", "Admin")
            append("email", email)
            append("password", password)
            append("confirmPassword", password)
            append("role", "admin")
            append("inviteCode", "STAFF-CHECK")
        }
    )

    // Submit a staff login form for authenticated staff flights requests.
    private suspend fun HttpClient.loginStaff(
        email: String = "staff@example.com",
        password: String = "StrongPass123!"
    ) = submitForm(
        url = "/staff/login",
        formParameters = parameters {
            append("email", email)
            append("password", password)
        }
    )
}
