package org.trancemountain.storageservice.repository.adapter

import java.io.InputStream

/**
 * Handles low-level binary storage operations.
 */
interface BinaryRepositoryStorageAdapter {

    /**
     * Creates a temporary file containing the data taken from an input stream
     * @param stream an input stream
     * @return the information about the temp file that was created
     */
    fun createTempFile(stream: InputStream): FileInfo

    /**
     * Deletes the temp file at a given path
     * @param path the relative path of a temp file to delete
     */
    fun deleteTempFile(path: String)

    /**
     * Returns information about files that begin with the specified hash prefix
     * @param hashPrefix the prefix to use in order to find files
     */
    fun filesWithHashPrefix(hashPrefix: String): List<FileInfo>

    /**
     * Opens an input stream for the file at the given location
     * @param location the location of the file for which an input stream should be returned
     */
    fun inputStreamForLocation(location: String): InputStream

    /**
     * Moves a temporary file at a given relative path to a permanent file at the given
     * relative path.
     * @param tempPath the relative path of a temporary file
     * @param targetLocation the relative path of the permanent file to be created.
     */
    fun moveTempFileToPermanentLocation(tempPath: String, targetLocation: String)

}