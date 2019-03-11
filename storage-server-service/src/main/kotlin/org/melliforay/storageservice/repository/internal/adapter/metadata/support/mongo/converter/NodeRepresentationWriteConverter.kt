package org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.converter

import org.bson.Document
import org.melliforay.storageservice.repository.NodeRepresentation
import org.springframework.core.convert.converter.Converter

class NodeRepresentationWriteConverter: Converter<NodeRepresentation, Document> {

    override fun convert(representation: NodeRepresentation): Document? {
        val doc = Document()
        doc["name"] = representation.name
        doc["path"] = representation.path
        doc["revision"] = representation.revision.toString()
        return doc
    }

}