package locators.ios;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** iOS locators for the screen shown right after sign-in (Layouts), its top bar and the bottom tab bar. */
public final class IosLandingLocators {

    private IosLandingLocators() {}

    public static final By LAYOUTS_TITLE =
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Layouts'");

    public static final By NOTIFICATIONS_BUTTON = AppiumBy.accessibilityId("navigationBar_notificationBell");

    public static final By FILTER_BUTTON = AppiumBy.accessibilityId("navigationBar_searchButton");

    public static final By MORE_OPTIONS_BUTTON = AppiumBy.accessibilityId("navigationBar_customButton");

    public static final By ALL_CAMERAS_CHIP_SELECTED = AppiumBy.iOSNsPredicateString(
            "name == 'tab_cell_label_selected' AND label == 'All cameras'");

    public static final By FIRST_CAMERA_TILE_NAME = AppiumBy.accessibilityId("cell_at_0_preview_view_name");

    // --- Bottom tab bar ---------------------------------------------------------

    public static final By TAB_LAYOUTS = AppiumBy.accessibilityId("layouts_button");

    public static final By TAB_TAGS = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Tags'");

    public static final By TAB_SEARCH = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Search'");

    public static final By TAB_MORE = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'More'");

    // --- "How likely are you to recommend ..." survey overlay (appears at random) ----

    public static final By SURVEY_TITLE = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name BEGINSWITH 'How likely is it that you would recommend'");

    public static final By SURVEY_CLOSE_BUTTON =
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Close'");
}
