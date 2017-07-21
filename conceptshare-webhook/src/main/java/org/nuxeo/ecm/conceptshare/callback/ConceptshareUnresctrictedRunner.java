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
package org.nuxeo.ecm.conceptshare.callback;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.webengine.jaxrs.context.RequestCleanupHandler;
import org.nuxeo.ecm.webengine.jaxrs.context.RequestContext;
import org.nuxeo.runtime.api.Framework;

public abstract class ConceptshareUnresctrictedRunner {

	protected final Log log = LogFactory.getLog(ConceptshareUnresctrictedRunner.class);

	protected CoreSession session;

	public Object runUnrestricted(String eventId, String assetId) {
		try {

			final LoginContext lc = Framework.login();

			CoreSession coreSession = null;
			RepositoryManager rm;
			try {
				rm = Framework.getLocalService(RepositoryManager.class);
				coreSession = rm.getDefaultRepository().open();

				// Run unrestricted operation
				return run(coreSession, eventId, assetId);

			} finally {
				final CoreSession session2close = coreSession;
				RequestContext.getActiveContext().addRequestCleanupHandler(new RequestCleanupHandler() {

					@Override
					public void cleanup(HttpServletRequest req) {
						try {
							Repository.close(session2close);
							lc.logout();
						} catch (Exception e) {
							log.error("Error during request context cleanup", e);
						}
					}
				});

			}
		} catch (Exception ex) {
			log.error("Unable to render page", ex);
			return null;
		}

	}

	public abstract Object run(CoreSession coreSession, String eventId, String assetId) throws NuxeoException;
}