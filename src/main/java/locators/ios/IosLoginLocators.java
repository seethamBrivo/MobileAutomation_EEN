package locators.ios;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * iOS locators for the sign-in flow: the app's sign-in entry point and the web identity-provider pages
 * (email, password). The signed-in marker is {@link IosDevicesLocators#MORE_TAB}.
 */
public final class IosLoginLocators {

    private IosLoginLocators() {}

    public static final By SIGN_IN_BUTTON = AppiumBy.accessibilityId("sign_in_button");

    // The web field has no name/label. Its value is the "Email" placeholder until typed, then the address.
    public static final By EMAIL_INPUT_FIELD = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeTextField' AND (value == 'Email' OR value CONTAINS '@')");

    // Scoped to the web page: the keyboard also has a "Next" key.
    public static final By NEXT_BUTTON = AppiumBy.xpath("//XCUIElementTypeWebView//XCUIElementTypeButton[@name='Next']");

    public static final By PASSWORD_FIELD = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeSecureTextField'");

    // The welcome screen's native button is named "sign_in_button", so this only matches the web page's button.
    public static final By SIGN_IN_BUTTON_TO_LOGIN = AppiumBy.xpath("//XCUIElementTypeButton[@name=\"Sign in\"]");
}
