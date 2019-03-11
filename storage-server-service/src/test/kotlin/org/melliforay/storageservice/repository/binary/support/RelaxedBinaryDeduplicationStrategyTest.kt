package org.melliforay.storageservice.repository.binary.support

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.melliforay.storageservice.repository.internal.adapter.binary.FileInfo
import org.melliforay.storageservice.repository.internal.support.RelaxedBinaryDeduplicationStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@TestPropertySource(properties = ["melliforay.service.storage.binary.deduplication.strategy=relaxed"])
@DisplayName("a relaxed binary de-duplication strategy")
class RelaxedBinaryDeduplicationStrategyTest {

    @Configuration
    @Import(RelaxedBinaryDeduplicationStrategy::class)
    internal class Config

    @Autowired
    private lateinit var strategy: RelaxedBinaryDeduplicationStrategy

    @Test
    @DisplayName("should treat binaries with the same hash and size as duplicates")
    fun shouldTreatSameSizeAsDuplicate() {
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 5)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertTrue(retOpt.isPresent, "Duplicate file not returned")
    }

    @Test
    @DisplayName("should not treat binaries with different sizes as duplicates")
    fun shouldTreatDifferentSizeAsUnique() {
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 2)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertFalse(retOpt.isPresent, "Duplicate file returned")
    }

    @Test
    @DisplayName("should throw an exception when finding mutliple files with the same hash and size")
    fun shouldThrowExceptionOnMultipleMatches() {
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo1 = FileInfo("perm/file/hash", 5)
        val permFileInfo2 = FileInfo("perm/file2/hash", 5)
        assertThrows(IllegalArgumentException::class.java) {
            strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo1, permFileInfo2))
        }

    }

}