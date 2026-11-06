package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the My Profile screen (profile section, time zone and time format options).
 */
public final class MyProfileLocators {

    private MyProfileLocators() {}

    // --- Profile section -------------------------------------------------------------

    public static final By PROFILE_DETAILS_HEADING = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Profile details']"), // android
            AppiumBy.accessibilityId("Profile details")); // ios

    // Stable: Profile section is always first [1] child in ScrollView on My Profile screen
    public static final By PROFILE_SECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View"), // android
            AppiumBy.accessibilityId("myprofile_button")); // ios: the profile row of the More screen, it opens Profile

    /** iOS only: the back button of the Profile screen (Android goes back with the system back). */
    public static final By BACK_BUTTON = AppiumBy.accessibilityId("BackButton"); // ios

    // --- Time zone -------------------------------------------------------------------

    public static final By TIMEZONE_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Time zone']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Time zone'")); // ios
    public static final By CURRENT_TIMEZONE_VALUE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Time zone']/following-sibling::android.widget.TextView[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Time zone']/following-sibling::XCUIElementTypeStaticText[1]")); // ios

    // --- Time format -----------------------------------------------------------------

    public static final By TIME_FORMAT_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Time format']");
    public static final By CURRENT_TIME_FORMAT_VALUE = AppiumBy.xpath("//android.widget.TextView[@text='Time format']/following-sibling::android.widget.TextView[1]");

    public static By timeFormatOption(String format) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + format + "']");
    }
}
