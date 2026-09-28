package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

public class VSPEventsPage {

    private static final Logger logger = LogManager.getLogger(VSPEventsPage.class);
    private final AppiumDriver driver;

    // Stable: ProfileMain back button at fixed position [2] in profile screen
    private final By backButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ProfileMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View");
    private final By vspSection = AppiumBy.xpath("//android.widget.TextView[@text='VSP']");
    private final By eventsOption = AppiumBy.xpath("//android.widget.TextView[@text='Events']");
    private final By vspEventsHeading = AppiumBy.xpath("//android.widget.TextView[@text='VSP Events']");
    // Scoped to VspMain screen - timestamp pattern specific to event list (Today, HH:MM format)
    private final By timestampWithTime = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]//android.widget.TextView[contains(@text, 'Today,') and contains(@text, ':')]");
    // Stable: First event card at position [4][1] in VspMain event list
    private final By firstEventInList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]/android.view.View");
    private final By profileTimezone = AppiumBy.xpath("//android.widget.TextView[@text='Profile timezone']");
    private final By profileTimezoneValue = AppiumBy.xpath("//android.widget.TextView[@text='Profile timezone']/following-sibling::android.widget.TextView[1]");
    // Stable: Close button at fixed position [2] in event details modal
    private final By closeEventDetailsButton = AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View");

    public VSPEventsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateBackFromProfile() {
        WaitUtils.click(driver, backButton);
    }

    public void verifyVSPSectionVisible() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, vspSection).isDisplayed(),
                "VSP section should be visible");
    }

    public void clickEvents() {
        WaitUtils.click(driver, eventsOption);
    }

    public void verifyVSPEventsScreenDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, vspEventsHeading).isDisplayed(),
                "VSP Events screen heading should be displayed");
    }

    public void verifyTimezoneAppliedInVSPEvents() {
        boolean timestampVisible = WaitUtils.isVisible(driver, timestampWithTime, TimeoutConstants.FILTER_RESULT_TIMEOUT);
        Assert.assertTrue(timestampVisible,
                "Timestamps should be displayed in VSP Events with selected timezone");

        String timestampText = WaitUtils.waitForVisibility(driver, timestampWithTime).getText();
        logger.info("VSP Event timestamp: {}", timestampText);

        Assert.assertTrue(timestampText.matches("Today,\\s*\\d{1,2}:\\d{2}"),
                "Timestamp should be in format 'Today, HH:MM' reflecting selected timezone, but was: " + timestampText);

        WaitUtils.click(driver, firstEventInList);

        Assert.assertTrue(WaitUtils.waitForVisibility(driver, profileTimezone).isDisplayed(),
                "Profile timezone label should be displayed in event details");

        String timezoneValue = WaitUtils.waitForVisibility(driver, profileTimezoneValue).getText();
        logger.info("Event profile timezone: {}", timezoneValue);

        Assert.assertTrue(timezoneValue.contains("UTC") || timezoneValue.contains(":"),
                "Profile timezone should display UTC offset or time, but was: " + timezoneValue);

        WaitUtils.click(driver, closeEventDetailsButton);
    }
}
