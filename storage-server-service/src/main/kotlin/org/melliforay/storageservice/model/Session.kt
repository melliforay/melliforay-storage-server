package org.melliforay.storageservice.model

/**
 * A connection to the metadata and binary stores; similar to a JDBC Connection object.
 */
interface Session {

    /**
     * Returns the ID of this session.
     */
    fun getSessionID(): String

    /**
     * Returns the ID of the user that owns this session.
     */
    fun getUserID(): String

}