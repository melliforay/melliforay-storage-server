package org.melliforay.storageservice.repository

/**
 * A reference to a binary object.
 * @param path the path of the binary object
 */
class BinaryReference(val path: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BinaryReference) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}