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

package org.melliforay.storageservice.repository.internal

import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.NodeRepresentation
import org.melliforay.storageservice.repository.Session
import java.util.Optional

/**
 * Governs the high-level logic surrounding the manipulation of node metadata.  The metadata
 * manager sits atop a variety of back-end adapters that provide access to the various
 * sections of the metadata store, including the catalog, working area, journal, current data partition,
 * and archived data partitions.
 *
 * The metadata manager service is not concerned with working with [Node]s.  It is only concerned
 * with providing access to [NodeRepresentation] objects.  It is not concerned with any type
 * of in-memory caching, either.
 */
interface MetadataManagerService {

    /**
     * Initializes the metadata store if it is not already initialized.
     */
    fun initializeMetadataStore()

    /**
     * Returns the current revision of the repository.
     */
    fun repositoryRevision(): RevisionNumber

    /**
     * Returns either a session's working copy of a node representation, or the
     * representation of that node in the current data partition if the session has
     * no working copy of it, if either exist.
     * @param session the session being used to retrieve the node representation
     * @param path the path of the node representation to retrieve
     * @return either the working copy or committed copy of the node, or empty if neither exist
     */
    fun nodeRepresentation(session: Session, path: String): Optional<NodeRepresentation>

}