package org.melliforay.storageservice.service.clustering.support

import com.hazelcast.config.Config
import com.hazelcast.core.EntryEvent
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.map.listener.EntryAddedListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.melliforay.storageservice.model.Session
import org.melliforay.storageservice.model.support.ClusterSessionInfo
import org.melliforay.storageservice.service.clustering.ClusterSynchronizationService
import java.util.Optional
import java.util.UUID
import javax.annotation.PostConstruct

@Service
class HazelcastClusterSynchronizationService: ClusterSynchronizationService {

    private val SESSION_MAP_NAME = "sessionMap"

    @Autowired
    private lateinit var config: Config

    private lateinit var hazelcastInstance: HazelcastInstance

    @PostConstruct
    private fun init() {
        config.instanceName = "Storage server ${UUID.randomUUID()}"
        config.groupConfig.name = "StorageService"
        hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config)

        val sMap: IMap<String, Session> = hazelcastInstance.getMap<String, Session>(SESSION_MAP_NAME)
        sMap.addEntryListener(object: EntryAddedListener<String, ClusterSessionInfo> {
            override fun entryAdded(entry: EntryEvent<String, ClusterSessionInfo>?) {
                println("Added session ${entry!!.value} with key ${entry.key}")
            }
        }, true)

    }

    override fun addSessionInfo(key: String, info: ClusterSessionInfo) {
        val sessionMap = hazelcastInstance.getMap<String, ClusterSessionInfo>(SESSION_MAP_NAME)
        sessionMap[key] = info
    }

    override fun getSessionInfo(key: String): Optional<ClusterSessionInfo> {
        val session = hazelcastInstance.getMap<String, ClusterSessionInfo>(SESSION_MAP_NAME).get(key)
        return when (session) {
            null -> Optional.empty()
            else -> Optional.of(session)
        }
    }
}