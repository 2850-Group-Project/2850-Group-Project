package com.flightbooking

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {
    private val tempDbFiles = mutableListOf<Path>()

    @AfterTest
    fun cleanupTempDatabases() {
        tempDbFiles.forEach { it.deleteIfExists() }
        tempDbFiles.clear()
    }

    @Test
    fun loginPageLoads() = testApplication {
        configureApp(newTestDbUrl())

        val response = client.get("/login")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Login"))
    }

    @Test
    fun unauthenticatedHomeRedirectsToLogin() = testApplication {
        configureApp(newTestDbUrl())
        val client = createClient { followRedirects = false }

        val response = client.get("/home")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/login", response.headers[HttpHeaders.Location])
    }

    @Test
    fun registerThenLoginRedirectsToHomeAndSetsSessionCookie() = testApplication {
        configureApp(newTestDbUrl())
        val client = createClient { followRedirects = false }

        val registerResponse = client.post("/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "student@example.com",
                    "password" to "Password123!",
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

    @Test
    fun staffRegisterRejectsWrongInviteCode() = testApplication {
        configureApp(newTestDbUrl())

        val response = client.post("/staff/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "firstName" to "Alex",
                    "lastName" to "Admin",
                    "email" to "staff@example.com",
                    "password" to "StrongPass123!",
                    "role" to "admin",
                    "inviteCode" to "WRONG-CODE"
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Invalid invite code"))
    }

    @Test
    fun unauthenticatedStaffFlightsRedirectsToStaffLogin() = testApplication {
        configureApp(newTestDbUrl())
        val client = createClient { followRedirects = false }

        val response = client.get("/staff/flights")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/staff/login", response.headers[HttpHeaders.Location])
    }

    private fun newTestDbUrl(): String {
        val dbFile = Files.createTempFile("flight-booking-test-", ".db")
        tempDbFiles.add(dbFile)
        return "jdbc:sqlite:${dbFile.toAbsolutePath()}"
    }

    private fun ApplicationTestBuilder.configureApp(dbUrl: String) {
        environment { config = MapApplicationConfig() }
        application { testModule(dbUrl) }
    }
}
