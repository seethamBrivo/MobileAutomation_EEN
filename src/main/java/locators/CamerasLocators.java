package locators;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the Cameras screen (reached from the More menu) and the camera search helper shared with the
 * bridge cameras screen: camera list, context menu, preview overlay, status labels and the search/filter UI.
 * <p>
 * iOS notes: the camera rows carry no ids, so a row's name is the text placed right before its context-menu
 * button, and the list keeps rows that scrolled off screen in the hierarchy, so the iOS row locators only match
 * what is on screen ({@code visible == 1}).
 */
public final class CamerasLocators {

    private CamerasLocators() {}

    // --- Navigation and list ---------------------------------------------------

    public static final By CAMERAS_ITEM = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Cameras']"),
            // ios
            // iOS: the "Cameras" tile on the Dashboard.
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Cameras'"));

    public static final By CAMERA_LIST_CONTAINER = PlatformLocator.get(
            // android
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView"),
            // ios
            AppiumBy.accessibilityId("Filter by camera"));

    public static final By FIRST_CAMERA_IN_LIST = PlatformLocator.get(
            // android
            AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]"),
            // ios
            AppiumBy.xpath("(//XCUIElementTypeStaticText[@visible='true'][following-sibling::*[1][@name='ic_contextMenu']])[1]"));

    public static final By FIRST_OFFLINE_OR_ERROR_CAMERA = PlatformLocator.get(
            // android
            AppiumBy.xpath("(//android.widget.TextView[@text='Offline' or @text='Camera error'])[1]/ancestor::android.view.View[@clickable='true'][1]"),
            // ios
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND (name == 'Offline' OR name == 'Camera error') AND visible == 1"));

    public static final By FIRST_ONLINE_CAMERA = PlatformLocator.get(
            // android
            AppiumBy.xpath("(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[@clickable='true'][1]"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Online' AND visible == 1"));

    public static final By ALL_VISIBLE_CAMERAS = PlatformLocator.get(
            // android
            AppiumBy.xpath(
            "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]]//android.widget.TextView[1]"
            ),
            // ios
            AppiumBy.xpath("//XCUIElementTypeStaticText[@visible='true'][following-sibling::*[1][@name='ic_contextMenu']]"));

    // --- Context menu ----------------------------------------------------------

    public static final By CONTEXT_MENU_CONTAINER = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.view.ViewGroup[.//android.widget.TextView[@text='Camera settings']]"),
            // ios
            AppiumBy.accessibilityId("Camera settings"));

    public static final By CAMERA_SETTINGS_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text=\"Camera settings\"]"),
            // ios
            AppiumBy.accessibilityId("Camera settings"));

    public static final By HISTORY_BROWSER_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='History browser']"),
            // ios
            AppiumBy.accessibilityId("History browser"));

    public static final By LIVE_VIDEO_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Live video']"),
            // ios
            AppiumBy.accessibilityId("Live video"));

    public static final By TURN_ON_CAMERA_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Turn on camera']"),
            // ios
            AppiumBy.accessibilityId("Turn on camera"));

    public static final By TURN_OFF_CAMERA_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Turn off camera']"),
            // ios
            AppiumBy.accessibilityId("Turn off camera"));

    // --- Camera preview --------------------------------------------------------

    // TODO: Replace with testTag when mobile team adds an identifier to the preview overlay.
    // Positional: the preview container and its name label carry no content-desc/testTag yet.
    // iOS: the preview floats in its own layer; the menu's title button, named after the camera, is what is exposed.
    public static final By CAMERA_PREVIEW = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[2]"),
            // ios
            AppiumBy.xpath("//XCUIElementTypeButton[@name='Camera settings']/preceding::XCUIElementTypeButton[1]"));

    public static final By CAMERA_NAME_IN_PREVIEW = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[1]//android.widget.TextView[1]"),
            // ios
            AppiumBy.xpath("//XCUIElementTypeButton[@name='Camera settings']/preceding::XCUIElementTypeButton[1]"));

    // iOS: the timestamp exists in the hierarchy but is reported as not visible, so it is checked for presence.
    public static final By TIMESTAMP = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[contains(@text, ':')]"),
            // ios
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]{2}:[0-9]{2}:[0-9]{2}$'"));

    // --- Status labels ---------------------------------------------------------

    public static final By ONLINE_STATUS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Online']"),
            // ios
            iosStatusLabelWithText("Online"));

    public static final By OFFLINE_STATUS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Offline']"),
            // ios
            iosStatusLabelWithText("Offline"));

    public static final By INVALID_CREDENTIALS_STATUS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Invalid Credentials']"),
            // ios
            iosStatusLabelWithText("Invalid Credentials"));

    public static final By DEVICE_ERROR_STATUS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Device error']"),
            // ios
            iosStatusLabelWithText("Device error"));

    public static final By CONNECTIVITY_ISSUE_STATUS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Connectivity issue']"),
            // ios
            iosStatusLabelWithText("Connectivity issue"));

    public static final By TURNED_OFF_STATUS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Turned off']"),
            // ios
            iosStatusLabelWithText("Turned off"));

    // iOS: no visible filter here, because the rows behind an open context menu are not reported as visible.
    private static By iosStatusLabelWithText(String text) {
        return AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '" + text + "'");
    }

    // --- Search and filter -----------------------------------------------------

    // iOS: the field's name stays "Filter by camera" whatever is typed; its value is the typed text (or the placeholder).
    public static final By SEARCH_BAR = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.EditText"),
            // ios
            AppiumBy.accessibilityId("Filter by camera"));

    public static final By SEARCH_PLACEHOLDER = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]"),
            // ios
            AppiumBy.accessibilityId(UiTextConstants.SEARCH_PLACEHOLDER));

    // iOS: "6 cameras" / "1 camera", tighter than a " camera" match, which would also hit the "Multi-cameras" chip.
    public static final By CAMERA_COUNTER = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]+ cameras?$'"));

    // iOS: an illustration (with "0 cameras") instead of a message.
    public static final By EMPTY_STATE_MESSAGE = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.EMPTY_STATE_MESSAGE + "\"]"),
            // ios
            AppiumBy.accessibilityId("camera_empty"));

    /** iOS only: clears the search field. */
    public static final By CLEAR_TEXT_BUTTON = AppiumBy.accessibilityId("Clear text");

    /** iOS only: the keyboard's "search" key (disabled while the field is empty), used to close the keyboard. */
    public static final By KEYBOARD_SEARCH_KEY = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND label == 'search'");
}
