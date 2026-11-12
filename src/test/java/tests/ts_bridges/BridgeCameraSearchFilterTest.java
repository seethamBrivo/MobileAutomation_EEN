package tests.ts_bridges;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BridgeCamerasPage;
import pages.DevicesPage;
import utils.JsonDataReader;

public class BridgeCameraSearchFilterTest extends BaseTest {

    private DevicesPage devicesPage;
    private BridgeCamerasPage bridgeCamerasPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToBridgeCameras() {
        devicesPage = loginAndOpenDevicesDashboard(TestDataConstants.ENDUSER_CREDS);
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeListDisplayed();
        devicesPage.openFirstOnlineBridgeContextMenu();
        bridgeCamerasPage = new BridgeCamerasPage(driver);
        bridgeCamerasPage.verifyCamerasOptionDisplayed();
        bridgeCamerasPage.clickCamerasOption();
        bridgeCamerasPage.verifyCameraListDisplayed();
    }

    @Test(description = "EEPD-TC-53382: POS-Bridges - Filter for respective bridge cameras by search term in Dashboard in VMS mobile app",
          groups = {"regression", "bridges", "ios"})
    public void verifyBridgeCameraSearchFilterFunctionality() {
        String validSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.BRIDGE_VALID_SEARCH_TERM);
        String invalidSearchTerm = JsonDataReader.getSearchFilterValue(TestDataConstants.INVALID_SEARCH_TERM);
        // Step 6: The cameras of the selected bridge are listed
        bridgeCamerasPage.verifyListScopedToOneBridge();
        String initialCount = bridgeCamerasPage.cameraSearchFilter().captureCounterAndVerifyFormat();
        // Step 7: Tap on the search bar
        bridgeCamerasPage.cameraSearchFilter().tapSearchBarAndVerify();
        // Step 8: Provide a search term
        bridgeCamerasPage.cameraSearchFilter().searchAndVerifyResults(validSearchTerm, initialCount);
        // Step 9: Interact with a camera from the filtered list
        bridgeCamerasPage.verifyCanInteractWithFilteredCamera();
        // Step 10: Cancel the filtering
        bridgeCamerasPage.cameraSearchFilter().clearSearchAndVerifyRestored(initialCount);
        // Step 8 (no results): a term that matches nothing shows the empty state
        bridgeCamerasPage.cameraSearchFilter().searchWithNoResultsAndVerifyEmptyState(invalidSearchTerm);
        bridgeCamerasPage.clickBackButton();
        devicesPage.verifyBridgeListDisplayed();
    }
}
