package org.melliforay.storageservice.repository.internal

import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.rest.Credentials
import org.springframework.stereotype.Service
import java.util.Optional

/**
 * Provides access to sessions and allows them to be closed.
 */
@Service
interface SessionService {

    /**
     * Creates a session for a user with the given credentials, at a given revision.
     * @param credentials the credentials to use to authenticate a user
     * @param revision the revision number that the session should use
     * @return a session if the credentials are valid, or an empty optional if not
     */
    fun getSession(credentials: Credentials, revision: RevisionNumber): Optional<Session>

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