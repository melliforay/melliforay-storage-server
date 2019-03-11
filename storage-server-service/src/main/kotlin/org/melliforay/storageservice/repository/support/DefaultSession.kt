package org.melliforay.storageservice.repository.support

import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.Node
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.repository.internal.SessionService
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.lang.RuntimeException
import java.util.Optional

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class DefaultSession(private val sessionID: String, private val userID: String, private val revision: RevisionNumber): Session {

    @Autowired
    private lateinit var nodeRepository: InternalNodeRepository

    override fun getSessionID(): String = sessionID

    override fun getUserID(): String = userID

    override fun revision(): RevisionNumber = revision

    override fun rootNode(): Node {
        val reprOpt = nodeRepository.node(this, "/")
        return when (reprOpt.isPresent) {
            true -> reprOpt.get()
            else -> throw RuntimeException("Root node not found")
        }
    }

    override fun node(path: String): Optional<Node> {
        return nodeRepository.node(this, path)
    }

    override fun close() {
        nodeRepository.closeSession(this)
    }
}