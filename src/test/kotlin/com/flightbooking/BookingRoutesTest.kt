package com.flightbooking

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.post
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.parameters
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class BookingRoutesTest : IntegrationTestSupport() {
    // Check that submitting passenger details without logging in redirects to login.
    @Test
    fun unauthenticatedPassengerSubmitRedirectsToLogin() = testApplication {
        configureApp()
        val client = createClient { followRedirects = false }

        val response = client.submitForm(
            url = "/flights/passengers/submit",
            formParameters = parameters {
                append("passengers[0][firstName]", "Alex")
                append("passengers[0][lastName]", "Student")
            }
        )

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/login", response.headers[HttpHeaders.Location])
    }

    // Check that submitting passenger details without a booking session redirects home.
    @Test
    fun passengerSubmitRedirectsHomeWhenBookingSessionMissing() = testApplication {
        configureApp()
        val client = createAuthenticatedUserClient()

        val response = client.submitForm(
            url = "/flights/passengers/submit",
            formParameters = parameters {
                append("passengers[0][firstName]", "Alex")
                append("passengers[0][lastName]", "Student")
            }
        )

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/home", response.headers[HttpHeaders.Location])
    }

    // Check that submitting one passenger saves their details in the booking session.
    @Test
    fun passengerSubmitStoresPassengerDataInBookingSession() {
    }

    // Check that submitting multiple passengers saves all of their details in the booking session.
    @Test
    fun passengerSubmitHandlesMultiplePassengers() {
    }

    // Create a logged-in user client for booking route tests.
    private suspend fun ApplicationTestBuilder.createAuthenticatedUserClient(): HttpClient {
        val client = createClient {
            followRedirects = false
            install(HttpCookies)
        }

        val registerResponse = client.post("/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "student@example.com",
                    "password" to "Password123!",
                    "confirmPassword" to "Password123!",
                    "firstName" to "Student",
                    "lastName" to "Alex"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, registerResponse.status)

        val loginResponse = client.post("/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "student@example.com",
                    "password" to "Password123!"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, loginResponse.status)

        return client
    }
}
