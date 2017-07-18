package com.nuxeo.conceptshare;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.junit.Assert;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.SimpleFeature;



@Features({ CoreFeature.class, TransactionalFeature.class})
//@Deploy({ //
//        "conceptshare-core",//
//        "studio.extensions.conceptshare",//
//})
public class ConceptShareWSFeature extends SimpleFeature {
 

	private  File loadResource(String path) throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource(path);
        File file = new File(url.toURI());
		Assert.assertTrue(file.exists());

		return file;
    }
	
    @Override
    public void beforeRun(FeaturesRunner runner) throws Exception {
    		
    		// Create nuxeo config settings to test
        Properties properties = Framework.getProperties();
        properties.put(ConceptshareService.API_USER_PROPERTY, "mhilaire@nuxeo.com");
        properties.put(ConceptshareService.API_USER_PASSWORD_PROPERTY, "****");
        properties.put(ConceptshareService.ENDPOINT_URL, "https://test.dev02.conceptshare.com/API/Service.svc");
        properties.put(ConceptshareService.WSDL_URL_PROPERTY, loadResource("conceptshare-services.wsdl").toURI().toString());
        properties.put(ConceptshareService.DEFAULT_PROJECT_PROPERTY, "TestProject");
        properties.put(ConceptshareService.PARTNER_KEY_PROPERTY, "****");
        properties.put(ConceptshareService.PARTNER_PASSWORD_PROPERTY, "****");
        
    }

}