package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import locators.CameraSettingsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

/** The Camera settings screen: header, status, name editing and the Enable switch. */
public class CameraSettingsPage {

    private static final Logger logger = LogManager.getLogger(CameraSettingsPage.class);

    private final AppiumDriver driver;
    private final CamerasPage camerasPage;
    private final DevicesPage devicesPage;
    private int enableSwitchTaps;

    public CameraSettingsPage(AppiumDriver driver) {
        this.driver = driver;
        this.camerasPage = new CamerasPage(driver);
        this.devicesPage = new DevicesPage(driver);
    }

    public void verifyCameraSettingsScreenDisplayed() {
        WaitUtils.assertVisible(driver, CameraSettingsLocators.GENERAL_SECTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT,
                "Camera settings screen should be displayed");
    }

    public void verifyCameraPreviewIconDisplayed() {
        WaitUtils.assertVisible(driver, CameraSettingsLocators.PREVIEW_ICON,
                "Camera preview icon should be visible in the top right corner of the Camera settings screen");
    }

    public boolean isCameraPreviewIconDisplayed() {
        return WaitUtils.isVisible(driver, CameraSettingsLocators.PREVIEW_ICON, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void verifyStatusIs(String expectedStatus) {
        String actualStatus = readStatus();
        Assert.assertEquals(actualStatus, expectedStatus,
                "Camera status in settings should be '" + expectedStatus + "' but was '" + actualStatus + "'");
    }

    public String readName() {
        return WaitUtils.waitForVisibility(driver, CameraSettingsLocators.NAME_FIELD).getText().trim();
    }

    public void clickNameField() {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, CameraSettingsLocators.NAME_FIELD);
            return;
        }
        tapAtCentre(CameraSettingsLocators.NAME_FIELD);
    }

    public void enterName(String name) {
        WaitUtils.clearAndType(driver, CameraSettingsLocators.NAME_FIELD, name);
        String seen = WaitUtils.pollText(driver, CameraSettingsLocators.NAME_FIELD, name,
                TimeoutConstants.FILTER_RESULT_TIMEOUT);
        Assert.assertEquals(seen, name, "The typed name should be in the Name field before it is saved");
    }

