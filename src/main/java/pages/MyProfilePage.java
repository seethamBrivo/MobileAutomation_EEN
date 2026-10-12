package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

public class MyProfilePage {

    private static final Logger logger = LogManager.getLogger(MyProfilePage.class);
    private final AppiumDriver driver;

    // Stable: Profile section is always first [1] child in ScrollView on My Profile screen
    private final By profileSection = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View");
    private final By timezoneOption = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']");
    private final By currentTimezoneValue = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']/following-sibling::android.widget.TextView[1]");

    public MyProfilePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickProfileSection() {
        WaitUtils.click(driver, profileSection);
    }

    public void scrollToTimezoneOption() {
        // The profile screen renders after a load; swiping before it is up exhausts the scroll
        // budget (BrowserStack build #11, where Time zone was on screen right after the failure).
        if (!WaitUtils.isVisible(driver, timezoneOption, TimeoutConstants.DETAILS_LOAD_TIMEOUT)) {
            boolean found = GestureUtils.scrollUntilPresent(driver, timezoneOption);
            Assert.assertTrue(found, "Could not find timezone option even after scrolling");
        }
    }

    public void clickTimezoneOption() {
        WaitUtils.click(driver, timezoneOption);
    }

    public String getCurrentTimezone() {
        String timezone = WaitUtils.waitForVisibility(driver, currentTimezoneValue).getText();
        logger.info("Current timezone: {}", timezone);
        return timezone;
    }

    public void verifyTimezoneChanged(String expectedTimezone) {
        String actualTimezone = getCurrentTimezone();
        String normalizedActual = actualTimezone.toLowerCase();
        String normalizedExpected = expectedTimezone.toLowerCase();

        // Handle deprecated timezone aliases (Calcutta/Kolkata may differ across Android versions)
        boolean matches = normalizedActual.contains(normalizedExpected) ||
                         (normalizedExpected.contains("calcutta") && normalizedActual.contains("kolkata")) ||
                         (normalizedExpected.contains("kolkata") && normalizedActual.contains("calcutta"));

        Assert.assertTrue(matches,
                "Timezone should contain '" + expectedTimezone + "' (or its alias) but was: " + actualTimezone);
    }
}
