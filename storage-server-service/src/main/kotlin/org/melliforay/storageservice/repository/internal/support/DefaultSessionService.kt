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

import org.apache.logging.log4j.LogManager
import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.model.support.ClusterSessionInfo
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.repository.internal.SessionService
import org.melliforay.storageservice.repository.support.DefaultSession
import org.melliforay.storageservice.rest.Credentials
import org.melliforay.storageservice.service.clustering.ClusterSynchronizationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import javax.annotation.PostConstruct

@Service
class DefaultSessionService: SessionService {

    private val logger = LogManager.getLogger(DefaultSessionService::class.java)

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var authProviders: List<AuthenticationProvider>

    @Autowired
    private lateinit var clusterSynchronizationService: ClusterSynchronizationService

    private lateinit var providerManager: ProviderManager

    @PostConstruct
    private fun init() {
        providerManager = ProviderManager(authProviders)
    }

    override fun getSession(credentials: Credentials, revision: RevisionNumber): Optional<Session> {
        logger.info("Getting session for user {}", credentials.username)
        if (credentials.username == null || credentials.password == null) {
            return Optional.empty()
        } else {
            val creds = UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
            try {
                val authCredentials = providerManager.authenticate(creds)
                return when (authCredentials.isAuthenticated) {
                    true -> {
                        val sessionKey = UUID.randomUUID().toString()
                        val sessionInfo = ClusterSessionInfo(sessionKey, authCredentials.name, revision.toString())
                        val session = applicationContext.getBean(DefaultSession::class.java, sessionKey, authCredentials.name, revision)
                        logger.info("Adding session $sessionKey")
                        clusterSynchronizationService.addSessionInfo(sessionKey, sessionInfo)
                        Optional.of(session)
                    }
                    false -> Optional.empty()
                }
            } catch (bce: BadCredentialsException) {
                return Optional.empty()
            }
        }
    }

    override fun getSession(token: String): Optional<Session> {
        val sessionInfoOpt = clusterSynchronizationService.getSessionInfo(token)
        return sessionInfoOpt.map { info -> applicationContext.getBean(DefaultSession::class.java, info.getSessionID(), info.getUserID(), RevisionNumber(info.getRevision()))}
    }

    override fun closeSession(session: Session) {
        clusterSynchronizationService.removeSessionInfo(session.getSessionID())
    }
}