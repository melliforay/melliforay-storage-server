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

import org.bson.Document
import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.BinaryReference
import org.melliforay.storageservice.repository.NodeReference
import org.melliforay.storageservice.repository.NodeRepresentation
import org.springframework.core.convert.converter.Converter

/**
 * Converts a BSON document to a [NodeRepresentation].
 */
class NodeRepresentationReadConverter: Converter<Document, NodeRepresentation> {

    override fun convert(doc: Document): NodeRepresentation? {
        val name = doc.getString("name")
        val path = doc.getString("path")
        val revision = doc.getString("revision")

        val representation = NodeRepresentation(name, path, RevisionNumber(revision))

        representation.properties = mapFromDocument(doc.get("properties", Document::class.java))

        val snapshots: Document? = doc.get("snapshots", Document::class.java)
        if (snapshots != null) {
            val snapshotMap = snapshots.map {
                it.key to mapFromDocument(it.value as Document)
            }.toMap()
            representation.snapshots = snapshotMap
        }

        return representation
    }

    /**
     * Produces an immutable map from a [Document].
     * @param doc the document from which the map should be produced
     */
    private fun mapFromDocument(doc: Document): Map<String, Any> {
        return doc.map {
            val entryVal: Any = it.value
            val value = when (entryVal) {
                is Document -> objectFromDocument(entryVal)
                else -> it.value
            }
            it.key to value
        }.toMap()
    }

    /**
     * Produces one of the specialized data types, i.e. a [BinaryReference] or
     * [NodeReference], from a [Document] object.
     * @param doc the document from which the data should be produced
     */
    private fun objectFromDocument(doc: Document): Any {
        val type = doc.getString("type")
        return when (type) {
            "BinaryReference" -> BinaryReference(doc.getString("path"))
            "NodeReference" -> {
                val sr: String? = doc["snapshotRevision"] as String?
                val revision: RevisionNumber? = when (sr) {
                    null -> null
                    else -> RevisionNumber(sr)
                }
                NodeReference(doc.getString("path"), revision)
            }
            else -> throw RuntimeException("Unknown property $doc")
        }
    }
}