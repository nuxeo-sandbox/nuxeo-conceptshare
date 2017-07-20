package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

public class ConceptshareCallbackListener implements EventListener {

	public static volatile int counter = 0;
	public static volatile int failedCounter = 0;

	public static final String ASSET_CREATED = "ASSET_CREATED";
	public static final String ASSET_ERROR = "ASSET_ERROR";
	public static final String CS_ASSET_ID = "csAssetId";

	@Override
	public void handleEvent(Event event) {
		if (event.getName().equals(ASSET_CREATED) || event.getName().equals(ASSET_ERROR)) {
			EventContext ctx = event.getContext();

			CoreSession session = event.getContext().getCoreSession();
			String assetId = (String) ctx.getProperty(CS_ASSET_ID);

			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() {
					DocumentModelList docList = session.query("SELECT * From Document WHERE dc:title = '" + assetId+"'");

					String status = "";
					if (ASSET_CREATED.equals(event.getName())) {
						status = "uploaded";
					} else if (ASSET_ERROR.equals(event.getName())) {
						status = "error";
					}

					// There should be only one doc.. but in case it has been duplicated
					for (DocumentModel doc : docList) {
						doc.setPropertyValue("dc:description", status);
						session.saveDocument(doc);
					}

				}
			}.runUnrestricted();

		}
	}
}