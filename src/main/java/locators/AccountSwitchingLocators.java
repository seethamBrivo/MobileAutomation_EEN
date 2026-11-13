package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for Account Switching feature.
 * Used for switching between multiple accounts (Resellers and End-Users).
 * Supports both Android and iOS platforms.
 */
public final class AccountSwitchingLocators {

    private AccountSwitchingLocators() {}

    // region Navigation
    public static final By OTHER_ACCOUNTS_ITEM = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Other accounts']"), // android
            AppiumBy.accessibilityId("otheraccounts_button")); // ios
    // endregion

    // region More Menu Profile
    /**
     * Profile account name in More menu (for getting text).
     * Android: Finds TextView inside the profile section (PROFILE_SECTION is the clickable container).
     * iOS: Uses same element as MyProfileLocators.PROFILE_SECTION (myprofile_button).
     */
    public static final By PROFILE_ACCOUNT_NAME = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View[@clickable='true']//android.widget.TextView[1]"), // android - TextView with text
            AppiumBy.accessibilityId("myprofile_button")); // ios - same as PROFILE_SECTION
    // endregion

    // region Account Lists
    public static final By RESELLERS_SECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Resellers']"), // android
            AppiumBy.accessibilityId("Resellers")); // ios

    public static final By END_USERS_SECTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='End-users']"), // android
            AppiumBy.accessibilityId("End-users")); // ios

    /**
     * First account row in the list.
     * Android: Positional XPath (TODO: request testTag from app team).
     * iOS: First Cell element that is not a header (cells with 'Header' trait are section headers).
     */
    public static final By FIRST_ACCOUNT_ROW = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[@clickable='true']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeCell' AND visible == 1 AND NOT (traits CONTAINS 'Header')")); // ios - first non-header cell (account row)
    // endregion
}
