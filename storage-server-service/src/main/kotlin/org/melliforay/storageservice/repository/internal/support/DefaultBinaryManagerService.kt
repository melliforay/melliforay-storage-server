package org.melliforay.storageservice.repository.internal.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.melliforay.storageservice.repository.internal.BinaryManagerService
import org.melliforay.storageservice.repository.internal.adapter.binary.BinaryRepositoryStorageAdapter
import org.melliforay.storageservice.repository.internal.adapter.binary.FileInfo
import java.io.File
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.Optional
import java.util.UUID

@Repository
class DefaultBinaryManagerService: BinaryManagerService {

    @Autowired
    private lateinit var storageAdapter: BinaryRepositoryStorageAdapter

    @Autowired
    private lateinit var deduplicationStrategy: BinaryDeduplicationStrategy

    private val hexFormat = "%02x"

    override fun createFile(stream: InputStream): String {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        val dis = DigestInputStream(stream, messageDigest)
        try {
            val tempLocationInfo = storageAdapter.createTempFile(dis)

            val digestBytes = messageDigest.digest()
            val sb = StringBuffer()
            for (b in digestBytes) sb.append(String.format(hexFormat, b)) // convert byte to hex char
            val sha1hash = sb.toString()

            val sha1RelativePath = sha1hash.chunked(2).joinToString(File.separator)
            val targetPath = "$sha1RelativePath/$sha1hash-${UUID.randomUUID()}"

            val permanentFilesWithHash = storageAdapter.filesWithHashPrefix(sha1RelativePath)
            if (permanentFilesWithHash.isEmpty()) {
                storageAdapter.moveTempFileToPermanentLocation(tempLocationInfo.path, targetPath)
                return targetPath
            } else {
                val duplicateFilePathOptional: Optional<FileInfo> = deduplicationStrategy.findDuplicateBinary(tempLocationInfo, permanentFilesWithHash)
                if (duplicateFilePathOptional.isPresent) {
                    storageAdapter.deleteTempFile(tempLocationInfo.path)
                    return duplicateFilePathOptional.get().path
                } else {
                    storageAdapter.moveTempFileToPermanentLocation(tempLocationInfo.path, targetPath)
                    return targetPath
                }
            }

        } finally {
            stream.close()
            dis.close()
        }
    }



    override fun getInputStreamForFile(path: String): Optional<InputStream> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fileExists(hashPath: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(hashPath: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}