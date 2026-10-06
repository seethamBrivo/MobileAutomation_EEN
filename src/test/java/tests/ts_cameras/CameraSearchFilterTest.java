package tests.ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;
import utils.JsonDataReader;

public class CameraSearchFilterTest extends BaseTest {

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        loginAs(TestDataConstants.ENDUSER_CREDS);
        devicesPage = new DevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        camerasPage = new CamerasPage(driver);
    }

    @Test(description = "EEPD-TC-15581: POS-Cameras - Filter for cameras by search term under Cameras screen in Dashboard in VMS mobile app",
          groups = {"regression", "cameras"})
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
        camerasPage.verifyAllVisibleCamerasContainSearchTerm(validSearchTerm);
        camerasPage.verifyCameraCounterDisplayed();
        String filteredCount = camerasPage.getCameraCounterText();
        camerasPage.validateCounterFormat(filteredCount);
        camerasPage.verifyFilteredCountLessThanInitial(filteredCount, initialCount);
        camerasPage.verifyCanInteractWithFilteredCamera();
        camerasPage.clearSearchIfNeeded();
        camerasPage.verifyDefaultCameraListRestored();
        camerasPage.verifyCameraCounterDisplayed();
        String restoredCount = camerasPage.getCameraCounterText();
        camerasPage.validateCounterFormat(restoredCount);
        camerasPage.verifyCountersMatch(restoredCount, initialCount);
        camerasPage.enterSearchTerm(invalidSearchTerm);
        camerasPage.verifyEmptyStateDisplayed();
        camerasPage.clearSearchIfNeeded();
        camerasPage.verifyDefaultCameraListRestored();
    }
}
