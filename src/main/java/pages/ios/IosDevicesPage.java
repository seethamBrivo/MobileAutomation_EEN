package pages.ios;

import io.appium.java_client.AppiumDriver;
import locators.ios.IosDevicesLocators;
import org.testng.Assert;
import utils.WaitUtils;

/** iOS counterpart of the navigation in {@code pages.DevicesPage}: More menu and the Dashboard ("All devices"). */
public class IosDevicesPage {

    private final AppiumDriver driver;

    public IosDevicesPage(AppiumDriver driver) {
        this.driver = driver;
    }

    /** More tab, then Dashboard. */
    public void navigateToDashboard() {
        WaitUtils.click(driver, IosDevicesLocators.MORE_TAB);
        WaitUtils.click(driver, IosDevicesLocators.DASHBOARD_BUTTON);
    }

    public void verifyAllDevicesSectionIsDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, IosDevicesLocators.ALL_DEVICES_TITLE).isDisplayed(),
                "All devices section should be displayed");
    }
}
