package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/** Locators for the Camera settings screen. */
public final class CameraSettingsLocators {

    private CameraSettingsLocators() {}

    // --- Screen and header -----------------------------------------------------

    public static final By GENERAL_SECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='General']"), // android
            AppiumBy.accessibilityId("GENERAL")); // ios

    public static final By HEADER_TITLE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView[.//android.widget.TextView[@text='General']]"
                    + "/following-sibling::android.view.View[1]/android.widget.TextView"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeStaticText")); // ios

    // Android: positional, the clickable View inside the last child of the header.
    // TODO: Replace with testTag when mobile team adds an identifier to the preview icon.
    public static final By PREVIEW_ICON = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView[.//android.widget.TextView[@text='General']]"
                    + "/following-sibling::android.view.View[1]/android.view.View[last()]"
                    + "/android.view.View[@clickable='true']"), // android
            AppiumBy.accessibilityId("navigationBar_cameraPreview")); // ios

    // --- Status ----------------------------------------------------------------

    public static final By STATUS_VALUE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Status']/following-sibling::android.widget.TextView[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Status']/following-sibling::XCUIElementTypeStaticText[1]")); // ios

    // --- Name ------------------------------------------------------------------

    // iOS: the first text field of the screen; the second one is "Camera username".
    public static final By NAME_FIELD = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.EditText)[1]"), // android
            AppiumBy.xpath("(//XCUIElementTypeTextField)[1]")); // ios

    // --- Enable switch ---------------------------------------------------------

    public static final By ENABLE_ROW = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Enable']/ancestor::android.view.View[@clickable='true'][1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeSwitch' AND name == 'Enable'")); // ios

    // --- iOS only --------------------------------------------------------------

    /** iOS: the Enable switch while it is on. The switch reports its state as the value "1" (on) or "0" (off). */
    public static final By ENABLE_SWITCH_ON = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeSwitch' AND name == 'Enable' AND value == '1'"); // ios

    /** iOS: the Enable switch while it is off. */
    public static final By ENABLE_SWITCH_OFF = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeSwitch' AND name == 'Enable' AND value == '0'"); // ios

    /** iOS: the navigation bar's back button (Android goes back with the system back). */
    public static final By BACK_BUTTON = AppiumBy.accessibilityId("BackButton"); // ios

    /** iOS: the keyboard key that saves the name (Android saves with the keyboard's Done action). */
    public static final By KEYBOARD_DONE_KEY =
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Done'"); // ios
}
