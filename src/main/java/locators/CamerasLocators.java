package locators;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

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
            AppiumBy.xpath("//android.widget.TextView[@text='Cameras']"), // android
            // iOS: the "Cameras" tile on the Dashboard.
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Cameras'")); // ios

    public static final By CAMERA_LIST_CONTAINER = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView"), // android
            AppiumBy.accessibilityId("Filter by camera")); // ios

    public static final By FIRST_CAMERA_IN_LIST = PlatformLocator.get(
            AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]"), // android
            AppiumBy.xpath("(//XCUIElementTypeStaticText[@visible='true'][following-sibling::*[1][@name='ic_contextMenu']])[1]")); // ios

    public static final By FIRST_OFFLINE_OR_ERROR_CAMERA = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.TextView[@text='Offline' or @text='Camera error'])[1]/ancestor::android.view.View[@clickable='true'][1]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND (name == 'Offline' OR name == 'Camera error')"
                            + " AND visible == 1")); // ios

    public static final By FIRST_ONLINE_CAMERA = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[@clickable='true'][1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText'"
                    + " AND name == 'Online' AND visible == 1")); // ios

    public static final By ALL_VISIBLE_CAMERAS = PlatformLocator.get(
            AppiumBy.xpath(
            "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]]//android.widget.TextView[1]"
            ), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@visible='true'][following-sibling::*[1][@name='ic_contextMenu']]")); // ios

    /** Android only: the "Offline" label inside a camera card, to tell an offline camera from one in error. */
    public static final By OFFLINE_STATUS_IN_CAMERA_CARD = AppiumBy.xpath(".//android.widget.TextView[@text='Offline']");

    // Android only.
    public static final By SECOND_ONLINE_CAMERA = AppiumBy.xpath(
            "(//android.widget.TextView[@text='Online'])[2]/ancestor::android.view.View[@clickable='true'][1]");

    // Android only: a camera card is a full-width clickable row; its name and status are its first two TextViews.
    public static final By CAMERA_CARD = AppiumBy.xpath(
            "//android.view.View[@clickable='true'][starts-with(@bounds, '[0,')][android.widget.TextView]");

    /** Android only: looked up inside a camera card; in order the name, the status, then the tags. */
    public static final By TEXTS_IN_CAMERA_CARD = AppiumBy.xpath(".//android.widget.TextView");

    // Android only: the 3-dots button is the last clickable child of the card (the first is the preview).
    public static final By THREE_DOTS_OF_FIRST_ONLINE_CAMERA = AppiumBy.xpath(
            "(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[@clickable='true'][1]"
                    + "/android.view.View[@clickable='true'][last()]");

    /** Android only: the Cameras tile on the Dashboard lists the cameras per status; their sum is the total. */
    public static final By CAMERAS_TILE_COUNTS = AppiumBy.xpath(
            "//android.widget.TextView[@text='Cameras']/ancestor::android.view.View[@clickable='true'][1]"
                    + "//android.widget.TextView[string-length(@text) > 0"
                    + " and string-length(translate(@text, '0123456789', '')) = 0]");

    public static By cameraCardWithName(String name) {
        return AppiumBy.xpath("//android.widget.TextView[@text=" + XPathUtils.xpathLiteral(name) + "]"
                + "/ancestor::android.view.View[@clickable='true'][1]");
    }

    // --- Context menu ----------------------------------------------------------

    public static final By CONTEXT_MENU_CONTAINER = PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup[.//android.widget.TextView[@text='Camera settings']]"), // android
            AppiumBy.accessibilityId("Camera settings")); // ios

    public static final By CAMERA_SETTINGS_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Camera settings\"]"), // android
            AppiumBy.accessibilityId("Camera settings")); // ios

    public static final By HISTORY_BROWSER_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='History browser']"), // android
            AppiumBy.accessibilityId("History browser")); // ios

    public static final By LIVE_VIDEO_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Live video']"), // android
            AppiumBy.accessibilityId("Live video")); // ios

    public static final By TURN_ON_CAMERA_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Turn on camera']"), // android
            AppiumBy.accessibilityId("Turn on camera")); // ios

    public static final By TURN_OFF_CAMERA_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Turn off camera']"), // android
            AppiumBy.accessibilityId("Turn off camera")); // ios

    // --- Camera preview --------------------------------------------------------

    // iOS: the preview exposes only the menu's title button (named after the camera), so the preview check and the
    // name check below find the same element. The lookup is relative to the "Camera settings" button.
    // TODO: Replace with testTag when mobile team adds an identifier to the preview overlay.
    // Android structural: the preview card carries no content-desc/testTag, so it is the card just above the options
    // list. The old absolute path (.../View[1]/View[2]) no longer matched: the card now holds a single View.
    // iOS: the preview floats in its own layer; the menu's title button, named after the camera, is what is exposed.
    public static final By CAMERA_PREVIEW = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView[.//android.widget.TextView[@text='Camera settings']]"
                    + "/preceding-sibling::android.view.View[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeButton[@name='Camera settings']/preceding::XCUIElementTypeButton[1]")); // ios

    // Android: the name is the first text that is a direct child of the card. An offline or error card also holds a
    // status text ("Offline"), but it sits inside a nested View, so only a direct child is the name on every card.
    public static final By CAMERA_NAME_IN_PREVIEW = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView[.//android.widget.TextView[@text='Camera settings']]"
                    + "/preceding-sibling::android.view.View[1]/android.widget.TextView[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeButton[@name='Camera settings']/preceding::XCUIElementTypeButton[1]")); // ios

    /** Android only: the time of the frame, the card's second direct-child text on an online camera. */
    public static final By CAMERA_FRAME_TIME_IN_PREVIEW =
            AppiumBy.xpath("//android.widget.ScrollView[.//android.widget.TextView[@text='Camera settings']]"
                    + "/preceding-sibling::android.view.View[1]/android.widget.TextView[2]");

    // iOS: the timestamp exists in the hierarchy but is reported as not visible, so it is checked for presence.
    public static final By TIMESTAMP = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, ':')]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]{2}:[0-9]{2}:[0-9]{2}$'")); // ios

    // --- Status labels ---------------------------------------------------------

    public static final By ONLINE_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Online']"), // android
            iosStatusLabelWithText("Online")); // ios

    public static final By OFFLINE_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Offline']"), // android
            iosStatusLabelWithText("Offline")); // ios

    public static final By INVALID_CREDENTIALS_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Invalid Credentials']"), // android
            iosStatusLabelWithText("Invalid Credentials")); // ios

    public static final By DEVICE_ERROR_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Device error']"), // android
            iosStatusLabelWithText("Device error")); // ios

    public static final By CONNECTIVITY_ISSUE_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Connectivity issue']"), // android
            iosStatusLabelWithText("Connectivity issue")); // ios

    public static final By TURNED_OFF_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Turned off']"), // android
            iosStatusLabelWithText("Turned off")); // ios

    public static final By CAMERA_ERROR_STATUS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Camera error']"), // android
            iosStatusLabelWithText("Camera error")); // ios

    // iOS: no visible filter here, because the rows behind an open context menu are not reported as visible.
    private static By iosStatusLabelWithText(String text) {
        return AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '" + text + "'");
    }

    // --- Search and filter -----------------------------------------------------

    // iOS: the field's name stays "Filter by camera" whatever is typed; its value is the typed text (or the placeholder).
    public static final By SEARCH_BAR = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.EditText"), // android
            AppiumBy.accessibilityId("Filter by camera")); // ios

    public static final By SEARCH_PLACEHOLDER = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]"), // android
            AppiumBy.accessibilityId(UiTextConstants.SEARCH_PLACEHOLDER)); // ios

    // iOS: "6 cameras" / "1 camera", tighter than a " camera" match, which would also hit the "Multi-cameras" chip.
    public static final By CAMERA_COUNTER = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText'"
                    + " AND name MATCHES '^[0-9]+ cameras?$'")); // ios

    // iOS: an illustration (with "0 cameras") instead of a message.
    public static final By EMPTY_STATE_MESSAGE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.EMPTY_STATE_MESSAGE + "\"]"), // android
            AppiumBy.accessibilityId("camera_empty")); // ios

    /** iOS only: clears the search field. */
    public static final By CLEAR_TEXT_BUTTON = AppiumBy.accessibilityId("Clear text");

    /** iOS only: the keyboard's "search" key (disabled while the field is empty), used to close the keyboard. */
    public static final By KEYBOARD_SEARCH_KEY = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND label == 'search'");

    /** Android only: the name of each filter chip, as the row shows them from left to right. */
    public static final By FILTER_CHIP_NAME = AppiumBy.xpath(
            "//android.view.View[@content-desc='trailingIcon']/preceding-sibling::android.widget.TextView[1]");

    // Android only: a tag under a camera is a clickable chip with a checkbox; the filter chips have none.
    public static final By FIRST_TAG = AppiumBy.xpath(
            "(//android.view.View[@clickable='true'][android.widget.CheckBox])[1]");

    public static final By FIRST_TAG_NAME = AppiumBy.xpath(
            "(//android.view.View[@clickable='true'][android.widget.CheckBox])[1]/android.widget.TextView");

    /** Android only: the number next to the "Tags" filter chip while a tag filter is applied. */
    public static final By ACTIVE_TAGS_FILTER_COUNT = AppiumBy.xpath(
            "//android.widget.TextView[@text='Tags']/following-sibling::android.widget.TextView[1]");

    /** Android only: appears next to the counter while a filter is applied. */
    public static final By CLEAR_FILTER_BUTTON = AppiumBy.xpath("//android.widget.TextView[@text='Clear filter']");
}
