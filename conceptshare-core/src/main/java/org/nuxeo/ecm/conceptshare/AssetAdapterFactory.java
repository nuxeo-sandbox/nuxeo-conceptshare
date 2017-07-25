package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class AssetAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if (doc.hasSchema("CS-FileProperties")){
            return new AssetAdapter(doc);
        }else{
            return null;
        }
    }
}
