package org.melliforay.storageservice.repository.internal.support

import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.NodeRepresentation
import org.melliforay.storageservice.repository.Session
import org.melliforay.storageservice.repository.internal.MetadataManagerService
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryCatalogAdapter
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryDataPartitionAdapter
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryWorkingAreaAdapter
import org.melliforay.storageservice.repository.internal.support.metadata.command.MetadataStoreInitializationCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class DefaultMetadataManagerService: MetadataManagerService {

    @Autowired
    private lateinit var context: ApplicationContext

    @Autowired
    private lateinit var catalogAdapter: MetadataRepositoryCatalogAdapter

    @Autowired
    private lateinit var workingAreaAdapter: MetadataRepositoryWorkingAreaAdapter

    @Autowired
    private lateinit var dataPartitionAdapter: MetadataRepositoryDataPartitionAdapter

    override fun initializeMetadataStore() {
        val command = context.getBean(MetadataStoreInitializationCommand::class.java)
        command.execute()
    }

    override fun repositoryRevision(): RevisionNumber {
        val revisionOpt = catalogAdapter.currentRepositoryRevision()
        return when (revisionOpt.isPresent) {
            true -> revisionOpt.get()
            false -> throw RuntimeException("Repository has no current revision")
        }
    }

    override fun nodeRepresentation(session: Session, path: String): Optional<NodeRepresentation> {
        val workingRepOpt: Optional<NodeRepresentation> = workingAreaAdapter.nodeRepresentation(session, path)
        return when (workingRepOpt.isPresent) {
            true -> workingRepOpt
            false -> dataPartitionAdapter.nodeRepresentation(session, path)
        }
    }
}