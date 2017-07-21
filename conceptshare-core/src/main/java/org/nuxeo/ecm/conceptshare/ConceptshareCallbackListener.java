package org.nuxeo.ecm.conceptshare;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;
import static org.nuxeo.ecm.conceptshare.ConceptshareConstants.*;

public class ConceptshareCallbackListener implements EventListener {

	public static volatile int counter = 0;
	public static volatile int failedCounter = 0;

	public static final String ASSET_CREATED = "ASSET_CREATED";
	public static final String ASSET_ERROR = "ASSET_ERROR";
	public static final String CS_ASSET_ID = "csAssetId";

	private static Log log = LogFactory.getLog(ConceptshareCallbackListener.class);

	@Override
	public void handleEvent(Event event) {
		if (event.getName().equals(ASSET_CREATED) || event.getName().equals(ASSET_ERROR)) {
			EventContext ctx = event.getContext();

			CoreSession session = event.getContext().getCoreSession();
			String assetId = (String) ctx.getProperty(CS_ASSET_ID);

			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() {
					String query = "SELECT * From Document WHERE " + ASSET_ID_PROP + " = '" + assetId + "'";
					DocumentModelList assetList = session.query(query);

					String status = "";
					if (ASSET_CREATED.equals(event.getName())) {
						status = "available";
					} else if (ASSET_ERROR.equals(event.getName())) {
						status = "failed";
					}

					// There should be only one doc.. but in case it has been duplicated
					for (DocumentModel doc : assetList) {
						doc.setPropertyValue(FILE_STATUS_PROP, status);
						session.saveDocument(doc);

						ConceptshareService csService = Framework.getService(ConceptshareService.class);

						String collectionQuery = "SELECT * From Collection WHERE collection:documentIds = '"
								+ doc.getId() + "'";
						DocumentModelList collections = session.query(collectionQuery);

						if (collections.size() == 0) {
							log.warn("No collection have been found for assetId " + assetId
									+ ". Possible cause the asset has been removed from the collection. No notifications sent to conceptshare to update the review.");
						}
						for (DocumentModel collection : collections) {
							long reviewId = (long) collection.getPropertyValue(REVIEW_ID_PROP);

							try {
								csService.addReviewItem((int) reviewId, Integer.parseInt(assetId));
								collection.followTransition("to_Ready");
							} catch (Exception e) {
								log.error(
										"Conceptshare review update failed while processing webservices call for asset "
												+ doc.getPathAsString() + " and collection "
												+ collection.getPathAsString(),
										e);
							}

						}

					}

					if (assetList.size() == 0) {
						log.warn("No asset have been found for asset ID " + assetId
								+ ". Possible cause the asset has been deleted. No notifications sent to conceptshare to update the review.");
					}

				}
			}.runUnrestricted();

		}
	}
}