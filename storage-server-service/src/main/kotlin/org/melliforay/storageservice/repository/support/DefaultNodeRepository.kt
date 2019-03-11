package org.melliforay.storageservice.repository.support

import org.melliforay.storageservice.repository.Node
import org.melliforay.storageservice.repository.NodeRepository
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.repository.internal.MetadataManagerService
import org.melliforay.storageservice.repository.internal.SessionService
import org.melliforay.storageservice.rest.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.util.Optional
import javax.annotation.PostConstruct

@Service
class DefaultNodeRepository: NodeRepository, InternalNodeRepository {

    @Autowired
    private lateinit var sessionService: SessionService

    @Autowired
    private lateinit var metadataManagerService: MetadataManagerService

    @Autowired
    private lateinit var context: ApplicationContext

    @PostConstruct
    private fun init() {
        metadataManagerService.initializeMetadataStore()
    }

    override fun getSession(credentials: Credentials): Optional<Session> {
        val revision = metadataManagerService.repositoryRevision()
        return sessionService.getSession(credentials, revision)
    }

    override fun getSession(token: String): Optional<Session> {
       return sessionService.getSession(token)
    }

    override fun closeSession(session: Session) {
        sessionService.closeSession(session)
        // TODO: remove any changes in the working area
    }

    override fun node(session: Session, path: String): Optional<Node> {
        val representationOpt =  metadataManagerService.nodeRepresentation(session, path)
        return representationOpt.map { context.getBean(DefaultNode::class.java, it) }
    }
}