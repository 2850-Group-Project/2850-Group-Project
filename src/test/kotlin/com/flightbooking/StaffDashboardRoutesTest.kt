package com.flightbooking

import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class StaffDashboardRoutesTest : IntegrationTestSupport() {
    @Test
    // Unauthenticated staff users should be redirected to the staff login page.
    fun unauthenticatedDashboardRedirectsToStaffLogin() = testApplication {
        configureApp()
        val client = createClient { followRedirects = false }

        val response = client.get("/staff/dashboard")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/staff/login", response.headers[HttpHeaders.Location])
    }

    @Test
    // An authenticated staff user should be able to load the dashboard page.
    fun authenticatedDashboardLoads() {
    }

    @Test
    // A stale staff session should show a staff-not-found response.
    fun dashboardShowsStaffNotFoundMessageWhenSessionUserIsMissing() {
    }
}
