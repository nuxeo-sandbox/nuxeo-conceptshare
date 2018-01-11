package org.nuxeo.ecm.conceptshare;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.conceptshare.adapters.AssetAdapter;
import org.nuxeo.ecm.conceptshare.adapters.ReviewAdapter;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({
    "org.nuxeo.ecm.conceptshare.core"
})
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
    
    @Test
    public void itCanVersionAsset() throws Exception {
        AssetAdapter asset = createAsset();
        DocumentRef assetV0 = new IdRef(asset.getDocumentModel().getId());
        DocumentRef assetV1 = session.checkIn(assetV0, VersioningOption.MAJOR, "testversioning");
        session.checkOut(assetV0);
        session.restoreToVersion(assetV0, assetV1);
    }
    
    protected AssetAdapter createAsset() {
        String doctype = "File";
        String testTitle = "My Asset";

        DocumentModel doc = session.createDocumentModel("/", "test-asset", doctype);
        doc.addFacet("CS-File");
        doc = session.createDocument(doc);
        
        AssetAdapter adapter = doc.getAdapter(AssetAdapter.class);
        adapter.setTitle(testTitle);
        session.save();

        Assert.assertNotNull("The adapter can't be used on the " + doctype + " document type", adapter);
        Assert.assertEquals("Document title does not match when using the adapter", testTitle, adapter.getTitle());

        return adapter;
    }
    
    protected ReviewAdapter createReview() {
        String doctype = "ConceptShareReview";
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
