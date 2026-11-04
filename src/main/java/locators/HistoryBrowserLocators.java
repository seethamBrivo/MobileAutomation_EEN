package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for opening the History browser from a camera preview or the live video, and for its timeline,
 * cursor position and player controls.
 */
public final class HistoryBrowserLocators {

    private HistoryBrowserLocators() {}

    // --- Camera preview --------------------------------------------------------

    // btn_more carries no content-desc, so it is addressed by resource-id inside the first card's content-desc.
    public static final By FIRST_PREVIEW_THREE_DOTS =
            AppiumBy.xpath("//*[@content-desc='cell_at_0_preview_view_preview_card']"
                    + "//android.widget.ImageView[@resource-id='com.een.vms.android:id/btn_more']");

    public static final By FIRST_PREVIEW_CARD =
            AppiumBy.accessibilityId("cell_at_0_preview_view_preview_card");

    // --- Live video ------------------------------------------------------------

    public static final By LIVE_STREAM_SCREEN =
            AppiumBy.accessibilityId("LiveStream");

    // One toggle button: it opens the history browser from the live video and the live video from the history
    // browser. Its text changes with the screen, so it is found by id and the screen is checked by content-desc.
    public static final By NAVIGATION_BUTTON =
            AppiumBy.id("com.een.vms.android:id/navigation_button");

    // --- History browser -------------------------------------------------------

    public static final By HISTORY_BROWSER_SCREEN =
            AppiumBy.accessibilityId("HistoryBrowser");

    // The back button's label: the camera's name.
    public static final By CAMERA_NAME =
            AppiumBy.id("com.een.vms.android:id/camera_name");

    // The player holds the camera preview (a still frame while paused) and the event overlay.
    public static final By PLAYER_PREVIEW =
            AppiumBy.id("com.een.vms.android:id/player_container");

    // The save (export) button of the action bar, left of the calendar picker.
    public static final By SAVE_BUTTON =
            AppiumBy.id("com.een.vms.android:id/export");

