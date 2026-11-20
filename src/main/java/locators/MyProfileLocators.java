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

    public static final By TIME_FORMAT_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Time format']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Time format'")); // ios

    public static final By CURRENT_TIME_FORMAT_VALUE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Time format']/following-sibling::android.widget.TextView[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Time format']/following-sibling::XCUIElementTypeStaticText[1]")); // ios

    public static By timeFormatOption(String format) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.TextView[@text='" + format + "']"), // android
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '" + format + "'")); // ios
    }

    // --- Pull-to-refresh indicators (Android only) ----------------------------------

    public static final By LOADING_INDICATOR = AppiumBy.className("android.widget.ProgressBar");

    // --- Profile Details Option (Android only) --------------------------------------

    public static final By PROFILE_DETAILS_OPTION = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.view.View/android.view.View");

    // --- Login Email Change (Android only) ------------------------------------------

    public static final By LOGIN_EMAIL_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Login email']");

    public static final By VERIFY_PASSWORD_INPUT = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.widget.EditText");

    public static final By NEW_EMAIL_INPUT = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");

    public static final By NEXT_BUTTON = AppiumBy.xpath("//android.widget.TextView[@text='Next']");

    public static final By NEXT_BUTTON_CLICKABLE = AppiumBy.xpath("//android.widget.TextView[@text='Next']/..");

    public static final By PASSWORD_ERROR_MESSAGE = AppiumBy.xpath("//android.widget.TextView[@text='Password is not correct.']");

    public static final By EMAIL_VALIDATION_ERROR_MESSAGE = AppiumBy.xpath("//android.widget.TextView[@text='Enter a valid email address']");

    // --- Confirmation Screen (Android only - EPD-TC-1327) ---------------------------

    public static final By AUTH_CODE_INPUT = AppiumBy.xpath("//android.widget.EditText");

    public static final By CONFIRM_BUTTON = AppiumBy.xpath("//android.widget.TextView[@text='Confirm']");

    public static final By AUTH_CODE_ERROR_MESSAGE = AppiumBy.xpath("//android.widget.TextView[@text='Security code is not correct.']");

    // --- Contact us / Call Support (Android only - EPD-TC-1329) ---------------------

    public static final By SUPPORT_PIN_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Support PIN']");

    public static final By CALL_SUPPORT_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Call Support']");

    public static final By PHONE_PERMISSION_ICON = AppiumBy.xpath("//android.widget.ImageView[@resource-id='com.android.permissioncontroller:id/permission_icon']");
}
