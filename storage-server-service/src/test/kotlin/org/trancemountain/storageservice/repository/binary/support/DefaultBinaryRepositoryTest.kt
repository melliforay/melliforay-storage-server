package org.trancemountain.storageservice.repository.binary.support

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.trancemountain.storageservice.repository.binary.BinaryDeduplicationStrategy
import org.trancemountain.storageservice.repository.binary.BinaryRepository
import org.trancemountain.storageservice.repository.binary.adapter.BinaryRepositoryStorageAdapter
import org.trancemountain.storageservice.repository.binary.adapter.FileInfo
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.Optional

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@DisplayName("a default binary repository")
class DefaultBinaryRepositoryTest {

    @Configuration
    @Import(DefaultBinaryRepository::class)
    internal class Config {

        @Bean
        fun mockStrategy(): BinaryDeduplicationStrategy = mock(BinaryDeduplicationStrategy::class.java)

        @Bean
        fun mockAdapter(): BinaryRepositoryStorageAdapter = mock(BinaryRepositoryStorageAdapter::class.java)

    }

    @Autowired
    private lateinit var adapter: BinaryRepositoryStorageAdapter

    @Autowired
    private lateinit var strategy: BinaryDeduplicationStrategy

    @Autowired
    private lateinit var repository: BinaryRepository

    @BeforeEach
    private fun init() {
        reset(adapter)
        reset(strategy)
    }

    private fun getHash(bytes: ByteArray): String {
        val stream = ByteArrayInputStream(bytes)
        val messageDigest = MessageDigest.getInstance("SHA-1")
        val dis = DigestInputStream(stream, messageDigest)
        dis.bufferedReader().use{ it.readText() }
        val sb = StringBuffer()
        val digestBytes = messageDigest.digest()
        for (b in digestBytes) sb.append(String.format("%02x", b)) // convert byte to hex char
        return sb.toString()
    }

    @Test
    @DisplayName("should write a binary with a unique hash to storage")
    fun testWriteUniqueFile() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)

        `when`(strategy.findDuplicateBinary(any(), any())).thenReturn(Optional.empty())
        `when`(adapter.createTempFile(any())).thenAnswer {
            (it.getArgument(0) as InputStream).bufferedReader().use { it.readText() }
            FileInfo("permanent/location", 5)
        }

        val permanentPath = repository.createFile(stream)

        verify(adapter).createTempFile(any())
        verify(adapter).moveTempFileToPermanentLocation(anyString(), anyString())
        assertNotNull(permanentPath, "Null file path returned")
    }

    @Test
    @DisplayName("should deduplicate a binary when there is a matching permanent file")
    fun testDeduplicateOnMatchingHash() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)
        val hash = getHash(bytes)
        val path = hash.chunked(2).joinToString(File.separator)
        val existingPath = "permanent/path"
        `when`(strategy.findDuplicateBinary(any(), any())).thenReturn(Optional.of(FileInfo(existingPath, 5)))
        `when`(adapter.createTempFile(any())).thenAnswer {
            (it.getArgument(0) as InputStream).bufferedReader().use { it.readText() }
            FileInfo("temp/file", 5)
        }
        `when`(adapter.inputStreamForTemporaryLocation(eq("temp/file"))).thenReturn(ByteArrayInputStream(bytes))
        `when`(adapter.inputStreamForPermanentLocation(contains(path))).thenReturn(ByteArrayInputStream(bytes))
        `when`(adapter.filesWithHashPrefix(path)).thenReturn(mutableListOf(FileInfo("$path/$hash", 5)))
        val retPath = repository.createFile(stream)
        verify(adapter, times(0)).moveTempFileToPermanentLocation(any(), any())
        assertEquals(existingPath, retPath, "Existing path not returned")
    }

    @Test
    @DisplayName("should deduplicate a binary when its hash exists but the file size is different")
    fun testDeduplicateOnMatchingHashDifferentSize() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)
        val hash = getHash(bytes)
        val path = hash.chunked(2).joinToString(File.separator)
        `when`(adapter.createTempFile(any())).thenReturn(FileInfo("temp/path", 5))
        `when`(adapter.filesWithHashPrefix(path)).thenReturn(mutableListOf(FileInfo("$path/$hash", bytes.size + 1L)))
        repository.createFile(stream)
        verify(adapter, times(1)).moveTempFileToPermanentLocation(any(), any())
    }

    @Test
    @DisplayName("should deduplicate a binary when its hash exists and the file size matches but the contents are different")
    fun testDeduplicateOnMatchingHashSameSizeDifferentContent() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)
        val hash = getHash(bytes)
        val path = hash.chunked(2).joinToString(File.separator)
        `when`(adapter.createTempFile(any())).thenAnswer {
            (it.getArgument(0) as InputStream).bufferedReader().use { it.readText() }
            FileInfo("temp/file", 5)
        }
        `when`(adapter.inputStreamForTemporaryLocation(eq("temp/file"))).thenReturn(ByteArrayInputStream(bytes))
        `when`(adapter.inputStreamForPermanentLocation(contains(path))).thenReturn(ByteArrayInputStream(byteArrayOf(2, 3, 4, 5, 6)))
        `when`(adapter.filesWithHashPrefix(path)).thenReturn(mutableListOf(FileInfo("$path/$hash", 5)))
        repository.createFile(stream)
        verify(adapter, times(1)).moveTempFileToPermanentLocation(any(), any())
    }

}