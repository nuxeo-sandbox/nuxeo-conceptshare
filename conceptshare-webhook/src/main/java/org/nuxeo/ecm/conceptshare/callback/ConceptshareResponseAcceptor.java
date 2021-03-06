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

public interface ConceptshareResponseAcceptor {

	public static final String ASSET_CREATED_EVENT = "ASSET_CREATED";
    
    public static final String ASSET_ERROR_EVENT = "ASSET_ERROR";
    		
    	public static final String CS_ASSET_ID = "csAssetId";
    	

    /**
     * Should resolve callback for the eventID and assetId
	*/
    boolean process(String callbackId, String path);
}
