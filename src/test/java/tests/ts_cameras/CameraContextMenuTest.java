package tests.ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;

public class CameraContextMenuTest extends BaseTest {

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        loginAs(TestDataConstants.TS02_CREDS);
        devicesPage = new DevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        camerasPage = new CamerasPage(driver);
    }

    @Test(description = "EEPD-TC-12355: POS-Cameras - display context menu after long press on camera in Dashboard in mobile VMS app",
          groups = {"regression", "cameras"})
    public void verifyCameraContextMenuAfterLongPress() {
        // Step 3: Tap on the Cameras item
        camerasPage.openCamerasList();
        // Step 4: Long press the first offline or error camera and verify its context menu
        camerasPage.verifyFirstOfflineOrErrorCameraContextMenu();
        // Step 4 (online camera): long press the first online camera; its menu preview shows a timestamp
        camerasPage.verifyFirstOnlineCameraContextMenu();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(ITestResult testResult) {
        try {
            if (driver != null) {
                camerasPage.dismissContextMenu();
            }
        } catch (Throwable t) {
            logger.warn("Cleanup: could not dismiss context menu: {}", t.toString());
        } finally {
            super.tearDown(testResult);
        }
    }
}
