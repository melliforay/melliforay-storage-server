package org.melliforay.storageservice.repository

import org.melliforay.storageservice.RevisionNumber

interface Node {

    fun name(): String

    fun path(): String

    fun revision(): RevisionNumber

}