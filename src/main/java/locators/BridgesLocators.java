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

    /** iOS: the name text of the first bridge; it is both what a tap opens and what holds the name. */
    // TODO: ask the mobile team for an id on the bridge rows
    private static final By FIRST_BRIDGE_NAME_TEXT_IOS = AppiumBy.xpath(
            "(//XCUIElementTypeImage[@name='ic_bridgeDevice'])[1]/following-sibling::XCUIElementTypeStaticText[1]");

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
            // Found by its "Add bridge" text: a hint line between the fields and the buttons shifted the positional
            // path onto the "See how to install" button in the 2026-11-19 build.
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[.//android.widget.TextView[@text='Add bridge']]"
                    + "/android.widget.Button"), // android
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
            FIRST_BRIDGE_NAME_TEXT_IOS); // ios: the name text; tapping the row container does not open it

    public static final By FIRST_BRIDGE_NAME_TEXT =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.widget.TextView[1]"), // android
            FIRST_BRIDGE_NAME_TEXT_IOS); // ios

    // TODO: ask the mobile team for an id on the bridge 3-dots button (iOS finds it by position)
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

    /** Android only: the row of the bridge with this name, bounded to the nearest clickable ancestor of its name. */
    public static By bridgeRowNamed(String bridgeName) {
        return AppiumBy.xpath("//android.widget.TextView[@text=" + XPathUtils.xpathLiteral(bridgeName)
                + "]/ancestor::android.view.View[@clickable='true'][1]");
    }

    // --- Delete bridge ---------------------------------------------------------

    public static final By DELETE_BRIDGE_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Delete bridge']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Delete bridge'")); // ios

    // Found by the question the dialog asks: the earlier positional path matched any four nested Views.
    public static final By DELETE_CONFIRMATION_DIALOG =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'delete this bridge')]"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeAlert")); // ios

    // The positional path ("...View/View[2]/Button") matched nothing in the 2026-11-19 build, where the button is a
    // childless sibling of the question text; found by its "Delete" label inside the dialog instead.
    public static final By DELETE_BUTTON =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'delete this bridge')]"
                    + "/following-sibling::android.view.View[@clickable='true'][.//android.widget.TextView[@text='Delete']]"), // android
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

    public static final By RESTART_BRIDGE_BUTTON = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']"), // android
            AppiumBy.xpath("//XCUIElementTypeButton[@name='Restart bridge']")); // ios

    public static final By RESTART_CONFIRMATION_DIALOG = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restart') or contains(@text, 'reboot')]"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert[@name='Restart bridge']")); // ios

    public static final By CONFIRM_RESTART_BUTTON = PlatformLocator.get(
            // The build uploaded on 2026-11-19 has no content-desc on the button and shows "Restart" in a clickable
            // View, so that View is what gets tapped; the first two alternatives are the earlier build's.
            AppiumBy.xpath("//android.widget.Button[@content-desc='positive_button'] | //android.widget.TextView[@text='RESTART']"
                    + " | //android.widget.TextView[@text='Restart']/ancestor::android.view.View[@clickable='true'][1]"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert[@name='Restart bridge']//XCUIElementTypeButton[@name='Restart']")); // ios

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the post-restart confirmation snackbar carries no content-desc/testTag yet.
    public static final By BRIDGE_RESTARTED_MESSAGE = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View"), // android
            CommonLocators.SNACKBAR_TEXT); // ios

    public static By contextMenuOption(String text) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.TextView[@text='" + text + "']"), // android
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == '" + text + "'")); // ios
    }

    // --- iOS only --------------------------------------------------------------

    /** iOS: the name text of the first online bridge: the text just before its "Online" label. */
    // TODO: ask the mobile team for an id on the bridge rows (found by the "Online" label next to it)
    public static final By FIRST_ONLINE_BRIDGE_NAME_TEXT = AppiumBy.xpath(
            "(//XCUIElementTypeStaticText[@name='Online'])[1]/preceding-sibling::XCUIElementTypeStaticText[1]"); // ios

    /** iOS: while the filter field has focus, the back button is replaced by this button. */
    public static final By CLOSE_FILTER_BUTTON = AppiumBy.accessibilityId("Close"); // ios

    /**
     * iOS: the close button of the "See device statuses instantly / See how to add the widget" tip at the bottom of
     * the All devices screen. Found next to the tip's text, because the filter's "Close" button has the same name.
     */
    public static final By WIDGET_TIP_CLOSE_BUTTON = AppiumBy.xpath(
            "//XCUIElementTypeStaticText[@name='See device statuses instantly']"
                    + "/following-sibling::XCUIElementTypeButton[@name='Close']"); // ios

    /** iOS: the keyboard key of the filter field; tapping it closes the keyboard that hides "No matching bridges". */
    public static final By KEYBOARD_SEARCH_KEY =
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Search'"); // ios

    /** iOS: the keyboard key that confirms the name typed into the bridge settings. */
    public static final By KEYBOARD_DONE_KEY =
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Done'"); // ios

    /** iOS: the three dots of the first online bridge: the button just before its "Online" label. */
    public static final By FIRST_ONLINE_BRIDGE_THREE_DOTS = AppiumBy.xpath(
            "(//XCUIElementTypeStaticText[@name='Online'])[1]"
                    + "/preceding-sibling::XCUIElementTypeButton[@name='ic_contextMenu'][1]"); // ios

    /** The name field of the settings screen, found by the name it holds. */
    public static By nameFieldHolding(String name) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.EditText[@text=" + XPathUtils.xpathLiteral(name) + "]"), // android
                AppiumBy.iOSNsPredicateString(
                        "type == 'XCUIElementTypeTextField' AND value == '" + name.replace("'", "\\'") + "'")); // ios
    }

    /**
     * What is tapped to edit the name. Android: the "Name" row of the settings list, checked to hold {@code name};
     * the row opens a sheet with the name field (the 2026-11-19 build shows the name as plain text, no longer as an
     * inline field). iOS: the inline text field itself.
     */
    public static By nameFieldTapTarget(String name) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.TextView[@text='Name']/ancestor::android.view.View[@clickable='true'][1]"
                        + "[.//android.widget.TextView[@text=" + XPathUtils.xpathLiteral(name) + "]]"), // android
                nameFieldHolding(name)); // ios
    }

    /** Android: the Save button of the name sheet, which replaces the keyboard's Done key. */
    public static final By NAME_SAVE_BUTTON = AppiumBy.xpath("//android.widget.TextView[@text='Save']"); // android
}
