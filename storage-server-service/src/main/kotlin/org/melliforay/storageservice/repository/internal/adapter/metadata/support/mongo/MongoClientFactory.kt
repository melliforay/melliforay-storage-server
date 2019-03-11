package org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo

import com.mongodb.MongoClient
import org.springframework.beans.factory.FactoryBean
import org.springframework.stereotype.Component

@Component
class MongoClientFactory: FactoryBean<MongoClient> {



    override fun getObject(): MongoClient? {
        return MongoClient()
    }

    override fun getObjectType(): Class<*>? {
        return MongoClient::class.java
    }
}