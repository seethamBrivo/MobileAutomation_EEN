package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

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

    public static final By TOTAL_ACCOUNTS =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'accounts')]");

    // --- Account card (first row in list) --------------------------------------------

    // TODO: Replace with testTag - no content-desc on this Compose node yet
    public static final By TRANSFORMATION_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[2]/android.view.View/android.view.View");

    // TODO: Replace with testTag - positional paths for resource icons and status dots
    public static final By BRIDGE_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[1]");

    public static final By CAMERA_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[4]");

    public static final By DOT_GREEN =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[2]");

    public static final By DOT_RED =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[3]");

    public static final By DOT_YELLOW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[7]");

    public static final By DOT_GREY =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[8]");

    // TODO: Replace with testTag - positional path for the three-dots menu button
    public static final By THREE_DOTS_BUTTON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[3]/android.view.View[1]/android.view.View[9]");

    // --- Context menu options --------------------------------------------------------

    public static final By CONTEXT_MENU_SETTINGS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Settings\"]");

    public static final By CONTEXT_MENU_DELETE_ACCOUNT =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Delete account\"]");

    // --- Toolbar ---------------------------------------------------------------------

    public static final By FILTER_BY_ACCOUNT_NAME =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Filter by account name\"]");

    // TODO: Replace with testTag - no content-desc on this Compose button yet
    public static final By ADD_ACCOUNT_BUTTON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]"
                    + "/android.view.View/android.view.View/android.view.View/android.view.View"
                    + "/android.view.View[5]/android.view.View/android.view.View");

    // --- Bottom navigation bar -------------------------------------------------------

    public static final By NAV_DASHBOARD =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Dashboard\"]");

    public static final By NAV_USERS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Users\"]");

    public static final By NAV_BUSINESS_PORTAL =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Business Portal\"]");

    public static final By NAV_MORE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"More\"]");

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
}

