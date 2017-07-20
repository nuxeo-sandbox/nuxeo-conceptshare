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


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

@Path("/conceptshare")
@WebObject(type="conceptshare")
public class CallbackReceiver {

    private static final Log log = LogFactory.getLog(CallbackReceiver.class);
    
    
    @POST
    @Path("callback")
    public Response process(@Context HttpServletRequest request, @FormParam(value = "EVENT_ID") String eventId, @FormParam(value = "ASSET_ID") String assetId) throws IOException {
    		
        ConceptshareResponseAcceptor acceptorService = Framework.getService(ConceptshareResponseAcceptor.class);
        if (acceptorService == null) {
            log.error("Could not find an implementation of ConceptshareResponseAcceptor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if (acceptorService.process(eventId, assetId)) {
            return Response.status(Response.Status.OK).build();
        }
        log.warn("No callback registered for event ID: " + eventId );
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}