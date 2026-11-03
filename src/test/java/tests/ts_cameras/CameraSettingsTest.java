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

public class CameraSettingsTest extends BaseTest {

    private DevicesPage devicesPage;
    private CamerasPage camerasPage;
    private CameraSettingsPage settingsPage;
    private String originalCameraName;
    private String renamedCameraName;
    private int enableSwitchTaps;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToDashboard() {
        originalCameraName = null;
        renamedCameraName = null;
        enableSwitchTaps = 0;
        devicesPage = null;
        camerasPage = null;
        settingsPage = null;
        devicesPage = loginAndOpenDevicesDashboard(TestDataConstants.ENDUSER_CREDS);
        camerasPage = new CamerasPage(driver);
        settingsPage = new CameraSettingsPage(driver);
    }

    /** Android only. "Status obtained from SSE" is not automated; only the shown status is checked. */
    @Test(description = "EEPD-TC-8861: POS-Camera Settings - changing the name and checking the status of Camera in "
            + "camera settings in mobile VMS app", groups = {"regression", "cameras"})
    public void verifyChangeCameraNameAndCheckStatus() {
        // Step 2: Cameras in Dashboard, long press the first online camera, open Camera settings
        openSettingsOfFirstOnlineCamera();
        // Step 3: the status is shown in section "Status"
        String status = settingsPage.readStatus();
        logger.info("Camera '{}' status: {}", originalCameraName, status);
        settingsPage.verifyStatusIs(CamerasPage.STATUS_ONLINE, status);
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

    /** Android only. The switch shows no state, so the camera's Status is checked; the test takes about 4 minutes. */
    @Test(description = "EEPD-TC-11323: POS-Camera Settings - turning on/off camera in Dashboard (inside Camera "
            + "Settings screen) mobile VMS app", groups = {"regression", "cameras"})
    public void verifyTurnCameraOffAndOn() {
        // Step 2: Cameras in Dashboard, long press the first online camera, open Camera settings
        openSettingsOfFirstOnlineCamera();
        // Step 3: tap the switch next to Enable to turn the camera off, then on again
        toggleEnableSwitch();
        verifyCameraStatus(CamerasPage.STATUS_TURNED_OFF);
        toggleEnableSwitch();
        verifyCameraStatus(CamerasPage.STATUS_ONLINE);
    }

    /** Step 2 of both test cases. */
    private void openSettingsOfFirstOnlineCamera() {
        camerasPage.openCamerasList();
        if (!camerasPage.hasOnlineCamera()) {
            throw new SkipException("An online camera is needed on the End User account; none found in the list");
        }
        camerasPage.longPressFirstOnlineCameraAndVerifyContextMenu();
        camerasPage.clickCameraSettingsOption();
        settingsPage.verifyCameraSettingsScreenDisplayed();
        settingsPage.verifyCameraPreviewIconDisplayed();
        originalCameraName = settingsPage.readName();
    }

    /** The open screen does not refresh its status (the test case says a refresh might be needed), so reopen it. */
    private void verifyCameraStatus(String expectedStatus) {
        if (settingsPage.isStatusShownWithin(expectedStatus)) {
            return;
        }
        logger.warn("Status did not become '{}' on the open screen; reopening Camera settings to refresh it",
                expectedStatus);
        settingsPage.goBack();
        camerasPage.openContextMenuOfCameraNamed(originalCameraName);
        camerasPage.clickCameraSettingsOption();
        settingsPage.verifyStatusBecomes(expectedStatus);
    }

    /** Counted before the tap, so cleanup also undoes a tap that failed half-way. */
    private void toggleEnableSwitch() {
        enableSwitchTaps++;
        settingsPage.tapEnableSwitch();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(ITestResult testResult) {
        try {
            boolean restoreName = renamedCameraName != null;
            boolean restoreSwitch = enableSwitchTaps % 2 == 1;
            if (driver == null || devicesPage == null || !(restoreName || restoreSwitch)) {
                return;
            }
            String currentName = restoreName ? renamedCameraName : originalCameraName;
            try {
                devicesPage.recoverToDashboard();
                camerasPage.openCamerasList();
                camerasPage.openContextMenuOfCameraNamed(currentName);
                camerasPage.clickCameraSettingsOption();
            } catch (Throwable t) {
                logger.error("@AfterMethod: COULD NOT OPEN camera '{}' to restore it (name '{}', switch taps {})",
                        currentName, originalCameraName, enableSwitchTaps, t);
                return;
            }
            if (restoreName) {
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
