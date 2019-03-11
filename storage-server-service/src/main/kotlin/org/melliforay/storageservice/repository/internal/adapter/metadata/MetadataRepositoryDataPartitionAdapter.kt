package org.melliforay.storageservice.repository.internal.adapter.metadata

import org.melliforay.storageservice.repository.NodeRepresentation
import org.melliforay.storageservice.repository.Session
import java.util.Optional

interface MetadataRepositoryDataPartitionAdapter {

    fun writeNodeRepresentation(representation: NodeRepresentation)

    fun nodeRepresentation(session: Session, path: String): Optional<NodeRepresentation>

}