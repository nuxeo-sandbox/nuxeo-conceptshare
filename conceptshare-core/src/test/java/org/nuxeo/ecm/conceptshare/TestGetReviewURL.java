package org.nuxeo.ecm.conceptshare;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.conceptshare.adapters.ReviewAdapter;
import org.nuxeo.ecm.conceptshare.automation.GetReviewURL;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;

@RunWith(FeaturesRunner.class)
@Features({ConceptshareCoreFeature.class, AutomationFeature.class })
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestGetReviewURL {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldGetReviewUrl() throws OperationException {

        DocumentModel doc = session.createDocumentModel("/", "test-adapter", "ConceptShareReview");
        doc = session.createDocument(doc);
        ReviewAdapter adapter = doc.getAdapter(ReviewAdapter.class);
        adapter.setTitle("unit test - getreview URL");
        session.save();

        final String email = "mhilaire@nuxeo.com";
        final String reviewId = adapter.getReviewId();
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("reviewId", reviewId);
        String url = (String) automationService.run(ctx, GetReviewURL.ID, params);
        assertFalse(url.isEmpty());
    }
}
