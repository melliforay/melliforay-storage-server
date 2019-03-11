package org.melliforay.storageservice.repository.internal.adapter.binary.support.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.FactoryBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("melliforay.service.storage.binary.adapter", havingValue = "s3")
class AmazonS3ClientFactory: FactoryBean<AmazonS3> {

    override fun getObject(): AmazonS3? {
        return AmazonS3ClientBuilder.defaultClient()
    }

    override fun getObjectType(): Class<*>? {
        return AmazonS3::class.java
    }
}