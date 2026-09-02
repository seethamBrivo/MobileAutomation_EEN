package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.WaitUtils;

public class DeleteBridgePage {

    private final AppiumDriver driver;

    // Navigation elements
    private final By moreMenuOption = AppiumBy.xpath("//android.widget.TextView[@text='More']");
    private final By dashboardOption = AppiumBy.xpath("//android.widget.TextView[@text='Dashboard']");
    private final By allDevicesSection = AppiumBy.xpath("//android.widget.TextView[@text='All devices']");

    // Bridge list elements
    private final By bridgesTab = AppiumBy.xpath("//android.widget.TextView[@text='Bridges']");
    private final By bridgesScreenTitle = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']");
    private final By firstBridgeInList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]");
    private final By bridgeThreeDotsIcon = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.view.View[contains(@content-desc, 'icon')]");

    // Bridge settings page elements
    private final By deleteBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Delete bridge']");
    private final By bridgeSettingsTitle = AppiumBy.xpath("//android.widget.TextView[@text='Bridge settings']");

    // Filter elements
    private final By filterSearchField = AppiumBy.xpath("//android.widget.EditText");

    // Delete confirmation dialog elements
    private final By deleteConfirmationDialog = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Are you sure you want to remove this bridge')]");
    private final By deleteButton = AppiumBy.xpath("//android.widget.TextView[@text='DELETE']");
    private final By cancelButton = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']");

    // Success message
    private final By bridgeDeletedMessage = AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/snackbar_text' and @text='Bridge was deleted']");

    // Bridge status indicators
    private final By greenDotStatus = AppiumBy.xpath("//android.view.View[@content-desc='online_status']");
    private final By redDotStatus = AppiumBy.xpath("//android.view.View[@content-desc='offline_status']");
    private final By yellowDotStatus = AppiumBy.xpath("//android.view.View[@content-desc='connectivity_issue_status']");

    public DeleteBridgePage(AppiumDriver driver) {
        this.driver = driver;
    }

    // Navigation methods
    public void clickMoreMenuOption() {
        WaitUtils.waitForVisibility(driver, moreMenuOption).click();
    }

    public void clickDashboardOption() {
        WaitUtils.waitForVisibility(driver, dashboardOption).click();
    }

    public boolean isAllDevicesSectionDisplayed() {
        return WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed();
    }

    public void clickBridgesTab() {
        WaitUtils.waitForVisibility(driver, bridgesTab).click();
    }

    // Filter methods
    public void enterBridgeNameInFilter(String bridgeName) {
        var filterField = WaitUtils.waitForVisibility(driver, filterSearchField);
        filterField.clear();
        filterField.sendKeys(bridgeName);
    }

    // Bridge selection methods
    public boolean isBridgeListDisplayed() {
        return WaitUtils.waitForVisibility(driver, firstBridgeInList).isDisplayed();
    }

    public void clickFirstBridge() {
        WaitUtils.waitForVisibility(driver, firstBridgeInList).click();
    }

    public void clickFirstBridgeThreeDotsIcon() {
        WaitUtils.waitForVisibility(driver, bridgeThreeDotsIcon).click();
    }

    public boolean isBridgeThreeDotsIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeThreeDotsIcon).isDisplayed();
    }

    // Bridge settings page methods
    public boolean isBridgeSettingsPageDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeSettingsTitle).isDisplayed();
    }

    public void clickDeleteBridgeButton() {
        WaitUtils.waitForVisibility(driver, deleteBridgeButton).click();
    }

    public boolean isDeleteBridgeButtonDisplayed() {
        return WaitUtils.waitForVisibility(driver, deleteBridgeButton).isDisplayed();
    }

    // Delete confirmation dialog methods
    public boolean isDeleteConfirmationDialogDisplayed() {
        return WaitUtils.waitForVisibility(driver, deleteConfirmationDialog).isDisplayed();
    }

    public void clickDeleteButton() {
        // Wait for confirmation popup to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WaitUtils.waitForVisibility(driver, deleteButton).click();

        // Wait for delete to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickCancelButton() {
        WaitUtils.waitForVisibility(driver, cancelButton).click();
    }

    // Verification methods
    public boolean isBridgeDeletedMessageDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, bridgeDeletedMessage).isDisplayed();
        } catch (Exception e) {
            // Success message may not appear on demo accounts or disappear quickly
            return false;
        }
    }

    public boolean isUserNavigatedBackToBridgesTab() {
        return WaitUtils.waitForVisibility(driver, bridgesTab).isDisplayed();
    }

    public boolean areStatusIndicatorsDisplayed() {
        // Use findElements to avoid slow chained waits (30s on false)
        return !driver.findElements(greenDotStatus).isEmpty() ||
               !driver.findElements(redDotStatus).isEmpty() ||
               !driver.findElements(yellowDotStatus).isEmpty();
    }
}
