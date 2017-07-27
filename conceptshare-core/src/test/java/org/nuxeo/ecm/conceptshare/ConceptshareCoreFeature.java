package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.ecm.directory.sql.SQLDirectoryFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;
import org.nuxeo.runtime.test.runner.TargetExtensions;

@Features({ CoreFeature.class, PlatformFeature.class, TransactionalFeature.class, SQLDirectoryFeature.class })
@Deploy({ "org.nuxeo.ecm.conceptshare.core", "org.nuxeo.ecm.platform.collections.core",
        "org.nuxeo.ecm.conceptshare.ws.api", "org.nuxeo.ecm.directory.api", "org.nuxeo.ecm.directory.sql" })
@LocalDeploy({ "org.nuxeo.ecm.conceptshare.ws.api:conceptshare-service-test-contrib.xml" })
@PartialDeploy(bundle = "studio.extensions.conceptshare-integration", extensions = TargetExtensions.Automation.class)
public class ConceptshareCoreFeature extends SimpleFeature {

}
