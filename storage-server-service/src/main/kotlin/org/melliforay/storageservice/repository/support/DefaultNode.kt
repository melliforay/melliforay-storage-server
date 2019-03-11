package org.melliforay.storageservice.repository.support

import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.Node
import org.melliforay.storageservice.repository.NodeRepresentation
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class DefaultNode(val nodeRepresentation: NodeRepresentation): Node {

    override fun name(): String = nodeRepresentation.name

    override fun path(): String = nodeRepresentation.path

    override fun revision(): RevisionNumber = nodeRepresentation.revision

}