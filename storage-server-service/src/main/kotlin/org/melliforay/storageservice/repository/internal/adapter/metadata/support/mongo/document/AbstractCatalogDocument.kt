package org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.document

import org.springframework.data.mongodb.core.mapping.Document

@Document
abstract class AbstractCatalogDocument(var documentType: CatalogDocumentType)