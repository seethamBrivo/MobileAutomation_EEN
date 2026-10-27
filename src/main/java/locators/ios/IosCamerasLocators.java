package locators.ios;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * iOS locators for the Cameras screen (reached from the Dashboard): the camera list, the search field and
 * counter, the empty state and the long-press context menu.
 */
public final class IosCamerasLocators {

    private IosCamerasLocators() {}

    // --- List ------------------------------------------------------------------

    // The camera rows carry no ids: a row's name is the text placed right before its context-menu button (visible ones only).
    public static final By CAMERA_NAMES = AppiumBy.xpath(
            "//XCUIElementTypeStaticText[@visible='true'][following-sibling::*[1][@name='ic_contextMenu']]");

    public static final By FIRST_CAMERA = AppiumBy.xpath(
            "(//XCUIElementTypeStaticText[@visible='true'][following-sibling::*[1][@name='ic_contextMenu']])[1]");

    // The status text ("Online", "Offline", ...) shown in a row. The list keeps rows that scrolled off screen in the
    // hierarchy, so these only match what is actually on screen (visible == 1).
    public static final By FIRST_ONLINE_STATUS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name == 'Online' AND visible == 1");

    // Same statuses the Android test looks for ("Offline" and "Camera error"); found by scrolling the list.
    public static final By FIRST_OFFLINE_OR_ERROR_STATUS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND (name == 'Offline' OR name == 'Camera error') AND visible == 1");

    /** Any camera status label shown in a row. */
    public static final By ANY_STATUS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name IN {'Online', 'Offline', 'Invalid Credentials',"
                    + " 'Device error', 'Connectivity issue', 'Turned off', 'Camera error'}");

    /** "6 cameras" / "1 camera". Tighter than a "cameras" suffix, which would also match the "Multi-cameras" chip. */
    public static final By CAMERA_COUNTER = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]+ cameras?$'");

    // --- Search ----------------------------------------------------------------

    /** The search field; its name stays "Filter by camera" whatever is typed, its value shows the typed text. */
    public static final By SEARCH_FIELD = AppiumBy.accessibilityId("Filter by camera");

    public static final By CLEAR_TEXT_BUTTON = AppiumBy.accessibilityId("Clear text");

    /** The keyboard's "search" key, used to close the keyboard. */
    public static final By KEYBOARD_SEARCH_KEY = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND label == 'search'");

    /** iOS shows an illustration (with "0 cameras") instead of a message when nothing matches. */
    public static final By EMPTY_STATE_IMAGE = AppiumBy.accessibilityId("camera_empty");

    // --- Context menu (long press on a row) -------------------------------------

    public static final By CAMERA_SETTINGS_OPTION = AppiumBy.accessibilityId("Camera settings");

    public static final By HISTORY_BROWSER_OPTION = AppiumBy.accessibilityId("History browser");

    public static final By LIVE_VIDEO_OPTION = AppiumBy.accessibilityId("Live video");

    public static final By TURN_ON_CAMERA_OPTION = AppiumBy.accessibilityId("Turn on camera");

    public static final By TURN_OFF_CAMERA_OPTION = AppiumBy.accessibilityId("Turn off camera");

    /** The menu's title: a button named after the camera, right above the first option. */
    public static final By MENU_CAMERA_NAME = AppiumBy.xpath(
            "//XCUIElementTypeButton[@name='Camera settings']/preceding::XCUIElementTypeButton[1]");

    /**
     * The timestamp drawn on the menu's preview. The preview floats in its own layer, so the element exists
     * but is reported as not visible: check for presence, not visibility.
     */
    public static final By PREVIEW_TIMESTAMP = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]{2}:[0-9]{2}:[0-9]{2}$'");
}
