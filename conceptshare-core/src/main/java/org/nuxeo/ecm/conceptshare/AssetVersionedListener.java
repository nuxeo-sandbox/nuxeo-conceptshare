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

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class AssetVersionedListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        if (!(event.getContext() instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
        DocumentModel doc = docCtx.getSourceDocument();
        AssetAdapter assetDoc = doc.getAdapter(AssetAdapter.class);
        if (assetDoc != null) {
            // Skip first checkIn on V1 when asset has not been already added. CS version start at V1
            if ((event.getName().equals("documentRestored") || event.getName().equals("aboutToCheckIn"))
                    && "available".equals(assetDoc.getFileStatus())) {
                assetDoc.addVersion();
            }
        }
    }
}
