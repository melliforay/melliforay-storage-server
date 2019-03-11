package org.melliforay.storageservice.service.clustering

import org.melliforay.storageservice.model.support.ClusterSessionInfo
import java.util.Optional

/**
 * Cluster-wide coordination operations.
 */
interface ClusterSynchronizationService {

    /**
     * Adds information about a session to the cluster.
     * @param key the key of the session to be added
     * @param info information about the session
     */
    fun addSessionInfo(key: String, info: ClusterSessionInfo)

    /**
     * Retrieves information about a session from the cluster.
     * @param key the key of the session for which information should be retrieved
     * @return information about the session if it exists, or an empty optional if it doesn't
     */
    fun getSessionInfo(key: String): Optional<ClusterSessionInfo>

    /**
     * Removes the session information associated with the given key.
     * @param key the session key to remove
     */
    fun removeSessionInfo(key: String)

    /**
     * Attempts to acquire a global lock and execute a process.
     * @param lockName the name of the global lock to acquire
     * @param process the process to run if the lock is acquired
     */
    fun executeWithGlobalLock(lockName: String, process: () -> Unit)

}