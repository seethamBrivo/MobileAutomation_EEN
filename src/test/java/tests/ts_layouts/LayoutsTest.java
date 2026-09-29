package tests.ts_layouts;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LayoutsPage;
import utils.JsonDataReader;

public class LayoutsTest extends BaseTest {

    private LayoutsPage layoutsPage;
    private boolean layoutCreated = false;
    private String uniqueLayoutName;

    @BeforeMethod
    public void loginAndVerifyLayoutsScreen() {
        layoutCreated = false;
        loginAs(TestDataConstants.ENDUSER_CREDS);
        layoutsPage = new LayoutsPage(driver);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.clickFirstLayoutTab();
    }

    @Test(description = "EEPD-TC-9005: POS-Layouts - adding New Layout in mobile VMS app")
    public void verifyAddNewLayoutFunctionality() {
        String baseLayoutName = JsonDataReader.getLayoutDataValue(TestDataConstants.LAYOUT_NAME);
        // Append timestamp to ensure uniqueness for parallel test execution
        uniqueLayoutName = baseLayoutName + "_" + System.currentTimeMillis();
        String aspectRatio = JsonDataReader.getLayoutDataValue(TestDataConstants.ASPECT_RATIO);
        int maxCamerasPerRow = JsonDataReader.getLayoutDataIntValue(TestDataConstants.MAX_CAMERAS_PER_ROW);
        boolean showCameraTitleBars = JsonDataReader.getLayoutDataBooleanValue(TestDataConstants.SHOW_CAMERA_TITLE_BARS);

        layoutsPage.clickThreeDotsMenuButton();
        layoutsPage.verifyMenuPopupDisplayed();
        layoutsPage.verifyMenuOptions();
        layoutsPage.clickNewLayoutOption();
        layoutsPage.verifyNewLayoutScreenDisplayed();
        layoutsPage.setLayoutName(uniqueLayoutName);
        layoutsPage.selectAspectRatio(aspectRatio);
        layoutsPage.selectMaxCamerasPerRow(maxCamerasPerRow);
        layoutsPage.toggleShowCameraTitleBars(showCameraTitleBars);
        layoutsPage.clickAddCamerasIcon();
        layoutsPage.selectFirstCamera();
        layoutsPage.clickSaveButton();
        layoutsPage.clickAddButton();
        layoutCreated = true;
        layoutsPage.verifyLayoutCreatedSuccessfully(uniqueLayoutName);
    }

    @Test(description = "EEPD-TC-9163: POS-Layouts/Tags - show camera previews in Layouts/Tags sections in mobile app")
    public void verifyCameraPreviewsDisplayedInLayouts() {
        layoutsPage.waitForCameraPreviewsToLoad();
        layoutsPage.verifyFirstCameraPreviewElements();
        layoutsPage.clickAllCamerasTab();
        layoutsPage.waitForCameraPreviewsToLoad();
        layoutsPage.verifyFirstCameraPreviewElements();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (!layoutCreated || uniqueLayoutName == null) {
            logger.info("No layout created in this test, skipping teardown");
            return;
        }

        try {
            if (driver != null) {
                layoutsPage.deleteLayout(uniqueLayoutName);
                logger.info("Successfully deleted layout '{}' in teardown", uniqueLayoutName);
            }
        } catch (org.openqa.selenium.NoSuchSessionException |
                 org.openqa.selenium.SessionNotCreatedException e) {
            logger.warn("Driver session closed, skipping cleanup for layout '{}'", uniqueLayoutName);
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.warn("Teardown failed to delete layout '{}': {}", uniqueLayoutName, e.getMessage());
        } catch (Throwable t) {
            // Catch AssertionError and any other throwables to prevent test suite abortion
            logger.error("Unexpected error during teardown for layout '{}': {}", uniqueLayoutName, t.getMessage());
        } finally {
            layoutCreated = false;
            uniqueLayoutName = null;
        }
    }
}
