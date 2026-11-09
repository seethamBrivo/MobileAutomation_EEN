package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for opening the History browser from a camera preview or the live video, and for its timeline,
 * cursor position and player controls.
 */
public final class HistoryBrowserLocators {

    private HistoryBrowserLocators() {}

    // --- Shared fragments ------------------------------------------------------

    private static final String ANDROID_TIMELINE = "//*[@resource-id='com.een.vms.android:id/timeline_container']";

    private static final String ANDROID_EVENT_CARD =
            ANDROID_TIMELINE + "//*[@resource-id='com.een.vms.android:id/event_card']";

    private static final String IOS_TIMELINE_TEXTS = "//XCUIElementTypeScrollView//XCUIElementTypeStaticText";

    // The timeline labels are read as 24-hour times (see HistoryBrowserPage.parseTimeLabel), so a row label is
    // "HH:mm:ss" and an event card time "yyyy-MM-dd HH:mm:ss". iOS gives the rows no identifier, so they are told apart
    // by the shape of the text. TODO: replace with accessibility identifiers once the iOS app has them.
    private static final String IOS_ROW_TIME_NAME = "string-length(@name)=8 and substring(@name,3,1)=':'";

    private static final String IOS_DATE_TIME_NAME = "string-length(@name)=19 and substring(@name,5,1)='-'";

    // The "Timeline" title is visible while the rows under it are reported as not visible.
    private static final By IOS_TIMELINE_TITLE = AppiumBy.accessibilityId("Timeline");

    // The timeline is the scroll view that holds text; it has no identifier of its own, so this matches the first
    // such scroll view. TODO: replace with an accessibility identifier once the iOS app has one.
    private static final By IOS_TIMELINE_SCROLL =
            AppiumBy.xpath("//XCUIElementTypeScrollView[.//XCUIElementTypeStaticText]");

    // --- Camera preview --------------------------------------------------------

    // btn_more carries no content-desc, so it is addressed by resource-id inside the first card's content-desc.
    public static final By FIRST_PREVIEW_THREE_DOTS = PlatformLocator.get(
            AppiumBy.xpath("//*[@content-desc='cell_at_0_preview_view_preview_card']"
                    + "//android.widget.ImageView[@resource-id='com.een.vms.android:id/btn_more']"), // android
            AppiumBy.accessibilityId("cell_at_0_preview_view_moreButton")); // ios

    public static final By FIRST_PREVIEW_CARD = PlatformLocator.get(
            AppiumBy.accessibilityId("cell_at_0_preview_view_preview_card"), // android
            AppiumBy.accessibilityId("cell_at_0_preview_view")); // ios

    // --- Live video ------------------------------------------------------------

    public static final By LIVE_STREAM_SCREEN = PlatformLocator.get(
            AppiumBy.accessibilityId("LiveStream"), // android
            AppiumBy.accessibilityId("LiveVideoView")); // ios

