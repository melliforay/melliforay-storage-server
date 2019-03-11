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