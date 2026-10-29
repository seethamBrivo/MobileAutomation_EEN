package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the Reseller Dashboard screen used by TC-4516.
 * <p>
 * Most XPaths here are positional Compose paths — the mobile team has not yet added
 * testTag/content-desc attributes to these nodes. Each TODO marks a locator that should
 * be replaced once the app exposes a stable identifier.
 */
public final class ResellerDashboardLocators {

    private ResellerDashboardLocators() {}

    // --- Dashboard summary -----------------------------------------------------------

    public static final By TOTAL_ACCOUNTS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'accounts')]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]+ accounts?$'")); // ios

    // --- Account card (first row in list) --------------------------------------------

    // TODO: Replace with testTag - no content-desc on this Compose node yet
    public static final By TRANSFORMATION_ICON = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[2]/android.view.View/android.view.View"), // android
            AppiumBy.accessibilityId("largeCardIcon")); // ios

    // TODO: Replace with testTag - positional paths for resource icons and status dots
    public static final By BRIDGE_ICON = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[1]"), // android
            AppiumBy.accessibilityId("ic_bridgeDevice")); // ios

    public static final By CAMERA_ICON = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[4]"), // android
            AppiumBy.accessibilityId("ic_camera")); // ios

    public static final By DOT_GREEN = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[2]"), // android
            iosCameraStatusDotAtPosition(1)); // ios

    public static final By DOT_RED = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[3]"), // android
            iosCameraStatusDotAtPosition(2)); // ios

    public static final By DOT_YELLOW = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[7]"), // android
            iosCameraStatusDotAtPosition(3)); // ios

    public static final By DOT_GREY = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[8]"), // android
            iosCameraStatusDotAtPosition(4)); // ios

    // TODO: Replace with testTag - positional path for the three-dots menu button
    public static final By THREE_DOTS_BUTTON = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[9]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' AND name == 'ic_contextMenu' AND visible == 1")); // ios

    // --- Context menu options --------------------------------------------------------

    public static final By CONTEXT_MENU_SETTINGS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Settings\"]"), // android
            AppiumBy.accessibilityId("Settings")); // ios

    public static final By CONTEXT_MENU_DELETE_ACCOUNT = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Delete account\"]"), // android
            AppiumBy.accessibilityId("Delete account")); // ios

    // --- Toolbar ---------------------------------------------------------------------

    public static final By FILTER_BY_ACCOUNT_NAME = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Filter by account name\"]"), // android
            AppiumBy.accessibilityId("Filter by account")); // ios

    // TODO: Replace with testTag - no content-desc on this Compose button yet
    public static final By ADD_ACCOUNT_BUTTON = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[5]/android.view.View/android.view.View"), // android
            AppiumBy.iOSNsPredicateString("name == 'navigationBar_customButton' AND label == 'Add'")); // ios

    // --- Bottom navigation bar -------------------------------------------------------

    public static final By NAV_DASHBOARD = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Dashboard\"]"), // android
            AppiumBy.accessibilityId("dashboard_button")); // ios

    public static final By NAV_USERS = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Users\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Users'")); // ios

    public static final By NAV_BUSINESS_PORTAL = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Business Portal\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Business Portal'")); // ios

    public static final By NAV_MORE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"More\"]"), // android
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton'"
                            + " AND label == 'More' AND name != 'navigationBar_customButton'")); // ios

    // --- Add account form ------------------------------------------------------------

    // TODO: Replace with testTag - positional paths; no resource-id or testTag on EditTexts
    public static final By ACCOUNT_NAME_FIELD =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");

    public static final By ADD_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Add\"]");

    // TODO: Replace with testTag - positional path; no resource-id or testTag on EditText
    public static final By CUSTOMER_ID_FIELD =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.EditText");

    public static final By EMAIL_FIELD =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Add email\"]");

    // TODO: Replace with testTag - positional path; no resource-id or testTag on EditText
    public static final By EMAIL_INPUT_FIELD =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[4]/android.widget.EditText");

    public static final By EDITION_FIELD =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Edition\"]");

    public static final By EDITION_STANDARD =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Standard\"]");

    public static final By EDITION_PROFESSIONAL =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Professional\"]");

    public static final By EDITION_ENTERPRISE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Enterprise\"]");

    // TODO: Replace with testTag - positional path in ScrollView
    public static final By EDITION_NAV_ICON =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[5]/android.view.View");

    // TODO: Replace with testTag - generic back chevron reused across all sub-panels
    //       (edition, cloud retention, min retention, max retention)
    public static final By GENERIC_BACK_CHEVRON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[2]/android.view.View/android.view.View");

    // --- Camera defaults / Cloud retention -------------------------------------------

    public static final By CAMERA_DEFAULTS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='cameras defaults']");

    public static final By CLOUD_RETENTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Cloud retention\"]");

    public static final By CLOUD_RETENTION_DEFAULT_30_DAYS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"30 days\"]");

    // TODO: Replace with testTag - positional path in ScrollView
    public static final By CLOUD_RETENTION_NAV_ICON =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[7]/android.view.View");

    public static final By CLOUD_RETENTION_MIN_1_DAY =
            AppiumBy.xpath("//android.widget.TextView[@text=\"1 day (M10)\"]");

    public static final By CLOUD_RETENTION_MAX_5_YEARS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"5 years\"]");

    // --- Min retention ---------------------------------------------------------------

    public static final By MIN_RETENTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Min retention\"]");

    public static final By MIN_RETENTION_DEFAULT_14_DAYS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"14 days\"]");

    // TODO: Replace with testTag - positional path in ScrollView
    public static final By MIN_RETENTION_NAV_ICON =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[8]/android.view.View");

    public static final By MIN_RETENTION_MIN_NONE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"None\"]");

    public static final By MIN_RETENTION_MAX_180_DAYS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"180 days\"]");

    // --- Max retention ---------------------------------------------------------------

    public static final By MAX_RETENTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Max retention\"]");

    public static final By MAX_RETENTION_DEFAULT_60_DAYS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"60 days\"]");

    // TODO: Replace with testTag - positional path in ScrollView
    public static final By MAX_RETENTION_NAV_ICON =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[9]/android.view.View");

    public static final By MAX_RETENTION_MIN_NONE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"None\"]");

    public static final By MAX_RETENTION_MAX_1_YEAR =
            AppiumBy.xpath("//android.widget.TextView[@text=\"1 year\"]");

    // --- Resolution defaults ---------------------------------------------------------

    public static final By PREVIEW_RESOLUTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Preview resolution\"]");

    public static final By PREVIEW_RESOLUTION_DEFAULT =
            AppiumBy.xpath("//android.widget.TextView[@text=\"cif (SD1 320x240)\"]");

    public static final By FULL_VIDEO_RESOLUTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Full video resolution\"]");

    public static final By FULL_VIDEO_RESOLUTION_DEFAULT =
            AppiumBy.xpath("//android.widget.TextView[@text=\"high (HD1 1280x720)\"]");

    // TODO: Replace with testTag - positional path in ScrollView
    public static final By CLOUD_PREVIEW_ONLY_TOGGLE =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[12]/android.view.View/android.view.View");

    // --- Validation messages ---------------------------------------------------------

    public static final By ERROR_MAX_120_CHARS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Max 120 characters\"]");

    public static final By SUCCESS_ADDED =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Added successfully\"]");

    public static final By DELETE_CONFIRM_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text=\"DELETE\"]");

    // --- Page title / logout / delete feedback ----------------------------------------

    public static final By RESELLER_DASHBOARD_TITLE = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Reseller dashboard\"]"), // android
            AppiumBy.accessibilityId("Reseller dashboard")); // ios

    public static final By LAYOUTS_SCREEN =
            AppiumBy.xpath("(//android.widget.TextView[@text=\"Layouts\"])[2]");

    public static final By LOGOUT_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Log out\"]");

    public static final By CONFIRM_LOGOUT_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text=\"YES\"]");

    public static final By DELETED_SUCCESS_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Deleted successfully\"]");

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the first account row in the list carries no content-desc/testTag yet.
    public static final By FIRST_ACCOUNT_WITH_BRIDGES =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='ResellerDashboardMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]");

    /** Matches the list header "11 accounts" / "1 account" (not the "Filter by account name" hint). */
    public static final By ACCOUNTS_COUNT_LABEL =
            AppiumBy.xpath("//android.widget.TextView[contains(@text,' account')]");

    // --- Dynamic account locators ----------------------------------------------------

    public static By accountByName(String accountName) {
        if (PlatformLocator.isIos()) {
            // iOS: an account's name is a text named after itself.
            return AppiumBy.accessibilityId(accountName);
        }
        return AppiumBy.xpath(
                "//android.widget.TextView[@text=\"" + accountName + "\"]"
        );
    }

    /**
     * iOS: the status dots of the first account card's camera row, in order 1 green (online), 2 red (offline),
     * 3 yellow (warning), 4 grey (other). They are unlabelled images placed after the card's camera icon, so they are
     * found by that position; their colour is not exposed to the accessibility tree.
     */
    private static By iosCameraStatusDotAtPosition(int position) {
        return AppiumBy.xpath("(//XCUIElementTypeImage[@name='ic_camera'])[1]"
                + "/following-sibling::XCUIElementTypeImage[not(@name) or @name=''][" + position + "]");
    }

    /** {@code xpathSafeText} must already be an XPath string literal (quoted/escaped by the caller). */
    public static By accountByExactText(String xpathSafeText) {
        return AppiumBy.xpath("(//android.widget.TextView[@text=" + xpathSafeText + "])[1]");
    }
}
