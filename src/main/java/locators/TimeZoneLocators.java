package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the time zone picker screen (popup title, search field and results list).
 */
public final class TimeZoneLocators {

    private TimeZoneLocators() {}

    // --- Popup -----------------------------------------------------------------------

    public static final By TIMEZONE_POPUP = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Time zone']"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar[`name == 'Time zone'`]")); // ios

    // --- Search ----------------------------------------------------------------------

    // EditText in timezone picker - only one EditText exists on the timezone selection screen
    // Verified safe: search bar appears only after clicking timezone option, no other EditText present
    public static final By SEARCH_BAR = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.EditText"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeSearchField'")); // ios
    // iOS: the search field itself, whose placeholder is its name
    public static final By SEARCH_PLACEHOLDER = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Filter for time zones']"), // android
            AppiumBy.accessibilityId("Filter for time zones")); // ios

    // --- Results ---------------------------------------------------------------------

    // First timezone result - scoped by timezone format pattern (contains '/')
    public static final By FIRST_TIMEZONE_IN_LIST = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.TextView[contains(@text, '/')])[1]"), // android
            AppiumBy.xpath("(//XCUIElementTypeStaticText[contains(@name, '/')])[1]")); // ios
    public static final By EMPTY_STATE_MESSAGE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='No results found']"), // android
            AppiumBy.accessibilityId("No results found")); // ios
}
