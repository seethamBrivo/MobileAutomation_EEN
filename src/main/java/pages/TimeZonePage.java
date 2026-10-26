package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import locators.TimeZoneLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.WaitUtils;

public class TimeZonePage {

    private static final Logger logger = LogManager.getLogger(TimeZonePage.class);
    private final AppiumDriver driver;


    public TimeZonePage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ---- step-level groups (EEPD-TC-3920) -------------------------------------------------

    /** The Time zone popup is open and its search field shows the placeholder. */
    public void verifyPopupAndSearchPlaceholder() {
        verifyTimezonePopupDisplayed();
        verifySearchPlaceholderDisplayed();
    }

    /** Step "Tap on search bar and start typing": results follow the term; a no-match term shows the empty state. */
    public void searchTimezonesAndVerify(String searchTerm, String invalidSearchTerm) {
        clickSearchBar();
        enterSearchTerm(searchTerm);
        verifyTimezoneListAdjustedToInput(searchTerm);
        enterSearchTerm(invalidSearchTerm);
        verifyEmptyStateDisplayed();
        clearSearchField();
    }

    /** Step "Set the time zone by tapping on it". */
    public void selectTimezone(String timezone) {
        enterSearchTerm(timezone);
        selectAndVerifyFirstTimezone(timezone);
    }

    public void verifyTimezonePopupDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, TimeZoneLocators.TIMEZONE_POPUP).isDisplayed(),
                "Time zone popup should be displayed");
    }

    public void verifySearchPlaceholderDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, TimeZoneLocators.SEARCH_PLACEHOLDER).isDisplayed(),
                "Search placeholder 'Filter for time zones' should be displayed");
    }

    public void clickSearchBar() {
        WaitUtils.click(driver, TimeZoneLocators.SEARCH_BAR);
    }

    public void enterSearchTerm(String searchTerm) {
        WaitUtils.clearAndType(driver, TimeZoneLocators.SEARCH_BAR, searchTerm);
        hideKeyboard();
    }

    /**
     * Dismisses the soft keyboard. {@code mobile: hideKeyboard} is a silent no-op on this Compose
     * search field (the keyboard stayed up and covered the empty-state message in BrowserStack
     * build #9), so fall back to BACK - which only closes the IME while it is showing.
     */
    private void hideKeyboard() {
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.debug("mobile: hideKeyboard failed: {}", e.getMessage());
        }
        try {
            if (driver instanceof AndroidDriver androidDriver && androidDriver.isKeyboardShown()) {
                androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
            }
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.debug("Could not dismiss keyboard with BACK: {}", e.getMessage());
        }
    }

    public void verifyTimezoneListAdjustedToInput(String searchTerm) {
        WebElement firstTimezone = WaitUtils.waitForVisibility(driver, TimeZoneLocators.FIRST_TIMEZONE_IN_LIST, TimeoutConstants.FILTER_RESULT_TIMEOUT);
        String timezoneText = firstTimezone.getText();
        Assert.assertTrue(timezoneText.toLowerCase().contains(searchTerm.toLowerCase()),
                "Filtered timezone should contain '" + searchTerm + "' but was: " + timezoneText);
    }

    public void verifyEmptyStateDisplayed() {
        // The keyboard can reappear after the list re-filters and cover the empty-state message.
        hideKeyboard();
        // Fall back to presence: with the IME resizing the window the message can sit below the
        // visible area even though it is rendered.
        boolean shown = WaitUtils.isVisible(driver, TimeZoneLocators.EMPTY_STATE_MESSAGE, TimeoutConstants.FILTER_RESULT_TIMEOUT)
                || WaitUtils.isPresent(driver, TimeZoneLocators.EMPTY_STATE_MESSAGE);
        Assert.assertTrue(shown, "Empty state message 'No results found' should be displayed");
    }

    public void clearSearchField() {
        WaitUtils.clearAndType(driver, TimeZoneLocators.SEARCH_BAR, "");
        hideKeyboard();
        Assert.assertTrue(WaitUtils.isVisible(driver, TimeZoneLocators.FIRST_TIMEZONE_IN_LIST, TimeoutConstants.SHORT_TIMEOUT),
                "Timezone list should be restored after clearing search field");
    }

    /**
     * Select first timezone and verify it matches the expected search term.
     *
     * @param expectedSearchTerm The search term that should appear in the selected timezone
     */
    public void selectAndVerifyFirstTimezone(String expectedSearchTerm) {
        WebElement firstTimezone = WaitUtils.waitForVisibility(driver, TimeZoneLocators.FIRST_TIMEZONE_IN_LIST);
        String timezoneText = firstTimezone.getText();

        Assert.assertTrue(timezoneText.toLowerCase().contains(expectedSearchTerm.toLowerCase()),
                "First timezone should contain '" + expectedSearchTerm + "' but was: " + timezoneText);

        WaitUtils.click(driver, TimeZoneLocators.FIRST_TIMEZONE_IN_LIST);
        logger.info("Selected and verified timezone: {}", timezoneText);
    }
}
