package com.nuxeo.conceptshare;

import java.util.List;

import javax.xml.namespace.QName;

import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Asset;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Project;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Review;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ReviewItem;

public interface ConceptshareService {

	public static final String WSDL_URL_PROPERTY = "conceptshare.wsdlUrl";

	public static final String PARTNER_KEY_PROPERTY = "conceptshare.partnerKey";

	public static final String PARTNER_PASSWORD_PROPERTY = "conceptshare.partnerPassword";

	public static final String API_USER_PROPERTY = "conceptshare.apiUser";

	public static final String API_USER_PASSWORD_PROPERTY = "conceptshare.apiPassword";

	public static final String ENDPOINT_URL = "conceptshare.endpointUrl";
	
	public static final String DEFAULT_PROJECT_PROPERTY = "conceptshare.defaultProject";
	
	public static final String WS_NAMESPACE = "http://www.conceptshare.com/services/v4";

	public static final String LOCAL_SERVICE = "APIService";
	
	public final static String API_NS_V4 = "http://schemas.datacontract.org/2004/07/ConceptShare.V4.API";
	
	public static final QName USER_TOKEN = new QName(API_NS_V4,"UserToken");
	
	public static final QName PARTNER_TOKEN = new QName(API_NS_V4,"PartnerToken");
	public static final QName PARTNER_PWD = new QName(API_NS_V4,"PartnerPassword");

	
	public final static String BO_NS_V4 = "http://schemas.datacontract.org/2004/07/ConceptShare.V4.Framework.BusinessObjects";
	
	public static final QName PROJECT_ID = new QName(BO_NS_V4,"ProjectId");
	public static final QName REFERENCE_ID = new QName(BO_NS_V4,"ReferenceId");
	public static final QName REFERENCE_TYPE = new QName(BO_NS_V4,"ReferenceType");
	
    
	
	public List<Project> getProjects() throws Exception;
	
	public Project getProject(String name) throws Exception;

	public Project getDefaultProject() throws Exception;

	public Review createReview(String title, String description, String code) throws Exception;

	public String getReviewURL(Integer reviewId, int userId) throws Exception;

	public int getUserId(String email) throws Exception;

	public Asset addAsset(String assetName, String fileName, String url) throws Exception;

	public ReviewItem addReviewItem(int reviewId, int assetId) throws Exception;
}