    // The red line stays fixed while the rows scroll under it, so the cursor position is the time shown here.
    public static final By CURRENT_POSITION =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/calendar']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/text']");

    // The time above the video, which follows the frame that is shown.
    public static final By PREVIEW_TIME =
            AppiumBy.id("com.een.vms.android:id/frame_timestamp");

    public static final By TIMELINE_CONTAINER =
            AppiumBy.id("com.een.vms.android:id/timeline_container");

    // Landscape only: the timeline is a side panel with its own close button, and a button on the screen's right
    // edge reopens it.
    public static final By TIMELINE_CLOSE_BUTTON =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//*[@resource-id='com.een.vms.android:id/close']");

    public static final By TIMELINE_BUTTON =
            AppiumBy.id("com.een.vms.android:id/timeline_sidebar");

    // --- Event filter ----------------------------------------------------------

    public static final By FILTER_BUTTON =
            AppiumBy.id("com.een.vms.android:id/filter");

    // The filter button holds only an icon; a text node inside it would be an indication number.
    public static final By FILTER_BUTTON_INDICATION =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/filter']//android.widget.TextView");

    // --- Timeline controls -----------------------------------------------------

    public static final By PREVIOUS_FRAME_BUTTON =
            AppiumBy.id("com.een.vms.android:id/prev_key_frame");

    public static final By PREVIOUS_EVENT_BUTTON =
            AppiumBy.id("com.een.vms.android:id/prev_event");

    public static final By PLAY_PAUSE_BUTTON =
            AppiumBy.id("com.een.vms.android:id/video_action");

    public static final By NEXT_EVENT_BUTTON =
            AppiumBy.id("com.een.vms.android:id/next_event");

    public static final By NEXT_FRAME_BUTTON =
            AppiumBy.id("com.een.vms.android:id/next_key_frame");

    // --- Timeline --------------------------------------------------------------

    public static final By TIMELINE_EVENT_NAMES =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//*[@resource-id='com.een.vms.android:id/event_card']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/name']");

    // The title above the rows shares the "timeline" id with the panel around it, so the text tells them apart.
    public static final By TIMELINE_LABEL =
            AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/timeline'"
                    + " and @text='Timeline']");

    // The "content" id is shared with other screens, so the list is scoped to the timeline container.
    public static final By TIMELINE_LIST =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//androidx.recyclerview.widget.RecyclerView");

    // The full "yyyy-MM-dd HH:mm:ss" time of each event card, so events can be counted against the cursor.
    public static final By TIMELINE_EVENT_TIMES =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//*[@resource-id='com.een.vms.android:id/event_card']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp']");

    // Event cards reuse the "timestamp" id; only a row label sits next to the row's recordings_line bar.
    public static final By TIMELINE_ROW_TIMESTAMPS =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp'"
                    + " and ../*[@resource-id='com.een.vms.android:id/recordings_line']]");

    // --- Export screen ---------------------------------------------------------

    // SAVE TO section
    public static final By SAVE_TO_SECTION_TITLE =
            AppiumBy.xpath("//android.widget.TextView[@content-desc='save_destination_header_sub_header']");

    // Downloads selected indicator (default selection)
    public static final By DOWNLOADS_SELECTED_INDICATOR =
            AppiumBy.accessibilityId("downloads_destination_row_selected_icon");

    // TODO: Unscoped bare @text match - could false-positive on any "Archive" text.
    //       Scope to Export screen parent or request testTag from developers.
    public static final By ARCHIVE_TEXT =
            AppiumBy.xpath("//android.widget.TextView[@text='Archive']");

    // Select button in top right corner of Archive folder picker
    // TODO: Unscoped bare @text match - could false-positive on any "Select" text.
    //       Scope to folder picker parent or request testTag from developers.
    public static final By SELECT_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Select\"]");

    // First folder in the Archive folder picker
    // TODO: Positional XPath is fragile - will break if Compose view hierarchy changes.
    //       Request testTag from developers for stable identification.
    public static final By FIRST_FOLDER =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View"
                    + "/android.view.View[2]/android.view.View[1]");

    // Selected folder name displayed on Export screen after picking folder
    public static final By SELECTED_FOLDER_NAME =
            AppiumBy.xpath("//android.widget.TextView[@content-desc='archive_destination_row_sub_header']");

    // RANGE section
    public static final By RANGE_SECTION_TITLE =
            AppiumBy.xpath("//android.widget.TextView[@content-desc=\"duration_header_sub_header\"]");

    public static final By START_TIME_FIELD =
            AppiumBy.accessibilityId("start_time_row_header");

    public static final By END_TIME_FIELD =
            AppiumBy.accessibilityId("end_time_row_header");

    // Download type section
    public static final By DOWNLOAD_TYPE_HEADING =
            AppiumBy.xpath("//android.widget.TextView[@content-desc='type_header_sub_header']");

    public static final By DOWNLOAD_TYPE_VIDEO =
            AppiumBy.accessibilityId("video_selection_row_header");

    // Duration field
    public static final By DURATION_FIELD =
            AppiumBy.xpath("//android.widget.TextView[@content-desc=\"duration_row_header\"]");

    // Time Stamp toggle (ON by default)
    // TODO: Positional XPath is too generic (matches any View/View/View) - very fragile.
    //       Request specific testTag or content-desc from developers for reliable identification.
    public static final By TIME_STAMP_TOGGLE =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View"
                    + "/android.view.View");

    // Time Zone value
    public static final By TIME_ZONE_VALUE =
            AppiumBy.xpath("//android.widget.TextView[@content-desc='timezone_row_value']");

    // File name field for validation tests
    public static final By FILE_NAME_FIELD =
            AppiumBy.accessibilityId("file_name_row_text_field");

    public static final By VALIDATION_ERROR_TEXT =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Should not contain')]");

    // Export button at bottom of screen
    public static final By EXPORT_BUTTON =
            AppiumBy.xpath("//android.widget.Button[@content-desc=\"confirm_button\"]");

    // Cancel button at bottom of screen
    public static final By CANCEL_BUTTON =
            AppiumBy.accessibilityId("close_button");

    // Snackbar message after export
    public static final By SNACKBAR_MESSAGE =
            AppiumBy.accessibilityId("snackbar_een_text");

    // See progress button on snackbar
    public static final By SEE_PROGRESS_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@content-desc='snackbar_een_button']");

    // Progress tab/button
    // TODO: Unscoped bare @text match - could false-positive on any "Progress" text.
    //       Request testTag or content-desc from developers for reliable identification.
    public static final By PROGRESS_TAB =
            AppiumBy.xpath("//android.widget.TextView[@text='Progress']");
}
