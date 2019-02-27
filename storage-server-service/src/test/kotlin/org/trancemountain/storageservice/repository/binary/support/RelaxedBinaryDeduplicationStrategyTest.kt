package org.trancemountain.storageservice.repository.binary.support

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.trancemountain.storageservice.repository.binary.adapter.FileInfo

@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = AnnotationConfigContextLoader::class)
@TestPropertySource(properties = ["trance.service.storage.binary.deduplication.strategy=relaxed"])
@DisplayName("a relaxed binary de-duplication strategy")
class RelaxedBinaryDeduplicationStrategyTest {

    @Configuration
    @Import(RelaxedBinaryDeduplicationStrategy::class)
    internal class Config

    @Autowired
    private lateinit var strategy: RelaxedBinaryDeduplicationStrategy

    @DisplayName("should treat binaries with the same hash and size as duplicates")
    @Test
    fun shouldTreatSameSizeAsDuplicate() {
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 5)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertTrue(retOpt.isPresent, "Duplicate file not returned")
    }

    @DisplayName("should not treat binaries with different sizes as duplicates")
    @Test
    fun shouldTreatDifferentSizeAsUnique() {
        val tempFileInfo = FileInfo("temp/file", 5)
        val permFileInfo = FileInfo("perm/file/hash", 2)
        val retOpt = strategy.findDuplicateBinary(tempFileInfo, listOf(permFileInfo))
        assertFalse(retOpt.isPresent, "Duplicate file returned")
    }

}