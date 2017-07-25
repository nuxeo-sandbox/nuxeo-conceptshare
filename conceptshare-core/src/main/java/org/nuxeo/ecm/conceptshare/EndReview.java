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
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 */
@Operation(id = EndReview.ID, category = Constants.CAT_DOCUMENT, label = "EndReview", description = "Complete a review in conceptshare.")
public class EndReview {

	public static final String ID = "CS.EndReview";

	private static Log log = LogFactory.getLog(EndReview.class);

	@OperationMethod
	public void run(DocumentModel doc) throws Exception {

		ReviewAdapter reviewDoc = doc.getAdapter(ReviewAdapter.class);
		if (reviewDoc != null) {
			reviewDoc.endReview();
		} else {
			String msg = "Can not end review on non review object!";
			log.error(msg);
			throw new NuxeoException(msg);
		}
	}
}
