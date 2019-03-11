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