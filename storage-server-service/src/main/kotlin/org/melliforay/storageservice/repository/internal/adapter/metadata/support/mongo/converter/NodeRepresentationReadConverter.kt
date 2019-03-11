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