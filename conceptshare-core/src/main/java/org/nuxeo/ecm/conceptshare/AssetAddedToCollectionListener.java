/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     mhilaire
 *
 */
package org.nuxeo.ecm.conceptshare;

import static org.nuxeo.ecm.conceptshare.ConceptshareConstants.ASSET_ID_PROP;
import static org.nuxeo.ecm.conceptshare.ConceptshareConstants.CS_FILE_SCHEMA_NAME;
import static org.nuxeo.ecm.conceptshare.ConceptshareConstants.FILE_STATUS_PROP;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Asset;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.binary.BinaryBlob;
import org.nuxeo.ecm.core.blob.binary.BinaryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;
import org.nuxeo.runtime.api.Framework;

public class AssetAddedToCollectionListener implements EventListener {

	private static Log log = LogFactory.getLog(AssetAddedToCollectionListener.class);

	@Override
	public void handleEvent(Event event) {

		if (!(event.getContext() instanceof DocumentEventContext)) {
			return;
		}
		DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
		DocumentModel doc = docCtx.getSourceDocument();

		if (doc.hasSchema(CS_FILE_SCHEMA_NAME)) {

			try {
				BlobManager blobManager = Framework.getService(org.nuxeo.ecm.core.blob.BlobManager.class);
				BinaryBlob blob = (BinaryBlob) doc.getPropertyValue("file:content");
				if (blob != null) {
					BinaryManager binaryManager = blobManager.getBlobProvider(blob).getBinaryManager();
					if (binaryManager instanceof S3BinaryManager) {
						URI url = ((S3BinaryManager) binaryManager).getURI(blob, BlobManager.UsageHint.DOWNLOAD, null);
						if (url == null) {
							throw new NuxeoException(
									"Could not process the S3 presigned URI. Please make sure you have enabled the property nuxeo.s3storage.directdownload in nuxeo.conf");
						}
						Asset asset = Framework.getService(ConceptshareService.class).addAsset(doc.getTitle(),
								blob.getFilename(), url.toURL().toString());
						doc.setPropertyValue(FILE_STATUS_PROP, "pending");
						doc.setPropertyValue(ASSET_ID_PROP, asset.getId().toString());
						doc.getCoreSession().saveDocument(doc);
					} else {
						throw new NuxeoException("Asset " + doc.getPathAsString()
								+ " not added to conceptshare: Conceptshare integration currently support only S3BinaryManager."
								+ " You must install the S3 plugin to add asset into conceptshare.");
					}
				}
			} catch (Exception e) {
				log.error("Failed to create asset in conceptshare.", e);
			}

		}
	}
}
