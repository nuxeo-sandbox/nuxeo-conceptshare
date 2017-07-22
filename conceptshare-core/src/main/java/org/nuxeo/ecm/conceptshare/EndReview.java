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
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id = EndReview.ID, category = Constants.CAT_DOCUMENT, label = "EndReview", description = "Complete a review in conceptshare.")
public class EndReview {

	public static final String ID = "CS.EndReview";

	public static final int CS_COMPLETED_STATUS_ID = 77;

	private static Log log = LogFactory.getLog(EndReview.class);
	
	@OperationMethod
	public void run(DocumentModel doc) throws Exception {

		ConceptshareService cs = Framework.getService(ConceptshareService.class);
		String desc = (String) doc.getPropertyValue("dc:description");
		if(desc == null) {
			desc = "";
		}
		Review review = cs.endReview(Integer.parseInt((String)doc.getPropertyValue(ConceptshareConstants.REVIEW_ID_PROP)), doc.getTitle(), desc , doc.getId());
		
		if(review.getStatusId().getValue() == CS_COMPLETED_STATUS_ID)
		{
			doc.setPropertyValue(ConceptshareConstants.REVIEW_STATUS_PROP, "completed");
			doc.getCoreSession().saveDocument(doc);
		}else {
			log.warn("Review has not been updated, because the received status was not completed but " + review.getStatusName().getValue());
		}
		

	}
}
