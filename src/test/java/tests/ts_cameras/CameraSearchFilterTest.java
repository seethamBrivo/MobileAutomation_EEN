package tests.ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;

public class CameraSearchFilterTest extends BaseTest {

    private static final String VALID_SEARCH_TERM = "Camera";
    private static final String INVALID_SEARCH_TERM = "XYZ_NO_MATCH_12345";

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        devicesPage = loginAndOpenDevicesDashboard(TestDataConstants.ENDUSER_CREDS);
        camerasPage = new CamerasPage(driver);
    }

    @Test(description = "EEPD-TC-15581: POS-Cameras - Filter for cameras by search term under Cameras screen in Dashboard in VMS mobile app",
          groups = {"regression", "cameras", "ios"})
    public void verifyCameraSearchFilterFunctionality() {
        // Step 2: Go to All devices via the More menu and tap Cameras
        camerasPage.openCamerasList();
        String initialCount = camerasPage.cameraSearchFilter().captureCounterAndVerifyFormat();
        // Step 3: Tap on the search bar
        camerasPage.cameraSearchFilter().tapSearchBarAndVerify();
        // Step 4: Provide a search term
        camerasPage.cameraSearchFilter().searchAndVerifyResults(VALID_SEARCH_TERM, initialCount);
        // Step 5: Interact with a camera from the filtered list
        camerasPage.verifyCanInteractWithFilteredCamera();
        // Step 6: Cancel the filtering
        camerasPage.cameraSearchFilter().clearSearchAndVerifyRestored(initialCount);
        // Step 4 (no results): a term that matches nothing shows the empty state
        camerasPage.cameraSearchFilter().searchWithNoResultsAndVerifyEmptyState(INVALID_SEARCH_TERM);
    }
}
