package org.nuxeo.ecm.conceptshare;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class, ConceptshareCoreFeature.class })
public class TestReviewAdapter {
    @Inject
    CoreSession session;

    @Test
    public void shouldCallTheAdapter() {
        createReview();
    }

    @Test
    public void itCanEndReview() throws Exception {
        ReviewAdapter review = createReview();
        review.endReview();
        Assert.assertEquals(review.getReviewStatus(), "completed");
    }

    protected ReviewAdapter createReview() {
        String doctype = "Collection";
        String testTitle = "My Adapter Title";

        DocumentModel doc = session.createDocumentModel("/", "test-adapter", doctype);
        doc = session.createDocument(doc);
        // session.save() is only needed in the context of unit tests

        ReviewAdapter adapter = doc.getAdapter(ReviewAdapter.class);
        adapter.setTitle(testTitle);
        session.save();

        Assert.assertNotNull("The adapter can't be used on the " + doctype + " document type", adapter);
        Assert.assertEquals("Document title does not match when using the adapter", testTitle, adapter.getTitle());

        return adapter;
    }
}
