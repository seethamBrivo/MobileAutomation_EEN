package tests.ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;
import utils.JsonDataReader;

public class CameraSearchFilterTest extends BaseTest {

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;

    @BeforeMethod
    public void loginAndNavigateToDashboard() {
        loginAs(TestDataConstants.ENDUSER_CREDS);
        devicesPage = new DevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        camerasPage = new CamerasPage(driver);
    }

    @Test(description = "EEPD-TC-15581: POS-Cameras - Filter for cameras by search term under Cameras screen in Dashboard in VMS mobile app")
    public void verifyCameraSearchFilterFunctionality() {
        String validSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.VALID_SEARCH_TERM);
        String invalidSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.INVALID_SEARCH_TERM);

        camerasPage.clickCamerasItem();
        camerasPage.verifyCameraListDisplayed();
        String initialCount = camerasPage.getCameraCounterText();
        camerasPage.validateCounterFormat(initialCount);
        camerasPage.clickSearchBar();
        camerasPage.verifySearchPlaceholderDisplayed();
        camerasPage.verifySearchFieldEditable();
        camerasPage.enterSearchTerm(validSearchTerm);
        camerasPage.verifyCameraFoundInFilter(validSearchTerm);
        camerasPage.verifyFilteredListDisplayed();
        String firstCameraName = camerasPage.getFirstCameraName();
        Assert.assertTrue(firstCameraName.toLowerCase().contains(validSearchTerm.toLowerCase()),
                "First camera name '" + firstCameraName + "' should contain search term '" + validSearchTerm + "'");
        camerasPage.verifyCameraCounterDisplayed();
        String filteredCount = camerasPage.getCameraCounterText();
        camerasPage.validateCounterFormat(filteredCount);
        camerasPage.verifyFilteredCountLessThanOrEqualInitial(filteredCount, initialCount);
        camerasPage.verifyCanInteractWithFilteredCamera();
        camerasPage.clearSearchIfNeeded();
        camerasPage.verifyDefaultCameraListRestored();
        camerasPage.verifyCameraCounterDisplayed();
        String restoredCount = camerasPage.getCameraCounterText();
        camerasPage.validateCounterFormat(restoredCount);
        camerasPage.verifyCountersMatch(restoredCount, initialCount);
        camerasPage.enterSearchTerm(invalidSearchTerm);
        camerasPage.verifyEmptyStateDisplayed();
        camerasPage.verifyCameraCounterDisplayed();
        String emptyStateCount = camerasPage.getCameraCounterText();
        camerasPage.validateCounterFormat(emptyStateCount);
        int emptyCount = camerasPage.extractCountFromCounterText(emptyStateCount);
        Assert.assertEquals(emptyCount, 0, "Counter should show 0 cameras when no matches found");
        camerasPage.clearSearchIfNeeded();
        camerasPage.verifyDefaultCameraListRestored();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        try {
            if (driver != null && camerasPage != null) {
                camerasPage.clearSearchIfNeeded();
            }
        } catch (Exception e) {
            logger.warn("Cleanup: could not clear search: {}", e.getMessage());
        }
    }
}
