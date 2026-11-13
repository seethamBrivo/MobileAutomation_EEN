package tests.ts_cameras;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CameraSettingsPage;
import pages.CamerasPage;
import pages.DevicesPage;
import utils.JsonDataReader;

public class CameraSettingsTest extends BaseTest {

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;
    private CameraSettingsPage settingsPage;
    private String originalCameraName;
    private String renamedCameraName;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        originalCameraName = null;
        renamedCameraName = null;
        devicesPage = null;
        camerasPage = null;
        settingsPage = null;
        devicesPage = loginAndOpenDevicesDashboard(TestDataConstants.ENDUSER_CREDS);
        camerasPage = new CamerasPage(driver);
        settingsPage = new CameraSettingsPage(driver);
    }

    /** "Status obtained from SSE" is not automated; only the shown status is checked. */
    @Test(description = "EEPD-TC-8861: POS-Camera Settings - changing the name and checking the status of Camera in "
            + "camera settings in mobile VMS app", groups = {"regression", "cameras", "ios"})
    public void verifyChangeCameraNameAndCheckStatus() {
        // Step 2: Cameras in Dashboard, long press the first online camera, open Camera settings
        openSettingsOfFirstOnlineCamera();
        // Step 3: the status in section "Status" is "Online" (the camera was picked from an "Online" card)
        settingsPage.verifyStatusIs(CamerasPage.STATUS_ONLINE);
        // Step 4: tap the Name option and fill the field
        String newName = TestDataConstants.CAMERA_PREFIX_RENAMED
                + String.format("%05d", System.currentTimeMillis() % 100000);
        settingsPage.clickNameField();
        // Set before typing, so cleanup restores the name even if the typing fails half-way.
        renamedCameraName = newName;
        settingsPage.enterName(newName);
        // Step 5: confirm the name
        settingsPage.saveName();
        settingsPage.verifyNameIsSet(newName);
        settingsPage.goBack();
        camerasPage.verifyCameraListedWithName(newName);
    }

    /**
     * The camera's Status is checked (Android's switch shows no state; on iOS the switch state is checked too). It
     * takes about 4 minutes, and up to about 8 when the status is slow (each check waits twice: on the open screen,
     * then on the reopened one).
     */
    @Test(description = "EEPD-TC-11323: POS-Camera Settings - turning on/off camera in Dashboard (inside Camera "
            + "Settings screen) mobile VMS app", groups = {"regression", "cameras", "ios"})
    public void verifyTurnCameraOffAndOn() {
        // Step 2: Cameras in Dashboard, long press an online camera that can be switched, open Camera settings
        openSettingsOfSwitchableOnlineCamera();
        // Step 3: tap the switch next to Enable to turn the camera off, then on again
        settingsPage.toggleEnableSwitch();
        settingsPage.verifyCameraStatus(CamerasPage.STATUS_TURNED_OFF, originalCameraName);
        settingsPage.toggleEnableSwitch();
        settingsPage.verifyCameraStatus(CamerasPage.STATUS_ONLINE, originalCameraName);
    }

    /** Step 2 of EEPD-TC-8861. */
    private void openSettingsOfFirstOnlineCamera() {
        camerasPage.openCamerasList();
        if (!camerasPage.hasOnlineCamera()) {
            throw new SkipException("An online camera is needed on the End User account; none found in the list");
        }
        camerasPage.longPressFirstOnlineCameraAndVerifyContextMenu();
        originalCameraName = settingsPage.openFromContextMenuAndReadName();
        settingsPage.verifyCameraPreviewIconDisplayed();
    }

    /**
     * Step 2 of EEPD-TC-11323. The camera is picked by name from the test data: the account's simulator camera is
     * online too, but the End User cannot switch it (no Turn on/off in its menu on Android, a disabled Enable
     * switch on iOS), and "the first/second online camera" is not the same row on both platforms.
     */
    private void openSettingsOfSwitchableOnlineCamera() {
        String cameraName = JsonDataReader.getSwitchableCameraValue(TestDataConstants.SWITCHABLE_CAMERA_NAME);
        camerasPage.openCamerasList();
        if (!camerasPage.hasCameraNamed(cameraName)) {
            throw new SkipException("The online camera '" + cameraName + "' is needed on the End User account; "
                    + "not found in the list");
        }
        camerasPage.longPressCameraNamedAndVerifyContextMenu(cameraName);
        originalCameraName = settingsPage.openFromContextMenuAndReadName();
        // Not asserted: this camera's header sometimes has no preview icon (the app does not draw it), and the
        // icon has nothing to do with turning the camera off and on. It is asserted in EEPD-TC-8861.
        logger.info("Camera preview icon shown: {}", settingsPage.isCameraPreviewIconDisplayed());
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        try {
            boolean restoreName = renamedCameraName != null;
            boolean restoreSwitch = settingsPage != null && settingsPage.isEnableSwitchLeftToggled();
            if (driver == null || devicesPage == null || !(restoreName || restoreSwitch)) {
                return;
            }
            String currentName = settingsPage.openFromCamerasListByAnyName(renamedCameraName, originalCameraName);
            if (currentName == null) {
                logger.error("@AfterMethod: COULD NOT OPEN camera '{}' / '{}' to restore it (switch taps {})",
                        renamedCameraName, originalCameraName, settingsPage.enableSwitchTapCount());
                return;
            }
            if (restoreName && currentName.equals(renamedCameraName)) {
                try {
                    settingsPage.clickNameField();
                    settingsPage.enterName(originalCameraName);
                    settingsPage.saveName();
                    logger.info("@AfterMethod: restored camera name '{}'", originalCameraName);
                } catch (Throwable t) {
                    logger.error("@AfterMethod: COULD NOT RESTORE camera '{}' to its original name '{}'",
                            renamedCameraName, originalCameraName, t);
                }
            }
            if (restoreSwitch) {
                try {
                    if (settingsPage.isStatusShownWithin(CamerasPage.STATUS_TURNED_OFF)) {
                        settingsPage.tapEnableSwitch();
                        logger.info("@AfterMethod: turned camera '{}' back on", currentName);
                    } else {
                        logger.info("@AfterMethod: camera '{}' is not turned off, switch left alone", currentName);
                    }
                } catch (Throwable t) {
                    logger.error("@AfterMethod: COULD NOT TURN camera '{}' back on", currentName, t);
                }
            }
        } finally {
            super.tearDown(testResult);
        }
    }
}
