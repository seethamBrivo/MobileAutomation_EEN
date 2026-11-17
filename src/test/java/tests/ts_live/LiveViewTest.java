package tests.ts_live;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LiveViewPage;
import utils.DeviceUtils;
import utils.JsonDataReader;

public class LiveViewTest extends BaseTest {

    private LiveViewPage liveViewPage;
    private String cameraName;

    @BeforeMethod(alwaysRun = true)
    public void setupMechanicalZoomTest() {
        liveViewPage = loginAndCreatePage(TestDataConstants.TS02_CREDS, LiveViewPage::new);
        cameraName = JsonDataReader.getMechanicalZoomCameraValue(TestDataConstants.MECHANICAL_ZOOM_CAMERA_NAME);
        if (cameraName == null || cameraName.isEmpty()) {
            throw new IllegalStateException("Camera name cannot be null or empty for mechanical zoom test");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupMechanicalZoomTest() {
        try {
            DeviceUtils.rotateToPortrait(driver);
        } catch (Exception e) {
            logger.warn("Failed to reset device orientation to portrait", e);
        }
    }

    // Skipped: EPD-14419 - Mechanical Zoom tests failing due to camera preview is not loading
    // https://brivosys.atlassian.net/browse/EPD-14419
    @Test(description = "EEPD-TC-36684: POS-Live Stream - Mechanical Zoom of Auto focus functionality of live stream",
          groups = {"regression", "live", "liveview", "mechanical-zoom", "ios"},
          enabled = false)
    public void verifyMechanicalZoomAutoFocusFunctionality() {
        liveViewPage.navigateToLiveView(cameraName);
        liveViewPage.verifyMechanicalZoomButtonInBothOrientationsAndClick();
        liveViewPage.verifyMechanicalZoomMenuClickAutoFocusAndClose();
        liveViewPage.verifyMechanicalZoomHideUnhideBehavior();
    }

    // Skipped: EPD-14419 - Mechanical Zoom tests failing due to camera preview is not loading
    // https://brivosys.atlassian.net/browse/EPD-14419
    @Test(description = "EEPD-TC-36681: POS-Live Stream - Mechanical Zoom of Zoom In/out functionality of live stream",
          groups = {"regression", "live", "liveview", "mechanical-zoom", "ios"},
          enabled = false)
    public void verifyMechanicalZoomInOutFunctionality() {
        liveViewPage.navigateToLiveView(cameraName);
        liveViewPage.verifyMechanicalZoomButtonInBothOrientationsAndClick();
        liveViewPage.verifyMechanicalZoomMenuClickZoomInOutAndClose();
        liveViewPage.verifyMechanicalZoomHideUnhideBehavior();
    }

}
