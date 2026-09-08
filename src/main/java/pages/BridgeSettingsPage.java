package pages;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;
public class BridgeSettingsPage {
    private final AppiumDriver driver;
    private final By bridgeSettingsTitle = AppiumBy.xpath("//android.widget.TextView[@text='Bridge settings']");
    private final By restartBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");
    private final By restartConfirmationDialog = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restart') or contains(@text, 'reboot')]");
    private final By confirmRestartButton = AppiumBy.xpath("//android.widget.TextView[@text='RESTART']");
    private final By cancelRestartButton = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']");
    private final By bridgeRestartedMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restarted') or contains(@text, 'rebooted')]");
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

    public boolean isBridgeSettingsPageDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeSettingsTitle).isDisplayed();
    }
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
    public void clickNameOption() {
        WaitUtils.waitForClickability(driver, bridgeNameOption).click();
    }
    public String getCurrentBridgeNameFromEditField() {
        return WaitUtils.waitForVisibility(driver, bridgeNameEditField).getText();
    }
    public void clearBridgeName() {
        var nameField = WaitUtils.waitForVisibility(driver, bridgeNameEditField);
        nameField.clear();
    }
    public void enterNewBridgeName(String newName) {
        var nameField = WaitUtils.waitForVisibility(driver, bridgeNameEditField);
        nameField.click(); // Click to focus the field first
        nameField.clear();
        nameField.sendKeys(newName);
    }
    public void clickDoneOnKeyboard() {
        driver.executeScript("mobile: performEditorAction",
            java.util.Map.of("action", "done"));
    }
    public void clickTickButtonOnKeyboard() {
        By tickButton = AppiumBy.xpath("//android.widget.FrameLayout[@content-desc='Done']");
        WaitUtils.waitForClickability(driver, tickButton).click();
    }
    public boolean isSavedSuccessfullyMessageDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, savedSuccessfullyMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public void verifyBridgeSettingsPageDisplayed() {
        Assert.assertTrue(isBridgeSettingsPageDisplayed(), "Bridge settings page should be displayed");
    }
    public void verifyRestartBridgeConfirmationDisplayed() {
        Assert.assertTrue(isRestartBridgeConfirmationDisplayed(), "Restart bridge confirmation should be displayed");
    }
    public void verifyBridgeRestartedMessageDisplayed() {
        Assert.assertTrue(isBridgeRestartedMessageDisplayed(), "Bridge restarted message should be displayed");
    }
    public void verifyStatusSectionDisplayed() {
        Assert.assertTrue(isStatusSectionDisplayed(), "Status section should be displayed");
    }
    public void verifyOnlineStatusDisplayed() {
        Assert.assertTrue(isOnlineStatusDisplayed(), "Online status should be displayed");
    }
    public void verifyOfflineStatusDisplayed() {
        Assert.assertTrue(isOfflineStatusDisplayed(), "Offline status should be displayed");
    }
    public void verifySavedSuccessfullyMessageDisplayed() {
        Assert.assertTrue(isSavedSuccessfullyMessageDisplayed(), "Saved successfully message should be displayed");
    }
}