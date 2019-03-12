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