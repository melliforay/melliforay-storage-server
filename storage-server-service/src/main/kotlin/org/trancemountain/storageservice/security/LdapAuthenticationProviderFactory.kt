package org.trancemountain.storageservice.security

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.ldap.core.support.BaseLdapPathContextSource
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.ldap.authentication.BindAuthenticator
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider
import org.springframework.stereotype.Component

/**
 * Creates an LDAP AuthenticationProvider that combines the default Spring
 * LDAP configurations with additional system-specific configurations.
 */
@Component
@ConditionalOnProperty("trance.security.providers.ldap.userDnLookup")
class LdapAuthenticationProviderFactory: FactoryBean<AuthenticationProvider> {

    @Value("\${trance.security.providers.ldap.userDnLookup}")
    private lateinit var userDnLookup: String

    @Autowired
    private lateinit var contextSource: BaseLdapPathContextSource

    override fun getObject(): AuthenticationProvider? {
        val ba = BindAuthenticator(contextSource)
        ba.setUserDnPatterns(arrayOf(userDnLookup))
        return LdapAuthenticationProvider(ba)
    }

    override fun getObjectType(): Class<*>? {
        return AuthenticationProvider::class.java
    }

}