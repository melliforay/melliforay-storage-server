package org.melliforay.storageservice.controller

import org.melliforay.storageservice.service.SessionService
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
    fun getHello(@CookieValue("melliforayToken") sessionToken: String): Mono<String> {
        println("Using session token $sessionToken")
        val sessionOpt = sessionService.getSession(sessionToken)
        return when (sessionOpt.isPresent) {
            true -> Mono.just("You are ${sessionOpt.get().getUserID()}")
            false -> Mono.just("I don't know you!")
        }
    }

}