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