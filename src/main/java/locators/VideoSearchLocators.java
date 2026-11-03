package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the Video Search screen, its event page and the back navigation from the VSP Events screen.
 */
public final class VideoSearchLocators {

    private VideoSearchLocators() {}

    // --- Navigation ------------------------------------------------------------------

    // Stable: Back button at fixed position [6] in VspMain screen
    public static final By BACK_BUTTON_FROM_VSP_EVENTS = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View/android.view.View");

    // --- Search ----------------------------------------------------------------------

    public static final By DATE_FIELD = AppiumBy.id("com.een.vms.android:id/date");

    // --- Video Search home -----------------------------------------------------------

    // The bottom bar's Search tab, found by its content-desc inside the bar so no other "Search" text can match.
    public static final By SEARCH_TAB =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/bottom_navigation']"
                    + "//*[@content-desc='Search']");

    public static final By VIDEO_SEARCH_SCREEN = AppiumBy.accessibilityId("VideoSearchMain");

    public static final By LATEST_EVENTS_HEADLINE = AppiumBy.id("com.een.vms.android:id/latest_events_headline");

    // Skeleton shown in place of the Person section until its events have loaded.
    public static final By PEOPLE_LOADING = AppiumBy.id("com.een.vms.android:id/people_loading");

    public static final By FIRST_PERSON_CARD =
            AppiumBy.xpath("(//*[@resource-id='com.een.vms.android:id/people']"
                    + "//*[@resource-id='com.een.vms.android:id/preview'])[1]");

    // --- Search functionality --------------------------------------------------------

    public static final By SEARCH_ICON = AppiumBy.id("com.een.vms.android:id/img_search");
    public static final By SEARCH_HINT_FIELD = AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/hint\"]");
    public static final By SEARCH_INPUT_FIELD = AppiumBy.xpath("//android.widget.EditText[@resource-id=\"com.een.vms.android:id/edit_text\"]");
    public static final By SEARCH_BACK_BUTTON = AppiumBy.id("com.een.vms.android:id/img_back");

    // --- Event page (bottom sheet opened from a card) ------------------------------------

    private static final String EVENT_SHEET = "//*[@resource-id='com.een.vms.android:id/design_bottom_sheet']";

    public static final By EVENT_CAMERA_NAME =
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/title']");

    public static final By EVENT_DATE =
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/subtitle']");

    // The frame's time of day. Only the loaded sheet has it, so it also marks the end of the loading shimmer.
    public static final By EVENT_TIME =
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/key_images']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp']");

    // "current event / events in the scope", for example 145/146.
    public static final By EVENT_COUNTER =
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/counter_text']");

    public static final By EVENT_TAGS_TITLE =
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/tags_title']");

    public static final By EVENT_TAGS =
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/tags_container']"
                    + "//android.widget.Button");

    public static final By VIEW_IN_HISTORY_BROWSER_BUTTON = AppiumBy.accessibilityId("viewInHistoryBrowser_button");

    // --- Key image detail screen (overlays in event bottom sheet) -------------------

    private static final String KEY_IMAGE_OVERLAY_BASE = "//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/overlay\"]";
    public static final By CAMERA_PREVIEW_FIRST = AppiumBy.xpath("(" + KEY_IMAGE_OVERLAY_BASE + ")[1]");
    public static final By KEY_IMAGE_OVERLAY = AppiumBy.xpath(KEY_IMAGE_OVERLAY_BASE);
    public static final By CLOSE_BUTTON = AppiumBy.xpath("//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/close\"]");

    // --- Helper method for parameterized suggestion locators ------------------------

    public static By suggestionByText(String text) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + text + "']");
    }
}
