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

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class ConceptShareReviewCreatedListener implements EventListener {

  @Override
  public void handleEvent(Event event) {

    if (!(event.getContext() instanceof DocumentEventContext)) {
      return;
    }
    DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
    DocumentModel doc = docCtx.getSourceDocument();
    CoreSession session = doc.getCoreSession();

    // Only applies to ConceptShareReview documents.
    if ("ConceptShareReview".equals(doc.getType())) {

      if ("ConceptShareReview".equals(doc.getType()) && !doc.hasFacet("CS-Review")) {
        doc.addFacet("CS-Review");
        session.saveDocument(doc);
      }

      ReviewAdapter reviewDoc = doc.getAdapter(ReviewAdapter.class);
      if (reviewDoc != null) {
        reviewDoc.createReview();
      }
    }
  }
}
