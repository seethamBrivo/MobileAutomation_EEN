package tests.ts_video_search;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.VideoSearchPage;

public class VideoSearchTest extends BaseTest {

    private static final String VIDEO_SEARCH_TERM = "Person";

    private VideoSearchPage videoSearchPage;

    @BeforeMethod(alwaysRun = true)
    public void setupVideoSearchTest() {
        videoSearchPage = loginAndCreatePage(TestDataConstants.ENDUSER_CREDS, VideoSearchPage::new);
        videoSearchPage.verifyVideoSearchInBottomNav();
        videoSearchPage.navigateToVideoSearch();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null) {
            return;
        }
        try {
            if (videoSearchPage != null) {
                videoSearchPage.dismissAnyOpenScreens();
            }
        } catch (RuntimeException e) {
            logger.warn("@AfterMethod: cleanup failed: {}", e.toString());
        }
    }

    @Test(description = "EEPD-TC-9119: POS-Video Search - searching by the content/text" +
                         " in Video Search in VMS app",
          groups = {"regression", "video_search"})
    public void verifyVideoSearchByContent() {
        videoSearchPage.openSearchFieldAndVerifyAutosuggestions();
        videoSearchPage.searchForText(VIDEO_SEARCH_TERM);
        videoSearchPage.closeSearchField();
    }

    @Test(description = "EEPD-TC-5227: POS-Video Search - showing the key image of camera" +
                         " with relevant tags in Home Page in Video Search",
          groups = {"regression", "video_search"})
    public void verifyKeyImageWithRelevantTags() {
        videoSearchPage.openCameraPreviewAndVerifyDetails();
        videoSearchPage.verifySwipeNavigation();
        videoSearchPage.dismissDetailScreenWithXButton();
        videoSearchPage.clickCameraPreview();
        videoSearchPage.dismissDetailScreenWithSwipeDown();
    }
}
