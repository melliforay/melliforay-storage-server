package org.trancemountain.storageservice.repository.binary.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.trancemountain.storageservice.repository.binary.BinaryDeduplicationStrategy
import org.trancemountain.storageservice.repository.binary.adapter.BinaryRepositoryStorageAdapter
import org.trancemountain.storageservice.repository.binary.adapter.FileInfo
import java.util.Optional

/**
 * A conservative approach to duplicate detection. If there is an existing file with the same hash and size
 * as the given temporary file, it will then perform a byte-by-byte comparison of the temporary file and the
 * existing file in order to decide if the two are exactly the same. This obviously carries a significant performance
 * impact but guarantees that two files with the same hash and size, but different content, will not be wrongly
 * treated as duplicates.
 */
@Component
@ConditionalOnProperty("trance.service.storage.binary.deduplication.strategy", havingValue = "conservative")
class ConservativeBinaryDeduplicationStrategy: BinaryDeduplicationStrategy {

    @Autowired
    private lateinit var storageAdapter: BinaryRepositoryStorageAdapter

    private val comparisonBufferSize = 32000

    override fun findDuplicateBinary(temporaryFile: FileInfo,permanentFilesWithHash: List<FileInfo>): Optional<FileInfo> {
        val filesWithSameSize = permanentFilesWithHash.filter { it.size == temporaryFile.size }

        return when(filesWithSameSize.size) {
            0 -> Optional.empty()
            else -> {
                val matchingBinaryFiles = filesWithSameSize.filter { compareBinaryData(temporaryFile, it) }

                when(matchingBinaryFiles.size) {
                    0 -> Optional.empty()
                    1 -> Optional.of(matchingBinaryFiles.first())
                    else -> throw IllegalArgumentException("Found multiple matching permanent files with the same hash, size and data")
                }
            }
        }
    }

    private fun compareBinaryData(tempLocationInfo: FileInfo, targetInfo: FileInfo): Boolean {
        val tempInputStream = storageAdapter.inputStreamForTemporaryLocation(tempLocationInfo.path)
        val permanentInputStream = storageAdapter.inputStreamForPermanentLocation(targetInfo.path)
        try {
            val tempBuffer = ByteArray(comparisonBufferSize)
            val permBuffer = ByteArray(comparisonBufferSize)
            var doesMatch = true
            while (doesMatch) {
                val tempRead = tempInputStream.read(tempBuffer, 0, tempBuffer.size)
                val permRead = permanentInputStream.read(permBuffer, 0, permBuffer.size)
                if (tempRead == -1 && permRead == -1) {
                    break
                } else  if (tempRead != permRead) {
                    doesMatch = false
                    break
                } else {
                    for (i in 0 until tempRead) {
                        if (tempBuffer[i] != permBuffer[i]) {
                            doesMatch = false
                            break
                        }
                    }
                }
            }
            return doesMatch
        } finally {
            tempInputStream.close()
            permanentInputStream.close()
        }
    }

}