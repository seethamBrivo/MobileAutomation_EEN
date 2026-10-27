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

    // The same navigation_button reads "Live" on the history browser and "History" on the live video.
    public static final By LIVE_BUTTON =
            AppiumBy.xpath("//android.widget.Button[@resource-id='com.een.vms.android:id/navigation_button'"
                    + " and @text='Live']");

    public static final By HISTORY_BUTTON =
            AppiumBy.xpath("//android.widget.Button[@resource-id='com.een.vms.android:id/navigation_button'"
                    + " and @text='History']");

    // --- History browser -------------------------------------------------------

    public static final By HISTORY_BROWSER_SCREEN =
            AppiumBy.accessibilityId("HistoryBrowser");

    // The red line stays fixed while the rows scroll under it, so the cursor position is the time shown here.
    public static final By CURRENT_POSITION =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/calendar']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/text']");

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

    // The "content" id is shared with other screens, so the list is scoped to the timeline container.
    public static final By TIMELINE_LIST =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//androidx.recyclerview.widget.RecyclerView");

    // Event cards reuse the "timestamp" id; only a row label sits next to the row's recordings_line bar.
    public static final By TIMELINE_ROW_TIMESTAMPS =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/timeline_container']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp'"
                    + " and ../*[@resource-id='com.een.vms.android:id/recordings_line']]");
}
