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

package org.melliforay.storageservice.repository.internal.adapter.metadata

import org.melliforay.storageservice.repository.NodeRepresentation
import org.melliforay.storageservice.repository.Session
import java.util.Optional

/**
 * Interface for low-level working area operations.
 */
interface MetadataRepositoryWorkingAreaAdapter {

    /**
     * Creates a node representation in the working area for a session.
     * @param session the session for which a working copy of a node representation should be saved
     * @param representation the node representation to save
     */
    fun createNodeRepresentation(session: Session, representation: NodeRepresentation)

    /**
     * Returns the working node revision for the given session and path.
     * @param session the session for which a node representation should be retrieved
     * @param path the path of the node representation to retrieve
     * @return the working copy of the node representation, or empty if no representation exists
     */
    fun nodeRepresentation(session: Session, path: String): Optional<NodeRepresentation>

}