package org.trancemountain.storageservice.service

import org.springframework.stereotype.Service
import org.trancemountain.storageservice.model.Session
import org.trancemountain.storageservice.rest.Credentials
import java.util.Optional

/**
 * Provides access to sessions and allows them to be closed.
 */
@Service
interface SessionService {

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

    /**
     * Closes a session
     * @param session the session to close
     */
    fun closeSession(session: Session)

}