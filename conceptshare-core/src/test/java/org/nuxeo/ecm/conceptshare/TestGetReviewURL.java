package org.nuxeo.ecm.conceptshare;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.conceptshare.core")
public class TestGetReviewURL {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;


    @Ignore("requires ConceptShareWSFeature to run successfully")
    @Test
    public void shouldCallWithParameters() throws OperationException {
        final String email = "mhilaire@nuxeo.com";
        final long reviewId = 1;
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("reviewId", reviewId);
        String url = (String) automationService.run(ctx, GetReviewURL.ID, params);
        assertFalse(url.isEmpty());
    }
}
