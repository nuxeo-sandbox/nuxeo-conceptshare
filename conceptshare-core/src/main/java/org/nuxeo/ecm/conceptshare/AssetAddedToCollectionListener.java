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

import org.nuxeo.ecm.collections.api.CollectionConstants;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class AssetAddedToCollectionListener implements EventListener {

	@Override
	public void handleEvent(Event event) {

		if (!(event.getContext() instanceof DocumentEventContext)) {
			return;
		}
		DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
		DocumentModel doc = docCtx.getSourceDocument();

		AssetAdapter assetDoc = doc.getAdapter(AssetAdapter.class);
		CoreSession session = doc.getCoreSession();
		if (assetDoc != null) {
			if (assetDoc.getAssetId() == null) {
				// If asset has not yet been uploaded already then upload it
				assetDoc.createAsset();
			} else {
				// Simply add directly the asset into conceptshare
				DocumentRef collectionRef = (DocumentRef) docCtx.getProperties()
						.get(CollectionConstants.COLLECTION_REF_EVENT_CTX_PROP);
				ReviewAdapter review = session.getDocument(collectionRef).getAdapter(ReviewAdapter.class);
				review.addAssetToCollection(assetDoc);
			}

		}
	}
}
