package tests.ts_bridges;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BridgeCamerasPage;
import pages.DevicesPage;
import utils.JsonDataReader;

public class BridgeCameraSearchFilterTest extends BaseTest {

    private DevicesPage devicesPage;
    private BridgeCamerasPage bridgeCamerasPage;

    @BeforeMethod
    public void loginAndNavigateToBridgeCameras() {
        loginAs(TestDataConstants.ENDUSER_CREDS);
        devicesPage = new DevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeListDisplayed();
        devicesPage.openFirstBridgeContextMenu();
        bridgeCamerasPage = new BridgeCamerasPage(driver);
        bridgeCamerasPage.verifyCamerasOptionDisplayed();
        bridgeCamerasPage.clickCamerasOption();
        bridgeCamerasPage.verifyCameraListDisplayed();
    }

    @Test(description = "EEPD-TC-53382: POS-Bridges - Filter for respective bridge cameras by search term in Dashboard in VMS mobile app")
    public void verifyBridgeCameraSearchFilterFunctionality() {
        String validSearchTerm = JsonDataReader.getTestData().getAsJsonObject("search_filter").get("valid_search_term").getAsString();
        String invalidSearchTerm = JsonDataReader.getTestData().getAsJsonObject("search_filter").get("invalid_search_term").getAsString();

        String initialCount = bridgeCamerasPage.getCameraCounterText();
        bridgeCamerasPage.validateCounterFormat(initialCount);
        bridgeCamerasPage.clickSearchBar();
        bridgeCamerasPage.verifySearchPlaceholderDisplayed();
        bridgeCamerasPage.verifySearchFieldEditable();
        bridgeCamerasPage.enterSearchTerm(validSearchTerm);
        bridgeCamerasPage.verifyCameraFoundInFilter(validSearchTerm);
        bridgeCamerasPage.verifyFilteredListDisplayed();
        bridgeCamerasPage.verifyCameraCounterDisplayed();
        String filteredCount = bridgeCamerasPage.getCameraCounterText();
        bridgeCamerasPage.validateCounterFormat(filteredCount);
        bridgeCamerasPage.verifyFilteredCountLessThanOrEqualInitial(filteredCount, initialCount);
        bridgeCamerasPage.verifyCanInteractWithFilteredCamera();
        bridgeCamerasPage.clearSearchIfNeeded();
        bridgeCamerasPage.verifyDefaultCameraListRestored();
        bridgeCamerasPage.verifyCameraCounterDisplayed();
        String restoredCount = bridgeCamerasPage.getCameraCounterText();
        bridgeCamerasPage.validateCounterFormat(restoredCount);
        bridgeCamerasPage.verifyCountersMatch(restoredCount, initialCount);
        bridgeCamerasPage.enterSearchTerm(invalidSearchTerm);
        bridgeCamerasPage.verifyEmptyStateDisplayed();
        bridgeCamerasPage.verifyCameraCounterDisplayed();
        String emptyStateCount = bridgeCamerasPage.getCameraCounterText();
        bridgeCamerasPage.validateCounterFormat(emptyStateCount);
        bridgeCamerasPage.clearSearchIfNeeded();
        bridgeCamerasPage.verifyDefaultCameraListRestored();
        // Navigate back: first from cameras view, then from bridge context menu
        bridgeCamerasPage.clickBackButton();
        driver.navigate().back();
        devicesPage.verifyBridgeListDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        try {
            if (driver != null && bridgeCamerasPage != null) {
                bridgeCamerasPage.clearSearchIfNeeded();
                // Navigate back: first from cameras view, then from bridge context menu
                bridgeCamerasPage.clickBackButton();
                driver.navigate().back();
            }
        } catch (Exception e) {
            logger.debug("Cleanup: already at bridge list or navigation not needed: {}", e.getMessage());
        }
    }
}
