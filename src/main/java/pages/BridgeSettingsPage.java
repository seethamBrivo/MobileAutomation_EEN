package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.WaitUtils;

public class BridgeSettingsPage {

    private final AppiumDriver driver;

    // Bridge settings page elements
    private final By bridgeSettingsTitle = AppiumBy.xpath("//android.widget.TextView[@text='Bridge settings']");

    // Restart Bridge elements
    private final By restartBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");
    private final By restartConfirmationDialog = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restart') or contains(@text, 'reboot')]");
    private final By confirmRestartButton = AppiumBy.xpath("//android.widget.TextView[@text='RESTART']");
    private final By cancelRestartButton = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']");

    // Success/Error messages
    private final By bridgeRestartedMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restarted') or contains(@text, 'rebooted')]");

    // Bridge settings options
    private final By bridgeNameField = AppiumBy.xpath("//android.widget.EditText[@content-desc='bridge_name']");
    private final By bridgeNameOption = AppiumBy.xpath("//android.widget.TextView[@text='Name']");
    private final By bridgeNameEditField = AppiumBy.xpath("//android.widget.EditText");
    private final By bridgeIdField = AppiumBy.xpath("//android.widget.TextView[@content-desc='bridge_id']");
    private final By bridgeStatusIndicator = AppiumBy.xpath("//android.view.View[@content-desc='bridge_status']");
    private final By statusSection = AppiumBy.xpath("//android.widget.TextView[@text='Status']");
    private final By onlineStatus = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Online')]");
    private final By offlineStatus = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Offline')]");
    private final By savedSuccessfullyMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Saved Successfully')]");

    public BridgeSettingsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ========== VERIFICATION METHODS ==========

    public boolean isBridgeSettingsPageDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeSettingsTitle).isDisplayed();
    }

    // ========== RESTART BRIDGE METHODS ==========

    public void clickRestartBridgeButton() {
        WaitUtils.waitForVisibility(driver, restartBridgeButton).click();
    }

    public boolean isRestartBridgeConfirmationDisplayed() {
        return WaitUtils.waitForVisibility(driver, restartConfirmationDialog).isDisplayed();
    }

    public void confirmRestartBridge() {
        WaitUtils.waitForClickability(driver, confirmRestartButton).click();
    }

    public void cancelRestartBridge() {
        WaitUtils.waitForClickability(driver, cancelRestartButton).click();
    }

    public boolean isBridgeRestartedMessageDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, bridgeRestartedMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ========== BRIDGE INFO METHODS ==========

    public String getBridgeName() {
        return WaitUtils.waitForVisibility(driver, bridgeNameField).getText();
    }

    public String getBridgeId() {
        return WaitUtils.waitForVisibility(driver, bridgeIdField).getText();
    }

    public boolean isBridgeOnline() {
        try {
            String status = WaitUtils.waitForVisibility(driver, bridgeStatusIndicator).getAttribute("content-desc");
            return status != null && status.toLowerCase().contains("online");
        } catch (Exception e) {
            return false;
        }
    }

    // ========== BRIDGE STATUS METHODS ==========

    public boolean isStatusSectionDisplayed() {
        return WaitUtils.waitForVisibility(driver, statusSection).isDisplayed();
    }

    public boolean isOnlineStatusDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, onlineStatus).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOfflineStatusDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, offlineStatus).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getBridgeStatus() {
        if (isOnlineStatusDisplayed()) {
            return "Online";
        } else if (isOfflineStatusDisplayed()) {
            return "Offline";
        }
        return "Unknown";
    }

    // ========== BRIDGE NAME EDIT METHODS ==========

    public void clickNameOption() {
        WaitUtils.waitForClickability(driver, bridgeNameOption).click();
    }

    public void clearBridgeName() {
        var nameField = WaitUtils.waitForVisibility(driver, bridgeNameEditField);
        nameField.clear();
    }

    public void enterNewBridgeName(String newName) {
        var nameField = WaitUtils.waitForVisibility(driver, bridgeNameEditField);
        nameField.clear();
        nameField.sendKeys(newName);
    }

    public void clickDoneOnKeyboard() {
        // Press Enter/Done key on keyboard
        driver.executeScript("mobile: performEditorAction",
            java.util.Map.of("action", "done"));
    }

    public boolean isSavedSuccessfullyMessageDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, savedSuccessfullyMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}