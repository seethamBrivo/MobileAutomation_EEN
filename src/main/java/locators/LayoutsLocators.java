package locators;

import io.appium.java_client.AppiumBy;
import java.util.List;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

/**
 * Locators for the layouts screens: the layouts list, the three-dots menu, the new-layout form,
 * the add-cameras picker, the layouts search filter and the camera preview cards.
 */
public final class LayoutsLocators {

    private LayoutsLocators() {}

    // --- Layout tabs and three-dots menu ---------------------------------------

    public static final By ALL_CAMERAS_TAB =
            AppiumBy.accessibilityId("cell_at_0_layout_tab");

    public static final By FIRST_LAYOUT_TAB =
            AppiumBy.accessibilityId("cell_at_1_layout_tab");

    // iOS: the nav-bar button is also "Add" / "Save" on the form screens, so it is told apart by its label.
    public static final By THREE_DOTS_MENU_BUTTON = PlatformLocator.get(
            // android
            AppiumBy.id("com.een.vms.android:id/img_more"),
            // ios
            AppiumBy.iOSNsPredicateString("name == 'navigationBar_customButton' AND label == 'More'"));

    // iOS: the id of this entry differs between the layouts and layout-tab menus, the label does not.
    public static final By ADD_CAMERAS_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Add cameras']"),
            // ios
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' AND name BEGINSWITH 'contextMenu' AND label == 'Add cameras'"));

