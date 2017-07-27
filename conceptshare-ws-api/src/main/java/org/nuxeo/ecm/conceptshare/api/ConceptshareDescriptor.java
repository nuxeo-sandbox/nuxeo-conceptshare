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
package org.nuxeo.ecm.conceptshare.api;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("config")
public class ConceptshareDescriptor {

    @XNode("wsdlUrl")
    protected String wsdlUrl = "";

    @XNode("partnerKey")
    protected String partnerKey = "";

    @XNode("partnerPassword")
    protected String partnerPassword = "";

    @XNode("apiUser")
    protected String apiUser = "";

    @XNode("apiPassword")
    protected String apiPassword = "";

    @XNode("endpointUrl")
    protected String endpointUrl = "";

    @XNode("defaultProject")
    protected String defaultProject = "";

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public String getPartnerKey() {
        return partnerKey;
    }

    public String getPartnerPassword() {
        return partnerPassword;
    }

    public String getApiUser() {
        return apiUser;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public String getDefaultProject() {
        return defaultProject;
    }

    public void merge(ConceptshareDescriptor other) {
        if (!StringUtils.isEmpty(other.wsdlUrl)) {
            wsdlUrl = other.wsdlUrl;
        }
        if (!StringUtils.isEmpty(other.partnerKey)) {
            partnerKey = other.partnerKey;
        }
        if (!StringUtils.isEmpty(other.partnerPassword)) {
            partnerPassword = other.partnerPassword;
        }
        if (!StringUtils.isEmpty(other.apiUser)) {
            apiUser = other.apiUser;
        }
        if (!StringUtils.isEmpty(other.apiPassword)) {
            apiPassword = other.apiPassword;
        }
        if (!StringUtils.isEmpty(other.endpointUrl)) {
            endpointUrl = other.endpointUrl;
        }
        if (!StringUtils.isEmpty(other.defaultProject)) {
            defaultProject = other.defaultProject;
        }
    }
}
