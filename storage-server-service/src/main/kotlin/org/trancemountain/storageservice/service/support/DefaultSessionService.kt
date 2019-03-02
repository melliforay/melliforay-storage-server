package org.trancemountain.storageservice.service.support

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.trancemountain.storageservice.model.Session
import org.trancemountain.storageservice.model.support.ClusterSessionInfo
import org.trancemountain.storageservice.model.support.DefaultSession
import org.trancemountain.storageservice.rest.Credentials
import org.trancemountain.storageservice.service.SessionService
import org.trancemountain.storageservice.service.clustering.ClusterSynchronizationService
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

    override fun getSession(credentials: Credentials): Optional<Session> {
        logger.info("Getting session for user {}", credentials.username)
        if (credentials.username == null || credentials.password == null) {
            return Optional.empty()
        } else {
            val creds = UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
            val authCredentials = providerManager.authenticate(creds)
            return when (authCredentials.isAuthenticated) {
                true -> {
                    val sessionKey = UUID.randomUUID().toString()
                    val sessionInfo = ClusterSessionInfo(sessionKey, authCredentials.name)
                    val session = applicationContext.getBean(DefaultSession::class.java, sessionKey, authCredentials.name)
                    logger.info("Adding session $sessionKey")
                    clusterSynchronizationService.addSessionInfo(sessionKey, sessionInfo)
                    Optional.of(session)
                }
                false -> Optional.empty()
            }
        }
    }

    override fun getSession(token: String): Optional<Session> {
        val sessionInfoOpt = clusterSynchronizationService.getSessionInfo(token)
        return sessionInfoOpt.map { info -> applicationContext.getBean(DefaultSession::class.java, info.getSessionID(), info.getUserID())}
    }

    override fun closeSession(session: Session) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}