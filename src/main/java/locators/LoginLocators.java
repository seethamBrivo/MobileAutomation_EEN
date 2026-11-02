package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the sign-in flow: the app's sign-in entry point, the web identity-provider pages
 * (email, password), the biometrics prompt shown after sign-in and the signed-in landing marker.
 */
public final class LoginLocators {

    private LoginLocators() {}

    // --- Sign-in entry and identity provider pages ------------------------------

    public static final By SIGN_IN_BUTTON = PlatformLocator.get(
            AppiumBy.accessibilityId("sign_in_btn_button"), // android
            AppiumBy.accessibilityId("sign_in_button")); // ios

    public static final By EMAIL_INPUT_FIELD = PlatformLocator.get(
            AppiumBy.className("android.widget.EditText"), // android
            // The iOS web field has no name/label. Its value is the "Email" placeholder until typed, then the address.
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' AND (value == 'Email' OR value CONTAINS '@')")); // ios

    public static final By NEXT_BUTTON = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.Button[@text=\"Next\"]"), // android
            // iOS: scoped to the web page, because the keyboard also has a "Next" key.
            AppiumBy.xpath("//XCUIElementTypeWebView//XCUIElementTypeButton[@name='Next']")); // ios

    public static final By PASSWORD_FIELD = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.EditText[@resource-id=\"authentication--input__password\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeSecureTextField'")); // ios

    public static final By SIGN_IN_BUTTON_TO_LOGIN = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.Button[@resource-id=\"next\"]"), // android
            // iOS: the welcome screen's native button is named "sign_in_button", so this only matches the web page's button.
            AppiumBy.xpath("//XCUIElementTypeButton[@name=\"Sign in\"]")); // ios

    // --- Biometrics prompt ------------------------------------------------------

    // NOTE: negative_button is intentionally excluded - it's used as confirm delete in UsersPage
    public static final By NO_THANKS_OPTION_IN_BIOMETRICS_AUTHENTICATION_POP_UP =
            AppiumBy.id("com.een.vms.android:id/positive_button");

    public static final By NO_THANKS_BUTTON_BY_TEXT =
            AppiumBy.xpath("//android.widget.Button[contains(@text, 'NO THANKS') or contains(@text, 'No Thanks') or contains(@text, 'No thanks')]");

    public static final By BIOMETRICS_NEGATIVE_BUTTON =
            AppiumBy.id("android:id/button2");

    // --- Signed-in landing and verification ------------------------------------

    /** Signed in: the bottom navigation's More tab is showing (it is never on the sign-in screen). */
    public static final By SIGNED_IN_MARKER = BottomNavigationLocators.MORE_TAB;

    /**
     * The identity provider can answer a correct password with "Account verification - we need to verify
     * you with a Security Code" (sent by email). A test cannot finish that step, so it is recognised and
     * reported as such instead of being waited out as a missing landing screen.
     */
    public static final By VERIFICATION_PROMPT = PlatformLocator.get(
            AppiumBy.xpath("//*[contains(@text,'Account verification') or contains(@text,'Security Code')]"), // android
            // Not seen on iOS yet: written from the Android text, so it only matters if an account is asked to verify.
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText'"
                            + " AND (label CONTAINS 'Account verification' OR label CONTAINS 'Security Code')")); // ios
}
