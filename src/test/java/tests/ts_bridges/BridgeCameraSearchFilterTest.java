package tests.ts_bridges;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.Assert;
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
        devicesPage.clickBridgeThreeDotsButton();
        bridgeCamerasPage = new BridgeCamerasPage(driver);
        bridgeCamerasPage.verifyCamerasOptionDisplayed();
        bridgeCamerasPage.clickCamerasOption();
        bridgeCamerasPage.verifyCameraListDisplayed();
    }

    @Test(description = "EEPD-TC-53382: POS-Bridges - Filter for respective bridge cameras by search term in Dashboard in VMS mobile app")
    public void verifyBridgeCameraSearchFilterFunctionality() {
        String validSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.VALID_SEARCH_TERM);
        String invalidSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.INVALID_SEARCH_TERM);

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
        int emptyCount = bridgeCamerasPage.extractCountFromCounterText(emptyStateCount);
        Assert.assertEquals(emptyCount, 0, "Counter should show 0 cameras when no matches found");
        bridgeCamerasPage.clearSearchIfNeeded();
        bridgeCamerasPage.verifyDefaultCameraListRestored();
        bridgeCamerasPage.clickBackButton();
        devicesPage.verifyBridgeListDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Test already handles navigation back to bridge list, cleanup only needed on test failure
        try {
            if (driver != null && bridgeCamerasPage != null) {
                bridgeCamerasPage.clearSearchIfNeeded();
            }
        } catch (Exception e) {
            logger.warn("Cleanup: could not clear search: {}", e.getMessage());
        }
    }
}
