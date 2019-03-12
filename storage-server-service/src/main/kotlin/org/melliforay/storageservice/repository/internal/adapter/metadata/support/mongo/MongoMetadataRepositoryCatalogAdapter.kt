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

package org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo

import com.mongodb.MongoClient
import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryCatalogAdapter
import org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.document.CatalogDocumentType
import org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.document.RepositoryRevisionDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.Optional
import javax.annotation.PostConstruct

@Component
@ConditionalOnProperty("melliforay.service.storage.metadata.adapter.type", havingValue = "mongo")
class MongoMetadataRepositoryCatalogAdapter: MetadataRepositoryCatalogAdapter {

    @Value("\${melliforay.service.storage.metadata.adapter.mongo.databaseName:melliforay}")
    private lateinit var dbName: String

    @Value("\${melliforay.service.storage.metadata.adapter.mongo.collection.catalog:catalog}")
    private lateinit var catalogCollectionName: String

    @Autowired
    private lateinit var client: MongoClient

    private lateinit var template: MongoTemplate

    @PostConstruct
    private fun init() {
        template = MongoTemplate(client, dbName)
    }

    private fun getRevisionDocument(): RepositoryRevisionDocument? {
        val query = Query()
        query.addCriteria(Criteria.where("documentType").`is`(CatalogDocumentType.REVISION_DOCUMENT))
        return template.findOne(query, RepositoryRevisionDocument::class.java, catalogCollectionName)
    }

    override fun setRepositoryRevision(revisionNumber: RevisionNumber) {
        val existingDoc = getRevisionDocument()
        val doc = when(existingDoc) {
            null -> RepositoryRevisionDocument(revisionNumber.toString())
            else -> {
                existingDoc.revision = revisionNumber.toString()
                existingDoc
            }
        }
        template.save(doc, catalogCollectionName)
    }

    override fun currentRepositoryRevision(): Optional<RevisionNumber> {
        val revisionDocument: RepositoryRevisionDocument? = getRevisionDocument()
        return when(revisionDocument) {
            null -> Optional.empty()
            else -> Optional.of(RevisionNumber(revisionDocument.revision))
        }
    }
}