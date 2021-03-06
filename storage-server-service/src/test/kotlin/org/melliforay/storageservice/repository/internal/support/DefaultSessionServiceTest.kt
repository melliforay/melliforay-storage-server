/*
 * Copyright (C) 2019 melliFORAY contributors (https://github.com/orgs/melliforay/teams/melliforay-contributors)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.melliforay.storageservice.repository.internal.support

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.model.support.ClusterSessionInfo
import org.melliforay.storageservice.repository.internal.SessionService
import org.melliforay.storageservice.repository.support.DefaultSession
import org.melliforay.storageservice.repository.support.InternalNodeRepository
import org.melliforay.storageservice.rest.Credentials
import org.melliforay.storageservice.service.clustering.ClusterSynchronizationService
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import java.util.Optional

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@DisplayName("a session service")
class DefaultSessionServiceTest {

    @Configuration
    @Import(DefaultSessionService::class, DefaultSession::class)
    internal class Config

    @Autowired
    private lateinit var sessionService: SessionService

    @MockBean
    private lateinit var mockAuthProvider: AuthenticationProvider

    @MockBean
    private lateinit var clusterService: ClusterSynchronizationService

    @MockBean
    private lateinit var nodeRepository: InternalNodeRepository

    @BeforeEach
    private fun init() {
        reset(mockAuthProvider)
        `when`(mockAuthProvider.supports(UsernamePasswordAuthenticationToken::class.java)).thenReturn(true)
        reset(clusterService)
        reset(nodeRepository)
    }

    @Test
    @DisplayName("should return a session for an authorized user")
    fun getSessionForAuthUser() {
        val auth = mock(Authentication::class.java)
        `when`(auth.isAuthenticated).thenReturn(true)
        `when`(auth.name).thenReturn("user")
        `when`(mockAuthProvider.authenticate(any())).thenReturn(auth)
        val creds = mock(Credentials::class.java)
        `when`(creds.username).thenReturn("user")
        `when`(creds.password).thenReturn("pass")
        val session = sessionService.getSession(creds, RevisionNumber(0))
        assertTrue(session.isPresent, "session not returned")
    }

    @Test
    @DisplayName("should not return a session for an authorized user without a username")
    fun getSessionWithNoUsername() {
        val creds = mock(Credentials::class.java)
        `when`(creds.password).thenReturn("pass")
        val session = sessionService.getSession(creds, RevisionNumber(0))
        assertFalse(session.isPresent, "session returned")
    }

    @Test
    @DisplayName("should not return a session for an authorized user without a password")
    fun getSessionWithNoPassword() {
        val creds = mock(Credentials::class.java)
        `when`(creds.username).thenReturn("user")
        val session = sessionService.getSession(creds, RevisionNumber(0))
        assertFalse(session.isPresent, "session returned")
    }

    @Test
    @DisplayName("should not return a session for an unauthorized user")
    fun getSessionForUnauthUser() {
        val auth = mock(Authentication::class.java)
        `when`(auth.isAuthenticated).thenReturn(false)
        `when`(mockAuthProvider.authenticate(any())).thenReturn(auth)
        val creds = mock(Credentials::class.java)
        `when`(creds.username).thenReturn("user")
        `when`(creds.password).thenReturn("pass")
        val session = sessionService.getSession(creds, RevisionNumber(0))
        assertFalse(session.isPresent, "session returned")
    }

    @Test
    @DisplayName("should be able to get a session with a valid token")
    fun getSessionForToken() {
        val info = ClusterSessionInfo("token", "userID", "0")
        `when`(clusterService.getSessionInfo(eq("token"))).thenReturn(Optional.of(info))
        val session = sessionService.getSession("token")
        assertTrue(session.isPresent, "session not returned")
    }

    @Test
    @DisplayName("should not be able to get a session with an invalid token")
    fun getSessionForInvalidToken() {
        `when`(clusterService.getSessionInfo(eq("token"))).thenReturn(Optional.empty())
        val session = sessionService.getSession("token")
        assertFalse(session.isPresent, "session not returned")
    }

}