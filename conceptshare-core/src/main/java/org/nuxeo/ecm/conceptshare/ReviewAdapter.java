package org.nuxeo.ecm.conceptshare;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Review;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
public class ReviewAdapter {
    protected final DocumentModel doc;

    protected String titleXpath = "dc:title";

    protected String descriptionXpath = "dc:description";

    public static final String CS_REVIEW_PROP_PREFIX = "CSReviewProp";

    public static final String REVIEW_ID_PROP = CS_REVIEW_PROP_PREFIX + ":ReviewId";

    public static final String REVIEW_STATUS_PROP = CS_REVIEW_PROP_PREFIX + ":ReviewStatus";

    private static Log log = LogFactory.getLog(ReviewAdapter.class);

    public ReviewAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    // Basic methods
    //
    // Note that we voluntarily expose only a subset of the DocumentModel API in
    // this adapter.
    // You may wish to complete it without exposing everything!
    // For instance to avoid letting people change the document state using your
    // adapter,
    // because this would be handled through workflows / buttons / events in your
    // application.
    //
    public void save() {
        CoreSession session = doc.getCoreSession();
        session.saveDocument(doc);
    }

    public DocumentRef getParentRef() {
        return doc.getParentRef();
    }

    // Technical properties retrieval
    public String getId() {
        return doc.getId();
    }

    public String getName() {
        return doc.getName();
    }

    public String getPath() {
        return doc.getPathAsString();
    }

    public String getState() {
        return doc.getCurrentLifeCycleState();
    }

    // Metadata get / set
    public String getTitle() {
        return doc.getTitle();
    }

    public void setTitle(String value) {
        doc.setPropertyValue(titleXpath, value);
    }

    public void setReviewId(String reviewId) {
        doc.setPropertyValue(REVIEW_ID_PROP, reviewId);
    }

    public String getReviewId() {
        return (String) doc.getPropertyValue(REVIEW_ID_PROP);
    }

    public String getDescription() {
        return (String) doc.getPropertyValue(descriptionXpath);
    }

    public void setDescription(String value) {
        doc.setPropertyValue(descriptionXpath, value);
    }

    public void setReviewStatus(String status) {
        doc.setPropertyValue(REVIEW_STATUS_PROP, status);
    }

    public String getReviewStatus() {
        return (String) doc.getPropertyValue(REVIEW_STATUS_PROP);
    }

    public void addAssetToCollection(AssetAdapter asset) {
        String reviewId = this.getReviewId();

        try {
            getCSService().addReviewItem(Integer.parseInt(reviewId), Integer.parseInt(asset.getAssetId()));
            this.setReviewStatus("ready");
            this.save();
        } catch (Exception e) {
            log.error("Conceptshare review update failed while processing webservices call for asset " + asset.getPath()
                    + " and collection " + doc.getPathAsString(), e);
        }

    }

    public void createReview() {
        if (this.getReviewId() == null) {

            String desc = "";
            if (this.getDescription() != null) {
                desc = (String) this.getDescription();
            }
            try {
                Review review = getCSService().createReview(doc.getTitle(), desc, doc.getId());
                this.setReviewId(review.getId().toString());
                this.save();

                // TODO : Implement member selection feature
                // XXX : For current demo purpose, we are adding manually all members to the
                // review
                getCSService().addReviewMember("nuxeo.demo.dam+administrator@gmail.com",
                        Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("nuxeo.demo.dam+alice@gmail.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("nuxeo.demo.dam+bob@gmail.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("nuxeo.demo.dam+josh@gmail.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("nuxeo.demo.dam+julie@gmail.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("nuxeo.demo.dam+lisa@gmail.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("nuxeo.demo.dam+sunny@gmail.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("ngrant@nuxeo.com", Integer.parseInt(this.getReviewId()));
                getCSService().addReviewMember("rrowles@nuxeo.com", Integer.parseInt(this.getReviewId()));

            } catch (Exception e) {
                log.error("Failed to create review in conceptshare.", e);
            }
        } else {
            log.warn("Can not create a review once it has already been created");
        }

    }

    public void endReview() throws Exception {
        String desc = this.getDescription();
        if (desc == null) {
            desc = "";
        }
        Review review = getCSService().endReview(Integer.parseInt(this.getReviewId()), doc.getTitle(), desc,
                doc.getId());

        int completedStatus = getCSService().getReviewStatusId(ConceptshareService.REVIEW_COMPLETED_STATUS);
        if (review.getStatusId().getValue() == completedStatus) {
            this.setReviewStatus("completed");
            this.save();
        } else {
            log.warn("Review has not been updated, because the received status was not completed but "
                    + review.getStatusName().getValue());
        }
    }

    public void addMember(String email) throws Exception {
        getCSService().addReviewMember(email, Integer.parseInt(this.getReviewId()));
    }

    protected ConceptshareService getCSService() {
        return Framework.getService(ConceptshareService.class);
    }
}
