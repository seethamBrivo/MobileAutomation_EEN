package tests.ts_cameras;

import base.BaseTest;
import constants.DashboardTab;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;

public class CameraContextMenuTest extends BaseTest {

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;
    private boolean tagFilterApplied;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        // TestNG keeps one instance per class, so these still hold the previous test's values.
        tagFilterApplied = false;
        devicesPage = null;
        camerasPage = null;
        devicesPage = loginAndOpenDevicesDashboard(TestDataConstants.TS02_CREDS);
        camerasPage = new CamerasPage(driver);
    }

    @Test(description = "EEPD-TC-12355: POS-Cameras - display context menu after long press on camera in Dashboard in mobile VMS app",
          groups = {"regression", "cameras", "ios"})
    public void verifyCameraContextMenuAfterLongPress() {
        // Step 3: Tap on the Cameras item
        camerasPage.openCamerasList();
        // Step 4: Long press the first offline or error camera and verify its context menu
        camerasPage.verifyFirstOfflineOrErrorCameraContextMenu();
        // Step 4 (online camera): long press the first online camera; its menu preview shows a timestamp
        camerasPage.verifyFirstOnlineCameraContextMenu();
    }

    /**
     * Not automated: "status updates in real time (SSE)", the preview image and the optional icons (step 4), the
     * loading indicator of the next page (step 7) and the logout (step 10, done at suite level). Needs an online
     * camera on the TS02 account, and a camera with a tag for step 9: without one the test skips there, after steps
     * 2 to 8 have run. The tag filter check covers the cards on screen, not the whole list.
     */
    @Test(description = "EEPD-TC-20846: POS-Cameras - Display list of cameras under Cameras item in Dashboard in "
            + "mobile VMS app", groups = {"regression", "cameras", "ios"})
    public void verifyCamerasListContent() {
        // Step 2: Dashboard via the More menu: All devices lists Bridges, Cameras, Speakers and Switches
        devicesPage.verifyDashboardTabsDisplayed(DashboardTab.BRIDGES, DashboardTab.CAMERAS,
                DashboardTab.SPEAKERS, DashboardTab.SWITCHES);
        int camerasOnDashboard = camerasPage.readCamerasTileTotal();
        // Step 3: Tap the Camera option
        camerasPage.openCamerasList();
        // Step 4: the counter matches the Dashboard, every card shows a name and a listed status
        camerasPage.verifyCounterShowsCameraCount(camerasOnDashboard);
        camerasPage.verifyVisibleCameraCardsShowNameAndStatus();
        // Step 5: the filter chips
        camerasPage.verifyFilterChipsInOrder();
        // Step 6: the tags of a card scroll sideways
        boolean tagRowChecked = camerasPage.verifyTagRowScrolls();
        // Step 7: scrolling down loads the next page of cameras
        camerasPage.verifyNextPageLoadsOnScroll();
        // Step 8: the 3-dots button of a camera opens its menu (closed by cleanup)
        camerasPage.openMenuWithThreeDotsOfFirstOnlineCameraAndVerifyOptions();
        camerasPage.dismissContextMenu();
        // Only this step needs a tag, so the skip comes here and steps 2 to 8 have already run.
        if (!camerasPage.hasCameraWithTag()) {
            throw new SkipException("A camera with a tag is needed on the TS02 account; none found in the list");
        }
        // Step 9: tap a tag under a camera: the cameras are filtered by it and the Tags filter is active
        // Set before the tap, so cleanup clears the filter even if the tap or a check after it fails.
        tagFilterApplied = true;
        String tagName = camerasPage.tapFirstTag();
        camerasPage.verifyCamerasFilteredByTag(tagName, camerasOnDashboard);
        camerasPage.verifyTagsFilterIsActive();
        // Last, so a missing precondition of step 6 does not hide the result of steps 7 to 9.
        if (!tagRowChecked) {
            throw new SkipException("Step 6 did not run: it needs a camera on screen whose tags run past the "
                    + "screen edge on the TS02 account; steps 2 to 5 and 7 to 9 passed");
        }
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        try {
            if (driver != null && camerasPage != null) {
                camerasPage.dismissContextMenu();
            }
        } catch (Throwable t) {
            logger.warn("Cleanup: could not dismiss context menu: {}", t.toString());
        } finally {
            clearTagFilter();
            super.tearDown(testResult);
        }
    }

    private void clearTagFilter() {
        try {
            if (driver != null && camerasPage != null && tagFilterApplied) {
                camerasPage.clearFilterIfApplied();
            }
        } catch (Throwable t) {
            logger.warn("Cleanup: could not clear the tag filter: {}", t.toString());
        }
    }
}
