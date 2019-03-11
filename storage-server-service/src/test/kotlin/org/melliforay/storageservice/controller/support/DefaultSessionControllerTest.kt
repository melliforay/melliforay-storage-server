package org.melliforay.storageservice.controller.support

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.reset
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.melliforay.storageservice.controller.SessionController
import org.melliforay.storageservice.repository.NodeRepository
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.repository.support.InternalNodeRepository
import org.melliforay.storageservice.rest.Credentials
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
    private lateinit var nodeRepository: NodeRepository

    @MockBean
    private lateinit var internalNodeRepository: InternalNodeRepository

    @Autowired
    private lateinit var controller: SessionController

    @BeforeEach
    private fun reset() {
        reset(nodeRepository)
        reset(internalNodeRepository)
    }

    @Test
    @DisplayName("should pass a session retrieve request by credentials to a session service and return the session's token")
    fun testGetSession() {
        `when`(nodeRepository.getSession(any<Credentials>())).thenReturn(Optional.of(mock(Session::class.java)))
        controller.getSession(mock(Credentials::class.java))
        verify(nodeRepository).getSession(any<Credentials>())
    }

    @Test
    @DisplayName("should return a not authorized response if a session cannot be created")
    fun testGetUnauthorized() {
        `when`(nodeRepository.getSession(any<Credentials>())).thenReturn(Optional.empty())
        val res: Mono<ResponseEntity<Void>> = controller.getSession(mock(Credentials::class.java))
        res.subscribe { assertEquals(HttpStatus.UNAUTHORIZED, it.statusCode) }
    }

    @Test
    @DisplayName("should pass a session close request to a session")
    fun testCloseSession() {
        val mockSession = mock(Session::class.java)
        `when`(nodeRepository.getSession(any<String>())).thenReturn(Optional.of(mockSession))
        controller.closeSession("blah")
        verify(mockSession).close()
    }

    @Test
    @DisplayName("should return a not found response if closing a session that can't be found")
    fun testCloseUnknown() {
        `when`(nodeRepository.getSession(any<Credentials>())).thenReturn(Optional.empty())
        val res: Mono<ResponseEntity<Void>> = controller.closeSession("")
        res.subscribe { assertEquals(HttpStatus.NOT_FOUND, it.statusCode) }
    }
}