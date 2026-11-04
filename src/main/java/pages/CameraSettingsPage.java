package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.util.Map;
import locators.CameraSettingsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/** The Camera settings screen (Android): header, status and name editing. */
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
        String actualStatus = WaitUtils.waitForVisibility(driver, CameraSettingsLocators.STATUS_VALUE).getText().trim();
        Assert.assertEquals(actualStatus, expectedStatus,
                "Camera status in settings should be '" + expectedStatus + "' but was '" + actualStatus + "'");
    }

    public String readName() {
        return WaitUtils.waitForVisibility(driver, CameraSettingsLocators.NAME_FIELD).getText().trim();
    }

    public void clickNameField() {
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
        driver.executeScript("mobile: performEditorAction", Map.of("action", "done"));
    }

    public void goBack() {
        driver.navigate().back();
    }

    public void verifyNameIsSet(String expectedName) {
        String seen = WaitUtils.pollText(driver, CameraSettingsLocators.HEADER_TITLE, expectedName,
                TimeoutConstants.LIST_LOAD_TIMEOUT);
        Assert.assertEquals(seen, expectedName,
                "Camera settings header should show the new name '" + expectedName + "' but was '" + seen + "'");
    }

    public void tapEnableSwitch() {
        tapAtCentre(CameraSettingsLocators.ENABLE_ROW);
    }

    /** Non-asserting check, so cleanup can ask where the camera ended up. */
    public boolean isStatusShownWithin(String expectedStatus) {
        return expectedStatus.equals(pollForStatus(expectedStatus));
    }

    public void verifyStatusBecomes(String expectedStatus) {
        String seen = pollForStatus(expectedStatus);
        Assert.assertEquals(seen, expectedStatus,
                "Camera status should become '" + expectedStatus + "' after the Enable switch but was '" + seen + "'");
    }

    /** The last status seen: the expected one, or whatever was shown when the budget ran out. Never null. */
    private String pollForStatus(String expectedStatus) {
        return WaitUtils.pollText(driver, CameraSettingsLocators.STATUS_VALUE, expectedStatus,
                TimeoutConstants.CAMERA_STATUS_CHANGE_TIMEOUT);
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

    /** Counted before the tap, so {@link #isEnableSwitchLeftToggled()} also covers a tap that failed half-way. */
    public void toggleEnableSwitch() {
        enableSwitchTaps++;
        tapEnableSwitch();
    }

    /** True when an odd number of {@link #toggleEnableSwitch()} taps were made, so the camera needs one more. */
    public boolean isEnableSwitchLeftToggled() {
        return enableSwitchTaps % 2 == 1;
    }

    public int enableSwitchTapCount() {
        return enableSwitchTaps;
    }

    /**
     * The open screen does not refresh its status (the test case says a refresh might be needed), so when the
     * status is not shown in time the camera's Camera settings are reopened from the cameras list.
     */
    public void verifyCameraStatus(String expectedStatus, String cameraName) {
        if (isStatusShownWithin(expectedStatus)) {
            return;
        }
        logger.warn("Status did not become '{}' on the open screen; reopening Camera settings to refresh it",
                expectedStatus);
        goBack();
        camerasPage.openContextMenuOfCameraNamed(cameraName);
        camerasPage.clickCameraSettingsOption();
        verifyStatusBecomes(expectedStatus);
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
}
