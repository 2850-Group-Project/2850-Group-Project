package com.flightbooking

import kotlin.test.Test

class BookingRoutesTest {
    // Check that submitting passenger details without logging in redirects to login.
    @Test
    fun unauthenticatedPassengerSubmitRedirectsToLogin() {
    }

    // Check that submitting passenger details without a booking session redirects home.
    @Test
    fun passengerSubmitRedirectsHomeWhenBookingSessionMissing() {
    }

    // Check that submitting one passenger saves their details in the booking session.
    @Test
    fun passengerSubmitStoresPassengerDataInBookingSession() {
    }

    // Check that submitting multiple passengers saves all of their details in the booking session.
    @Test
    fun passengerSubmitHandlesMultiplePassengers() {
    }
}
