package org.melliforay.storageservice.controller.support

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.reset
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.melliforay.storageservice.controller.SessionController
import org.melliforay.storageservice.model.Session
import org.melliforay.storageservice.rest.Credentials
import org.melliforay.storageservice.service.SessionService
import reactor.core.publisher.Mono
import java.util.Optional

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@DisplayName("a session controller")
class DefaultSessionControllerTest {

    @Configuration
    @Import(DefaultSessionController::class)
    internal class Config

    @MockBean
    private lateinit var sessionService: SessionService

    @Autowired
    private lateinit var controller: SessionController

    @BeforeEach
    private fun reset() {
        reset(sessionService)
    }

    @Test
    @DisplayName("should pass a session retrieve request by credentials to a session service and return the session's token")
    fun testGetSession() {
        `when`(sessionService.getSession(any<Credentials>())).thenReturn(Optional.of(mock(Session::class.java)))
        controller.getSession(mock(Credentials::class.java))
        verify(sessionService).getSession(any<Credentials>())
    }

    @Test
    @DisplayName("should return a not authorized response if a session cannot be created")
    fun testGetUnauthorized() {
        `when`(sessionService.getSession(any<Credentials>())).thenReturn(Optional.empty())
        val res: Mono<ResponseEntity<Void>> = controller.getSession(mock(Credentials::class.java))
        res.subscribe { assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode) }
    }

    @Test
    @DisplayName("should pass a session close request to a session service")
    fun testCloseSession() {
        `when`(sessionService.getSession(any<String>())).thenReturn(Optional.of(mock(Session::class.java)))
        controller.closeSession("blah")
        verify(sessionService).closeSession(any())
    }

    @Test
    @DisplayName("should return a not found response if closing a session that can't be found")
    fun testCloseUnknown() {
        `when`(sessionService.getSession(any<Credentials>())).thenReturn(Optional.empty())
        val res: Mono<ResponseEntity<Void>> = controller.closeSession("")
        res.subscribe { assertEquals(HttpStatus.NOT_FOUND, it.statusCode) }
    }
}