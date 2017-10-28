package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class ReviewAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if ("ConceptShareReview".equals(doc.getType()) && doc.hasSchema("CS-ReviewProperties")){
            return new ReviewAdapter(doc);
        }else{
            return null;
        }
    }
}
