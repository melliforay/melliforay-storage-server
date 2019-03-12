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
import org.melliforay.storageservice.repository.Session
import java.util.Optional

/**
 * Defines the operations that a node repository supports, but which should not be used outside
 * this package.  DO NOT USE THIS INTERFACE OUTSIDE OF THIS PACKAGE.
 */
internal interface InternalNodeRepository {

    /**
     * Returns the node at the given path from the metadata repository, based on the revision of the given session.
     * @param session the session to use in order to retrieve the node.
     * @param path the path of the node to retrieve, or null to retrieve the root node
     * @return the node at the given path, or empty if it doesn't exist
     */
    fun node(session: Session, path: String): Optional<Node>

    /**
     * Closes the given session.
     * @param session the session to close
     */
    fun closeSession(session: Session)

}