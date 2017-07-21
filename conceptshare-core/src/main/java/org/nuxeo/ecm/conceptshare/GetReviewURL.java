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

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id=GetReviewURL.ID, category=Constants.CAT_DOCUMENT, label="GetReviewURL", description="Return the direct url to review in conceptshare.")
public class GetReviewURL {

    public static final String ID = "CS.GetReviewURL";

    @Param(name = "email", required = true)
    protected String email;
    
    @Param(name = "reviewId", required = true)
    protected long reviewId;

    @OperationMethod
    public String run() throws NuxeoException {
    		
    		ConceptshareService cs = Framework.getService(ConceptshareService.class);
        try {
			int userId = cs.getUserId(email);
			return cs.getReviewURL(userId, (int) reviewId);
		} catch (Exception e) {
			throw new NuxeoException("Failed to get review URL from conceptshare.",e);
		}
    }
}
