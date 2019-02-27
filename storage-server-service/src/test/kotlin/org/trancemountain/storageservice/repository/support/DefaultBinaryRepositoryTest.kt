package org.trancemountain.storageservice.repository.support

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
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
import org.trancemountain.storageservice.repository.BinaryRepository
import org.trancemountain.storageservice.repository.adapter.BinaryRepositoryStorageAdapter
import org.trancemountain.storageservice.repository.adapter.FileInfo
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@DisplayName("a default binary repository")
class DefaultBinaryRepositoryTest {

    @Configuration
    @Import(DefaultBinaryRepository::class)
    internal class Config {

        @Bean
        fun mockAdapter(): BinaryRepositoryStorageAdapter = mock(BinaryRepositoryStorageAdapter::class.java)

    }

    @Autowired
    private lateinit var adapter: BinaryRepositoryStorageAdapter

    @Autowired
    private lateinit var repository: BinaryRepository

    @BeforeEach
    private fun init() {
        reset(adapter)

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
    @DisplayName("should be able to write a binary with a unique hash to storage")
    fun testWriteUniqueFile() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)

        val hash = getHash(bytes)
        `when`(adapter.createTempFile(any())).thenAnswer {
            (it.getArgument(0) as InputStream).bufferedReader().use { it.readText() }
            FileInfo(hash, 5)
        }
        repository.createFile(stream)
        verify(adapter).createTempFile(any())
        val path = hash.chunked(2).joinToString(File.separator)
        verify(adapter).moveTempFileToPermanentLocation(anyString(), eq("$path/$hash"))
    }

    @Test
    @DisplayName("should be able to deduplicate a binary when there is a matching permanent file")
    fun testDeduplicateOnMatchingHash() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)
        val hash = getHash(bytes)
        val path = hash.chunked(2).joinToString(File.separator)
        `when`(adapter.createTempFile(any())).thenAnswer {
            (it.getArgument(0) as InputStream).bufferedReader().use { it.readText() }
            FileInfo("temp/file", 5)
        }
        `when`(adapter.inputStreamForLocation(eq("temp/file"))).thenReturn(ByteArrayInputStream(bytes))
        `when`(adapter.inputStreamForLocation(contains(path))).thenReturn(ByteArrayInputStream(bytes))
        `when`(adapter.filesWithHashPrefix(path)).thenReturn(mutableListOf(FileInfo("$path/$hash", 5)))
        repository.createFile(stream)
        verify(adapter, times(0)).moveTempFileToPermanentLocation(any(), any())
    }

    @Test
    @DisplayName("should be able to deduplicate a binary when its hash exists but the file size is different")
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
    @DisplayName("should be able to deduplicate a binary when its hash exists and the file size matches but the contents are different")
    fun testDeduplicateOnMatchingHashSameSizeDifferentContent() {
        val bytes: ByteArray = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(bytes)
        val hash = getHash(bytes)
        val path = hash.chunked(2).joinToString(File.separator)
        `when`(adapter.createTempFile(any())).thenAnswer {
            (it.getArgument(0) as InputStream).bufferedReader().use { it.readText() }
            FileInfo("/tmp/file", 5)
        }
        `when`(adapter.inputStreamForLocation(eq("/tmp/file"))).thenReturn(ByteArrayInputStream(bytes))
        `when`(adapter.inputStreamForLocation(contains(path))).thenReturn(ByteArrayInputStream(byteArrayOf(2, 3, 4, 5, 6)))
        `when`(adapter.filesWithHashPrefix(path)).thenReturn(mutableListOf(FileInfo("$path/$hash", 5)))
        repository.createFile(stream)
        verify(adapter, times(1)).moveTempFileToPermanentLocation(any(), any())
    }

}