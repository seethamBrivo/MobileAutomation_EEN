package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.MyProfileLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

public class MyProfilePage {

    private static final Logger logger = LogManager.getLogger(MyProfilePage.class);
    private final AppiumDriver driver;

    public MyProfilePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickProfileSection() {
        WaitUtils.click(driver, MyProfileLocators.PROFILE_SECTION);
        WaitUtils.waitForVisibility(driver, MyProfileLocators.PROFILE_DETAILS_HEADING, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        logger.info("Profile section expanded");
    }

    /** Steps "Go to the TIME section and click the Time zone item". */
    public void openTimezonePicker() {
        clickProfileSection();
        scrollToTimezoneOption();
        clickTimezoneOption();
    }

    public void scrollToTimezoneOption() {
        // Wait for profile screen to render before swiping to avoid exhausting scroll budget
        if (!WaitUtils.isVisible(driver, MyProfileLocators.TIMEZONE_OPTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT)) {
            // iOS keeps the rows below the screen in the page, so it scrolls until the row is really visible.
            boolean found = PlatformLocator.isIos()
                    ? GestureUtils.scrollUntilVisible(driver, MyProfileLocators.TIMEZONE_OPTION)
                    : GestureUtils.scrollUntilPresent(driver, MyProfileLocators.TIMEZONE_OPTION);
            Assert.assertTrue(found, "Could not find timezone option even after scrolling");
        }
    }

    public void clickTimezoneOption() {
        WaitUtils.click(driver, MyProfileLocators.TIMEZONE_OPTION);
    }

    public String getCurrentTimezone() {
        String timezone = WaitUtils.waitForVisibility(driver, MyProfileLocators.CURRENT_TIMEZONE_VALUE).getText();
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
        // SAFETY FIX: Removed blind swipeUp when visible - it was unnecessary and cost 3s per probe
        if (!WaitUtils.isVisible(driver, MyProfileLocators.TIME_FORMAT_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            boolean found = GestureUtils.scrollUntilVisible(driver, MyProfileLocators.TIME_FORMAT_OPTION);
            Assert.assertTrue(found, "Could not find time format option even after scrolling");
        }
    }

    public void clickTimeFormatOption() {
        WaitUtils.click(driver, MyProfileLocators.TIME_FORMAT_OPTION);
    }

    public String getCurrentTimeFormat() {
        String timeFormat = WaitUtils.getTextWithRetry(driver, MyProfileLocators.CURRENT_TIME_FORMAT_VALUE, TimeoutConstants.SHORT_TIMEOUT);
        logger.info("Current time format: {}", timeFormat);
        return timeFormat;
    }

    public void selectTimeFormat(String format) {
        WaitUtils.click(driver, MyProfileLocators.timeFormatOption(format));
        logger.info("Selected time format: {}", format);
    }

    public void verifyTimeFormatChanged(String expectedFormat) {
        String actualFormat = getCurrentTimeFormat();
        Assert.assertEquals(actualFormat, expectedFormat,
                "Time format should be '" + expectedFormat + "' but was: " + actualFormat);
    }
}
