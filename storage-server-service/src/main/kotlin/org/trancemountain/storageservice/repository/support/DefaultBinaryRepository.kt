package org.trancemountain.storageservice.repository.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.trancemountain.storageservice.repository.BinaryRepository
import org.trancemountain.storageservice.repository.adapter.BinaryRepositoryStorageAdapter
import org.trancemountain.storageservice.repository.adapter.FileInfo
import java.io.File
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.Optional

@Repository
class DefaultBinaryRepository: BinaryRepository {

    @Autowired
    private lateinit var storageAdapter: BinaryRepositoryStorageAdapter

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
            val filesAtPath = storageAdapter.filesWithHashPrefix(sha1RelativePath)
            val targetPath = "$sha1RelativePath/$sha1hash"
            if (filesAtPath.isEmpty()) {
                storageAdapter.moveTempFileToPermanentLocation(tempLocationInfo.path, targetPath)
            } else {
                val filesWithSameSize = filesAtPath.filter { it.size == tempLocationInfo.size }
                if (filesWithSameSize.isEmpty()) {
                    storageAdapter.moveTempFileToPermanentLocation(tempLocationInfo.path, "${targetPath}_${filesAtPath.size}")
                } else {
                    // do a binary comparison of the temp file with the files with the same hash and size.
                    // if one matches, delete the temp file.
                    // if more than one matches, that's an error.
                    // if none match, move the temp file into that hash path with a new name

                    val matchingBinaryFiles = filesWithSameSize.filter { compareBinaryData(tempLocationInfo, it) }

                    if (matchingBinaryFiles.isEmpty()) {
                        storageAdapter.moveTempFileToPermanentLocation(tempLocationInfo.path, "${targetPath}_${filesAtPath.size}")
                    } else if (matchingBinaryFiles.size == 1) {
                        storageAdapter.deleteTempFile(tempLocationInfo.path)
                    } else {
                        throw IllegalArgumentException("Found multiple matching permanent files with the same hash, size and data")
                    }
                }
            }
            return targetPath
        } finally {
            stream.close()
            dis.close()
        }
    }

    private fun compareBinaryData(tempLocationInfo: FileInfo, targetInfo: FileInfo): Boolean {
        val tempInputStream = storageAdapter.inputStreamForLocation(tempLocationInfo.path)
        val permanentInputStream = storageAdapter.inputStreamForLocation(targetInfo.path)
        try {
            val tempBuffer = ByteArray(2048)
            val permBuffer = ByteArray(2048)
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