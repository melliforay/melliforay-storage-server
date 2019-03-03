package org.melliforay.storageservice.model.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.melliforay.storageservice.model.Session
import org.melliforay.storageservice.service.SessionService

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class DefaultSession(private val sessionID: String, private val userID: String): Session {

    @Autowired
    private lateinit var sessionService: SessionService

    override fun getSessionID(): String = sessionID

    override fun getUserID(): String = userID

}