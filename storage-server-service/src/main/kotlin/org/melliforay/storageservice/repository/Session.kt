package org.melliforay.storageservice.repository

import org.melliforay.storageservice.RevisionNumber
import java.util.Optional

/**
 * A connection to the metadata and binary stores; similar to a JDBC Connection object.
 */
interface Session {

    /**
     * Returns the ID of this session.
     */
    fun getSessionID(): String

    /**
     * Returns the ID of the user that owns this session.
     */
    fun getUserID(): String

    /**
     * Returns the root node.
     */
    fun rootNode(): Node

    /**
     * Returns the revision number of the session
     */
    fun revision(): RevisionNumber

    /**
     * Returns the node at the given path, if it's available.
     * @param path the path of the node to retrieve
     * @return an optional containing the given node, or empty if the node doesn't exist
     */
    fun node(path: String): Optional<Node>

    /**
     * Closes this session, discarding any unsaved changes.
     */
    fun close()

}