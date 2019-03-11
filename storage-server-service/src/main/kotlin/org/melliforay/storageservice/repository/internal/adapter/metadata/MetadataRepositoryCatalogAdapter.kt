package org.melliforay.storageservice.repository.internal.adapter.metadata

import org.melliforay.storageservice.RevisionNumber
import java.util.Optional

/**
 * Interface for low-level catalog operations.
 */
interface MetadataRepositoryCatalogAdapter {

    fun setRepositoryRevision(revisionNumber: RevisionNumber)

    /**
     * Returns the current revision of the repository, or empty if the repository has no revision.
     */
    fun currentRepositoryRevision(): Optional<RevisionNumber>

}