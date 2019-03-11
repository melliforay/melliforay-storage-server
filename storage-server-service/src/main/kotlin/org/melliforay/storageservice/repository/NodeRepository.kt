package org.melliforay.storageservice.repository

import org.melliforay.storageservice.rest.Credentials
import java.util.Optional

/**
 * Provides access to nodes. Although a NodeRepository is generally responsible for all operations involving
 * creating, modifying and deleting data in the system, only publicly-visible methods are contained in this
 * interface.
 */
interface NodeRepository {

    /**
     * Creates a session for a user with the given credentials.
     * @param credentials the credentials to use to authenticate a user
     * @return a session if the credentials are valid, or an empty optional if not
     */
    fun getSession(credentials: Credentials): Optional<Session>

    /**
     * Gets the active session with the given session token.
     * @param token the token for the session to retrieve.
     */
    fun getSession(token: String): Optional<Session>

}