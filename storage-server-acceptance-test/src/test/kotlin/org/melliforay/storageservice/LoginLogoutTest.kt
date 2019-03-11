package org.melliforay.storageservice

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.melliforay.storageservice.rest.Credentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("the session controller")
class LoginLogoutTest {

    private val sessionUrl = "/v1/session"

    @Value("\${local.server.port}")
    private lateinit var port: Integer

    @BeforeEach
    private fun init() {
        RestAssured.port = port.toInt()
    }

    @Test
    @DisplayName("should return a session token for a valid login")
    fun testLogin() {
        val creds = Credentials()
        creds.username = "michacod"
        creds.password = "password"
        val response = RestAssured.given().contentType(ContentType.JSON).body(creds).`when`().post(sessionUrl).andReturn()
        assertEquals(200, response.statusCode, "Wrong HTTP status code")
        val cookie = response.header("Set-Cookie")
        assertTrue(cookie.contains("melliforayToken"), "Missing token")
    }

    @Test
    @DisplayName("should return an HTTP 401 for an invalid login")
    fun testLoginRejected() {
        val creds = Credentials()
        creds.username = "asdfasdf"
        creds.password = "password"
        val response = RestAssured.given().contentType(ContentType.JSON).body(creds).`when`().post(sessionUrl).andReturn()
        assertEquals(401, response.statusCode, "Wrong HTTP status code")
    }

    @Test
    @DisplayName("should unset a session token for a valid logout")
    fun testLogout() {
        val creds = Credentials("michacod", "password")
        val response = RestAssured.given().contentType(ContentType.JSON).body(creds).`when`().post(sessionUrl).andReturn()
        assertEquals(200, response.statusCode, "Wrong HTTP status code")
        val cookie = response.header("Set-Cookie")
        val regex = Regex("melliforayToken=([\\w-]+)")
        val result = regex.find(cookie)
        assertNotNull(result)
        val tokenRes = result!!.groups[1]
        assertNotNull(tokenRes)
        val token = tokenRes!!.value

        // ok. now log out
        val logoutRes = RestAssured.given().header("Cookie", "melliforayToken=$token").`when`().delete(sessionUrl).andReturn()
        assertEquals(200, logoutRes.statusCode, "Wrong HTTP status code")
        val unsetCookie = logoutRes.header("Set-Cookie")
        assertTrue(unsetCookie.contains("melliforayToken"), "Missing token")
        assertTrue(unsetCookie.contains("expires"))
    }


    /**
     * TODO: remove this! it's for testing SonarQube
     */
    @Test
    @DisplayName("should unset a session token for a valid logout")
    fun testTEst() {
        val creds = Credentials("michacod", "password")
        val response = RestAssured.given().contentType(ContentType.JSON).body(creds).`when`().post(sessionUrl).andReturn()
        assertEquals(200, response.statusCode, "Wrong HTTP status code")
        val cookie = response.header("Set-Cookie")
        val regex = Regex("melliforayToken=([\\w-]+)")
        val result = regex.find(cookie)
        assertNotNull(result)
        val tokenRes = result!!.groups[1]
        assertNotNull(tokenRes)
        val token = tokenRes!!.value

        // ok. now log out
        val logoutRes = RestAssured.given().header("Cookie", "melliforayToken=$token").`when`().get("/hello").andReturn()
        assertEquals(200, logoutRes.statusCode, "Wrong HTTP status code")
    }

}