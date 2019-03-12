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