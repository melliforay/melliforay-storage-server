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