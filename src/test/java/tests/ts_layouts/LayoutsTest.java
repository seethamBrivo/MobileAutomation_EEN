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
        String layoutName = JsonDataReader.getLayoutDataValue(TestDataConstants.LAYOUT_NAME);
        String aspectRatio = JsonDataReader.getLayoutDataValue(TestDataConstants.ASPECT_RATIO);
        int maxCamerasPerRow = JsonDataReader.getLayoutDataIntValue(TestDataConstants.MAX_CAMERAS_PER_ROW);
        boolean showCameraTitleBars = JsonDataReader.getLayoutDataBooleanValue(TestDataConstants.SHOW_CAMERA_TITLE_BARS);

        layoutsPage.clickThreeDotsMenuButton();
        layoutsPage.verifyMenuPopupDisplayed();
        layoutsPage.verifyMenuOptions();
        layoutsPage.clickNewLayoutOption();
        layoutsPage.verifyNewLayoutScreenDisplayed();
        layoutsPage.setLayoutName(layoutName);
        layoutsPage.selectAspectRatio(aspectRatio);
        layoutsPage.selectMaxCamerasPerRow(maxCamerasPerRow);
        layoutsPage.toggleShowCameraTitleBars(showCameraTitleBars);
        layoutsPage.clickAddCamerasIcon();
        layoutsPage.selectFirstCamera();
        layoutsPage.clickSaveButton();
        layoutsPage.clickAddButton();
        // Set flag after the action that creates the layout, before verification
        layoutCreated = true;
        layoutsPage.verifyLayoutCreatedSuccessfully();
    }

    @Test(description = "EEPD-TC-9163: POS-Layouts/Tags - show camera previews in Layouts/Tags sections in mobile app")
    public void verifyCameraPreviewsDisplayedInLayouts() {
        layoutsPage.waitForCameraPreviewsToLoad();
        layoutsPage.verifyFirstCameraPreviewElements();
        layoutsPage.clickAllCamerasTab();
        layoutsPage.waitForCameraPreviewsToLoad();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (!layoutCreated) {
            logger.info("No layout created in this test, skipping teardown");
            return;
        }

        String layoutName = JsonDataReader.getLayoutDataValue(TestDataConstants.LAYOUT_NAME);
        try {
            if (driver != null) {
                try {
                    driver.getSessionId();
                    layoutsPage.deleteLayout(layoutName);
                    logger.info("Successfully deleted layout '{}' in teardown", layoutName);
                } catch (org.openqa.selenium.NoSuchSessionException e) {
                    logger.warn("Driver session already closed, skipping layout deletion");
                }
            }
        } catch (Exception e) {
            logger.warn("Teardown failed to delete layout '{}': {}", layoutName, e.getMessage());
        } finally {
            layoutCreated = false;
        }
    }
}
