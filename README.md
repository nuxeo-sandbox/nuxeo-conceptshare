## Principles & Concepts

This package allows to edit, annotate and review assets in [conceptshare](https://www.conceptshare.com/) UI
### Create review
`Collection` is actually `review` in conceptshare.
Reviews are created in conceptshare when a collection is created in Nuxeo.

### Adding Asset
When an asset is being added to a collection, it automatically uploads the files into conceptshare. Once conceptshare calls back nuxeo to confirm upload is done, the asset is added to the review

### Accessing to review
When a collection contains at least one asset, its status change to `Ready`. A link `Access review` allows you to directly browse the asset contained in the review to directly edit them and share your ideas on the asset.

### Ending a review
TODO

### Get the PDF review
TODO

## How it works

## Build
1. change the `nuxeo.defaults` and other webservices config in the `ConceptshareWSFeature` class
2. `cd nuxeo-conceptshare && mvn clean install`

Note: If `mvn clean install` fails for webservices `Failed to read schema document 'xjc.xsd', because 'file' access is not allowed due to restriction set by the accessExternalSchema property.` please perform the following action : https://stackoverflow.com/questions/23011547/webservice-client-generation-error-with-jdk8


## Deploy (how to install build product)

Required packages:

- DAM
- JSF-UI


1. `<nuxeoHome>/bin/nuxeoCtl mp-install /path/to/sources/nuxeo-conceptshare/conceptshare-package/target/conceptshare-package-1.0-SNAPSHOT.zip`
2. For callback make your nuxeo instance is available to the internet (setup your internet firewall on your modem if pointing to your local) and add this URL to conceptshare `http://<yourIP>:8080/nuxeo/site/conceptshare/callback`



# Resources (Documentation and other links)

- [Conceptshare website](https://www.conceptshare.com/)
- [Conceptshare integration documentation] (https://integrate.conceptshare.com/main-hub)
- [Conceptshare integration tutorial] (https://integrate.conceptshare.com/tutorial-online-proofing-for-isvs)
- [Roadmap of this integration] (https://ext.prodpad.com/ext/roadmap/bf4315af5d6924f54a593fdc1dc609fefe63a3e2)



# Contributing / Reporting issues

Link to JIRA component (or project if there is no component for that project).
Sample: https://jira.nuxeo.com/browse/NXP/component/14503/
Sample: https://jira.nuxeo.com/secure/CreateIssue!default.jspa?project=NXP

# License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)


# About Nuxeo

The [Nuxeo Platform](http://www.nuxeo.com/products/content-management-platform/) is an open source customizable and extensible content management platform for building business applications. It provides the foundation for developing [document management](http://www.nuxeo.com/solutions/document-management/), [digital asset management](http://www.nuxeo.com/solutions/digital-asset-management/), [case management application](http://www.nuxeo.com/solutions/case-management/) and [knowledge management](http://www.nuxeo.com/solutions/advanced-knowledge-base/). You can easily add features using ready-to-use addons or by extending the platform using its extension point system.

The Nuxeo Platform is developed and supported by Nuxeo, with contributions from the community.

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with
SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.
More information is available at [www.nuxeo.com](http://www.nuxeo.com).
