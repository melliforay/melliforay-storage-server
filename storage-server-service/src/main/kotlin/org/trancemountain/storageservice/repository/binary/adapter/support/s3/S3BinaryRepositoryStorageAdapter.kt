package org.trancemountain.storageservice.repository.binary.adapter.support.s3

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ListObjectsRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.trancemountain.storageservice.repository.binary.adapter.BinaryRepositoryStorageAdapter
import org.trancemountain.storageservice.repository.binary.adapter.FileInfo
import java.io.InputStream
import java.util.UUID

@Component
@ConditionalOnProperty("trance.service.storage.binary.adapter", havingValue = "s3")
class S3BinaryRepositoryStorageAdapter: BinaryRepositoryStorageAdapter {

    @Value("trance.service.storage.binary.adapter.s3.bucketName")
    private lateinit var bucketName: String

    @Value("trance.service.storage.binary.adapter.s3.tempPrefix:temp")
    private lateinit var tempLocationPrefix: String

    @Value("trance.service.storage.binary.adapter.s3.permanentPrefix:permanent")
    private lateinit var permanentLocationPrefix: String

    private val s3 = AmazonS3ClientBuilder.defaultClient()

    override fun createTempFile(stream: InputStream): FileInfo {
        // find a unique temp file location for the input stream
        var tempLocation: String?
        do {
            val uuid = UUID.randomUUID().toString()
            tempLocation = "$tempLocationPrefix/$uuid"
        } while (s3.doesObjectExist(bucketName, tempLocation))

        // write the data to that location and return it
        val putResult = s3.putObject(bucketName, tempLocation!!, stream, null)
        return FileInfo(tempLocation, putResult.metadata.contentLength)
    }

    override fun inputStreamForTemporaryLocation(location: String): InputStream {
        return s3.getObject(bucketName, "$tempLocationPrefix/$location").objectContent
    }

    override fun deleteTempFile(path: String) {
        s3.deleteObject(bucketName, path)
    }

    override fun filesWithHashPrefix(hashPrefix: String): List<FileInfo> {
        val request = ListObjectsRequest().withBucketName(bucketName).withPrefix(hashPrefix)
        return s3.listObjects(request).objectSummaries.map { FileInfo(it.key, it.size) }
    }

    override fun inputStreamForPermanentLocation(location: String): InputStream {
        return s3.getObject(bucketName, "$permanentLocationPrefix/$location").objectContent
    }

    override fun moveTempFileToPermanentLocation(tempPath: String, targetLocation: String) {
        s3.copyObject(bucketName, tempPath, bucketName, "$permanentLocationPrefix/$targetLocation")
        deleteTempFile(tempPath)
    }
}