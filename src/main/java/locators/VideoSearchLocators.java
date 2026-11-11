package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

/**
 * Locators for the Video Search screen, its event page and the back navigation from the VSP Events screen.
 */
public final class VideoSearchLocators {

    private VideoSearchLocators() {}

    // --- Navigation ------------------------------------------------------------------

    // Stable: Back button at fixed position [6] in VspMain screen
    public static final By BACK_BUTTON_FROM_VSP_EVENTS = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View/android.view.View"), // android
            AppiumBy.accessibilityId("BackButton")); // ios

    // --- Search ----------------------------------------------------------------------

    // iOS: the date button of Video Search, whose name is the date ("Nov 05, 2026")
    public static final By DATE_FIELD = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/date"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' AND name MATCHES '^[A-Z][a-z]{2} [0-9]{1,2}, [0-9]{4}$'")); // ios

    // --- Video Search home -----------------------------------------------------------

    // The bottom bar's Search tab, found by its content-desc inside the bar so no other "Search" text can match.
    public static final By SEARCH_TAB = PlatformLocator.get(
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/bottom_navigation']"
                    + "//*[@content-desc='Search']"), // android
            BottomNavigationLocators.SEARCH_TAB); // ios

    public static final By VIDEO_SEARCH_SCREEN = PlatformLocator.get(
            AppiumBy.accessibilityId("VideoSearchMain"), // android
            AppiumBy.accessibilityId("VideoSearchViewController")); // ios

    public static final By LATEST_EVENTS_HEADLINE = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/latest_events_headline"), // android
            AppiumBy.accessibilityId("Latest events")); // ios

    // Skeleton shown in place of the Person section until its events have loaded.
    public static final By PEOPLE_LOADING = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/people_loading"), // android
            AppiumBy.accessibilityId("loading_indicator")); // ios

    // iOS: the card has no identifier, so it is the first cell of the first scroll view in the collection (the Person
    // row). TODO: replace with an accessibility identifier once the iOS app has one.
    private static final String IOS_FIRST_PERSON_CARD =
            "(//XCUIElementTypeCollectionView//XCUIElementTypeScrollView[1]/XCUIElementTypeCell)[1]";

    public static final By FIRST_PERSON_CARD = PlatformLocator.get(
            AppiumBy.xpath("(//*[@resource-id='com.een.vms.android:id/people']"
                    + "//*[@resource-id='com.een.vms.android:id/preview'])[1]"), // android
            AppiumBy.xpath(IOS_FIRST_PERSON_CARD)); // ios

    // --- Search functionality --------------------------------------------------------

    public static final By SEARCH_ICON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/img_search"), // android
            AppiumBy.accessibilityId("navigationBar_searchButton")); // ios

    // iOS: the hint and the input are one text field, whose value is the hint while it is empty.
    public static final By SEARCH_HINT_FIELD = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/hint']"), // android
            AppiumBy.accessibilityId("navigationBar_searchField")); // ios

    public static final By SEARCH_INPUT_FIELD = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.EditText[@resource-id='com.een.vms.android:id/edit_text']"), // android
            AppiumBy.accessibilityId("navigationBar_searchField")); // ios

    public static final By SEARCH_BACK_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/img_back"), // android
            AppiumBy.accessibilityId("navigationBar_searchCloseButton")); // ios

    // Only the results screen has day arrows, which makes this a marker of the results.
    public static final By RESULTS_PREVIOUS_DAY_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/left_arrow"), // android
            AppiumBy.accessibilityId("ic left arrow")); // ios

    // iOS: every result card shows its position in the group ("1 / 78").
    public static final By FIRST_RESULT_CARD = PlatformLocator.get(
            AppiumBy.xpath("(//*[@resource-id='com.een.vms.android:id/aggregates']"
                    + "//*[@resource-id='com.een.vms.android:id/preview'])[1]"), // android
            iosTextMatching("^[0-9]+ / [0-9]+$", true)); // ios

    // iOS only: the Search key of the keyboard (Android presses its Enter key instead).
    public static final By KEYBOARD_SEARCH_KEY = AppiumBy.iOSClassChain(
            "**/XCUIElementTypeKeyboard/**/XCUIElementTypeButton[`name == \"Search\"`]");

    // --- Event page (bottom sheet opened from a card) ------------------------------------

    private static final String EVENT_SHEET = "//*[@resource-id='com.een.vms.android:id/design_bottom_sheet']";

    public static final By EVENT_CAMERA_NAME = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/title']"), // android
            AppiumBy.xpath("//XCUIElementTypeButton[@name='ic close']"
                    + "/following-sibling::XCUIElementTypeStaticText[1]")); // ios

    public static final By EVENT_DATE = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/subtitle']"), // android
            iosTextMatching("^[A-Z][a-z]{2} [0-9]{1,2}, [0-9]{4}$", true)); // ios

    // The frame's time of day. Only the loaded sheet has it, so it also marks the end of the loading shimmer.
    public static final By EVENT_TIME = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/key_images']"
                    + "//android.widget.TextView[@resource-id='com.een.vms.android:id/timestamp']"), // android
            iosTextMatching("^[0-9]{2}:[0-9]{2}:[0-9]{2}$", true)); // ios

    // "current event / events in the scope", for example 145/146.
    public static final By EVENT_COUNTER = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/counter_text']"), // android
            iosTextMatching("^[0-9]+ / [0-9]+$", true)); // ios

    public static final By EVENT_TAGS_TITLE = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/tags_title']"), // android
            AppiumBy.accessibilityId("Relevant tags")); // ios

    // iOS: the tags have no identifier, so this is the text after the "Relevant tags" title, which is the first tag
    // only (Android matches every tag button). TODO: replace with an accessibility identifier once the iOS app has one.
    public static final By EVENT_TAGS = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//*[@resource-id='com.een.vms.android:id/tags_container']"
                    + "//android.widget.Button"), // android
            AppiumBy.xpath("//XCUIElementTypeStaticText[@name='Relevant tags']"
                    + "/following::XCUIElementTypeStaticText[1]")); // ios

    public static final By VIEW_IN_HISTORY_BROWSER_BUTTON = PlatformLocator.get(
            AppiumBy.accessibilityId("viewInHistoryBrowser_button"), // android
            AppiumBy.accessibilityId("View in history browser")); // ios

    // --- Key image detail screen (overlays in event bottom sheet) -------------------

    private static final String KEY_IMAGE_OVERLAY_BASE =
            "//android.widget.ImageView[@resource-id='com.een.vms.android:id/overlay']";

    public static final By CAMERA_PREVIEW_FIRST = PlatformLocator.get(
            AppiumBy.xpath("(" + KEY_IMAGE_OVERLAY_BASE + ")[1]"), // android
            AppiumBy.xpath(IOS_FIRST_PERSON_CARD)); // ios

    // iOS: the scroll view after the sheet's close button. TODO: use an accessibility identifier once the app has one.
    public static final By KEY_IMAGE_OVERLAY = PlatformLocator.get(
            AppiumBy.xpath(KEY_IMAGE_OVERLAY_BASE), // android
            AppiumBy.xpath("//XCUIElementTypeButton[@name='ic close']"
                    + "/following::XCUIElementTypeScrollView[1]")); // ios

    // iOS only: the texts inside the key image, found by presence (iOS reports the time as not visible once a tag is
    // tapped).
    public static final By KEY_IMAGE_TEXTS = AppiumBy.xpath("//XCUIElementTypeButton[@name='ic close']"
            + "/following::XCUIElementTypeScrollView[1]//XCUIElementTypeStaticText");

    public static final By CLOSE_BUTTON = PlatformLocator.get(
            AppiumBy.xpath(EVENT_SHEET + "//android.widget.ImageView[@resource-id='com.een.vms.android:id/close']"), // android
            AppiumBy.accessibilityId("ic close")); // ios

    // iOS only: shown next to a tag once it has been tapped.
    public static final By SELECTED_TAG_SEARCH_BUTTON = AppiumBy.accessibilityId("ic videosearch");

    // --- Helper method for parameterized suggestion locators ------------------------

    public static By suggestionByText(String text) {
        return PlatformLocator.isIos()
                ? AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND name == '"
                        + text + "'")
                : AppiumBy.xpath("//android.widget.TextView[@text=" + XPathUtils.xpathLiteral(text) + "]");
    }

    public static By appliedSearchTerm(String term) {
        return PlatformLocator.isIos()
                ? AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND name == '"
                        + term + "'")
                : AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/chip_group']//*[@text="
                        + XPathUtils.xpathLiteral(term) + "]");
    }

    public static By recentSearch(String phrase) {
        return PlatformLocator.isIos()
                ? AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND visible == 1 AND name == '"
                        + phrase + "'")
                : AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/search_history']"
                        + "//*[@resource-id='com.een.vms.android:id/title' and @text="
                        + XPathUtils.xpathLiteral(phrase) + "]");
    }

    /** iOS: a static text whose name matches {@code regex}, optionally only while it is on screen. */
    private static By iosTextMatching(String regex, boolean visibleOnly) {
        String visible = visibleOnly ? " AND visible == 1" : "";
        return AppiumBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText'" + visible + " AND name MATCHES '" + regex + "'");
    }
}
