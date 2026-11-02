package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the Video Search screen and the back navigation from the VSP Events screen.
 */
public final class VideoSearchLocators {

    private VideoSearchLocators() {}

    // --- Navigation ------------------------------------------------------------------

    // Stable: Back button at fixed position [6] in VspMain screen
    public static final By BACK_BUTTON_FROM_VSP_EVENTS = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View/android.view.View");

    // --- Search ----------------------------------------------------------------------

    public static final By SEARCH_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Search']");
    public static final By DATE_FIELD = AppiumBy.id("com.een.vms.android:id/date");

    public static final By SEARCH_ICON = AppiumBy.id("com.een.vms.android:id/img_search");
    public static final By SEARCH_HINT_FIELD = AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/hint\"]");
    // Safe: EditText appears only after clicking hint field, no other EditText elements visible
    public static final By SEARCH_INPUT_FIELD = AppiumBy.className("android.widget.EditText");
    public static final By BACK_BUTTON = AppiumBy.id("com.een.vms.android:id/img_back");

    public static final By SUGGESTION_PERSON = AppiumBy.xpath("//android.widget.TextView[@text='Person']");
    public static final By SUGGESTION_MAN = AppiumBy.xpath("//android.widget.TextView[@text='Man']");
    public static final By SUGGESTION_WOMAN = AppiumBy.xpath("//android.widget.TextView[@text='Woman']");
    public static final By SUGGESTION_VEHICLE = AppiumBy.xpath("//android.widget.TextView[@text='Vehicle']");
    public static final By SUGGESTION_BACKPACK = AppiumBy.xpath("//android.widget.TextView[@text='Backpack']");

    // --- Key Image Detail Screen ------------------------------------------------

    public static final By CAMERA_PREVIEW_FIRST = AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/overlay\"])[1]");
    public static final By KEY_IMAGE_OVERLAY = AppiumBy.xpath("//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/overlay\"]");
    public static final By CAMERA_NAME = AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/title\"]");
    public static final By DATE_SUBTITLE = AppiumBy.id("com.een.vms.android:id/subtitle");
    public static final By TIMESTAMP = AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/timestamp\"]");
    // Stable: First LinearLayout in tab_layout displays current event number (e.g., "1 of 5")
    public static final By EVENT_NUMBER_SCOPE = AppiumBy.xpath("//android.widget.HorizontalScrollView[@resource-id=\"com.een.vms.android:id/tab_layout\"]/android.widget.LinearLayout/android.widget.LinearLayout[1]");
    public static final By TAGS_TITLE = AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/tags_title\"]");
    public static final By ANY_TAG = AppiumBy.xpath("//android.widget.Button[@resource-id=\"com.een.vms.android:id/chip\"]");
    public static final By VIEW_HISTORY_BROWSER_BUTTON = AppiumBy.xpath("//android.widget.Button[@content-desc=\"viewInHistoryBrowser_button\"]");
    public static final By CLOSE_BUTTON = AppiumBy.xpath("//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/close\"]");
}
