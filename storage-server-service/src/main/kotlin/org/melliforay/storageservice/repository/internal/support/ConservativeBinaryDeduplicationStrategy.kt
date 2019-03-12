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

package org.melliforay.storageservice.repository.internal.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.melliforay.storageservice.repository.internal.adapter.binary.BinaryRepositoryStorageAdapter
import org.melliforay.storageservice.repository.internal.adapter.binary.FileInfo
import java.util.Optional

/**
 * A conservative approach to duplicate detection. If there is an existing file with the same hash and size
 * as the given temporary file, it will then perform a byte-by-byte comparison of the temporary file and the
 * existing file in order to decide if the two are exactly the same. This obviously carries a significant performance
 * impact but guarantees that two files with the same hash and size, but different content, will not be wrongly
 * treated as duplicates.
 */
@Component
@ConditionalOnProperty("melliforay.service.storage.binary.deduplication.strategy", havingValue = "conservative")
class ConservativeBinaryDeduplicationStrategy: BinaryDeduplicationStrategy {

    @Autowired
    private lateinit var storageAdapter: BinaryRepositoryStorageAdapter

    private val comparisonBufferSize = 32000

    override fun findDuplicateBinary(temporaryFile: FileInfo, permanentFilesWithHash: List<FileInfo>): Optional<FileInfo> {
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