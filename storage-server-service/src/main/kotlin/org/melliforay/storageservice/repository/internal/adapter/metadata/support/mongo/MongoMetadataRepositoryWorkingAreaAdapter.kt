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
import org.melliforay.storageservice.repository.NodeRepresentation
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryWorkingAreaAdapter
import org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.converter.NodeRepresentationReadConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.stereotype.Component
import java.util.Optional
import javax.annotation.PostConstruct

@Component
class MongoMetadataRepositoryWorkingAreaAdapter: MetadataRepositoryWorkingAreaAdapter {

    @Value("\${melliforay.service.storage.metadata.adapter.mongo.databaseName:melliforay}")
    private lateinit var dbName: String

    @Value("\${melliforay.service.storage.metadata.adapter.mongo.collection.workingArea:workingArea}")
    private lateinit var workingAreaCollectionName: String

    @Autowired
    private lateinit var client: MongoClient

    private lateinit var template: MongoTemplate

    @PostConstruct
    private fun init() {
        val dbFactory = SimpleMongoDbFactory(client, dbName)
        val context = MongoMappingContext()
        val resolver = DefaultDbRefResolver(dbFactory)
        val converter = MappingMongoConverter(resolver, context)
        converter.setCustomConversions(MongoCustomConversions(listOf(NodeRepresentationReadConverter())))
        converter.afterPropertiesSet()
        template = MongoTemplate(dbFactory, converter)
    }

    override fun createNodeRepresentation(session: Session, representation: NodeRepresentation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nodeRepresentation(session: Session, path: String): Optional<NodeRepresentation> {
        return Optional.empty()
    }
}