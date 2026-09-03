package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.WaitUtils;

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
}
