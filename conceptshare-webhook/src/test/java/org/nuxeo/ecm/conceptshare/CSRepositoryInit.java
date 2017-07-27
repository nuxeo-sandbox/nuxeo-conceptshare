package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;

/**
 * Default repository initializer that create the default DM doc hierarchy.
 */
public class CSRepositoryInit implements RepositoryInit {

	public static final String DEFAULT_DOMAIN = "/default-domain";

	@Override
	public void populate(CoreSession session) {

		DocumentModel asset1 = session.createDocumentModel(DEFAULT_DOMAIN, "asset1", "File");
		asset1.setProperty("dublincore", "title", "asset1");
		asset1.addFacet("CS-File");
		asset1.setPropertyValue("CSFileProp:AssetId", "1");
		asset1 = session.createDocument(asset1);

		asset1 = session.saveDocument(asset1);

		session.save();

	}

}