package org.trancemountain.storageservice.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.apache.logging.log4j.Level
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Used to change Log4J 2 log levels in a running server.
 */
@RestController
@RequestMapping("/v1/logger")
@Api(tags = ["Log Controller"], value = "Default Log Controller", description = "Provides operations for getting and setting the levels of loggers.")
interface LoggerController {

    /**
     * Gets the current log level of the given logger.
     * @param loggerName the name of a logger
     * @return the logger's current level
     */
    @ApiOperation(value = "Gets the level of a logger.", response = String::class)
    @ApiResponses(ApiResponse(code = 200, message = "If the logger level can be found"))
    @GetMapping("/{loggerName}/level")
    fun getLogLevel(@PathVariable("loggerName") loggerName: String): String

    /**
     * Sets the level of a logger.
     * @param loggerName the logger to alter
     * @param level the level to which the logger should be set
     */
    @ApiOperation(value = "Sets the level of a logger.")
    @ApiResponses(ApiResponse(code = 200, message = "If the logger level can be set"))
    @PutMapping("/{loggerName}/level/{level}")
    fun setLogLevel(@PathVariable("loggerName") loggerName: String, @PathVariable("level") level: Level)

}