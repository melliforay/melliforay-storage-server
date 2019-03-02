package org.trancemountain.storageservice.model.support

import java.io.Serializable

class ClusterSessionInfo(private val sessionID: String, private val userID: String): Serializable {

    fun getSessionID(): String = sessionID

    fun getUserID(): String = userID

}