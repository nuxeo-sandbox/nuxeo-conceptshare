package com.nuxeo.conceptshare;

import java.util.List;

import javax.xml.namespace.QName;

import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Project;

public interface ConceptshareService {

	public static final String WSDL_URL_PROPERTY = "conceptshare.wsdlUrl";

	public static final String PARTNER_KEY_PROPERTY = "conceptshare.partnerKey";

	public static final String PARTNER_PASSWORD_PROPERTY = "conceptshare.partnerPassword";

	public static final String API_USER_PROPERTY = "conceptshare.apiUser";

	public static final String API_USER_PASSWORD_PROPERTY = "conceptshare.partnerPassword";

	public static final String ENDPOINT_URL = "conceptshare.endpointUrl";
	
	public static final String WS_NAMESPACE = "http://www.conceptshare.com/services/v4";

	public static final String LOCAL_SERVICE = "APIService";
	
	public final static String SCHEMA_NS_V4 = "http://schemas.datacontract.org/2004/07/ConceptShare.V4.API";
	
	public static final QName USER_TOKEN = new QName(SCHEMA_NS_V4,"UserToken");
	
	public static final QName PARTNER_TOKEN = new QName(SCHEMA_NS_V4,"PartnerToken");
	public static final QName PARTNER_PWD = new QName(SCHEMA_NS_V4,"PartnerPassword");
	
	/** Add some methods here. 
     * @throws Exception **/
	public void createReview(String name) throws Exception;

	public List<Project> getProjects() throws Exception;
}
