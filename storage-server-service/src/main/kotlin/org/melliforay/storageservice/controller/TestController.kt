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

package org.melliforay.storageservice.controller

import org.melliforay.storageservice.repository.Node
import org.melliforay.storageservice.repository.internal.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TestController {

    @Autowired
    private lateinit var sessionService: SessionService

    @GetMapping("/hello")
    fun getHello(@CookieValue("melliforayToken") sessionToken: String): Mono<Node> {
        println("Using session token $sessionToken")
        val sessionOpt = sessionService.getSession(sessionToken)
        return when (sessionOpt.isPresent) {
            true -> Mono.just(sessionOpt.get().rootNode())
            false -> Mono.empty()
        }
    }

}