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