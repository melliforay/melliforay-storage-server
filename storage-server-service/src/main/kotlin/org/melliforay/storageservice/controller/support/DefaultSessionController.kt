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

package org.melliforay.storageservice.controller.support

import org.apache.logging.log4j.LogManager
import org.melliforay.storageservice.controller.SessionController
import org.melliforay.storageservice.repository.NodeRepository
import org.melliforay.storageservice.rest.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class DefaultSessionController: SessionController {

    private val logger = LogManager.getLogger(DefaultSessionController::class.java)

    @Autowired
    private lateinit var nodeRepository: NodeRepository

    override fun getSession(@RequestBody credentials: Credentials): Mono<ResponseEntity<Void>> {
        logger.info("Getting session for {}", credentials.username)
        val sessionOpt = nodeRepository.getSession(credentials)
        return when (sessionOpt.isPresent) {
            true -> Mono.just(ResponseEntity.ok().header("Set-Cookie", "melliforayToken=${sessionOpt.get().getSessionID()}").build())
            false -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
        }
    }

    override fun closeSession(@CookieValue("melliforayToken") token: String): Mono<ResponseEntity<Void>> {
        val sessionOpt = nodeRepository.getSession(token)
        return when (sessionOpt.isPresent) {
            true -> {
                sessionOpt.get().close()
                Mono.just(ResponseEntity.ok().header("Set-Cookie", "melliforayToken=; expires=Thu, 01 Jan 1970 00:00:00 GMT").build())
            }
            false -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
        }
    }

}