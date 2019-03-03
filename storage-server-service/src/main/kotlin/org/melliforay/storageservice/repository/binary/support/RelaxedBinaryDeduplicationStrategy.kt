package org.melliforay.storageservice.repository.binary.support

import org.melliforay.storageservice.repository.binary.BinaryDeduplicationStrategy
import org.melliforay.storageservice.repository.binary.adapter.FileInfo
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.Optional

/**
 * A relaxed form of duplication detection. Considers an existing file with the same hash and size
 * as the given temporary file as being a duplicate.  Although this is likely a perfectly safe assumption
 * to make, it does not account for the extremely remote possibility that two binaries of the same size
 * and hash actually contain different content.
 */
@Component
@ConditionalOnProperty("melliforay.service.storage.binary.deduplication.strategy", havingValue = "relaxed")
class RelaxedBinaryDeduplicationStrategy: BinaryDeduplicationStrategy {

    override fun findDuplicateBinary(temporaryFile: FileInfo, permanentFilesWithHash: List<FileInfo>): Optional<FileInfo> {
        val filesWithSameSize = permanentFilesWithHash.filter { it.size == temporaryFile.size }
        return when (filesWithSameSize.size) {
            0 -> Optional.empty()
            1 -> Optional.of(filesWithSameSize.first())
            else -> throw IllegalArgumentException("Found multiple files size ${temporaryFile.size}")
        }
    }
}