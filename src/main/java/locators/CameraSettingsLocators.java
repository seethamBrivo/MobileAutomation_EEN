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

    // The 2026-11-19 build wraps the list in two Views, so its header is a sibling of the grandparent; the second
    // path is the earlier build's, where the header followed the ScrollView directly.
    private static final String HEADER_ANDROID =
            "//android.widget.ScrollView[.//android.widget.TextView[@text='General']]"
                    + "/parent::android.view.View/parent::android.view.View/following-sibling::android.view.View[1]";
    private static final String HEADER_ANDROID_EARLIER =
            "//android.widget.ScrollView[.//android.widget.TextView[@text='General']]"
                    + "/following-sibling::android.view.View[1]";

    public static final By HEADER_TITLE = PlatformLocator.get(
            AppiumBy.xpath(HEADER_ANDROID + "/android.view.View/android.view.View[2]/android.widget.TextView"
                    + " | " + HEADER_ANDROID_EARLIER + "/android.widget.TextView"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeStaticText")); // ios

    // Android: positional, the clickable View inside the last child of the header.
    // TODO: Replace with testTag when mobile team adds an identifier to the preview icon.
    public static final By PREVIEW_ICON = PlatformLocator.get(
            AppiumBy.xpath(HEADER_ANDROID + "/android.view.View/android.view.View[last()]"
                    + "/android.view.View[@clickable='true']"
                    + " | " + HEADER_ANDROID_EARLIER + "/android.view.View[last()]"
                    + "/android.view.View[@clickable='true']"), // android
            AppiumBy.accessibilityId("navigationBar_cameraPreview")); // ios

    // --- Status ----------------------------------------------------------------

    public static final By STATUS_VALUE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Status']/following-sibling::android.widget.TextView[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Status']/following-sibling::XCUIElementTypeStaticText[1]")); // ios

    // --- Name ------------------------------------------------------------------

    // iOS: the first text field of the screen, by position; the second one is "Camera username".
    // TODO: Replace with an accessibility id when the mobile team adds one to the Name field
    public static final By NAME_FIELD = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.EditText)[1]"), // android
            AppiumBy.xpath("(//XCUIElementTypeTextField)[1]")); // ios

    /**
     * Android: the Name value on the settings list. The 2026-11-19 build shows it as plain text; tapping its row opens
     * a sheet whose single EditText is {@link #NAME_FIELD}.
     */
    public static final By NAME_VALUE = AppiumBy.xpath(
            "//android.widget.TextView[@text='Name']/following-sibling::android.widget.TextView[1]"); // android

    /** Android: the Name row that opens the name sheet. */
    public static final By NAME_ROW = AppiumBy.xpath(
            "//android.widget.TextView[@text='Name']/ancestor::android.view.View[@clickable='true'][1]"); // android

    /** Android: the Save button of the name sheet, which replaces the keyboard's Done key. */
    public static final By NAME_SAVE_BUTTON = AppiumBy.xpath("//android.widget.TextView[@text='Save']"); // android

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
