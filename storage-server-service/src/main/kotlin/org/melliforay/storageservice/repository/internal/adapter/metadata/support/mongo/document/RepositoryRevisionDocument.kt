package org.melliforay.storageservice.repository.internal.adapter.metadata.support.mongo.document

class RepositoryRevisionDocument(var revision: String): AbstractCatalogDocument(CatalogDocumentType.REVISION_DOCUMENT)