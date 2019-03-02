package org.trancemountain.storageservice.security

import org.springframework.beans.factory.FactoryBean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

/**
 * Creates a custom AuthenticationProvider that authenticates users against the current
 * set of internally-created users (i.e. users not authenticated against an external
 * system like LDAP).
 */
@Component
class LocalUserAuthenticationProviderFactory: FactoryBean<AuthenticationProvider> {

    private class TestProvider: AuthenticationProvider {
        override fun authenticate(auth: Authentication?): Authentication {
           val isAuthenticated = (auth?.credentials.toString() == "test" && auth?.name == "test")
            if (isAuthenticated) {
                return UsernamePasswordAuthenticationToken(auth!!.name, auth.credentials.toString(), listOf<GrantedAuthority>(SimpleGrantedAuthority("SUPERDUDE")))
            } else {
                throw BadCredentialsException("User doesn't have the right password")
            }
        }

        override fun supports(clazz: Class<*>?): Boolean {
            return clazz == UsernamePasswordAuthenticationToken::class.java
        }
    }

    private val provider = TestProvider()

    override fun getObject(): AuthenticationProvider? {
        return provider
    }

    override fun getObjectType(): Class<*>? {
        return AuthenticationProvider::class.java
    }
}