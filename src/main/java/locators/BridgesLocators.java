package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the bridge screens: the devices list, the add-bridge form, the bridge list and the bridge settings.
 * <p>
 * Several XPaths are positional Compose paths because the app has no testTag/content-desc on those nodes yet.
 * The TODO beside each marks a locator to replace once the app exposes a stable identifier.
 */
public final class BridgesLocators {

    private BridgesLocators() {}

    // --- Bottom navigation and tabs --------------------------------------------

    // iOS: the tab bar's button; its name varies, and the top bar has its own "More" button.
    public static final By MORE_MENU_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='More']"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton'"
                            + " AND label == 'More' AND name != 'navigationBar_customButton'")); // ios

    public static final By DASHBOARD_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Dashboard']"), // android
            AppiumBy.accessibilityId("dashboard_button")); // ios

    public static final By ALL_DEVICES_PAGE =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View");

    public static final By ALL_DEVICES_SECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='All devices']"), // android
            AppiumBy.accessibilityId("All devices")); // ios

    public static final By BRIDGES_TAB =
            AppiumBy.xpath("//android.widget.TextView[@text='Bridges']");

    public static final By CAMERAS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");

    public static final By SPEAKERS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Speakers']");

    public static final By SWITCHES_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Switches']");

    // --- Add-bridge flow -------------------------------------------------------

    // Positional Compose path: no testTag exists on this node yet. Tracked for the mobile team.
    // The header is [back, title, sort, add] with no ids, so the "+" is the last clickable item after the
    // "All devices" title. Only this form is used: a positional form
    // (".../View[5]/View[2]/View") lands on the filter icon of the Bridges list screen, and the tap then opens the
    // filter instead of the add menu. clickOnAddBridgeIcon() makes sure the All devices screen is showing first.
    public static final By ADD_BRIDGE_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]//android.widget.TextView[@text=\"All devices\"]/following-sibling::android.view.View[@clickable=\"true\"][last()]");

    public static final By ADD_BRIDGE_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Add bridge']");

    public static final By ADD_CAMERA_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Add camera']");

    public static final By ADD_CAMERA_DIRECT_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Add Camera Direct']");

    public static final By BRIDGE_NAME_FIELD =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.widget.EditText");

    public static final By ATTACH_ID_FIELD =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");

    public static final By ADD_BRIDGE_BUTTON =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.Button");

    public static final By CONFLICT_ERROR_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'There is a conflict in the operation')]");

    // --- Bridge list -----------------------------------------------------------

    public static final By ONLINE_BRIDGE_ROWS =
            AppiumBy.xpath("//android.widget.TextView[@text='Online']/ancestor::android.view.View[@clickable='true'][1]");

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes
    public static final By BRIDGES_BACK_BUTTON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View");

    public static final By FIRST_BRIDGE_IN_LIST =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]");

    public static final By FIRST_BRIDGE_NAME_TEXT =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.widget.TextView[1]");

    public static final By BRIDGE_THREE_DOTS_BUTTON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View/android.view.View[7]");

    public static final By FILTER_SEARCH_FIELD =
            AppiumBy.xpath("//android.widget.EditText");

    public static final By NO_MATCHING_BRIDGES_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[@text='No matching bridges']");

    public static final By RESTART_BRIDGE_CONTEXT_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");

    // --- Delete bridge ---------------------------------------------------------

    public static final By DELETE_BRIDGE_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text='Delete bridge']");

    // TODO: Replace with testTag when mobile team adds proper dialog identifiers
    // Delete confirmation popup button (Compose UI positional path)
    public static final By DELETE_CONFIRMATION_DIALOG =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View");

    // TODO: Replace with testTag when mobile team adds proper dialog identifiers
    public static final By DELETE_BUTTON =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");

    // --- Bridge settings screen: status and name -------------------------------

    public static final By BRIDGE_SETTINGS_PAGE =
            AppiumBy.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout");

    public static final By STATUS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Status']");

    public static final By ONLINE_STATUS =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Online')]");

    public static final By OFFLINE_STATUS =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Offline')]");

    // Generic: the name editor is the only EditText on its screen. No testTag available yet.
    public static final By BRIDGE_NAME_EDIT_FIELD =
            AppiumBy.xpath("//android.widget.EditText");

    // --- Bridge settings screen: restart ---------------------------------------

    public static final By RESTART_BRIDGE_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");

    public static final By RESTART_CONFIRMATION_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restart') or contains(@text, 'reboot')]");

    public static final By CONFIRM_RESTART_BUTTON =
            AppiumBy.xpath("//android.widget.Button[@content-desc='positive_button'] | //android.widget.TextView[@text='RESTART']");

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the post-restart confirmation snackbar carries no content-desc/testTag yet.
    public static final By BRIDGE_RESTARTED_MESSAGE =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View");

    public static By contextMenuOption(String text) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + text + "']");
    }
}
