package org.nuxeo.ecm.conceptshare.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Asset;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.binary.BinaryBlob;
import org.nuxeo.ecm.core.blob.binary.BinaryManager;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;
import org.nuxeo.runtime.api.Framework;

import java.net.URI;
import java.net.URL;

/**
 *
 */
public class AssetAdapter {
    protected final DocumentModel doc;

    protected String titleXpath = "dc:title";

    protected String descriptionXpath = "dc:description";

    public static final String CS_FILE_SCHEMA_NAME = "CS-FileProperties";

    public static final String CS_FILE_PROP_PREFIX = "CSFileProp";

    public static final String ASSET_ID_PROP = CS_FILE_PROP_PREFIX + ":AssetId";

    public static final String FILE_STATUS_PROP = CS_FILE_PROP_PREFIX + ":FileStatus";

    public static final String DOC_ASSET_ID_CONTAINER_PROP = CS_FILE_PROP_PREFIX + ":AssetIDContainer";

    public static final String ASSET_ID_CONTAINER_PROP = "CS-AssetIdContainer:AssetId";

    private static Log log = LogFactory.getLog(AssetAdapter.class);

    public AssetAdapter(DocumentModel doc) {
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

    public String getDescription() {
        return (String) doc.getPropertyValue(descriptionXpath);
    }

    public void setDescription(String value) {
        doc.setPropertyValue(descriptionXpath, value);
    }

    public BinaryBlob getContent() {
        return (BinaryBlob) doc.getPropertyValue("file:content");
    }

    public void setFileStatus(String status) {
        doc.setPropertyValue(FILE_STATUS_PROP, status);
    }

    public void setAssetId(String assetId) {
        doc.setPropertyValue(ASSET_ID_PROP, assetId);
    }

    public String getAssetId() {
        return (String) doc.getPropertyValue(ASSET_ID_PROP);
    }
    
    public String getFileStatus() {
        return (String) doc.getPropertyValue(FILE_STATUS_PROP);
    }

    public void createAsset() {
        if (this.getAssetId() == null) {

            try {
                String url = this.getS3Url().toString();
                if (url != null) {
                    Asset asset = Framework.getService(ConceptshareService.class).addAsset(doc.getTitle(),
                            this.getContent().getFilename(), url);
                    this.setAssetId(asset.getId().toString());
                    this.setFileStatus("pending");

                    CoreSession session = doc.getCoreSession();

                    String path = session.getDocument(doc.getParentRef()).getPathAsString();
                    DocumentModel container = session.createDocumentModel(path, "asset-container-" + this.getId(),
                            "CS-AssetIdContainer");
                    container = session.createDocument(container);
                    container.setPropertyValue(ASSET_ID_CONTAINER_PROP, asset.getId().toString());
                    container = session.saveDocument(container);

                    doc.setPropertyValue(DOC_ASSET_ID_CONTAINER_PROP, container.getId());
                    this.save();
                    doc.getCoreSession().checkIn(new IdRef(getId()), VersioningOption.MAJOR, "");
                }

            } catch (Exception e) {
                log.error("Failed to create asset in conceptshare.", e);
            }
        } else {
            log.warn("Can not create an asset once it has already been created");
        }
    }

    /*
     * Required to store last assetId across versions. CS doesn't handle version restore
     */
    protected String getLastAssetId() {
        String containerId = (String) doc.getPropertyValue(DOC_ASSET_ID_CONTAINER_PROP);
        return (String) doc.getCoreSession().getDocument(new IdRef(containerId)).getPropertyValue(
                ASSET_ID_CONTAINER_PROP);
    }

    protected void setLastAssetId(String newAssetId) {
        String containerId = (String) doc.getPropertyValue(DOC_ASSET_ID_CONTAINER_PROP);
        DocumentModel container = doc.getCoreSession().getDocument(new IdRef(containerId));
        container.setPropertyValue(ASSET_ID_CONTAINER_PROP, newAssetId);
        container.getCoreSession().saveDocument(container);
    }

    public URL getS3Url() throws Exception {
        BlobManager blobManager = Framework.getService(org.nuxeo.ecm.core.blob.BlobManager.class);
        BinaryBlob blob = this.getContent();
        if (blob != null) {
            BinaryManager binaryManager = blobManager.getBlobProvider(blob).getBinaryManager();

            if (binaryManager instanceof S3BinaryManager) {
                URI url = ((S3BinaryManager) binaryManager).getURI(blob, BlobManager.UsageHint.DOWNLOAD, null);
                if (url == null) {
                    throw new NuxeoException(
                            "Could not process the S3 presigned URI. Please make sure you have enabled the property nuxeo.s3storage.directdownload in nuxeo.conf");
                } else {

                    return url.toURL();
                }

            } else {
                throw new NuxeoException("Asset " + doc.getPathAsString()
                        + " not added to conceptshare: Conceptshare integration currently support only S3BinaryManager."
                        + " You must install the S3 plugin to add asset into conceptshare.");
            }
        }
        return null;
    }

    public void addVersion() {
        createVersion(getLastAssetId());
    }

    protected void createVersion(String prevAssetId) {
        try {
            if (prevAssetId != null) {
                Asset newAsset = Framework.getService(ConceptshareService.class).addVersionedAsset(getTitle(),
                        Integer.parseInt(prevAssetId), this.getContent().getFilename(), getS3Url().toString());
                this.setAssetId(newAsset.getId().toString());
                setLastAssetId(newAsset.getId().toString());
                this.save();
            }
        } catch (Exception e) {
            throw new NuxeoException("Could not version asset " + doc.getPathAsString() + " in conceptshare", e);
        }
    }

}
