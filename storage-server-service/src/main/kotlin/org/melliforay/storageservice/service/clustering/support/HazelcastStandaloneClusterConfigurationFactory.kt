package org.melliforay.storageservice.service.clustering.support

import com.hazelcast.config.Config
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

/**
 * Creates a Hazelcast cluster configuration using multicast networking to connect nodes
 * in the cluster.
 */
@Configuration
@ConditionalOnProperty("melliforay.service.cluster.strategy", havingValue = "standalone")
class HazelcastStandaloneClusterConfigurationFactory: ClusterConfigurationFactory {

    override fun getObject(): Config? {
        val config = Config()
        config.networkConfig.join.tcpIpConfig.isEnabled = false
        return config
    }

}