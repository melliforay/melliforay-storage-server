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