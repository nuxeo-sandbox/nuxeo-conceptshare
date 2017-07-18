package com.nuxeo.conceptshare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Asset;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Review;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ReviewItem;
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

	public static final String DEFAULT_PROJECT = "TestProject";

	public static final String TEST_USER_EMAIL = "mhilaire@nuxeo.com";

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

	@Test
	public void itCanGetDefaultProject() throws Exception {
		assertEquals(DEFAULT_PROJECT, conceptshareservice.getDefaultProject().getName().getValue());
	}

	@Test
	public void itCanCreateReviewAndGetUrl() throws Exception {
		String reviewTitle = "myReviewUnitTest - " + new Date().getTime();

		Review review = createReview(reviewTitle);

		int userId = conceptshareservice.getUserId(TEST_USER_EMAIL);

		assertFalse(conceptshareservice.getReviewURL(review.getId(), userId).isEmpty());
	}

	@Test
	public void itCanCreateReviewAndAddAsset() throws Exception {
		long timestamp = new Date().getTime();
		String reviewTitle = "myReviewUnitTest - " + timestamp;
		String name = "Unit test - " + timestamp;
		String filename = name + ".png";
		Review review = createReview(reviewTitle);

		Asset asset = conceptshareservice.addAsset(name, filename, "https://www.nuxeo.com/assets/imgs/logo340x60.png");

		assertEquals(filename, asset.getFileName().getValue());
		// Should wait the cb
		//Thread.sleep(10000);
		
		ReviewItem ri = conceptshareservice.addReviewItem(review.getId(), asset.getId());
		assertEquals(asset.getId(), ri.getAssetId());
		assertEquals(review.getId(), ri.getReviewId());


	}

	public Review createReview(String reviewTitle) throws Exception {
		Review review = conceptshareservice.createReview(reviewTitle, "myDescription", "12345");
		assertEquals(reviewTitle, review.getTitle().getValue());
		return review;
	}

}
