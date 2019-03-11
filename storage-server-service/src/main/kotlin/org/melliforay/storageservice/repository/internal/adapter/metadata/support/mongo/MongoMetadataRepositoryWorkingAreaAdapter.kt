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