package org.melliforay.storageservice.repository

import org.melliforay.storageservice.RevisionNumber

/**
 * The underlying representation of the metadata for a given node.  Distinguished from
 * a [Node] in that it contains no navigation logic, metadata validations by node type,
 * etc.  It is simply a depiction of the node's metadata.
 */
class NodeRepresentation(val name: String, val path: String, val revision: RevisionNumber) {

    /**
     * The current properties of the node.
     */
    lateinit var properties: Map<String, Any>

    /**
     * The stored snapshots of the node.
     */
    lateinit var snapshots: Map<String, Map<String, Any>>

}