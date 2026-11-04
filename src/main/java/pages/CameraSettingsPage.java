package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.util.Map;
import locators.CameraSettingsLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/** The Camera settings screen (Android): header, status and name editing. */
public class CameraSettingsPage {

    private final AppiumDriver driver;

    public CameraSettingsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyCameraSettingsScreenDisplayed() {
        WaitUtils.assertVisible(driver, CameraSettingsLocators.GENERAL_SECTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT,
                "Camera settings screen should be displayed");
    }

    public void verifyCameraPreviewIconDisplayed() {
        WaitUtils.assertVisible(driver, CameraSettingsLocators.PREVIEW_ICON,
                "Camera preview icon should be visible in the top right corner of the Camera settings screen");
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
}
