package org.melliforay.storageservice.service.clustering.support

import com.hazelcast.config.Config
import org.springframework.beans.factory.FactoryBean

/**
 * A factory that produces a Hazelcast cluster configuration.
 */
interface ClusterConfigurationFactory: FactoryBean<Config> {

    override fun getObjectType(): Class<*>? {
        return Config::class.java
    }

}