package locators.ios;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * iOS locators for the layouts screens: the layout tabs, the three-dots menu, the new-layout form and its
 * value pickers, the add-cameras picker, the layouts search filter, the delete alert and the camera previews.
 */
public final class IosLayoutsLocators {

    private IosLayoutsLocators() {}

    // --- Layout tabs and three-dots menu ---------------------------------------

    public static final By ALL_CAMERAS_TAB = AppiumBy.accessibilityId("cell_at_0_layout_tab");

    public static final By FIRST_LAYOUT_TAB = AppiumBy.accessibilityId("cell_at_1_layout_tab");

    /** The layout tab that is currently selected. */
    public static final By SELECTED_LAYOUT_TAB = AppiumBy.accessibilityId("tab_cell_label_selected");

    // The nav-bar button is also "Add" / "Save" on the form screens, so it is told apart by its label.
    public static final By THREE_DOTS_MENU_BUTTON = AppiumBy.iOSNsPredicateString(
            "name == 'navigationBar_customButton' AND label == 'More'");

    // The id of this entry differs between the layouts and layout-tab menus, the label does not.
    public static final By ADD_CAMERAS_OPTION = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND name BEGINSWITH 'contextMenu' AND label == 'Add cameras'");

    public static final By NEW_LAYOUT_OPTION = AppiumBy.accessibilityId("contextMenuNewLayout");

    public static final By EDIT_LAYOUT_OPTION = AppiumBy.accessibilityId("contextMenuEditLayout");

    public static final By LAYOUT_SETTINGS_OPTION = AppiumBy.accessibilityId("contextMenuLayoutSettings");

    public static final By MAP_VIEW_OPTION = AppiumBy.accessibilityId("contextMenuMaps");

    public static final By ZOOM_IN_OPTION = AppiumBy.accessibilityId("contextMenuZoomIn");

    public static final By ZOOM_OUT_OPTION = AppiumBy.accessibilityId("contextMenuZoomOut");

    // --- New layout form -------------------------------------------------------

    public static final By BACK_BUTTON = AppiumBy.accessibilityId("BackButton");

    public static final By LAYOUT_NAME_LABEL = AppiumBy.accessibilityId("layoutSettingsName_nameText");

    /** Pre-filled with "New Layout", so it must be cleared before typing. */
    public static final By LAYOUT_NAME_FIELD = AppiumBy.accessibilityId("layoutSettingsName_textfield");

    // Aspect ratio and max cameras per row open a list of values; picking one returns to the form.
    public static final By ASPECT_RATIO_ROW = AppiumBy.accessibilityId("layoutSettingsAspectRatio");

    public static final By CAMERAS_PER_ROW_ROW = AppiumBy.accessibilityId("layoutSettingsCamerasPerRow");

    // Inside the opened list the value text is named after its value (the row on the form is not).
    public static final By ASPECT_RATIO_16_9 = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name == '16:9'");

    public static final By ASPECT_RATIO_4_3 = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name == '4:3'");

    /** The switch reports its state as the value "1" (on) or "0" (off). */
    public static final By SHOW_CAMERA_TITLE_BARS_SWITCH =
            AppiumBy.accessibilityId("layoutSettingsShowCameraTitleBars_switch");

    public static final By ADD_CAMERAS_ROW = AppiumBy.accessibilityId("layoutSettingsAddCameras");

    public static final By SELECT_CAMERA = AppiumBy.accessibilityId("cell_at_0_add_camera");

    public static final By SAVE_BUTTON = AppiumBy.iOSNsPredicateString(
            "name == 'navigationBar_customButton' AND label == 'Save'");

    public static final By ADD_BUTTON = AppiumBy.iOSNsPredicateString(
            "name == 'navigationBar_customButton' AND label == 'Add'");

    // --- Layouts search filter and delete --------------------------------------

    public static final By FILTER_ICON = AppiumBy.accessibilityId("navigationBar_searchButton");

    // The "Layouts" segment of the filter screen (the bottom tab bar's own button is "layouts_button").
    public static final By LAYOUTS_SEGMENT = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND name == 'Layouts'");

    public static final By SEARCH_FIELD = AppiumBy.accessibilityId("navigationBar_searchField");

    public static final By DELETE_LAYOUT_ROW = AppiumBy.accessibilityId("layoutSettingsDeleteLayout");

    // The confirmation is a native alert with "Cancel" and "Delete".
    public static final By DELETE_ALERT_CONFIRM = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND name == 'Delete'");

    // --- Camera previews -------------------------------------------------------

    public static final By CAMERA_PREVIEW_CARDS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeCell' AND name ENDSWITH '_preview_view'");

    public static final By CAMERA_PREVIEW_IMAGES = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeImage' AND name ENDSWITH '_previewImage'");

    // Drawn over the live video frame, so it only appears once the stream has started.
    public static final By CAMERA_TIMESTAMPS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name ENDSWITH '_timeLabel'");

    // --- Dynamic locators ------------------------------------------------------

    /** The layout's row in the filter results. */
    public static By layoutInSearchResults(String layoutName) {
        return AppiumBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText' AND name ENDSWITH '_list_search_name' AND label == "
                        + predicateLiteral(layoutName));
    }

    /** The layout's tab on the layouts screen (selected or not). */
    public static By layoutTabByName(String layoutName) {
        return AppiumBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText' AND (name == 'tab_cell_label_selected' OR name == '_label_not_selected')"
                        + " AND label == " + predicateLiteral(layoutName));
    }

    /** The value text inside the opened "Max cameras per row" list. */
    public static By camerasPerRowOption(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Invalid camera count for locator: " + count + " (must be 1-6)");
        }
        return AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '" + count + "'");
    }

    private static String predicateLiteral(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
