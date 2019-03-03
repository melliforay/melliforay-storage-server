package org.melliforay.storageservice.controller.support

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.melliforay.storageservice.controller.LoggerController

@RestController
class DefaultLoggerController: LoggerController {

    override fun getLogLevel(@PathVariable("loggerName") loggerName: String): String {
        return LogManager.getLogger(loggerName).level.standardLevel.toString()
    }

    override fun setLogLevel(@PathVariable("loggerName") loggerName: String, @PathVariable("level") level: Level) {
        val ctx = LogManager.getContext(false) as LoggerContext
        val config = ctx.configuration
        val loggerConfig = config.getLoggerConfig(loggerName)
        loggerConfig.level = level
        ctx.updateLoggers()
        val logger = LogManager.getLogger(loggerName)
        logger.log(level, "Set logger level to {}", level)
    }

}