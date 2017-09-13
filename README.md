## Principles & Concepts

This package allows to edit, annotate and review assets in [conceptshare](https://www.conceptshare.com/) UI

### Create review
`Collection` is actually `review` in conceptshare.
Reviews are created in conceptshare when a collection is created in Nuxeo.

### Adding Asset
When an asset is being added to a collection, it automatically uploads the files into conceptshare. Once conceptshare calls back nuxeo to confirm upload is done, the asset is added to the review. Asset are uploaded in conceptshare only one time and can be reused for other reviews. If the document added to the review in nuxeo doesn't have any binary, (like for folder or a file placeholder) asset creation won't be pushed to conceptshare.

### Accessing to review
When a collection contains at least one asset, its status change to `Ready`. A button `Access review` allows you to directly browse the asset contained in the review to directly edit them and share your ideas on the asset.

### Ending a review
Once you've done your review you can close it and complete it from nuxeo by clicking on the end review button. Its status will change to `completed`

### Asset versioning

Once you add an asset to a collection in Nuxeo, it automatically create and tag version `1.0` of the asset.

Conceptshare allows only up versioning and not revert whereas Nuxeo support it. Which means everytime you revert to a given version in Nuxeo you actually create a new version in Conceptshare. Therefore it is normal that version label doesn't match in Nuxeo and Conceptshare. 

From a technical point of view, since new version in Conceptshare triggers a new `assetId` as well, this is handled by creating a unique immutable `CS-AssetIdContainer` document for each asset allowing to update the latest `AssetId` of the given asset.


## How it works

## Pre requisites
Conceptshare is a cloud or on premise web appliaction. In order to integrate nuxeo you need the following:

- a conceptshare account with an api user, api password, partnerKey and partnerPassword

- a running conceptshare instance

- a default conceptshare project where your api user has access too

- each conceptshare user account must have a nuxeo user with the same email address 

- an AWS S3 bucket. See more [here](https://doc.nuxeo.com/nxdoc/amazon-s3-online-storage/)

- Currently users sync is not implemented. Make sure you have the following user account setup in your Nuxeo instance:

Username | FirstName | LastName | Email
--- | --- | --- | ---
Administrator | Admin | Istrator | **nuxeo.demo.dam+administrator@gmail.com**
Bob | Bob | Summit | **nuxeo.demo.dam+bob@gmail.com**
Alice | Alice | Spring | **nuxeo.demo.dam+alice@gmail.com**
Lisa | Lisa | Fall | **nuxeo.demo.dam+lisa@gmail.com**
Josh | Josh | Strong | **nuxeo.demo.dam+josh@gmail.com**
Julie | Julie | Winter | **nuxeo.demo.dam+julie@gmail.com**
Uri | Uri | Sunny | **nuxeo.demo.dam+sunny@gmail.com**


## Build
1. Update conceptshare webservices config in the [Core test contrib](https://github.com/nuxeo-sandbox/nuxeo-conceptshare/blob/master/conceptshare-core/src/test/resources/conceptshare-service-test-contrib.xml) and [WS test contrib](https://github.com/nuxeo-sandbox/nuxeo-conceptshare/blob/master/conceptshare-ws-api/src/test/resources/conceptshare-service-test-contrib.xml) to be able to run the unit test, otherwise add `-DskipTests` in the maven command if you prefer to skip this part
2. `cd nuxeo-conceptshare && mvn clean install`

Note: If `mvn clean install` fails for webservices `Failed to read schema document 'xjc.xsd', because 'file' access is not allowed due to restriction set by the accessExternalSchema property.` please perform the following action : https://stackoverflow.com/questions/23011547/webservice-client-generation-error-with-jdk8


## Deploy (how to install build product)

Required packages.:

- `nuxeo-dam`
- `amazon-s3-online-storage`
- `nuxeo-jsf-ui`
- `nuxeo-web-ui`


Those 3 pakcages will be automatically installed if not already present when you install the first time the nuxeo-conceptshare module.

1. `<nuxeoHome>/bin/nuxeoCtl mp-install /path/to/sources/nuxeo-conceptshare/conceptshare-package/target/conceptshare-package-<VERSION>.zip`
2. For callback make sure your nuxeo instance is available to the internet (setup your internet firewall on your modem if pointing to your local) and add this URL to conceptshare `http://<yourIP>:<port>/nuxeo/site/conceptshare/callback` in your account callbacks, and select event `ASSET_CREATED` and `ASSET_ERROR`
3. Check that the firewall allows only incoming connection from conceptshare IP (should be `40.114.6.151`) on the callback URL `/nuxeo/site/conceptshare/callback`
4. Edit `nuxeo.conf` and set the following properties:

`nuxeo.s3storage.region=<YourS3Region>`

`nuxeo.s3storage.bucket=<YourBucketName>`

`nuxeo.s3storage.awsid=<YourAWSID>`

`nuxeo.s3storage.awssecret=<YourAWSsecret>`

`conceptshare.partnerKey=<YourPartnerKey>`

`conceptshare.partnerPassword=<YourPartnerPassword>`

`conceptshare.apiUser=<YourAPIUserEmail>`

`conceptshare.apiPassword=<YourAPIUSerPassword>`

`conceptshare.endpointUrl=https://<conceptshareHost>/API/Service.svc/secure`

`conceptshare.defaultProject=<YourDefaultProjectName>`

5. Grant access nuxeo to S3 bucket by adding this [AWS policy](https://doc.nuxeo.com/nxdoc/amazon-s3-online-storage/#aws-configuration) 

# Studio customization

The conceptshare package embed a default UI config (JSF and WebUI) along with a set of predefined doc types, vocabularies, facets and schemas, all defined in the [Core module resources](https://github.com/nuxeo-sandbox/nuxeo-conceptshare/tree/master/conceptshare-core/src/main/resources). You may want to change layout or even extend the default schema, thus simply add the following snippet into your Nuxeo Studio's registries :

**In `Document Types` registry:**

```
{
  "doctypes": {
  
  ... Other doc types you may have added before ...
  
    "CS-AssetIdContainer": {
		"parent": "Document",
		"facets": [
			"HiddenInNavigation"
		],
		"schemas": [
			"uid",
			"CS-AssetIdContainer"
		]
	}
  }
}
```
**In `Document Schemas` registry:**

```
{
  "schemas": {
  
  ... Other schemas you may have added before ...
    
    
    "CS-FileProperties": {
		"@prefix": "CSFileProp",
		"AssetId": "string",
		"FileName": "string",
	    "FileStatus": "string",
	    "AssetIDContainer": "string"
	},
	"CS-ReviewProperties": {
		"@prefix": "CSReviewProp",
		"TypeOfReview": "string",
		"ReviewCreator": "string",
		"ReviewName": "string",
		"ReviewEndDate": "date",
		"ReviewId": "string",
		"ReviewStatus": "string"
	},
	"CS-AssetIdContainer": {
		"@prefix": "CSAssetIdContainer",
		"AssetId": "string"
	}
  }
}

```

**In `Document Facets` registry:**

```
{
   "facets":[
   
  ... Other facets you may have added before ...
     
     
      {
         "id":"CS-File",
         "description":"ConceptShare Asset",
          "schemas":["CS-FileProperties"]
      }
      ,{
         "id":"CS-Review",
         "description":"ConceptShare Review",
          "schemas":["CS-ReviewProperties"]
      }
   ]
}
```

**In `Automation Operations` registry:**


```
{
  "operations": [

  ... Other operations you may have added before ...
  
  {
    "id": "CS.GetReviewURL",
    "label": "Get review URL",
    "category": "Files",
    "description": "Return the direct url to review in conceptshare.",
    "url": "CS.GetReviewURL",
    "signature": [
      "void",
      "string"
    ],
    "params": [
      {
        "name": "email",
        "type": "string",
        "required": true,
        "order": 0,
        "values": []
      },
      {
        "name": "reviewId",
        "type": "long",
        "required": true,
        "order": 1,
        "values": []
      }
    ]
  },
  {
    "id": "CS.EndReview",
    "label": "End review",
    "category": "Files",
    "description": "Complete a review in conceptshare.",
    "url": "CS.EndReview",
    "signature": [
      "document",
      "void"
    ],
    "params": [
    ]
  }                
]}
```






# Resources (Documentation and other links)

- [Conceptshare website](https://www.conceptshare.com/)
- [Conceptshare integration documentation](https://integrate.conceptshare.com/main-hub)
- [Conceptshare integration tutorial](https://integrate.conceptshare.com/tutorial-online-proofing-for-isvs)
- [Roadmap of this integration](https://ext.prodpad.com/ext/roadmap/bf4315af5d6924f54a593fdc1dc609fefe63a3e2)



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
