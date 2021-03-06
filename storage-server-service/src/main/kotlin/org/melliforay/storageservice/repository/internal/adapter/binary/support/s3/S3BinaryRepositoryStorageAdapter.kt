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

package org.melliforay.storageservice.repository.internal.adapter.binary.support.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsRequest
import org.melliforay.storageservice.repository.internal.adapter.binary.BinaryRepositoryStorageAdapter
import org.melliforay.storageservice.repository.internal.adapter.binary.FileInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.InputStream
import java.util.UUID

@Component
@ConditionalOnProperty("melliforay.service.storage.binary.adapter", havingValue = "s3")
class S3BinaryRepositoryStorageAdapter: BinaryRepositoryStorageAdapter {

    @Value("\${melliforay.service.storage.binary.adapter.s3.bucketName:melliforay}")
    private lateinit var bucketName: String

    @Value("\${melliforay.service.storage.binary.adapter.s3.tempPrefix:temp}")
    private lateinit var tempLocationPrefix: String

    @Value("\${melliforay.service.storage.binary.adapter.s3.permanentPrefix:permanent}")
    private lateinit var permanentLocationPrefix: String

    @Autowired
    private lateinit var s3: AmazonS3

    override fun createTempFile(stream: InputStream): FileInfo {
        // find a unique temp file location for the input stream
        var tempLocation: String
        do {
            tempLocation = UUID.randomUUID().toString()
        } while (s3.doesObjectExist(bucketName, "$tempLocationPrefix/$tempLocation"))

        // write the data to that location and return it
        val putResult = s3.putObject(bucketName, "$tempLocationPrefix/$tempLocation", stream, null)
        return FileInfo(tempLocation, putResult.metadata.contentLength)
    }

    override fun inputStreamForTemporaryLocation(location: String): InputStream {
        return s3.getObject(bucketName, "$tempLocationPrefix/$location").objectContent
    }

    override fun deleteTempFile(path: String) {
        s3.deleteObject(bucketName, "$tempLocationPrefix/$path")
    }

    override fun filesWithHashPrefix(hashPrefix: String): List<FileInfo> {
        val request = ListObjectsRequest().withBucketName(bucketName).withPrefix(hashPrefix)
        return s3.listObjects(request).objectSummaries.map { FileInfo(it.key, it.size) }
    }

    override fun inputStreamForPermanentLocation(location: String): InputStream {
        return s3.getObject(bucketName, "$permanentLocationPrefix/$location").objectContent
    }

    override fun moveTempFileToPermanentLocation(tempPath: String, targetLocation: String) {
        s3.copyObject(bucketName, "$tempLocationPrefix/$tempPath", bucketName, "$permanentLocationPrefix/$targetLocation")
        deleteTempFile(tempPath)
    }
}