package com.flightbooking

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthRoutesTest : IntegrationTestSupport() {
    @Test
    // A user should be able to register, then log in and receive a session cookie.
    fun registerThenLoginRedirectsToHomeAndSetsSessionCookie() = testApplication {
        configureApp()
        val client = createClient { followRedirects = false }

        val registerResponse = client.post("/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "student@example.com",
                    "password" to "Password123!",
                    "confirmPassword" to "Password123!",
                    "firstName" to "Stu",
                    "lastName" to "Dent"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, registerResponse.status)
        assertEquals("/login", registerResponse.headers[HttpHeaders.Location])

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
        assertEquals("/home", loginResponse.headers[HttpHeaders.Location])
        assertNotNull(loginResponse.headers.getAll(HttpHeaders.SetCookie)?.find { it.contains("USER_SESSION") })
    }
}