    // One toggle button: it opens the history browser from the live video and the live video from the history
    // browser. Its text changes with the screen, so it is found by id and the screen is checked by content-desc.
    public static final By NAVIGATION_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/navigation_button"), // android
            AppiumBy.accessibilityId("streamerNavigationBar_playerButton")); // ios

    // --- History browser -------------------------------------------------------

    public static final By HISTORY_BROWSER_SCREEN = PlatformLocator.get(
            AppiumBy.accessibilityId("HistoryBrowser"), // android
            IOS_TIMELINE_TITLE); // ios

    // The back button's label: the camera's name.
    public static final By CAMERA_NAME = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/camera_name"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeStaticText")); // ios

    // The player holds the camera preview (a still frame while paused) and the event overlay. iOS gives the video no
    // identifier, so it is the unnamed image that is wider than an icon. TODO: replace with an accessibility id.
    public static final By PLAYER_PREVIEW = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/player_container"), // android
            AppiumBy.xpath("//XCUIElementTypeImage[not(@name) and @width >= 200]")); // ios

    // The save (export) button of the action bar, left of the calendar picker.
    public static final By SAVE_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/export"), // android
            AppiumBy.accessibilityId("ic_download")); // ios

    // The red line stays fixed while the rows scroll under it, so the cursor position is the time shown here.
    public static final By CURRENT_POSITION = PlatformLocator.get(
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/calendar']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/text']"), // android
            AppiumBy.xpath("//XCUIElementTypeImage[@name='calendar']"
                    + "/preceding-sibling::XCUIElementTypeStaticText[1]")); // ios

    // The time above the video, which follows the frame that is shown.
    public static final By PREVIEW_TIME = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/frame_timestamp"), // android
            AppiumBy.xpath("(//XCUIElementTypeStaticText[" + IOS_DATE_TIME_NAME
                    + " and not(ancestor::XCUIElementTypeScrollView)])[1]")); // ios

    public static final By TIMELINE_CONTAINER = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/timeline_container"), // android
            IOS_TIMELINE_SCROLL); // ios

    // Landscape only: the timeline is a side panel with its own close button, and a button on the screen's right
    // edge reopens it. Android only: the iOS app has no such buttons, so these two have no iOS value on purpose.
    public static final By TIMELINE_CLOSE_BUTTON =
            AppiumBy.xpath(ANDROID_TIMELINE + "//*[@resource-id='com.een.vms.android:id/close']");

    public static final By TIMELINE_BUTTON =
            AppiumBy.id("com.een.vms.android:id/timeline_sidebar");

    // iOS only, so it has no Android value on purpose: the overlay on the video prints the shown frame's time with
    // milliseconds ("header: yyyy-MM-dd HH:mm:ss.SSS"). The cursor label and the preview time stop at whole seconds,
    // which cannot tell apart two frames less than a second apart. TODO: replace with an accessibility id.
    public static final By FRAME_TIME_OVERLAY =
            AppiumBy.xpath("//XCUIElementTypeStaticText[starts-with(@name, 'header: ')]");

    // --- Event filter ----------------------------------------------------------

    public static final By FILTER_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/filter"), // android
            AppiumBy.accessibilityId("line.3.horizontal.decrease")); // ios

    // The filter button holds only an icon; a text node inside it would be an indication number.
    public static final By FILTER_BUTTON_INDICATION = PlatformLocator.get(
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/filter']//android.widget.TextView"), // android
            AppiumBy.xpath("//XCUIElementTypeButton[@name='line.3.horizontal.decrease']"
                    + "//XCUIElementTypeStaticText")); // ios

    // --- Timeline controls -----------------------------------------------------

    public static final By PREVIOUS_FRAME_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/prev_key_frame"), // android
            AppiumBy.accessibilityId("prev_frame_v3")); // ios

    public static final By PREVIOUS_EVENT_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/prev_event"), // android
            AppiumBy.accessibilityId("prev_event_v3")); // ios

    public static final By PLAY_PAUSE_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/video_action"), // android
            AppiumBy.iOSNsPredicateString("name == 'play_v3' OR name == 'pause_v3'")); // ios

    public static final By NEXT_EVENT_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/next_event"), // android
            AppiumBy.accessibilityId("next_event_v3")); // ios

    public static final By NEXT_FRAME_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/next_key_frame"), // android
            AppiumBy.accessibilityId("next_frame_v3")); // ios

    // --- Timeline --------------------------------------------------------------

    public static final By TIMELINE_EVENT_NAMES = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_EVENT_CARD
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/name']"), // android
            AppiumBy.xpath(IOS_TIMELINE_TEXTS + "[not(" + IOS_ROW_TIME_NAME + ") and not(" + IOS_DATE_TIME_NAME
                    + ")]")); // ios

    // The title above the rows shares the "timeline" id with the panel around it, so the text tells them apart.
    public static final By TIMELINE_LABEL = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/timeline'"
                    + " and @text='Timeline']"), // android
            IOS_TIMELINE_TITLE); // ios

    // The "content" id is shared with other screens, so the list is scoped to the timeline container.
    public static final By TIMELINE_LIST = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_TIMELINE + "//androidx.recyclerview.widget.RecyclerView"), // android
            IOS_TIMELINE_SCROLL); // ios

    // The full "yyyy-MM-dd HH:mm:ss" time of each event card, so events can be counted against the cursor.
    public static final By TIMELINE_EVENT_TIMES = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_EVENT_CARD
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp']"), // android
            AppiumBy.xpath(IOS_TIMELINE_TEXTS + "[" + IOS_DATE_TIME_NAME + "]")); // ios

    // Event cards reuse the "timestamp" id; only a row label sits next to the row's recordings_line bar.
    public static final By TIMELINE_ROW_TIMESTAMPS = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_TIMELINE
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp'"
                    + " and ../*[@resource-id='com.een.vms.android:id/recordings_line']]"), // android
            AppiumBy.xpath(IOS_TIMELINE_TEXTS + "[" + IOS_ROW_TIME_NAME + "]")); // ios

    /**
     * The timeline has drawn its rows. iOS reports the rows of the landscape timeline, and the first row above the
     * fold in portrait, as not visible although they are on screen, so the visible "Timeline" title stands for them.
     */
    public static final By TIMELINE_ROWS_LOADED = PlatformLocator.get(
            TIMELINE_ROW_TIMESTAMPS, // android
            IOS_TIMELINE_TITLE); // ios

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
            AppiumBy.xpath("//android.widget.TextView[@text='Select']");

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
