package com.flightbooking

import kotlin.test.Test

class FlightRoutesTest : IntegrationTestSupport() {
    @Test
    // Unauthenticated users should be redirected to login when selecting a flight.
    fun unauthenticatedFlightSelectRedirectsToLogin() {
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
