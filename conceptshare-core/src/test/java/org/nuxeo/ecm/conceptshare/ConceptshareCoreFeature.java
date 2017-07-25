package org.nuxeo.ecm.conceptshare;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;
import org.nuxeo.runtime.test.runner.TargetExtensions;



@Features({ CoreFeature.class, TransactionalFeature.class})
@Deploy({"org.nuxeo.ecm.conceptshare.core" , "org.nuxeo.ecm.platform.collections.core"})
@PartialDeploy(bundle = "studio.extensions.conceptshare-integration", extensions = TargetExtensions.Automation.class)
public class ConceptshareCoreFeature extends SimpleFeature {
 


}