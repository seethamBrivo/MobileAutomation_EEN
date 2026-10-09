package tests.ts_layouts;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import pages.LayoutsPage;
import utils.JsonDataReader;

public class LayoutsTest extends BaseTest {

    private LayoutsPage layoutsPage;
    private boolean layoutCreated = false;
    private String uniqueLayoutName;

    @BeforeMethod(alwaysRun = true)
    public void loginAndVerifyLayoutsScreen() {
        layoutCreated = false;
        loginAs(TestDataConstants.ENDUSER_CREDS);
        layoutsPage = new LayoutsPage(driver);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.clickFirstLayoutTab();
    }

    // TODO: Parameterize with @DataProvider to cover multiple aspect ratios (16:9, 4:3, 1:1)
    // and cameras-per-row values. Requires teardown to track List<String> createdLayoutNames.
    @Test(description = "EEPD-TC-9005: POS-Layouts - adding New Layout in mobile VMS app",
          groups = {"regression", "layouts"})
    public void verifyAddNewLayoutFunctionality() {
        String baseLayoutName = JsonDataReader.getLayoutDataValue(TestDataConstants.LAYOUT_NAME);
        uniqueLayoutName = utils.TestDataGenerator.generateUniqueName(baseLayoutName);
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

    @Test(description = "EEPD-TC-9163: POS-Layouts/Tags - show camera previews in Layouts/Tags sections in mobile app",
          groups = {"regression", "layouts"})
    public void verifyCameraPreviewsDisplayedInLayouts() {
        layoutsPage.waitForCameraPreviewsToLoad();
        layoutsPage.verifyFirstCameraPreviewElements();
        layoutsPage.clickAllCamerasTab();
        layoutsPage.waitForCameraPreviewsToLoad();
        layoutsPage.verifyFirstCameraPreviewElements();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
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
            super.tearDown(testResult);
        }
    }
}
