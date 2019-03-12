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

package org.melliforay.storageservice.security

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
@ConditionalOnProperty("melliforay.security.providers.ldap.userDnLookup")
class LdapAuthenticationProviderFactory: FactoryBean<AuthenticationProvider> {

    @Value("\${melliforay.security.providers.ldap.userDnLookup}")
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