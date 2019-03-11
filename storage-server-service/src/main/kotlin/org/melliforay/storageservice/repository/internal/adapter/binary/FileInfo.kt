package org.melliforay.storageservice.repository.internal.adapter.binary

/**
 * Provides information about the path and size of a file.
 */
data class FileInfo(val path: String, val size: Long)