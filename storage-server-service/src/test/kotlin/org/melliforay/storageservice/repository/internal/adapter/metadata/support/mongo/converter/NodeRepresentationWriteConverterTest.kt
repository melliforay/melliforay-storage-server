package org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.converter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.NodeRepresentation

@DisplayName("a node representation write converter")
class NodeRepresentationWriteConverterTest {

    private val converter = NodeRepresentationWriteConverter()

    private fun createRepresentation(): NodeRepresentation {
        val repr = NodeRepresentation("node name", "/node/path", RevisionNumber(33))
        return repr
    }

    @Test
    @DisplayName("should write a representation's name")
    fun writeName() {
        val repr = createRepresentation()
        val doc = converter.convert(repr)
        assertEquals(repr.name, doc!!.getString("name"))
    }

    @Test
    @DisplayName("should write a representation's path")
    fun writePath() {
        val repr = createRepresentation()
        val doc = converter.convert(repr)
        assertEquals(repr.path, doc!!.getString("path"))
    }

    @Test
    @DisplayName("should write a representation's revision")
    fun writeRevision() {
        val repr = createRepresentation()
        val doc = converter.convert(repr)
        assertEquals(repr.revision.toString(), doc!!.getString("revision"))
    }

}