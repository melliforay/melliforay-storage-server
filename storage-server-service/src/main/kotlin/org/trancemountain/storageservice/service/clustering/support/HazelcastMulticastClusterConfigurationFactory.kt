package org.trancemountain.storageservice.service.clustering.support

import com.hazelcast.config.Config
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

/**
 * Creates a Hazelcast cluster configuration using multicast networking to connect nodes
 * in the cluster.
 */
@Configuration
@ConditionalOnProperty("trance.service.cluster.strategy", havingValue = "multicast")
class HazelcastMulticastClusterConfigurationFactory: ClusterConfigurationFactory {

    override fun getObject(): Config? {
        val cfg = Config()
        val network = cfg.networkConfig
        network.interfaces.isEnabled = true
        network.interfaces.interfaces = listOf("192.168.1.*")
        val join = network.join
        join.multicastConfig.isEnabled = true
        return cfg
    }

}