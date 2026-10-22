package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
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

    public static final By MORE_MENU_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='More']");

    public static final By VSP_SECTION =
            AppiumBy.xpath(XPathUtils.textViewIgnoreCase("VSP"));

    public static final By EVENTS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Events']");

    public static final By VSP_EVENTS_HEADING =
            AppiumBy.xpath("//android.widget.TextView[@text='VSP Events']");

    public static final By VSP_MAIN_CONTAINER =
            AppiumBy.xpath(VSP_MAIN_SCREEN);

    // --- Card list elements ----------------------------------------------------------

    // TODO: Replace with testTag - positional path for toolbar in Compose view
    public static final By TOOLBAR =
            AppiumBy.xpath(VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[1]");

    // TODO: Replace with testTag - positional path for event cards in list area
    public static final By ALL_EVENT_CARDS =
            AppiumBy.xpath(VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.view.View[@clickable='true']");

    public static final By FIRST_EVENT_IN_LIST =
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.view.View[@clickable='true'])[1]");

    public static final By ANY_EVENT_THUMBNAIL =
            AppiumBy.xpath(VSP_MAIN_SCREEN + "//android.view.View[@clickable='true'][1]");

    // TODO: Replace with testTag - scope within VspMain list area with position constraints
    // Plate is typically first text (exclude filter labels, timestamps, headings)
    public static final By ANY_LICENSE_PLATE =
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.widget.TextView[string-length(@text) > 0 and not(contains(@text, 'VSP Events')) and not(contains(@text, 'Today,')) and not(contains(@text, 'Filter by'))])[1]");

    // Timestamp is any text with 'Today,' in the list area
    public static final By ANY_TIMESTAMP =
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.widget.TextView[contains(@text, 'Today,') or contains(@text, ':')])[1]");

    // Camera name is typically last text in the list area (exclude filter/heading text)
    public static final By ANY_CAMERA_NAME =
            AppiumBy.xpath("(" + VSP_MAIN_SCREEN + VIEW_HIERARCHY + "[4]//android.widget.TextView[string-length(@text) > 0 and not(contains(@text, 'Today,')) and not(contains(@text, 'Filter by')) and not(contains(@text, 'VSP Events'))])[last()]");

    public static final By TIMESTAMP_WITH_TIME =
            AppiumBy.xpath(VSP_MAIN_SCREEN + "//android.widget.TextView[contains(@text, 'Today,') and contains(@text, ':')]");

    // --- Empty state messages --------------------------------------------------------

    public static final By NO_EVENTS_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[@text='No license plate events yet']");

    public static final By ZERO_EVENTS_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[@text='0 license plate events']");

    // --- Detail page elements --------------------------------------------------------

    // Camera name format is "Org|Camera|Version" with pipes - not hardcoded to specific org
    public static final By DETAIL_CAMERA_NAME =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, '|')]");

    // TODO: Replace with testTag - positional path for close icon
    public static final By CLOSE_ICON =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View");

    // TODO: Replace with testTag - positional paths for thumbnail and plate crop
    public static final By DETAIL_THUMBNAIL =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View");

    public static final By DETAIL_PLATE_CROP =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[2]");

    // --- Detail page fields ----------------------------------------------------------

    public static final By ANY_UTC_TIMESTAMP =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'UTC')]");

    public static final By DETAIL_DEVICE_TIME =
            AppiumBy.xpath("(//android.widget.TextView[contains(@text, 'UTC')])[1]");

    public static final By DETAIL_PREFERRED_TIME =
            AppiumBy.xpath("(//android.widget.TextView[contains(@text, 'UTC')])[2]");

    // TODO: Replace with testTag - first value text after camera name (excludes camera, timestamps, labels, search link)
    public static final By DETAIL_PLATE_READING =
            AppiumBy.xpath("(//android.widget.TextView[not(contains(@text, '|') or contains(@text, 'UTC') " +
                    "or contains(@text, 'Search') or contains(@text, ':') " +
                    "or @text='Site' or @text='Make' or @text='Color' or @text='Direction' " +
                    "or @text='Vehicle type' or @text='Access type' or @text='Vehicle tags' or @text='Timestamp')])[1]");

    public static final By DETAIL_PLATE_REGION =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Search')]");

    // --- Detail page labels and optional fields --------------------------------------

    public static final By DETAIL_SITE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Site\"]");

    // Use label+sibling to get timestamp VALUE, not the label itself
    public static final By DETAIL_TIMESTAMP =
            AppiumBy.xpath("//android.widget.TextView[@text='Timestamp']/following-sibling::android.widget.TextView[1]");

    public static final By DETAIL_MAKE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Make\"]");

    public static final By DETAIL_COLOR =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Color\"]");

    public static final By DETAIL_DIRECTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Direction\"]");

    public static final By DETAIL_TYPE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Vehicle type\"]");

    public static final By DETAIL_ACCESS_TYPE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Access type\"]");

    public static final By DETAIL_VEHICLE_TAGS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Vehicle tags\"]");

    public static final By DETAIL_VIEW_HISTORY_LINK =
            AppiumBy.xpath("//android.widget.TextView[@text='View in history browser']");

    public static final By DETAIL_SEARCH_PLATE_LINK =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Search')]");
}
