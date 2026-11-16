package tests.ts_layouts;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import pages.HistoryBrowserPage;
import pages.LayoutsPage;

public class LayoutsTest extends BaseTest {

    /** The layout the add-layout test creates (also used by the local snackbar probe). */
    public static final String LAYOUT_NAME = "AutoTest_Layout";
    public static final String ASPECT_RATIO = "16:9";
    public static final int MAX_CAMERAS_PER_ROW = 2;
    public static final boolean SHOW_CAMERA_TITLE_BARS = true;

    private LayoutsPage layoutsPage;
    private boolean layoutCreated = false;
    private String uniqueLayoutName;
    private HistoryBrowserPage historyBrowserPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndVerifyLayoutsScreen() {
        layoutCreated = false;
        historyBrowserPage = null;
        layoutsPage = loginAndCreatePage(TestDataConstants.ENDUSER_CREDS, LayoutsPage::new);
        historyBrowserPage = new HistoryBrowserPage(driver);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.clickFirstLayoutTab();
    }

    // TODO: Parameterize with @DataProvider to cover multiple aspect ratios (16:9, 4:3, 1:1)
    // and cameras-per-row values. Requires teardown to track List<String> createdLayoutNames.
    @Test(description = "EEPD-TC-9005: POS-Layouts - adding New Layout in mobile VMS app",
          groups = {"regression", "layouts", "ios"})
    public void verifyAddNewLayoutFunctionality() {
        uniqueLayoutName = utils.TestDataGenerator.generateUniqueName(LAYOUT_NAME);

        // Steps 2 and 3: Open the 3-dots menu and choose New Layout
        layoutsPage.openNewLayoutForm();
        // Steps 4 to 7: Name, aspect ratio, max cameras per row and camera title bars
        layoutsPage.setLayoutDetails(uniqueLayoutName, ASPECT_RATIO, MAX_CAMERAS_PER_ROW, SHOW_CAMERA_TITLE_BARS);
        // Steps 8 and 9: Add a camera and save
        layoutsPage.addFirstCameraAndSave();
        // Step 10: Add the layout
        layoutsPage.clickAddButton();
        layoutCreated = true;
        layoutsPage.verifyLayoutCreatedSuccessfully(uniqueLayoutName);
    }

    @Test(description = "EEPD-TC-9163: POS-Layouts/Tags - show camera previews in Layouts/Tags sections in mobile app",
          groups = {"regression", "layouts", "ios"})
    public void verifyCameraPreviewsDisplayedInLayouts() {
        // Steps 2 and 3: Stay in the Layouts section and wait for the camera previews
        layoutsPage.verifyCameraPreviewsLoaded();
        // Same check on the All cameras tab
        layoutsPage.clickAllCamerasTab();
        layoutsPage.verifyCameraPreviewsLoaded();
    }

    @Test(description = "EEPD-TC-9012: POS-Layouts - displaying Context Menu (long press) in Layouts/Tags in mobile "
            + "VMS app",
          groups = {"regression", "layouts", "ios"})
    public void verifyContextMenuAfterLongPressInLayouts() {
        // Step 2: Stay in the Layouts section and wait for the camera previews
        layoutsPage.clickAllCamerasTab();
        layoutsPage.verifyCameraPreviewsLoaded();
        // Step 3: Long press on a camera preview: the menu, its options and the preview of the pressed camera
        String pressedCameraName = layoutsPage.readFirstCameraPreviewName();
        historyBrowserPage.openContextMenuOfFirstCameraPreview();
        layoutsPage.verifyContextMenuPreviewShowsCamera(pressedCameraName);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        // Its own step: a failure here (or no page after a failed sign-in) must not skip the layout deletion below.
        try {
            if (layoutsPage != null && driver != null) {
                layoutsPage.closeContextMenuIfOpenOnIos();
            }
        } catch (Throwable t) {
            logger.warn("Teardown: could not close the context menu: {}", t.toString());
        }
        try {
            if (layoutCreated && uniqueLayoutName != null && driver != null) {
                layoutsPage.deleteLayout(uniqueLayoutName);
                logger.info("Successfully deleted layout '{}' in teardown", uniqueLayoutName);
            } else {
                logger.info("No layout created in this test, skipping cleanup");
            }
        } catch (org.openqa.selenium.NoSuchSessionException |
                 org.openqa.selenium.SessionNotCreatedException e) {
            logger.warn("Driver session closed, skipping cleanup for layout '{}'", uniqueLayoutName);
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.warn("Teardown failed to delete layout '{}': {}", uniqueLayoutName, e.getMessage());
        } catch (Throwable t) {
            logger.error("Unexpected error during teardown for layout '{}': {}", uniqueLayoutName, t.getMessage());
        } finally {
            layoutCreated = false;
            uniqueLayoutName = null;
            try {
                // The long press leaves its menu open and turns multi-window reporting on. The menu is closed first,
                // because it is invisible once reporting is off, and the setting is switched off even if that fails.
                if (driver != null && historyBrowserPage != null) {
                    try {
                        historyBrowserPage.dismissContextMenuIfOpen();
                    } finally {
                        historyBrowserPage.resetMultiWindowSetting();
                    }
                }
            } catch (Throwable t) {
                logger.warn("Cleanup: could not close the long press menu or switch multi-window off: {}",
                        t.getMessage(), t);
            }
            super.tearDown(testResult);
        }
    }
}
