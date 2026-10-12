package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.WaitUtils;

public class TimeZonePage {

    private static final Logger logger = LogManager.getLogger(TimeZonePage.class);
    private final AppiumDriver driver;

    private final By timezonePopup = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']");
    // EditText in timezone picker - only one EditText exists on the timezone selection screen
    // Verified safe: search bar appears only after clicking timezone option, no other EditText present
    private final By searchBar = AppiumBy.xpath("//android.widget.EditText");
    private final By searchPlaceholder = AppiumBy.xpath("//android.widget.TextView[@text='Filter for time zones']");
    // First timezone result - scoped by timezone format pattern (contains '/')
    private final By firstTimezoneInList = AppiumBy.xpath("(//android.widget.TextView[contains(@text, '/')])[1]");
    private final By emptyStateMessage = AppiumBy.xpath("//android.widget.TextView[@text='No results found']");

    public TimeZonePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyTimezonePopupDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, timezonePopup).isDisplayed(),
                "Time zone popup should be displayed");
    }

    public void verifySearchPlaceholderDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, searchPlaceholder).isDisplayed(),
                "Search placeholder 'Filter for time zones' should be displayed");
    }

    public void clickSearchBar() {
        WaitUtils.click(driver, searchBar);
    }

    public void enterSearchTerm(String searchTerm) {
        WaitUtils.clearAndType(driver, searchBar, searchTerm);
        hideKeyboard();
    }

    private void hideKeyboard() {
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.debug("Keyboard already hidden or not present: {}", e.getMessage());
        }
    }

    public void verifyTimezoneListAdjustedToInput(String searchTerm) {
        WebElement firstTimezone = WaitUtils.waitForVisibility(driver, firstTimezoneInList, TimeoutConstants.FILTER_RESULT_TIMEOUT);
        String timezoneText = firstTimezone.getText();
        Assert.assertTrue(timezoneText.toLowerCase().contains(searchTerm.toLowerCase()),
                "Filtered timezone should contain '" + searchTerm + "' but was: " + timezoneText);
    }

    public void verifyEmptyStateDisplayed() {
        // The keyboard can reappear after the list re-filters and cover the empty-state message.
        hideKeyboard();
        Assert.assertTrue(WaitUtils.isVisible(driver, emptyStateMessage, TimeoutConstants.FILTER_RESULT_TIMEOUT),
                "Empty state message 'No results found' should be displayed");
    }

    public void clearSearchField() {
        WaitUtils.clearAndType(driver, searchBar, "");
        hideKeyboard();
        Assert.assertTrue(WaitUtils.isVisible(driver, firstTimezoneInList, TimeoutConstants.SHORT_TIMEOUT),
                "Timezone list should be restored after clearing search field");
    }

    public void selectFirstTimezone() {
        WebElement firstTimezone = WaitUtils.waitForVisibility(driver, firstTimezoneInList);
        String timezoneText = firstTimezone.getText();
        WaitUtils.click(driver, firstTimezoneInList);
        logger.info("Selected first timezone: {}", timezoneText);
    }

    /**
     * Select first timezone and verify it matches the expected search term.
     *
     * @param expectedSearchTerm The search term that should appear in the selected timezone
     */
    public void selectAndVerifyFirstTimezone(String expectedSearchTerm) {
        WebElement firstTimezone = WaitUtils.waitForVisibility(driver, firstTimezoneInList);
        String timezoneText = firstTimezone.getText();

        Assert.assertTrue(timezoneText.toLowerCase().contains(expectedSearchTerm.toLowerCase()),
                "First timezone should contain '" + expectedSearchTerm + "' but was: " + timezoneText);

        WaitUtils.click(driver, firstTimezoneInList);
        logger.info("Selected and verified timezone: {}", timezoneText);
    }
}
