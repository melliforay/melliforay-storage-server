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