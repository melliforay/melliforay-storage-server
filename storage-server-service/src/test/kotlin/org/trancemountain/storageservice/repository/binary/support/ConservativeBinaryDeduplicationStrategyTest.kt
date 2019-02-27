package org.trancemountain.storageservice.repository.binary.support

import com.nhaarman.mockito_kotlin.eq
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.trancemountain.storageservice.repository.binary.BinaryDeduplicationStrategy
import org.trancemountain.storageservice.repository.binary.adapter.BinaryRepositoryStorageAdapter
import org.trancemountain.storageservice.repository.binary.adapter.FileInfo
import java.io.ByteArrayInputStream

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@TestPropertySource(properties = ["trance.service.storage.binary.deduplication.strategy=conservative"])
@DisplayName("a conservative binary de-duplication strategy")
class ConservativeBinaryDeduplicationStrategyTest {

    @Configuration
    @Import(ConservativeBinaryDeduplicationStrategy::class)
    internal class Config {

        @Bean
        fun mockAdapter(): BinaryRepositoryStorageAdapter = mock(BinaryRepositoryStorageAdapter::class.java)

    }

    @Autowired
    private lateinit var adapter: BinaryRepositoryStorageAdapter

    @Autowired
    private lateinit var strategy: BinaryDeduplicationStrategy

    @BeforeEach
    private fun init() {
        reset(adapter)
    }

    @DisplayName("should not treat binaries with different sizes as duplicates")
    @Test
    fun shouldTreatDifferentSizeAsUnique() {
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 2)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertFalse(retOpt.isPresent, "Duplicate file returned")
    }

    @DisplayName("should treat binaries with same size and content as duplicates")
    @Test
    fun shouldTreatSameSizeAndContentAsDuplicate() {
        `when`(adapter.inputStreamForTemporaryLocation(eq("temp/file"))).thenReturn(ByteArrayInputStream(byteArrayOf(1, 2, 3)))
        `when`(adapter.inputStreamForPermanentLocation(eq("perm/file/hash"))).thenReturn(ByteArrayInputStream(byteArrayOf(1, 2, 3)))
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 5)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertTrue(retOpt.isPresent, "Duplicate not returned")
    }


    @DisplayName("should not treat binaries with same size but different content as duplicates")
    @Test
    fun shouldNotTreatSameSizeAndDifferentContentAsDuplicate() {
        `when`(adapter.inputStreamForTemporaryLocation(eq("temp/file"))).thenReturn(ByteArrayInputStream(byteArrayOf(1, 2, 3)))
        `when`(adapter.inputStreamForPermanentLocation(eq("perm/file/hash"))).thenReturn(ByteArrayInputStream(byteArrayOf(2, 3, 4)))
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 5)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertFalse(retOpt.isPresent, "Duplicate was returned")
    }


}