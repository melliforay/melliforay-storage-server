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

package org.melliforay.storageservice.repository

import org.melliforay.storageservice.RevisionNumber

/**
 * The underlying representation of the metadata for a given node.  Distinguished from
 * a [Node] in that it contains no navigation logic, metadata validations by node type,
 * etc.  It is simply a depiction of the node's metadata.
 */
class NodeRepresentation(val name: String, val path: String, val revision: RevisionNumber) {

    /**
     * The current properties of the node.
     */
    lateinit var properties: Map<String, Any>

    /**
     * The stored snapshots of the node.
     */
    lateinit var snapshots: Map<String, Map<String, Any>>

}