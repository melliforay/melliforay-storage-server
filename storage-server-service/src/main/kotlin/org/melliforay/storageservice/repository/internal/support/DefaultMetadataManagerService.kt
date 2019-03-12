/*
 * Copyright (C) 2019 melliFORAY contributors (https://github.com/orgs/melliforay/teams/melliforay-contributors)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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