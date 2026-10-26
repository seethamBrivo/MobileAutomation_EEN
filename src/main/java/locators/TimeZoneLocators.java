package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the time zone picker screen (popup title, search field and results list).
 */
public final class TimeZoneLocators {

    private TimeZoneLocators() {}

    // --- Popup -----------------------------------------------------------------------

    public static final By TIMEZONE_POPUP = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']");

    // --- Search ----------------------------------------------------------------------

    // EditText in timezone picker - only one EditText exists on the timezone selection screen
    // Verified safe: search bar appears only after clicking timezone option, no other EditText present
    public static final By SEARCH_BAR = AppiumBy.xpath("//android.widget.EditText");
    public static final By SEARCH_PLACEHOLDER = AppiumBy.xpath("//android.widget.TextView[@text='Filter for time zones']");

    // --- Results ---------------------------------------------------------------------

    // First timezone result - scoped by timezone format pattern (contains '/')
    public static final By FIRST_TIMEZONE_IN_LIST = AppiumBy.xpath("(//android.widget.TextView[contains(@text, '/')])[1]");
    public static final By EMPTY_STATE_MESSAGE = AppiumBy.xpath("//android.widget.TextView[@text='No results found']");
}
