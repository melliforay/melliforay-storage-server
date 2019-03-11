package org.melliforay.storageservice.model.support

import java.io.Serializable

class ClusterSessionInfo(private val sessionID: String, private val userID: String, private val revision: String): Serializable {

    fun getSessionID(): String = sessionID

    fun getUserID(): String = userID

    fun getRevision(): String = revision

}