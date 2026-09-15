package tests.ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;

public class CameraContextMenuTest extends BaseTest {

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

    @Test(description = "EEPD-TC-12355: POS-Cameras - display context menu after long press on camera in Dashboard in mobile VMS app")
    public void verifyCameraContextMenuAfterLongPress() {
        camerasPage.clickCamerasItem();
        camerasPage.verifyCameraListDisplayed();
        camerasPage.longPressOnCamera();
        camerasPage.verifyContextMenuDisplayed();
        camerasPage.verifyAllContextMenuOptions();
        camerasPage.verifyCameraPreviewDisplayed();
        camerasPage.verifyCameraNameDisplayed();
        camerasPage.verifyCameraStatusAndIndicators();
        camerasPage.dismissContextMenu();
        camerasPage.verifyOnlineCameraFound();
        camerasPage.longPressOnOnlineCamera();
        camerasPage.verifyTimestampDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null) {
            return;
        }
        try {
            camerasPage.dismissContextMenu();
        } catch (Exception e) {
            logger.warn("Cleanup: could not dismiss context menu: {}", e.getMessage());
        }
    }
}
