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
    private final By profileDetailsHeading = AppiumBy.xpath("//android.widget.TextView[@text='Profile details']");
    private final By profileSection = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View");
    private final By timezoneOption = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']");
    private final By currentTimezoneValue = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']/following-sibling::android.widget.TextView[1]");
    private final By timeFormatOption = AppiumBy.xpath("//android.widget.TextView[@text='Time format']");
    private final By currentTimeFormatValue = AppiumBy.xpath("//android.widget.TextView[@text='Time format']/following-sibling::android.widget.TextView[1]");

    public MyProfilePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickProfileSection() {
        WaitUtils.click(driver, profileSection);
        WaitUtils.waitForVisibility(driver, profileDetailsHeading, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        logger.info("Profile section expanded");
    }

    public void scrollToTimezoneOption() {
        // Wait for profile screen to render before swiping to avoid exhausting scroll budget
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

        // Handle timezone aliases (Calcutta/Kolkata vary across Android versions)
        boolean matches = normalizedActual.contains(normalizedExpected) ||
                         (normalizedExpected.contains("calcutta") && normalizedActual.contains("kolkata")) ||
                         (normalizedExpected.contains("kolkata") && normalizedActual.contains("calcutta"));

        Assert.assertTrue(matches,
                "Timezone should contain '" + expectedTimezone + "' (or its alias) but was: " + actualTimezone);
    }

    public void scrollToTimeFormatOption() {
        if (!WaitUtils.isVisible(driver, timeFormatOption, TimeoutConstants.SHORT_TIMEOUT)) {
            boolean found = GestureUtils.scrollUntilVisible(driver, timeFormatOption);
            Assert.assertTrue(found, "Could not find time format option even after scrolling");
        } else {
            GestureUtils.swipeUp(driver); // Ensure not at screen edge
        }
    }

    public void clickTimeFormatOption() {
        WaitUtils.click(driver, timeFormatOption);
    }

    public String getCurrentTimeFormat() {
        String timeFormat = WaitUtils.getTextWithRetry(driver, currentTimeFormatValue, TimeoutConstants.SHORT_TIMEOUT);
        logger.info("Current time format: {}", timeFormat);
        return timeFormat;
    }
    public void selectTimeFormat(String format) {
        By formatOption = AppiumBy.xpath("//android.widget.TextView[@text='" + format + "']");
        WaitUtils.click(driver, formatOption);
        logger.info("Selected time format: {}", format);
    }

    public void verifyTimeFormatChanged(String expectedFormat) {
        String actualFormat = getCurrentTimeFormat();
        Assert.assertEquals(actualFormat, expectedFormat,
                "Time format should be '" + expectedFormat + "' but was: " + actualFormat);
    }
}
