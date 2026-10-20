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
}