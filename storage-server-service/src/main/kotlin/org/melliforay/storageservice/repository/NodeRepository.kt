package org.melliforay.storageservice.repository

import org.melliforay.storageservice.model.Node
import java.util.Optional

/**
 * Provides access to nodes
 */
interface NodeRepository {

    fun node(path: String): Optional<Node>

}