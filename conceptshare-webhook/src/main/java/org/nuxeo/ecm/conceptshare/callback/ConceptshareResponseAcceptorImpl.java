package org.nuxeo.ecm.conceptshare.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.EventImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

public class ConceptshareResponseAcceptorImpl extends DefaultComponent implements ConceptshareResponseAcceptor {

	private static Log log = LogFactory.getLog(ConceptshareResponseAcceptorImpl.class);

	@Override
	public boolean process(String eventId, String assetId) {

		if (ASSET_CREATED_EVENT.equals(eventId) || ASSET_ERROR_EVENT.equals(eventId)) {
			return (boolean) processCallback(eventId, assetId);
		}
		return false;
	}

	protected Object processCallback(String eventId, String assetId) {
		if (ASSET_ERROR_EVENT.equals(eventId)) {
			log.warn("An error occurred on asset ID " + assetId + " creation in conceptshare, please review");
		}

		return new ConceptshareUnresctrictedRunner() {
			@Override
			public Object run(CoreSession session, String eventId, String assetId) throws NuxeoException {

				EventContext ctx = new EventContextImpl();
				ctx.setProperty(CS_ASSET_ID, assetId);
				ctx.setCoreSession(session);

				Event event = new EventImpl(eventId, ctx);

				EventService eventService = Framework.getService(EventService.class);
				eventService.fireEvent(event);

				return true;
			}
		}.runUnrestricted(eventId,assetId);

	}

}