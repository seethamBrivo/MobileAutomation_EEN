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
}
