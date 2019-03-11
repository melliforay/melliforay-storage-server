package org.melliforay.storageservice.repository

import org.melliforay.storageservice.RevisionNumber

/**
 * A reference that points to another [Node].
 * @param path the path of the node to be referenced
 * @param snapshotRevision the snapshot revision of the node to reference, or null if the reference is to the latest revision of the node
 */
class NodeReference(val path: String, val snapshotRevision: RevisionNumber?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NodeReference) return false

        if (path != other.path) return false
        if (snapshotRevision != other.snapshotRevision) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + (snapshotRevision?.hashCode() ?: 0)
        return result
    }

}