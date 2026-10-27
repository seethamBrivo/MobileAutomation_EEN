package tests.ios_ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ios.IosCamerasPage;
import pages.ios.IosDevicesPage;
import utils.JsonDataReader;

/** iOS counterpart of {@code tests.ts_cameras.CameraSearchFilterTest}: same test case and steps. */
public class IosCameraSearchFilterTest extends BaseTest {

    private IosDevicesPage devicesPage;
    private IosCamerasPage camerasPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        iosLoginAs(TestDataConstants.ENDUSER_CREDS);
        devicesPage = new IosDevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        camerasPage = new IosCamerasPage(driver);
    }

    @Test(description = "EEPD-TC-15581: POS-Cameras - Filter for cameras by search term under Cameras screen in Dashboard in VMS mobile app",
          groups = {"ios", "cameras"})
    public void verifyCameraSearchFilterFunctionality() {
        String validSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.VALID_SEARCH_TERM);
        String invalidSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.INVALID_SEARCH_TERM);
        // Step 2: Go to All devices via the More menu and tap Cameras
        camerasPage.openCamerasList();
        String initialCount = camerasPage.captureCounterAndVerifyFormat();
        // Step 3: Tap on the search bar
        camerasPage.tapSearchBarAndVerify();
        // Step 4: Provide a search term
        camerasPage.searchAndVerifyResults(validSearchTerm, initialCount);
        // Step 5: Interact with a camera from the filtered list
        camerasPage.verifyCanInteractWithFilteredCamera();
        // Step 6: Cancel the filtering
        camerasPage.clearSearchAndVerifyRestored(initialCount);
        // Step 4 (no results): a term that matches nothing shows the empty state
        camerasPage.searchWithNoResultsAndVerifyEmptyState(invalidSearchTerm);
    }
}
