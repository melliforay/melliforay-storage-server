package org.trancemountain.storageservice.service.clustering

import org.trancemountain.storageservice.model.support.ClusterSessionInfo
import java.util.Optional

interface ClusterSynchronizationService {

    fun addSessionInfo(key: String, info: ClusterSessionInfo)

    fun getSessionInfo(key: String): Optional<ClusterSessionInfo>

}