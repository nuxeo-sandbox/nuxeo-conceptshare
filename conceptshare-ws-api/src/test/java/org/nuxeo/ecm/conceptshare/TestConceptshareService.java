package org.nuxeo.ecm.conceptshare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Asset;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Review;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ReviewItem;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.ReviewMember;
import org.datacontract.schemas._2004._07.conceptshare_v4_framework.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.conceptshare.api.ConceptshareService;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({PlatformFeature.class})
@Deploy("org.nuxeo.ecm.conceptshare.ws.api")
@LocalDeploy("org.nuxeo.ecm.conceptshare.ws.api:conceptshare-service-test-contrib.xml")
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
    assertTrue(conceptshareservice.getProjects().size() > 0);
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
  public void itCanAddMember() throws Exception {
    String reviewTitle = "myReviewUnitTest - " + new Date().getTime();
    Review review = createReview(reviewTitle);

    String memberEmail = "nuxeo.demo.dam+administrator@gmail.com";

    ReviewMember member = conceptshareservice.addReviewMember(memberEmail, review.getId());
    assertEquals(memberEmail, member.getUserName().getValue());
  }

  @Test
  public void itCanAddAssetInReviewAndComplete() throws Exception {
    long timestamp = new Date().getTime();
    String reviewTitle = "myReviewUnitTest - " + timestamp;
    String name = "Unit test - " + timestamp;
    String filename = name + ".png";
    Review review = createReview(reviewTitle);

    Integer inProgressStatus = conceptshareservice.getReviewStatusId(ConceptshareService.REVIEW_IN_PROGRESS_STATUS);
    assertEquals(inProgressStatus, review.getStatusId().getValue());

    Asset asset = conceptshareservice.addAsset(name, filename, "https://www.nuxeo.com/assets/imgs/logo340x60.png");

    assertEquals(filename, asset.getFileName().getValue());

    ReviewItem ri = conceptshareservice.addReviewItem(review.getId(), asset.getId());

    assertEquals(asset.getId(), ri.getAssetId());
    assertEquals(review.getId(), ri.getReviewId());

    review = conceptshareservice.endReview(review.getId(), reviewTitle, "myDescription", "12345");
    assertEquals(review.getId(), review.getId());

    Integer completedStatus = conceptshareservice.getReviewStatusId(ConceptshareService.REVIEW_COMPLETED_STATUS);
    assertEquals(completedStatus, review.getStatusId().getValue());

  }

  @Test
  public void itCanRemoveItemFromReview() throws Exception {
    long timestamp = new Date().getTime();
    String reviewTitle = "myReviewUnitTest - " + timestamp;
    String name = "Unit test - " + timestamp;
    String filename = name + ".png";
    Review review = createReview(reviewTitle);

    Asset asset = conceptshareservice.addAsset(name, filename, "https://www.nuxeo.com/assets/imgs/logo340x60.png");
    ReviewItem ri = conceptshareservice.addReviewItem(review.getId(), asset.getId());
    assertEquals(ri.getReviewId(), review.getId());
    ri = conceptshareservice.removeReviewItem(review.getId(), ri.getAssetId());
  }

  @Test
  public void itCanVersionAsset() throws Exception {

    long timestamp = new Date().getTime();
    String reviewTitle = "myReviewUnitTest - " + timestamp;
    String name = "Unit test - " + timestamp;
    String filename = name + ".png";
    Review review = createReview(reviewTitle);

    Integer inProgressStatus = conceptshareservice.getReviewStatusId(ConceptshareService.REVIEW_IN_PROGRESS_STATUS);
    assertEquals(inProgressStatus, review.getStatusId().getValue());
    String assetV1 = "https://www.nuxeo.com/assets/imgs/logo340x60.png";
    String assetV2 = "https://www.nuxeo.com/assets/imgs/backgrounds/ea-background.png";

    Asset asset = conceptshareservice.addAsset(name, filename, assetV1);

    ReviewItem ri = conceptshareservice.addReviewItem(review.getId(), asset.getId());

    Asset newAsset = conceptshareservice.addVersionedAsset(name, asset.getId(), filename, assetV2);
    System.out.println(newAsset.getId());

    //Restore
    newAsset = conceptshareservice.addVersionedAsset(name, newAsset.getId(), filename, assetV1);
    //conceptshareservice.removeReviewItem(review.getId(), newAsset.getId());
    //ri = conceptshareservice.addReviewItem(review.getId(), asset.getId());
  }

  public Review createReview(String reviewTitle) throws Exception {
    Review review = conceptshareservice.createReview(reviewTitle, "myDescription", "12345");
    assertEquals(reviewTitle, review.getTitle().getValue());
    return review;
  }

  @Test
  public void itCanGetReviewStatusList() {
    List<Status> statusList = conceptshareservice.getReviewStatusList();
    assertTrue("Status list should not be empty", statusList.size() >= 3);
  }

}
