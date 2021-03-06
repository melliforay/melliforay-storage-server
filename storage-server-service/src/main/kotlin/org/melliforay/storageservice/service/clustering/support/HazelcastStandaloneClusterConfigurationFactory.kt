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
        config.networkConfig.join.multicastConfig.isEnabled = false
        config.networkConfig.join.awsConfig.isEnabled = false
        return config
    }

}