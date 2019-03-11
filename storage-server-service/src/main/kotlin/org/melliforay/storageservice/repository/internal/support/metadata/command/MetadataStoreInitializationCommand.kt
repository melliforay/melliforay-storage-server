package org.melliforay.storageservice.repository.internal.support.metadata.command

import org.apache.logging.log4j.LogManager
import org.melliforay.storageservice.RevisionNumber
import org.melliforay.storageservice.repository.NodeRepresentation
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryCatalogAdapter
import org.melliforay.storageservice.repository.internal.adapter.metadata.MetadataRepositoryDataPartitionAdapter
import org.melliforay.storageservice.service.clustering.ClusterSynchronizationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Initializes the metadata store.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class MetadataStoreInitializationCommand {

    private val logger = LogManager.getLogger(MetadataStoreInitializationCommand::class.java)

    private val initLockName = "metadataInitLock"

    @Autowired
    private lateinit var syncService: ClusterSynchronizationService

    @Autowired
    private lateinit var catalogAdapter: MetadataRepositoryCatalogAdapter

    @Autowired
    private lateinit var partitionAdapter: MetadataRepositoryDataPartitionAdapter

    fun execute() {
        syncService.executeWithGlobalLock(initLockName) {
            val revisionOpt = catalogAdapter.currentRepositoryRevision()
            when (revisionOpt.isPresent) {
                true -> logger.info("Metadata is current at revision ${revisionOpt.get()}")
                false -> {
                    logger.info("Initializing repository")
                    val revisionNumber = RevisionNumber(0)
                    val rootNode = NodeRepresentation("", "/", revisionNumber)
                    partitionAdapter.writeNodeRepresentation(rootNode)
                    catalogAdapter.setRepositoryRevision(revisionNumber)
                }
            }
        }
    }

}