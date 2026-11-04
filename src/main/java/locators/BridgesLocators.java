package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

/**
 * Locators for the bridge screens: the devices list, the add-bridge form, the bridge list and the bridge settings.
 * <p>
 * Several XPaths are positional Compose paths because the app has no testTag/content-desc on those nodes yet.
 * The TODO beside each marks a locator to replace once the app exposes a stable identifier.
 */
public final class BridgesLocators {

    private BridgesLocators() {}

    // --- Bottom navigation and tabs --------------------------------------------

    public static final By DASHBOARD_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Dashboard']"), // android
            AppiumBy.accessibilityId("dashboard_button")); // ios

    public static final By ALL_DEVICES_PAGE =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View"), // android
            AppiumBy.accessibilityId("All devices")); // ios

    public static final By ALL_DEVICES_SECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='All devices']"), // android
            AppiumBy.accessibilityId("All devices")); // ios

    public static final By BRIDGES_TAB =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Bridges']"), // android
            AppiumBy.accessibilityId("Bridges")); // ios

    public static final By CAMERAS_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Cameras']"), // android
            AppiumBy.accessibilityId("Cameras")); // ios

    public static final By SPEAKERS_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Speakers']"), // android
            AppiumBy.accessibilityId("Speakers")); // ios

    public static final By SWITCHES_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Switches']"), // android
            AppiumBy.accessibilityId("Switches")); // ios

    // --- Add-bridge flow -------------------------------------------------------

    // Positional Compose path: no testTag exists on this node yet. Tracked for the mobile team.
    // The header is [back, title, sort, add] with no ids, so the "+" is the last clickable item after the
    // "All devices" title. Only this form is used: a positional form
    // (".../View[5]/View[2]/View") lands on the filter icon of the Bridges list screen, and the tap then opens the
    // filter instead of the add menu. clickOnAddBridgeIcon() makes sure the All devices screen is showing first.
    public static final By ADD_BRIDGE_ICON =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]//android.widget.TextView[@text=\"All devices\"]/following-sibling::android.view.View[@clickable=\"true\"][last()]"), // android
            AppiumBy.accessibilityId("navigationBar_customButton")); // ios

    public static final By ADD_BRIDGE_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Add bridge']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Add bridge'")); // ios

    public static final By ADD_CAMERA_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Add camera']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Add camera'")); // ios

    public static final By ADD_CAMERA_DIRECT_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Add Camera Direct']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Add Camera Direct'")); // ios

    public static final By BRIDGE_NAME_FIELD =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.widget.EditText"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField' AND placeholderValue == 'Add bridge name'")); // ios

    public static final By ATTACH_ID_FIELD =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField' AND placeholderValue == 'XHVR-RCZ9-VBB5-JCBR'")); // ios

    public static final By ADD_BRIDGE_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.Button"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`name == 'Add bridge'`]")); // ios

    public static final By CONFLICT_ERROR_MESSAGE =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'There is a conflict in the operation')]"), // android
            AppiumBy.iOSNsPredicateString("label CONTAINS[c] 'conflict'")); // ios

    // --- Bridge list -----------------------------------------------------------

    public static final By ONLINE_BRIDGE_ROWS =
            AppiumBy.xpath("//android.widget.TextView[@text='Online']/ancestor::android.view.View[@clickable='true'][1]");

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes
    public static final By BRIDGES_BACK_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View"), // android
            AppiumBy.accessibilityId("BackButton")); // ios

    public static final By FIRST_BRIDGE_IN_LIST =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]"), // android
            AppiumBy.xpath("(//XCUIElementTypeImage[@name='ic_bridgeDevice'])[1]/following-sibling::XCUIElementTypeStaticText[1]")); // ios: the name text; tapping the row container does not open it

    public static final By FIRST_BRIDGE_NAME_TEXT =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.widget.TextView[1]"), // android
            AppiumBy.xpath("(//XCUIElementTypeImage[@name='ic_bridgeDevice'])[1]/following-sibling::XCUIElementTypeStaticText[1]")); // ios

    public static final By BRIDGE_THREE_DOTS_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View/android.view.View[7]"), // android
            AppiumBy.xpath("(//XCUIElementTypeButton[@name='ic_contextMenu'])[1]")); // ios

    public static final By FILTER_SEARCH_FIELD =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.EditText"), // android
            AppiumBy.accessibilityId("Filter by bridge")); // ios

    public static final By NO_MATCHING_BRIDGES_MESSAGE =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='No matching bridges']"), // android
            AppiumBy.accessibilityId("No matching bridges")); // ios

    public static final By RESTART_BRIDGE_CONTEXT_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Restart bridge'")); // ios

    // --- Delete bridge ---------------------------------------------------------

    public static final By DELETE_BRIDGE_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Delete bridge']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Delete bridge'")); // ios

    // TODO: Replace with testTag when mobile team adds proper dialog identifiers
    // Delete confirmation popup button (Compose UI positional path)
    public static final By DELETE_CONFIRMATION_DIALOG =
            PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeAlert")); // ios

    // TODO: Replace with testTag when mobile team adds proper dialog identifiers
    public static final By DELETE_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert//XCUIElementTypeButton[@name='Delete']")); // ios

    // --- Bridge settings screen: status and name -------------------------------

    public static final By BRIDGE_SETTINGS_PAGE =
            PlatformLocator.get(
            AppiumBy.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout"), // android
            AppiumBy.accessibilityId("GENERAL")); // ios

    public static final By STATUS_SECTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Status']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name == 'Status'")); // ios

    public static final By ONLINE_STATUS =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Online')]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label CONTAINS 'Online'")); // ios

    public static final By OFFLINE_STATUS =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Offline')]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label CONTAINS 'Offline'")); // ios

    // Generic: the name editor is the only EditText on its screen. No testTag available yet.
    public static final By BRIDGE_NAME_EDIT_FIELD =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.EditText"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField'")); // ios

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
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.TextView[@text='" + text + "']"), // android
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == '" + text + "'")); // ios
    }

    // --- iOS only --------------------------------------------------------------

    /** iOS: the name text of the first online bridge: the text just before its "Online" label. */
    public static final By FIRST_ONLINE_BRIDGE_NAME_TEXT = AppiumBy.xpath(
            "(//XCUIElementTypeStaticText[@name='Online'])[1]/preceding-sibling::XCUIElementTypeStaticText[1]"); // ios

    /** iOS: while the filter field has focus, the back button is replaced by this button. */
    public static final By CLOSE_FILTER_BUTTON = AppiumBy.accessibilityId("Close"); // ios

    /** iOS: the keyboard key that confirms the name typed into the bridge settings. */
    public static final By KEYBOARD_DONE_KEY =
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Done'"); // ios

    /** The name field of the settings screen, found by the name it holds. */
    public static By nameFieldHolding(String name) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.EditText[@text=" + XPathUtils.xpathLiteral(name) + "]"), // android
                AppiumBy.iOSNsPredicateString(
                        "type == 'XCUIElementTypeTextField' AND value == '" + name.replace("'", "\\'") + "'")); // ios
    }

    /**
     * What is tapped to focus the name field. Android: the view inside its {@code EditText}, which needs a real
     * touch. iOS: the inline text field itself.
     */
    public static By nameFieldTapTarget(String name) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.EditText[@text=" + XPathUtils.xpathLiteral(name)
                        + "]/android.view.View"), // android
                nameFieldHolding(name)); // ios
    }
}
