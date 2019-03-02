package org.trancemountain.storageservice.service.clustering.support

import com.hazelcast.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

/**
 * Creates a Hazelcast cluster configuration using AWS service discovery to connect
 * nodes in the cluster.
 *
 * The 4 key settings here are AWS access key and secret key, in order to provide
 * Hazelcast the ability to find AWS resources, and the cluster tag name/value.  Servers
 * that are tagged with the given tag and value are the ones identified as being candidates
 * to join the same cluster.
 */
@Configuration
@ConditionalOnProperty("trance.service.cluster.strategy", havingValue = "aws")
class HazelcastAwsClusterConfigurationFactory: ClusterConfigurationFactory {

    /**
     * The AWS secret key to use.
     */
    @Value("\${trance.service.cluster.secretKey}")
    private lateinit var secretKey: String

    /**
     * The AWS access key to use.
     */
    @Value("\${trance.service.cluster.accessKey}")
    private lateinit var accessKey: String

    /**
     * The service tag name to use in order to find nodes that should be in the cluster.
     */
    @Value("\${trance.service.cluster.tagKey}")
    private lateinit var tagKey: String

    /**
     * The service value to use in order to find nodes that should be in the cluster.
     */
    @Value("\${trance.service.cluster.tagValue}")
    private lateinit var tagValue: String

    override fun getObject(): Config? {
        val cfg = Config()

        val network = cfg.networkConfig
        val join = network.join
        join.multicastConfig.isEnabled = false
        join.awsConfig.isEnabled = true
        join.awsConfig.accessKey = accessKey
        join.awsConfig.secretKey = secretKey
        join.awsConfig.tagKey = tagKey
        join.awsConfig.tagValue = tagValue

        return cfg
    }

}