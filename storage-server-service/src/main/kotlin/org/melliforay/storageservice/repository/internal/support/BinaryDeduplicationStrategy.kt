package org.melliforay.storageservice.repository.internal.support

import org.melliforay.storageservice.repository.internal.adapter.binary.FileInfo
import java.util.Optional

/**
 * Governs the logic used by the [BinaryManagerService] in deciding when an incoming binary is a duplicate
 * of an existing binary.
 */
interface BinaryDeduplicationStrategy {

    /**
     * Returns information about a duplicate binary found in permanent storage.
     * @param temporaryFile information about the temporary binary being evaluated
     * @param permanentFilesWithHash the permanent binaries that have the same hash as the temporary binary
     * @return an optional containing information about the duplicate binary, or empty if no duplicate binary found
     */
    fun findDuplicateBinary(temporaryFile: FileInfo, permanentFilesWithHash: List<FileInfo>): Optional<FileInfo>

}