    /** The screen has no Save button; the keyboard's Done saves the name. */
    public void saveName() {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, CameraSettingsLocators.KEYBOARD_DONE_KEY);
            return;
        }
        driver.executeScript("mobile: performEditorAction", Map.of("action", "done"));
    }

    public void goBack() {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, CameraSettingsLocators.BACK_BUTTON);
            return;
        }
        driver.navigate().back();
    }

    public void verifyNameIsSet(String expectedName) {
        String seen = WaitUtils.pollText(driver, CameraSettingsLocators.HEADER_TITLE, expectedName,
                TimeoutConstants.LIST_LOAD_TIMEOUT);
        Assert.assertEquals(seen, expectedName,
                "Camera settings header should show the new name '" + expectedName + "' but was '" + seen + "'");
    }

    public void tapEnableSwitch() {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, CameraSettingsLocators.ENABLE_ROW);
            return;
        }
        tapAtCentre(CameraSettingsLocators.ENABLE_ROW);
    }

    /**
     * iOS only: the Enable switch shows its state (value "1" on, "0" off), which Android's does not, so the state is
     * checked next to the status. Android relies on the status alone.
     */
    private void verifyEnableSwitchMatches(String expectedStatus) {
        if (!PlatformLocator.isIos()) {
            return;
        }
        boolean off = CamerasPage.STATUS_TURNED_OFF.equals(expectedStatus);
        // The switch can still show the old state for a moment after the status text has changed (seen once: status
        // "Turned off" with the switch on), so it gets a short time to settle; if it never does, that is the failure.
        WaitUtils.assertVisible(driver,
                off ? CameraSettingsLocators.ENABLE_SWITCH_OFF : CameraSettingsLocators.ENABLE_SWITCH_ON,
                TimeoutConstants.DETAILS_LOAD_TIMEOUT,
                "The Enable switch should be " + (off ? "off" : "on") + " while the camera status is '"
                        + expectedStatus + "'");
    }

    /** Non-asserting check of where the camera ended up. */
    private boolean isStatusShownWithin(String expectedStatus) {
        return expectedStatus.equals(pollForStatus(expectedStatus));
    }

    public void verifyStatusBecomes(String expectedStatus) {
        String seen = pollForStatus(expectedStatus);
        Assert.assertEquals(seen, expectedStatus,
                "Camera status should become '" + expectedStatus + "' after the Enable switch but was '" + seen + "'");
        verifyEnableSwitchMatches(expectedStatus);
    }

    /** The last status seen: the expected one, or whatever was shown when the budget ran out. Never null. */
    private String pollForStatus(String expectedStatus) {
        return pollForStatus(expectedStatus, TimeoutConstants.CAMERA_STATUS_CHANGE_TIMEOUT);
    }

    private String pollForStatus(String expectedStatus, Duration timeout) {
        return WaitUtils.pollText(driver, CameraSettingsLocators.STATUS_VALUE, expectedStatus, timeout);
    }

    private String readStatus() {
        return WaitUtils.waitForVisibility(driver, CameraSettingsLocators.STATUS_VALUE).getText().trim();
    }

    private void tapAtCentre(By locator) {
        WebElement element = WaitUtils.waitForClickability(driver, locator);
        Point centre = GestureUtils.getElementCenter(element);
        GestureUtils.tap(driver, centre.x, centre.y);
    }

    /** Opens Camera settings from the open context menu and returns the camera's current name. */
    public String openFromContextMenuAndReadName() {
        camerasPage.clickCameraSettingsOption();
        verifyCameraSettingsScreenDisplayed();
        String name = readName();
        logger.info("Camera settings opened for camera '{}'", name);
        return name;
    }

    /** Counted before the tap, so a tap that failed half-way still tells cleanup that the switch was touched. */
    public void toggleEnableSwitch() {
        enableSwitchTaps++;
        tapEnableSwitch();
    }

    public int enableSwitchTapCount() {
        return enableSwitchTaps;
    }

    /**
     * The open screen does not refresh its status (the test case says a refresh might be needed). Android waits for
     * it, and reopens the camera's Camera settings from the cameras list when it is not shown in time; iOS reopens
     * them until the status matches.
     */
    public void verifyCameraStatus(String expectedStatus, String cameraName) {
        if (PlatformLocator.isIos()) {
            verifyStatusBecomesOnReopenedScreen(expectedStatus, cameraName);
            return;
        }
        if (isStatusShownWithin(expectedStatus)) {
            return;
        }
        logger.warn("Status did not become '{}' on the open screen; reopening Camera settings to refresh it",
                expectedStatus);
        reopenFromCamerasList(cameraName);
        verifyStatusBecomes(expectedStatus);
    }

    /**
     * iOS: the open screen never refreshes (measured: still the old status and switch 20 seconds after the tap), and a
     * camera takes a while to come back online, so the camera is reopened until the status is the expected one, for
     * as long as a status change may take. The Enable switch is checked once the status matches.
     */
    private void verifyStatusBecomesOnReopenedScreen(String expectedStatus, String cameraName) {
        Instant deadline = Instant.now().plus(TimeoutConstants.CAMERA_STATUS_CHANGE_TIMEOUT_IOS);
        String seen;
        do {
            reopenFromCamerasList(cameraName);
            seen = WaitUtils.pollText(driver, CameraSettingsLocators.STATUS_VALUE, expectedStatus,
                    TimeoutConstants.SHORT_TIMEOUT);
            if (expectedStatus.equals(seen)) {
                verifyEnableSwitchMatches(expectedStatus);
                return;
            }
            logger.info("Status of '{}' is '{}', waiting for '{}'", cameraName, seen, expectedStatus);
        } while (Instant.now().isBefore(deadline));
        Assert.fail("Camera status should become '" + expectedStatus + "' after the Enable switch but was '" + seen
                + "' when " + TimeoutConstants.CAMERA_STATUS_CHANGE_TIMEOUT_IOS.toSeconds() + " seconds had passed");
    }

    private void reopenFromCamerasList(String cameraName) {
        goBack();
        camerasPage.openContextMenuOfCameraNamed(cameraName);
        camerasPage.clickCameraSettingsOption();
    }

    /**
     * For cleanup: the camera has the new name only if the rename got as far as saving, so the names are tried in
     * order; null names are skipped. Returns the name that opened Camera settings, or null when none did.
     */
    public String openFromCamerasListByAnyName(String... names) {
        for (String name : names) {
            if (name == null) {
                continue;
            }
            try {
                devicesPage.recoverToDashboard();
                camerasPage.openCamerasList();
                camerasPage.openContextMenuOfCameraNamed(name);
                camerasPage.clickCameraSettingsOption();
                return name;
            } catch (Throwable t) {
                logger.warn("@AfterMethod: could not open camera '{}' from the cameras list", name, t);
            }
        }
        return null;
    }

    /**
     * The state the camera is in. iOS reads the Enable switch itself and fails when it shows neither state, so a
     * switch that has not rendered is never taken for "on". Android's switch exposes no state, so the Status line is
     * read ("Turned off" is the only status of a switched-off camera; Offline or an error mean a camera that is on
     * but not reachable).
     */
    private boolean isCameraTurnedOff() {
        if (PlatformLocator.isIos()) {
            Optional<WebElement> enableSwitch = WaitUtils.waitForAny(driver,
                    List.of(CameraSettingsLocators.ENABLE_SWITCH_ON, CameraSettingsLocators.ENABLE_SWITCH_OFF),
                    TimeoutConstants.DETAILS_LOAD_TIMEOUT);
            Assert.assertTrue(enableSwitch.isPresent(),
                    "The Enable switch should show its state (on or off) on the Camera settings screen");
            return "0".equals(enableSwitch.get().getAttribute("value"));
        }
        return CamerasPage.STATUS_TURNED_OFF.equals(readStatus());
    }

    /**
     * Start of EEPD-TC-11323: the test case assumes the camera is on. A camera an earlier run left off is switched on
     * first and checked to be "Online", so the test starts and ends in the same state. The tap is not counted, because
     * it is checked here and cleanup has nothing to undo for it.
     */
    public void turnCameraOnIfOff(String cameraName) {
        if (!isCameraTurnedOff()) {
            return;
        }
        logger.info("Camera '{}' is turned off (an earlier run may have left it so); switching it on first",
                cameraName);
        tapEnableSwitch();
        verifyCameraStatus(CamerasPage.STATUS_ONLINE, cameraName);
    }

    /**
     * For cleanup, on a freshly opened Camera settings screen. A camera that reads "Online" is left alone at once.
     * One that reads "Turned off" gets {@link TimeoutConstants#LIST_LOAD_TIMEOUT} to turn "Online" first, because the
     * status can lag the switch, and tapping again then would switch the camera off.
     */
    public boolean isCameraLeftTurnedOff() {
        return isCameraTurnedOff() && !CamerasPage.STATUS_ONLINE.equals(
                pollForStatus(CamerasPage.STATUS_ONLINE, TimeoutConstants.LIST_LOAD_TIMEOUT));
    }
}
