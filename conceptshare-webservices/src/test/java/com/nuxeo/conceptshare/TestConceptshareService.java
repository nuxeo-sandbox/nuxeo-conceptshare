package com.nuxeo.conceptshare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, ConceptShareWSFeature.class })
@Deploy("com.nuxeo.conceptshare.conceptshare-webservices")
public class TestConceptshareService {

    @Inject
    protected ConceptshareService conceptshareservice;

    @Test
    public void testService() {
        assertNotNull(conceptshareservice);
    }
    
    @Test
    public void itCanGetProjects() throws Exception {
    		assertEquals(1, conceptshareservice.getProjects().size());
    }
}
