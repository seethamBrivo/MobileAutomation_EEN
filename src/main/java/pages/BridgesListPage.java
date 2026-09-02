package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.WaitUtils;

import java.util.List;

public class BridgesListPage {

    private final AppiumDriver driver;

    // Navigation
    private final By moreMenuOption = AppiumBy.xpath("//android.widget.TextView[@text=\"More\"]");
    private final By dashboardOption = AppiumBy.xpath("//android.widget.TextView[@text=\"Dashboard\"]");

    // Dashboard sections
    private final By allDevicesSection = AppiumBy.xpath("//android.widget.TextView[@text=\"All devices\"]");
    private final By bridgesOption = AppiumBy.xpath("//android.widget.TextView[@text=\"Bridges\"]");
    private final By camerasOption = AppiumBy.xpath("//android.widget.TextView[@text=\"Cameras\"]");
    private final By speakersOption = AppiumBy.xpath("//android.widget.TextView[@text=\"Speakers\"]");
    private final By switchesOption = AppiumBy.xpath("//android.widget.TextView[@text=\"Switches\"]");

    // Bridge list elements
    private final By bridgesList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']");
    private final By bridgeItems = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View");
    private final By bridgeCounter = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'bridge')]");

    // Bridge item details
    private final By bridgeIcon = AppiumBy.xpath("//android.widget.ImageView[@content-desc='bridge_icon']");
    private final By bridgeName = AppiumBy.xpath("//android.widget.TextView[@content-desc='bridge_name']");
    private final By bridgeStatus = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline')]");
    private final By onlineStatus = AppiumBy.xpath("//android.widget.TextView[@text='Online']");
    private final By offlineStatus = AppiumBy.xpath("//android.widget.TextView[@text='Offline']");

    // Camera status counters
    private final By cameraStatusCounter = AppiumBy.xpath("//android.widget.TextView[contains(@content-desc, 'camera_status')]");

    // 3-dots menu
    private final By threeDotMenuButton = AppiumBy.xpath("//android.widget.ImageView[@content-desc='more_options']");
    private final By bridgeSettingsOption = AppiumBy.xpath("//android.widget.TextView[@text='Bridge settings']");
    private final By bridgeInformationOption = AppiumBy.xpath("//android.widget.TextView[@text='Bridge information']");
    private final By camerasMenuOption = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    private final By turnOnAllCamerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Turn on all cameras']");
    private final By turnOffAllCamerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Turn off all cameras']");

    // Loading indicator
    private final By loadingIndicator = AppiumBy.xpath("//android.widget.ProgressBar");

    public BridgesListPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickMoreMenuOption() {
        WaitUtils.waitForVisibility(driver, moreMenuOption).click();
    }

    public void clickDashboardOption() {
        WaitUtils.waitForVisibility(driver, dashboardOption).click();
    }

    public boolean isAllDevicesSectionDisplayed() {
        return WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed();
    }

    public boolean isBridgesOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgesOption).isDisplayed();
    }

    public boolean isCamerasOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, camerasOption).isDisplayed();
    }

    public boolean isSpeakersOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, speakersOption).isDisplayed();
    }

    public boolean isSwitchesOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, switchesOption).isDisplayed();
    }

    public void clickBridgesOption() {
        WaitUtils.waitForVisibility(driver, bridgesOption).click();
    }

    public boolean isBridgesListDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgesList).isDisplayed();
    }

    public List<WebElement> getBridgeItems() {
        return driver.findElements(bridgeItems);
    }

    public int getBridgeCount() {
        return getBridgeItems().size();
    }

    public String getBridgeCounterText() {
        return WaitUtils.waitForVisibility(driver, bridgeCounter).getText();
    }

    public boolean isBridgeIconDisplayed() {
        // Use findElements to avoid slow wait on false
        return !driver.findElements(bridgeIcon).isEmpty();
    }

    public boolean isBridgeNameDisplayed() {
        // Use findElements to avoid slow wait on false
        return !driver.findElements(bridgeName).isEmpty();
    }

    public boolean isBridgeStatusDisplayed() {
        // Use findElements to avoid slow wait on false
        return !driver.findElements(bridgeStatus).isEmpty();
    }

    public boolean isOnlineStatusDisplayed() {
        return !driver.findElements(onlineStatus).isEmpty();
    }

    public boolean isOfflineStatusDisplayed() {
        return !driver.findElements(offlineStatus).isEmpty();
    }

    public boolean isLoadingIndicatorDisplayed() {
        return !driver.findElements(loadingIndicator).isEmpty();
    }

    public void clickThreeDotMenu() {
        WaitUtils.waitForVisibility(driver, threeDotMenuButton).click();
    }

    public boolean isBridgeSettingsOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeSettingsOption).isDisplayed();
    }

    public boolean isBridgeInformationOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeInformationOption).isDisplayed();
    }

    public boolean isCamerasMenuOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, camerasMenuOption).isDisplayed();
    }

    public boolean isTurnOnAllCamerasOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, turnOnAllCamerasOption).isDisplayed();
    }

    public boolean isTurnOffAllCamerasOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, turnOffAllCamerasOption).isDisplayed();
    }
}