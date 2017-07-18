package org.nuxeo.ecm.conceptshare;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datacontract.schemas._2004._07.conceptshare_v4.APIContext;
import org.datacontract.schemas._2004._07.conceptshare_v4.ReferenceType;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ArrayOfAssetUploadParameter;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Asset;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Project;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ResourceUrlOptions;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ResourceUrlType;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Review;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ReviewItem;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ReviewType;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.User;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import com.conceptshare.services.v4.APIService;
import com.conceptshare.services.v4.IAPIService;
import com.conceptshare.services.v4.IAPIServiceAuthorizeAPIFaultExceptionFaultFaultMessage;
import com.conceptshare.services.v4.IAPIServiceGetProjectListAPIFaultExceptionFaultFaultMessage;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;

public class ConceptshareServiceImpl extends DefaultComponent implements ConceptshareService {

	protected static APIService csService = null;

	protected static APIContext apiContext = null;

	public static final Log log = LogFactory.getLog(ConceptshareServiceImpl.class);

	/**
	 * Component activated notification. Called when the component is activated. All
	 * component dependencies are resolved at that moment. Use this method to
	 * initialize the component.
	 *
	 * @param context
	 *            the component context.
	 */
	@Override
	public void activate(ComponentContext context) {
		super.activate(context);
	}

	/**
	 * Component deactivated notification. Called before a component is
	 * unregistered. Use this method to do cleanup if any and free any resources
	 * held by the component.
	 *
	 * @param context
	 *            the component context.
	 */
	@Override
	public void deactivate(ComponentContext context) {
		super.deactivate(context);
	}

	/**
	 * Application started notification. Called after the application started. You
	 * can do here any initialization that requires a working application (all
	 * resolved bundles and components are active at that moment)
	 *
	 * @param context
	 *            the component context. Use it to get the current bundle context
	 * @throws Exception
	 */
	@Override
	public void applicationStarted(ComponentContext context) {

	}

	protected IAPIService getOrCreateApiService() {
		if (csService == null) {
			String wsdlUrl = (Framework.getProperty(WSDL_URL_PROPERTY)) != null
					? Framework.getProperty(WSDL_URL_PROPERTY)
					: "file:${nuxeo.home}/nxserver/config/conceptshare-services.wsdl";
			String endpointUrl = Framework.getProperty(ENDPOINT_URL);

			if (endpointUrl == null) {
				log.warn(
						"Conceptshare mandatory settings endpoint URL is missing, conceptshare integration won't work.");
			} else {
				try {
					csService = new APIService(new URL(wsdlUrl), new QName(WS_NAMESPACE, LOCAL_SERVICE));
					((BindingProvider) csService.getBasicHttpBindingIAPIService()).getRequestContext()
							.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

				} catch (MalformedURLException e) {
					log.error("WSDL malformed URL : " + wsdlUrl + ". Conceptshare integration won't work", e);

				}
			}
		}

		return csService.getBasicHttpBindingIAPIService();
	}

	protected APIContext getOrCreateApiContext() {
		String apiUser = Framework.getProperty(API_USER_PROPERTY);
		String apiUserPwd = Framework.getProperty(API_USER_PASSWORD_PROPERTY);
		String partnerKey = Framework.getProperty(PARTNER_KEY_PROPERTY);
		String partnerPassword = Framework.getProperty(PARTNER_PASSWORD_PROPERTY);
		if (apiContext == null) {
			try {
				if (apiUser == null || apiUserPwd == null) {
					log.warn(
							"Conceptshare mandatory settings api credentials are missing, conceptshare integration won't work.");
				} else {
					User apiUsr = getOrCreateApiService().authorize(apiUser, apiUserPwd);

					apiContext = new APIContext();
					apiContext.setUserToken(
							(new JAXBElement<String>(USER_TOKEN, String.class, apiUsr.getApiToken().getValue())));
					apiContext.setPartnerToken(new JAXBElement<String>(PARTNER_TOKEN, String.class, partnerKey));
					apiContext.setPartnerPassword(new JAXBElement<String>(PARTNER_PWD, String.class, partnerPassword));
				}
			} catch (IAPIServiceAuthorizeAPIFaultExceptionFaultFaultMessage e) {
				log.error("Conceptshare credentials are wrong or not allowed!");
				throw new NuxeoException(e);
			}

		}
		return apiContext;
	}

	@Override
	public List<Project> getProjects() throws IAPIServiceGetProjectListAPIFaultExceptionFaultFaultMessage {
		apiContext = getOrCreateApiContext();
		return getOrCreateApiService().getProjectList(apiContext).getProject();

	}

	@Override
	public Project getProject(String name) throws IAPIServiceGetProjectListAPIFaultExceptionFaultFaultMessage {
		List<Project> projects = getProjects();
		for (Project project : projects) {
			if (project.getName().getValue().equals(name))
				return project;
		}
		return null;

	}

	@Override
	public Project getDefaultProject() throws IAPIServiceGetProjectListAPIFaultExceptionFaultFaultMessage {
		return getProject(Framework.getProperty(DEFAULT_PROJECT_PROPERTY));
	}

	@Override
	public Review createReview(String title, String description, String code) throws Exception {
		Integer projectId = getDefaultProject().getId();

		return getOrCreateApiService().addUpdateReviewFull(getOrCreateApiContext(), null, projectId, projectId,
				ReferenceType.PROJECT, ReviewType.FEEDBACK, null, null, null, null, null, null, null, title,
				description, code, new ArrayOfstring(), null, null, null, null, null, null, null);
	}

	@Override
	public int getUserId(String email) throws Exception {
		return getOrCreateApiService().getUserId(getOrCreateApiContext(), email);
	}

	@Override
	public Asset addAsset(String assetName, String fileName, String url) throws Exception {
		int projectId = getDefaultProject().getId();
		
		return getOrCreateApiService().addProjectAssetFromExternalUri( getOrCreateApiContext(), assetName, fileName,
				null, projectId, URLEncoder.encode(url, "UTF-8"), true, new ArrayOfAssetUploadParameter());
	}
	
	@Override
	public ReviewItem addReviewItem(int reviewId, int assetId) throws Exception {
		return getOrCreateApiService().addReviewItem(getOrCreateApiContext(), reviewId, assetId);
	}

	@Override
	public String getReviewURL(Integer reviewId, int userId) throws Exception {
		ResourceUrlOptions options = new ResourceUrlOptions();
		int projectId = getDefaultProject().getId();
		options.setProjectId(new JAXBElement<Integer>(PROJECT_ID, Integer.class, projectId));
		options.setReferenceId(new JAXBElement<Integer>(REFERENCE_ID, Integer.class, reviewId));
		options.setPreAuthenticateUser(true);
		options.setSecureUrl(true);
		options.setSingleUseOnly(false);
		options.setUrlType(ResourceUrlType.REFERENCE);

		// Add one to the ref type, ordinal start at 0 whereas the .Net start at 1
		int refType = ReferenceType.REVIEW.ordinal() + 1;

		options.setReferenceType(new JAXBElement<Integer>(REFERENCE_TYPE, Integer.class, refType));
		return getOrCreateApiService().getResourceUrlForUser(getOrCreateApiContext(), userId, options);
	}
}
