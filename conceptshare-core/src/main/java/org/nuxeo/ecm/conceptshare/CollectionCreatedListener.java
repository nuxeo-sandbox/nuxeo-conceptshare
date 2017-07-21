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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Review;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;
import static org.nuxeo.ecm.conceptshare.ConceptshareConstants.*;

public class CollectionCreatedListener implements EventListener {

	private static Log log = LogFactory.getLog(CollectionCreatedListener.class);
	
	@Override
    public void handleEvent(Event event) {

        if (!(event.getContext() instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
        DocumentModel doc = docCtx.getSourceDocument();
        
        
        if ("Collection".equals(doc.getType())) {
        		String desc = "";
        		if (doc.getPropertyValue("dc:description") != null)
        		{
        			desc = (String) doc.getPropertyValue("dc:description");  
        		}
        		try {
					Review review = Framework.getService(ConceptshareService.class).createReview(doc.getTitle(), desc , doc.getId());
					doc.setPropertyValue(REVIEW_ID_PROP, review.getId());
					doc.getCoreSession().saveDocument(doc);
				} catch (Exception e) {
					log.error("Failed to create review in conceptshare.", e);
				}
                    
        }
    }
}
