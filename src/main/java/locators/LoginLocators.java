package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the sign-in flow: the app's sign-in entry point, the web identity-provider pages
 * (email, password), the biometrics prompt shown after sign-in and the signed-in landing marker.
 */
public final class LoginLocators {

    private LoginLocators() {}

    // --- Sign-in entry and identity provider pages ------------------------------

    public static final By SIGN_IN_BUTTON =
            AppiumBy.accessibilityId("sign_in_btn_button");

    public static final By EMAIL_INPUT_FIELD =
            AppiumBy.className("android.widget.EditText");

    public static final By NEXT_BUTTON =
            AppiumBy.xpath("//android.widget.Button[@text=\"Next\"]");

    public static final By PASSWORD_FIELD =
            AppiumBy.xpath("//android.widget.EditText[@resource-id=\"authentication--input__password\"]");

    public static final By SIGN_IN_BUTTON_TO_LOGIN =
            AppiumBy.xpath("//android.widget.Button[@resource-id=\"next\"]");

    // --- Biometrics prompt ------------------------------------------------------

    // NOTE: negative_button is intentionally excluded - it's used as confirm delete in UsersPage
    public static final By NO_THANKS_OPTION_IN_BIOMETRICS_AUTHENTICATION_POP_UP =
            AppiumBy.id("com.een.vms.android:id/positive_button");

    public static final By NO_THANKS_BUTTON_BY_TEXT =
            AppiumBy.xpath("//android.widget.Button[contains(@text, 'NO THANKS') or contains(@text, 'No Thanks') or contains(@text, 'No thanks')]");

    public static final By BIOMETRICS_NEGATIVE_BUTTON =
            AppiumBy.id("android:id/button2");

    // --- Signed-in landing and verification ------------------------------------

    /** Bottom-navigation "More" tab: shown on every signed-in landing screen, never on the sign-in screen. */
    public static final By SIGNED_IN_MARKER =
            AppiumBy.xpath("//android.widget.TextView[@text='More']");

    /**
     * The identity provider can answer a correct password with "Account verification - we need to verify
     * you with a Security Code" (sent by email). A test cannot finish that step, so it is recognised and
     * reported as such instead of being waited out as a missing landing screen.
     */
    public static final By VERIFICATION_PROMPT = AppiumBy.xpath(
            "//*[contains(@text,'Account verification') or contains(@text,'Security Code')]");
}
