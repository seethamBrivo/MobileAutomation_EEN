package tests.ts_history;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HistoryBrowserPage;
import pages.LayoutsPage;

public class HistoryBrowserTest extends BaseTest {

    private LayoutsPage layoutsPage;
    private HistoryBrowserPage historyBrowserPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenLayouts() {
        layoutsPage = null;
        historyBrowserPage = null;
        loginAs(TestDataConstants.ENDUSER_CREDS);
        layoutsPage = new LayoutsPage(driver);
        historyBrowserPage = new HistoryBrowserPage(driver);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.verifyCameraPreviewsLoaded();
    }

    @Test(description = "EEPD-TC-2606: POS-History Browser - Pinch on timeline to change timeline level "
            + "in History Browser in mobile VMS app",
          groups = {"regression", "history"})
    public void verifyPinchChangesTimelineLevel() {
        historyBrowserPage.openHistoryBrowserFromFirstCameraPreview();
        historyBrowserPage.verifyDefaultTimelineLevel();
        historyBrowserPage.pinchInAndVerifyMaxTimelineLevel();
        historyBrowserPage.pinchOutAndVerifyMinTimelineLevel();
    }

    @Test(description = "EEPD-TC-2907: POS-History Browser - User is able to use Players/Controllers in mobile VMS app",
          groups = {"regression", "history"})
    public void verifyTimelineControlsMoveRedCursor() {
        historyBrowserPage.openLiveVideoOfFirstCameraPreview();
        historyBrowserPage.openHistoryBrowserFromLiveVideo();
        historyBrowserPage.verifyTimelineControlsDisplayed();
        historyBrowserPage.requireEventsOnTimeline();
        historyBrowserPage.ensurePlaybackPaused();
        historyBrowserPage.clickPreviousFrameAndVerifyCursorMoved();
        historyBrowserPage.clickNextFrameAndVerifyCursorMoved();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.clickNextEventAndVerifyCursorMoved();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        try {
            if (driver != null && historyBrowserPage != null) {
                historyBrowserPage.returnToLiveVideoIfInHistoryBrowser();
            }
        } catch (Throwable t) {
            logger.warn("Cleanup: could not switch the camera back to live video: {}", t.toString());
        } finally {
            super.tearDown(testResult);
        }
    }
}
