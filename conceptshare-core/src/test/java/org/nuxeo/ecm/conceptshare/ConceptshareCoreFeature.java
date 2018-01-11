package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.ecm.directory.sql.SQLDirectoryFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;

@Features({ CoreFeature.class, PlatformFeature.class, TransactionalFeature.class, SQLDirectoryFeature.class })
@Deploy({
    "org.nuxeo.ecm.platform.collections.core",
    "org.nuxeo.ecm.conceptshare.ws.api",
    "org.nuxeo.ecm.conceptshare.core"
})
@LocalDeploy({ "org.nuxeo.ecm.conceptshare.ws.api:sample-conceptshare-service-test-contrib.xml" })
public class ConceptshareCoreFeature extends SimpleFeature {
}
