package org.trancemountain.storageservice.repository.binary.adapter.support.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.trancemountain.storageservice.repository.binary.adapter.BinaryRepositoryStorageAdapter
import java.io.ByteArrayInputStream


@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@TestPropertySource(properties = [
    "trance.service.storage.binary.adapter=s3",
    "trance.service.storage.binary.adapter.s3.tempPrefix=tempo",
    "trance.service.storage.binary.adapter.s3.bucketName=trance",
    "trance.service.storage.binary.adapter.s3.permanentPrefix=perm"])
@DisplayName("an S3 binary repository storage adapter")
class S3BinaryRepositoryStorageAdapterTest {

    @Configuration
    @Import(S3BinaryRepositoryStorageAdapter::class)
    internal class Config

    @Autowired
    private lateinit var storageAdapter: BinaryRepositoryStorageAdapter

    @MockBean
    private lateinit var s3: AmazonS3

    @BeforeEach
    private fun init() {
        reset(s3)
    }

    @Test
    @DisplayName("should be able to store a temporary file")
    fun testStoreTemporaryFile() {
        val mockResult = mock(PutObjectResult::class.java)
        val mockMetadata = mock(ObjectMetadata::class.java)
        `when`(mockResult.metadata).thenReturn(mockMetadata)
        `when`(mockMetadata.contentLength).thenReturn(3)
        `when`(s3.putObject(any(), any(), any(), eq(null))).thenReturn(mockResult)
        val info = storageAdapter.createTempFile(ByteArrayInputStream(byteArrayOf(1, 2, 3)))
        verify(s3).putObject(any(), any(), any(), eq(null))
        assertNotNull(info, "File info not returned")
    }

    @Test
    @DisplayName("should use be able to get the input stream for a file at a temp location")
    fun testGetTempFile() {
        val s3Object = mock(S3Object::class.java)
        val mockStream = mock(S3ObjectInputStream::class.java)
        `when`(s3Object.objectContent).thenReturn(mockStream)
        `when`(s3.getObject(eq("trance"), eq("tempo/testfile/1"))).thenReturn(s3Object)
        val stream = storageAdapter.inputStreamForTemporaryLocation("testfile/1")
        assertNotNull(stream, "stream not returned")
        assertEquals(mockStream, stream, "stream mismatch")
    }

    @Test
    @DisplayName("should be able to delete a temp file at a given path")
    fun testDeleteTempFile() {
        storageAdapter.deleteTempFile("testfile/1")
        val captor = ArgumentCaptor.forClass(String::class.java)
        verify(s3).deleteObject(eq("trance"), captor.capture())
        assertEquals("tempo/testfile/1", captor.value, "wrong delete path")
    }

    @Test
    @DisplayName("should be able to list the permanent files with a given prefix")
    fun testListFilesWithHash() {
        val mockListing = mock(ObjectListing::class.java)
        val summary1 = mock(S3ObjectSummary::class.java)
        `when`(summary1.key).thenReturn("ab/cd/ef/gh/1")
        `when`(summary1.size).thenReturn(10)
        val summary2 = mock(S3ObjectSummary::class.java)
        `when`(summary2.key).thenReturn("ab/cd/ef/gh/2")
        `when`(summary2.size).thenReturn(20)
        val summaryList = listOf(summary1, summary2)
        `when`(mockListing.objectSummaries).thenReturn(summaryList)

        `when`(s3.listObjects(any<ListObjectsRequest>())).thenReturn(mockListing)
        val retInfoList = storageAdapter.filesWithHashPrefix("ab/cd/ef/gh")
        assertEquals(summaryList.size, retInfoList.size, "wrong file info count")
        for (i in 0 until summaryList.size) {
            assertEquals(summaryList[i].key, retInfoList[i].path)
            assertEquals(summaryList[i].size, retInfoList[i].size)
        }
    }

    @Test
    @DisplayName("should use be able to get the input stream for a file at a permanent location")
    fun testGetPermanentFile() {
        val s3Object = mock(S3Object::class.java)
        val mockStream = mock(S3ObjectInputStream::class.java)
        `when`(s3Object.objectContent).thenReturn(mockStream)
        `when`(s3.getObject(eq("trance"), eq("perm/ab/cd/1"))).thenReturn(s3Object)
        val stream = storageAdapter.inputStreamForPermanentLocation("ab/cd/1")
        assertNotNull(stream, "stream not returned")
        assertEquals(mockStream, stream, "stream mismatch")
    }

    @Test
    @DisplayName("should be able to move a temporary file to a permanent location")
    fun testMoveTempFileToPermanentStorage() {
        val tempPath = "uuid-1-2-3"
        val permPath = "ab/cd/ed/fg/1"
        storageAdapter.moveTempFileToPermanentLocation(tempPath, permPath)
        verify(s3).copyObject(eq("trance"), eq("tempo/uuid-1-2-3"), eq("trance"), eq("perm/ab/cd/ed/fg/1"))
        verify(s3).deleteObject(eq("trance"), eq("tempo/uuid-1-2-3"))
    }


}