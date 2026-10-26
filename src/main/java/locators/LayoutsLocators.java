package locators;

import io.appium.java_client.AppiumBy;
import java.util.List;
import org.openqa.selenium.By;
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

    public static final By THREE_DOTS_MENU_BUTTON =
            AppiumBy.id("com.een.vms.android:id/img_more");

    public static final By ADD_CAMERAS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Add cameras']");

    public static final By NEW_LAYOUT_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='New layout']");

    public static final By EDIT_LAYOUT_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Edit layout']");

    public static final By LAYOUT_SETTINGS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Layout settings']");

    public static final By MAP_VIEW_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Map view']");

    public static final By ZOOM_IN_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Zoom in']");

    public static final By ZOOM_OUT_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Zoom out']");

    // --- New layout form -------------------------------------------------------

    public static final By LAYOUT_NAME_LABEL =
            AppiumBy.accessibilityId("row_name_header");

    // The name row's tag carried a generated suffix ("row_name_dj0") that changed between app builds, so
    // the editable field is found by its type first, then by the stable "row_name_" prefix.
    public static final List<By> LAYOUT_NAME_EDIT_FIELDS = List.of(
            AppiumBy.xpath("//android.widget.EditText"),
            AppiumBy.xpath("//*[starts-with(@content-desc,'row_name_') and @content-desc!='row_name_header']"));

    public static final By ASPECT_RATIO_16_9 =
            AppiumBy.xpath("//android.widget.TextView[@text='16:9']");

    public static final By ASPECT_RATIO_4_3 =
            AppiumBy.xpath("//android.widget.TextView[@text='4:3']");

    // 10.5.2: the switch lost its "row_show_cameras_title_item_switch" accessibility id, so it is addressed by position.
    public static final By SHOW_CAMERA_TITLE_BARS_TOGGLE =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View");

    public static final By ADD_CAMERAS_ICON =
            AppiumBy.accessibilityId("row_add_cameras_end_icon");

    public static final By SELECT_CAMERA =
            AppiumBy.accessibilityId("cell_at_0_add_camera_check_box");

    public static final By SAVE_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text='Save']");

    public static final By ADD_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text='Add']");

    public static final By SUCCESS_MESSAGE =
            AppiumBy.id("com.een.vms.android:id/snackbar_text");

    // --- Layouts search filter and delete --------------------------------------

    public static final By FILTER_ICON =
            AppiumBy.id("com.een.vms.android:id/img_search");

    public static final By LAYOUTS_TAB =
            AppiumBy.xpath(XPathUtils.textViewIgnoreCase("Layouts"));

    public static final By SEARCH_BAR =
            AppiumBy.id("com.een.vms.android:id/search_bar");

    public static final By DELETE_BUTTON =
            AppiumBy.accessibilityId("delete_header");

    public static final By CONFIRM_BUTTON =
            AppiumBy.accessibilityId("negative_button");

    // --- Camera previews -------------------------------------------------------

    public static final By CAMERA_PREVIEW_CARDS =
            AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']");

    public static final By CAMERA_PREVIEW_IMAGES =
            AppiumBy.xpath("//android.widget.ImageView[@resource-id='com.een.vms.android:id/camera_preview']");

    public static final By CAMERA_TIMESTAMPS =
            AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']//android.widget.TextView[contains(@text, ':') and string-length(@text) > 3 and string-length(@text) < 25 and (contains(@text, '0') or contains(@text, '1') or contains(@text, '2') or contains(@text, '3') or contains(@text, '4') or contains(@text, '5') or contains(@text, '6') or contains(@text, '7') or contains(@text, '8') or contains(@text, '9'))]");

    // --- Dynamic locators ------------------------------------------------------

    public static By layoutByName(String layoutName) {
        String xpathValue = buildXPathStringLiteral(layoutName);
        return AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']"
                + "//androidx.cardview.widget.CardView[descendant::*[contains(@text, " + xpathValue + ")]]");
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
        return AppiumBy.xpath("//android.widget.TextView[@text='" + count + "']");
    }
}
