package com.flightbooking

import io.ktor.client.request.forms.submitForm
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class FlightRoutesTest : IntegrationTestSupport() {
    @Test
    // Unauthenticated users should be redirected to login when selecting a flight.
    fun unauthenticatedFlightSelectRedirectsToLogin() = testApplication {
        configureApp()
        val client = createClient { followRedirects = false }

        val response = client.submitForm(
            url = "/flights/select",
            formParameters = parameters {
                append("flightId", "1")
                append("fareId", "1")
                append("leg", "outbound")
            }
        )

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/login", response.headers[HttpHeaders.Location])
    }

    @Test
    // Flight selection should reject requests with no flight id.
    fun flightSelectRejectsMissingFlightId() {
    }

    @Test
    // Flight selection should reject requests with no fare id.
    fun flightSelectRejectsMissingFareId() {
    }

    @Test
    // Flight selection should reject requests with no leg value.
    fun flightSelectRejectsMissingLeg() {
    }

    @Test
    // Flight selection should reject requests with an invalid leg value.
    fun flightSelectRejectsInvalidLeg() {
    }

    @Test
    // Selecting an outbound flight should store the outbound booking session values.
    fun flightSelectStoresOutboundSelectionInBookingSession() {
    }

    @Test
    // Selecting a return flight should store the return booking session values.
    fun flightSelectStoresReturnSelectionInBookingSession() {
    }
}
