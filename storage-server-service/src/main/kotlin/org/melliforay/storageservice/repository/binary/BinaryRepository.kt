package org.melliforay.storageservice.repository.binary

import java.io.InputStream
import java.util.Optional

/**
 * A binary repository is responsible for storing a given piece of binary data
 * at a unique location and allowing it to be retrieved later from that location.
 *
 * The repository must also guarantee that multiple calls to store the exact same binary
 * data must return the exact same stored location, i.e., it must de-duplicate binary
 * files so that only one copy of a given binary is actually stored.
 */
interface BinaryRepository {

    /**
     * Creates a unique, de-duplicated file from the given input stream and returns its relative path within
     * the file storage system.
     * @param stream the stream to process
     * @return the relative path of this file within the data store
     */
    fun createFile(stream: InputStream): String

    /**
     * Returns an input stream for the file at the given path, or None if no such file exists
     * @param path a hashed file path originally returned from the createFile() method
     */
    fun getInputStreamForFile(path: String): Optional<InputStream>

    /**
     * Returns true if there is a file at the given path.
     */
    fun fileExists(hashPath: String): Boolean

    /**
     * Deletes the file at the given path.
     */
    fun delete(hashPath: String): Boolean


}