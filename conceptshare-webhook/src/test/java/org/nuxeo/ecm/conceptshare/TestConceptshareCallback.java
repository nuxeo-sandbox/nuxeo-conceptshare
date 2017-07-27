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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.sql.SQLDirectoryFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.runtime.test.runner.TargetExtensions;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RunWith(FeaturesRunner.class)
@Features({ WebEngineFeature.class, PlatformFeature.class, CoreFeature.class, SQLDirectoryFeature.class })
@Jetty(port = 18090)
@Deploy({ "org.nuxeo.ecm.conceptshare.core", "org.nuxeo.ecm.conceptshare.webhook",
		"org.nuxeo.ecm.platform.web.common" , "org.nuxeo.ecm.directory.api", "org.nuxeo.ecm.directory.sql"})
@LocalDeploy({ "org.nuxeo.ecm.conceptshare.webhook:conceptshare-callback-test-contrib.xml"})
@RepositoryConfig(init = CSRepositoryInit.class, cleanup = Granularity.METHOD)
@PartialDeploy(bundle = "studio.extensions.conceptshare-integration", extensions = TargetExtensions.Automation.class)
public class TestConceptshareCallback {

	protected static final String BASE_URL = "http://localhost:18090";
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

	private static final Integer TIMEOUT = 1000 * 60 * 5; // 5min

	protected Client client;

	@Inject
	CoreSession session;

	@Before
	public void setup() {
		client = Client.create();
		client.setConnectTimeout(TIMEOUT);
		client.setReadTimeout(TIMEOUT);
		client.setFollowRedirects(Boolean.FALSE);
	}

	@Test
	public void itCanSendWebhook() {

		String payload = createPayload("ASSET_CREATED", "100");

		WebResource webResource = client.resource(BASE_URL).path("conceptshare").path("callback");
		ClientResponse response = webResource.accept(CONTENT_TYPE).type(CONTENT_TYPE).post(ClientResponse.class,
				payload);

		assertEquals(200, response.getStatus());
	}

	@Test
	public void itReturnNotFoundForUnkownEvent() {

		String payload = createPayload("FAKE", "1");

		WebResource webResource = client.resource(BASE_URL).path("conceptshare").path("callback");
		ClientResponse response = webResource.accept(CONTENT_TYPE).type(CONTENT_TYPE).post(ClientResponse.class,
				payload);

		assertEquals(404, response.getStatus());
	}

	@Test
	public void itCanSetAssetStatus() {

		String payload = createPayload("ASSET_CREATED", "1");

		WebResource webResource = client.resource(BASE_URL).path("conceptshare").path("callback");
		ClientResponse response = webResource.accept(CONTENT_TYPE).type(CONTENT_TYPE).post(ClientResponse.class,
				payload);

		assertEquals(200, response.getStatus());

		assertEquals("available",
				session.getDocument(new PathRef("/default-domain/asset1")).getProperty("CS-FileProperties" , "FileStatus"));
	}

	public static String createPayload(String eventId, String assetId) {
		return "EVENT_GUID=1bf192fe-119c-4d6e-964c-8d917cacd37d&" + "EVENT_ID=" + eventId + "&"
				+ "RESOURCE_URL_METADATA=&" + "ASSET_ID=" + assetId + "&" + "PROJECT_ID=6&" + "DELIVERABLE_ID=&"
				+ "CREATED_BY_ID=6&" + "CREATED_BY_NAME=Maxime%20Hilaire&" + "ASSET_NAME=test.png&"
				+ "PREVIOUS_ASSET_ID=&" + "STATUS_ID=&" + "STATUS_CODE=&" + "STATUS_NAME=&" + "ACCOUNT_ID=3&"
				+ "FOLDER_ID=6&" + "DATE_CREATED=7%2F19%2F2017%205%3A07%3A08%20PM&"
				+ "DATE_MODIFIED=7%2F19%2F2017%205%3A07%3A12%20PM&"
				+ "DATE_REMOVED=&PROXY_EXTENSION=png&ORIGINAL_EXTENSION=png&NUMBER_OF_PAGES=&DURATION=&";
	}

}