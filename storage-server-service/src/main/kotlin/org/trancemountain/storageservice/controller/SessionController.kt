package org.trancemountain.storageservice.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.ResponseHeader
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.trancemountain.storageservice.rest.Credentials
import reactor.core.publisher.Mono

@RestController
@Api(tags = ["Session Controller"], value = "Session Controller", description = "Provides operations for obtaining and releasing Trance Mountain sessions. ")
@RequestMapping("/v1/session")
interface SessionController {

    /**
     * Returns a valid session token if the given credentials are authentic.
     */
    @ApiOperation("Creates a session for a valid user",  responseHeaders = [ResponseHeader(name = "Set-Cookie", description = "a session token in the form tranceToken=<token>; must be used in all subsequent requests")])
    @ApiResponses(value = [
        ApiResponse(code = 401, message = "Invalid credentials")
    ])
    @PostMapping
    fun getSession(@RequestBody credentials: Credentials): Mono<ResponseEntity<Void>>

    /**
     * Closes an active session.
     */
    @ApiOperation("Closes a session")
    @DeleteMapping
    fun closeSession(@ApiParam("the token of the session to close") @CookieValue("tranceToken") token: String): Mono<ResponseEntity<Void>>

}