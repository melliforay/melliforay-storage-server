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