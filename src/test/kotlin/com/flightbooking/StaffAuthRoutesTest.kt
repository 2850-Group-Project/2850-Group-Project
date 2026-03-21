package com.flightbooking

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StaffAuthRoutesTest : IntegrationTestSupport() {
    @Test
    // Staff registration should fail when the invite code is invalid.
    fun staffRegisterRejectsWrongInviteCode() = testApplication {
        configureApp()

        val response = client.post("/staff/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "firstName" to "Alex",
                    "lastName" to "Admin",
                    "email" to "staff@example.com",
                    "password" to "StrongPass123!",
                    "confirmPassword" to "StrongPass123!",
                    "role" to "admin",
                    "inviteCode" to "WRONG-CODE"
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Invalid invite code"))
    }
}