    public static final By NEW_LAYOUT_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='New layout']"),
            // ios
            AppiumBy.accessibilityId("contextMenuNewLayout"));

    public static final By EDIT_LAYOUT_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Edit layout']"),
            // ios
            AppiumBy.accessibilityId("contextMenuEditLayout"));

    public static final By LAYOUT_SETTINGS_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Layout settings']"),
            // ios
            AppiumBy.accessibilityId("contextMenuLayoutSettings"));

    public static final By MAP_VIEW_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Map view']"),
            // ios
            AppiumBy.accessibilityId("contextMenuMaps"));

    public static final By ZOOM_IN_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Zoom in']"),
            // ios
            AppiumBy.accessibilityId("contextMenuZoomIn"));

    public static final By ZOOM_OUT_OPTION = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Zoom out']"),
            // ios
            AppiumBy.accessibilityId("contextMenuZoomOut"));

    // --- New layout form -------------------------------------------------------

    public static final By LAYOUT_NAME_LABEL = PlatformLocator.get(
            // android
            AppiumBy.accessibilityId("row_name_header"),
            // ios
            AppiumBy.accessibilityId("layoutSettingsName_nameText"));

    // The name row's tag carried a generated suffix ("row_name_dj0") that changed between app builds, so
    // the editable field is found by its type first, then by the stable "row_name_" prefix.
    // iOS: the field is pre-filled with "New Layout", so it is cleared before typing.
    public static final List<By> LAYOUT_NAME_EDIT_FIELDS = PlatformLocator.isIos()
            ? List.of(AppiumBy.accessibilityId("layoutSettingsName_textfield"))
            : List.of(
                    AppiumBy.xpath("//android.widget.EditText"),
                    AppiumBy.xpath("//*[starts-with(@content-desc,'row_name_') and @content-desc!='row_name_header']"));

    // iOS only: aspect ratio and max cameras per row open a list of values; picking one returns to the form.
    public static final By ASPECT_RATIO_ROW = AppiumBy.accessibilityId("layoutSettingsAspectRatio");

    public static final By CAMERAS_PER_ROW_ROW = AppiumBy.accessibilityId("layoutSettingsCamerasPerRow");

    /** iOS only: the navigation bar's back button on the form and picker screens. */
    public static final By BACK_BUTTON = AppiumBy.accessibilityId("BackButton");

    // iOS: inside the opened list the value text is named after its value (the row on the form is not).
    public static final By ASPECT_RATIO_16_9 = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='16:9']"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '16:9'"));

    public static final By ASPECT_RATIO_4_3 = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='4:3']"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '4:3'"));

    // 10.5.2: the switch lost its "row_show_cameras_title_item_switch" accessibility id, so it is addressed by position.
    // iOS: the switch reports its state as the value "1" (on) or "0" (off).
    public static final By SHOW_CAMERA_TITLE_BARS_TOGGLE = PlatformLocator.get(
            // android
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View"),
            // ios
            AppiumBy.accessibilityId("layoutSettingsShowCameraTitleBars_switch"));

    public static final By ADD_CAMERAS_ICON = PlatformLocator.get(
            // android
            AppiumBy.accessibilityId("row_add_cameras_end_icon"),
            // ios
            AppiumBy.accessibilityId("layoutSettingsAddCameras"));

    public static final By SELECT_CAMERA = PlatformLocator.get(
            // android
            AppiumBy.accessibilityId("cell_at_0_add_camera_check_box"),
            // ios
            AppiumBy.accessibilityId("cell_at_0_add_camera"));

    public static final By SAVE_BUTTON = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Save']"),
            // ios
            AppiumBy.iOSNsPredicateString("name == 'navigationBar_customButton' AND label == 'Save'"));

    public static final By ADD_BUTTON = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.TextView[@text='Add']"),
            // ios
            AppiumBy.iOSNsPredicateString("name == 'navigationBar_customButton' AND label == 'Add'"));

    // Android only: iOS shows no snackbar; it lists the new layout among the tabs instead (see layoutTabByName).
    public static final By SUCCESS_MESSAGE =
            AppiumBy.id("com.een.vms.android:id/snackbar_text");

    // --- Layouts search filter and delete --------------------------------------

    public static final By FILTER_ICON = PlatformLocator.get(
            // android
            AppiumBy.id("com.een.vms.android:id/img_search"),
            // ios
            AppiumBy.accessibilityId("navigationBar_searchButton"));

    // iOS: the "Layouts" segment of the filter screen (the bottom tab bar's own button is "layouts_button").
    public static final By LAYOUTS_TAB = PlatformLocator.get(
            // android
            AppiumBy.xpath(XPathUtils.textViewIgnoreCase("Layouts")),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Layouts'"));

    public static final By SEARCH_BAR = PlatformLocator.get(
            // android
            AppiumBy.id("com.een.vms.android:id/search_bar"),
            // ios
            AppiumBy.accessibilityId("navigationBar_searchField"));

    public static final By DELETE_BUTTON = PlatformLocator.get(
            // android
            AppiumBy.accessibilityId("delete_header"),
            // ios
            AppiumBy.accessibilityId("layoutSettingsDeleteLayout"));

    // iOS: the confirmation is a native alert with "Cancel" and "Delete".
    public static final By CONFIRM_BUTTON = PlatformLocator.get(
            // android
            AppiumBy.accessibilityId("negative_button"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Delete'"));

    // --- Camera previews -------------------------------------------------------

    public static final By CAMERA_PREVIEW_CARDS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeCell' AND name ENDSWITH '_preview_view'"));

    public static final By CAMERA_PREVIEW_IMAGES = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.ImageView[@resource-id='com.een.vms.android:id/camera_preview']"),
            // ios
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' AND name ENDSWITH '_previewImage'"));

    // iOS: the time label is drawn over the live video, so it only appears once the stream has started.
    public static final By CAMERA_TIMESTAMPS = PlatformLocator.get(
            // android
            AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']//android.widget.TextView[contains(@text, ':') and string-length(@text) > 3 and string-length(@text) < 25 and (contains(@text, '0') or contains(@text, '1') or contains(@text, '2') or contains(@text, '3') or contains(@text, '4') or contains(@text, '5') or contains(@text, '6') or contains(@text, '7') or contains(@text, '8') or contains(@text, '9'))]"),
            // ios
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name ENDSWITH '_timeLabel'"));

    // --- Dynamic locators ------------------------------------------------------

    /** Android: the layout's card in the list. iOS: its row in the filter results. */
    public static By layoutByName(String layoutName) {
        if (PlatformLocator.isIos()) {
            return AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name ENDSWITH '_list_search_name' AND label == "
                            + quoteAsPredicateString(layoutName));
        }
        String xpathValue = buildXPathStringLiteral(layoutName);
        return AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']"
                + "//androidx.cardview.widget.CardView[descendant::*[contains(@text, " + xpathValue + ")]]");
    }

    /** iOS only: the layout's tab on the layouts screen (selected or not). */
    public static By layoutTabByName(String layoutName) {
        return AppiumBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText' AND (name == 'tab_cell_label_selected' OR name == '_label_not_selected')"
                        + " AND label == " + quoteAsPredicateString(layoutName));
    }

    private static String quoteAsPredicateString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String buildXPathStringLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder result = new StringBuilder("concat(");
        String[] parts = value.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(", \"'\", ");
            }
            result.append("'").append(parts[i]).append("'");
        }
        result.append(")");
        return result.toString();
    }

    /**
     * Returns locator for camera count option in max cameras per row selector.
     * Scoped to clickable TextView to avoid matching static on-screen digits.
     */
    public static By camerasPerRowOption(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Invalid camera count for locator: " + count + " (must be 1-6)");
        }
        return PlatformLocator.get(
                // android
                AppiumBy.xpath("//android.widget.TextView[@text='" + count + "']"),
                // ios
                // iOS: the value text inside the opened "Max cameras per row" list.
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == '" + count + "'"));
    }
}
