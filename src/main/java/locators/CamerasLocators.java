package locators;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the Cameras screen (reached from the More menu) and the camera search helper shared with the
 * bridge cameras screen: camera list, context menu, preview overlay, status labels and the search/filter UI.
 */
public final class CamerasLocators {

    private CamerasLocators() {}

    // --- Navigation and list ---------------------------------------------------

    public static final By CAMERAS_ITEM =
            AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");

    public static final By CAMERA_LIST_CONTAINER =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView");

    public static final By FIRST_CAMERA_IN_LIST =
            AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]");

    public static final By FIRST_OFFLINE_OR_ERROR_CAMERA =
            AppiumBy.xpath("(//android.widget.TextView[@text='Offline' or @text='Camera error'])[1]/ancestor::android.view.View[@clickable='true'][1]");

    public static final By FIRST_ONLINE_CAMERA =
            AppiumBy.xpath("(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[@clickable='true'][1]");

    public static final By ALL_VISIBLE_CAMERAS =
            AppiumBy.xpath(
            "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]]//android.widget.TextView[1]"
        );

    // --- Context menu ----------------------------------------------------------

    public static final By CONTEXT_MENU_CONTAINER =
            AppiumBy.xpath("//android.view.ViewGroup[.//android.widget.TextView[@text='Camera settings']]");

    public static final By CAMERA_SETTINGS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Camera settings\"]");

    public static final By HISTORY_BROWSER_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='History browser']");

    public static final By LIVE_VIDEO_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Live video']");

    public static final By TURN_ON_CAMERA_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Turn on camera']");

    public static final By TURN_OFF_CAMERA_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Turn off camera']");

    // --- Camera preview --------------------------------------------------------

    // TODO: Replace with testTag when mobile team adds an identifier to the preview overlay.
    // Positional: the preview container and its name label carry no content-desc/testTag yet.
    public static final By CAMERA_PREVIEW =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[2]");

    public static final By CAMERA_NAME_IN_PREVIEW =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[1]//android.widget.TextView[1]");

    public static final By TIMESTAMP =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, ':')]");

    // --- Status labels ---------------------------------------------------------

    public static final By ONLINE_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text='Online']");

    public static final By OFFLINE_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text='Offline']");

    public static final By INVALID_CREDENTIALS_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text='Invalid Credentials']");

    public static final By DEVICE_ERROR_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text='Device error']");

    public static final By CONNECTIVITY_ISSUE_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text='Connectivity issue']");

    public static final By TURNED_OFF_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text='Turned off']");

    // --- Search and filter -----------------------------------------------------

    public static final By SEARCH_BAR =
            AppiumBy.xpath("//android.widget.EditText");

    public static final By SEARCH_PLACEHOLDER =
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]");

    public static final By CAMERA_COUNTER =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]");

    public static final By EMPTY_STATE_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.EMPTY_STATE_MESSAGE + "\"]");
}
