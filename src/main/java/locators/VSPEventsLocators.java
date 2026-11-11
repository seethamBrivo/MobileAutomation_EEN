package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

/**
 * Locators for the VSP Events screen used by TC-37646 and TC-37714.
 * <p>
 * Most XPaths here are positional Compose paths — the mobile team has not yet added
 * testTag/content-desc attributes to these nodes. Each TODO marks a locator that should
 * be replaced once the app exposes a stable identifier.
 */
public final class VSPEventsLocators {

    private VSPEventsLocators() {}

    private static final String VSP_MAIN_SCREEN = "//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]";
    private static final String VIEW_HIERARCHY = "/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View";
    public static final String UTC_FORMAT_PATTERN = ".*\\(UTC[+-]\\d{2}:\\d{2}\\).*";

    // --- Navigation elements ---------------------------------------------------------

    public static final By VSP_SECTION = PlatformLocator.get(
            AppiumBy.xpath(XPathUtils.textViewIgnoreCase("VSP")), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'VSP'")); // ios

    public static final By EVENTS_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Events']"), // android
            AppiumBy.accessibilityId("events_button")); // ios

    public static final By VSP_EVENTS_HEADING = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='VSP Events']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'VSP Events'")); // ios

    // iOS: the events counter above the list, which shows once the list has loaded
    public static final By VSP_MAIN_CONTAINER = PlatformLocator.get(
            AppiumBy.xpath(VSP_MAIN_SCREEN), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name ENDSWITH 'license plate events'")); // ios

    // --- Card list elements ----------------------------------------------------------

    // TODO: Replace with testTag - positional path for toolbar in Compose view (Android)
    // iOS: The search field in the navigation bar serves as toolbar indicator
    public static final By TOOLBAR = PlatformLocator.get(
            AppiumBy.xpath(VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeSearchField' AND name == 'Filter by license plate'")); // ios - search field in nav bar

    // TODO: Replace with testTag - positional path for event cards in list area
    // iOS: Count timestamp texts as proxy for cards (format: "Today, 13:59")
    public static final By ALL_EVENT_CARDS = PlatformLocator.get(
            AppiumBy.xpath(VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.view.View[@clickable='true']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND name MATCHES '.*, [0-9]{1,2}:[0-9]{2}.*'")); // ios

    // iOS: an event card is not one element, so its time text stands for it
    public static final By FIRST_EVENT_IN_LIST = PlatformLocator.get(
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.view.View[@clickable='true'])[1]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND visible == 1 AND name MATCHES '.*, [0-9]{1,2}:[0-9]{2}.*'")); // ios

    // iOS: an event card is not one element and its thumbnail has no id, so the time text of a card stands for it
    // (the same as FIRST_EVENT_IN_LIST) instead of an image found by its size, which differs between devices.
    public static final By ANY_EVENT_THUMBNAIL = PlatformLocator.get(
            AppiumBy.xpath(VSP_MAIN_SCREEN + "//android.view.View[@clickable='true'][1]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND visible == 1 AND name MATCHES '.*, [0-9]{1,2}:[0-9]{2}.*'")); // ios

    // TODO: Replace with testTag - scope within VspMain list area with position constraints
    // Plate is typically first text (exclude filter labels, timestamps, headings)
    // iOS: rect.y > 300 excludes header (fragile, device-dependent)
    public static final By ANY_LICENSE_PLATE = PlatformLocator.get(
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.widget.TextView[string-length(@text) > 0 and not(contains(@text, 'VSP Events')) and not(contains(@text, 'Today,')) and not(contains(@text, 'Filter by'))])[1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND " +
                    "NOT (name CONTAINS '|' OR name CONTAINS 'Today' OR name CONTAINS 'license plate events' OR " +
                    "name == 'VSP Events' OR name CONTAINS 'Filter by') AND rect.y > 300")); // ios

    // Timestamp is any text with 'Today,' in the list area
    // iOS: Already defined as TIMESTAMP_WITH_TIME, but for consistency
    public static final By ANY_TIMESTAMP = PlatformLocator.get(
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.widget.TextView[contains(@text, 'Today,') or contains(@text, ':')])[1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND name MATCHES '.*, [0-9]{1,2}:[0-9]{2}.*'")); // ios

    // Camera name is typically last text in the list area (exclude filter/heading text)
    // iOS: Camera name contains pipes (e.g., "Eagle Eye Networks|Test Camera Simulator Dual|1.0")
    public static final By ANY_CAMERA_NAME = PlatformLocator.get(
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.widget.TextView[string-length(@text) > 0 and not(contains(@text, 'Today,')) and not(contains(@text, 'Filter by')) and not(contains(@text, 'VSP Events'))])[last()]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND name CONTAINS '|'")); // ios - camera names have pipes

    // iOS: the time of an event card, written as "<day>, <hh:mm>" (for example "Today, 15:50"); the day may change with
    // the time zone, so only the shape is matched. Not scoped to the cards: on the VSP Events screen the cards are the
    // only texts of that shape.
    // TODO: Replace with an accessibility id when the mobile team adds one to the event cards
    public static final By TIMESTAMP_WITH_TIME = PlatformLocator.get(
            AppiumBy.xpath(VSP_MAIN_SCREEN + "//android.widget.TextView[contains(@text, 'Today,') and contains(@text, ':')]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name MATCHES '.*, [0-9]{1,2}:[0-9]{2}.*'")); // ios

    // --- Empty state messages --------------------------------------------------------

    public static final By NO_EVENTS_MESSAGE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='No license plate events yet']"), // android
            AppiumBy.accessibilityId("No license plate events yet")); // ios

    public static final By ZERO_EVENTS_MESSAGE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='0 license plate events']"), // android
            AppiumBy.accessibilityId("0 license plate events")); // ios

    // --- Detail page elements --------------------------------------------------------

    // Camera name format is "Org|Camera|Version" with pipes - not hardcoded to specific org
    public static final By DETAIL_CAMERA_NAME = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, '|')]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS '|' AND visible == 1")); // ios

    // TODO: Replace with testTag - positional path for close icon (Android)
    public static final By CLOSE_ICON = PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND (name == 'xmark' OR label == 'Close')")); // ios

    // TODO: Replace with testTag - positional paths for thumbnail and plate crop (Android)
    // iOS: Uses image width to distinguish (>300 thumbnail, <200 crop) - device-dependent
    public static final By DETAIL_THUMBNAIL = PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' AND visible == 1 AND rect.width > 300")); // ios

    public static final By DETAIL_PLATE_CROP = PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[2]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' AND visible == 1 AND rect.width < 200")); // ios

    // --- Detail page fields ----------------------------------------------------------

    public static final By ANY_UTC_TIMESTAMP = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'UTC')]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS 'UTC' AND visible == 1")); // ios

    public static final By DETAIL_DEVICE_TIME = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.TextView[contains(@text, 'UTC')])[1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS 'UTC' AND visible == 1")); // ios - first UTC timestamp

    public static final By DETAIL_PREFERRED_TIME = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.TextView[contains(@text, 'UTC')])[2]"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Profile timezone']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Profile timezone label

    // TODO: Replace with testTag - first value text after camera name (excludes camera, timestamps, labels, search link)
    // iOS: rect.y > 400 ensures plate text below images (fragile, device-dependent)
    public static final By DETAIL_PLATE_READING = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.TextView[not(contains(@text, '|') or contains(@text, 'UTC') " +
                    "or contains(@text, 'Search') or contains(@text, ':') " +
                    "or @text='Site' or @text='Make' or @text='Color' or @text='Direction' " +
                    "or @text='Vehicle type' or @text='Access type' or @text='Vehicle tags' or @text='Timestamp')])[1]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND " +
                    "NOT (name CONTAINS '|' OR name CONTAINS 'UTC' OR name CONTAINS 'Search' OR name CONTAINS ':' OR " +
                    "name == 'Site' OR name == 'Make' OR name == 'Color' OR name == 'Direction' OR " +
                    "name == 'Vehicle type' OR name == 'Access type' OR name == 'Vehicle tags' OR name == 'Timestamp' OR " +
                    "name == 'Profile timezone') AND rect.y > 400")); // ios

    // --- Detail page labels and optional fields --------------------------------------

    public static final By DETAIL_SITE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Site\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Site' AND visible == 1")); // ios

    // Use label+sibling to get timestamp VALUE, not the label itself
    // iOS: The timestamp value is the StaticText following the "Timestamp" label
    public static final By DETAIL_TIMESTAMP = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Timestamp']/following-sibling::android.widget.TextView[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Timestamp']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Timestamp label

    // Android: finds label (original behavior), iOS: finds value after label
    public static final By DETAIL_MAKE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Make\"]"), // android - finds label
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Make']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Make label

    public static final By DETAIL_COLOR = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Color\"]"), // android - finds label
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Color']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Color label

    public static final By DETAIL_DIRECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Direction\"]"), // android - finds label
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Direction']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Direction label

    public static final By DETAIL_TYPE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Vehicle type\"]"), // android - finds label
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Vehicle type']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Vehicle type label

    public static final By DETAIL_ACCESS_TYPE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Access type\"]"), // android - finds label
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Access type']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Access type label

    public static final By DETAIL_VEHICLE_TAGS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Vehicle tags\"]"), // android - finds label
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Vehicle tags']/following-sibling::XCUIElementTypeStaticText[1]")); // ios - value after Vehicle tags label

    public static final By DETAIL_VIEW_HISTORY_LINK = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='View in history browser']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'View in history browser' AND visible == 1")); // ios - button, not text

    // Plate region link displays as "Search <plate>" (e.g., "Search P1173646")
    public static final By DETAIL_SEARCH_PLATE_LINK = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Search')]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name BEGINSWITH 'Search ' AND visible == 1")); // ios - button, not text
}
