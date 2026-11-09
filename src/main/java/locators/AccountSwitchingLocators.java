package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for Account Switching feature.
 * Used for switching between multiple accounts (Resellers and End-Users).
 */
public final class AccountSwitchingLocators {

    private AccountSwitchingLocators() {}

    // region Navigation
    public static final By OTHER_ACCOUNTS_ITEM =
            AppiumBy.xpath("//android.widget.TextView[@text='Other accounts']");
    // endregion

    // region More Menu Profile
    /** Profile section in More menu where current account name is displayed */
    public static final By PROFILE_SECTION_IN_MORE =
            AppiumBy.xpath("//android.widget.ScrollView");
    // endregion

    // region Account Lists
    public static final By RESELLERS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Resellers']");

    public static final By END_USERS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='End-users']");

    // TODO: Ask app team for stable IDs - positional XPath breaks if view hierarchy changes
    /**
     * First account row in the list. Positional because Compose doesn't provide stable IDs.
     * Must read account name from within this same container to ensure name and click target match.
     */
    public static final By FIRST_ACCOUNT_ROW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View");

    /**
     * Account name within the first account row.
     * Uses contains(@text, '-') pattern - assumes account names have hyphen (e.g., "MK-Reseller").
     */
    public static final By FIRST_ACCOUNT_NAME_IN_ROW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]//android.widget.TextView[contains(@text, '-')]");
    // endregion

    // region Switching Indicators
    public static final By SWITCHING_LOADER =
            AppiumBy.xpath("//android.view.View[@content-desc='Loading']");
    // endregion

    /**
     * Returns a locator for an account row by its name.
     * Scoped to the account switching screen to avoid false matches.
     */
    public static By accountRowByName(String accountName) {
        return AppiumBy.xpath(
                "//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']" +
                "//android.widget.TextView[@text='" + accountName + "']/ancestor::android.view.View[contains(@class, 'View')][1]");
    }

    /**
     * Returns a locator for finding an account name in the More menu profile section.
     * Scoped to profile area to avoid matching names in Other Accounts list.
     */
    public static By profileAccountName(String accountName) {
        return AppiumBy.xpath(
                "//android.widget.ScrollView//android.widget.TextView[@text='" + accountName + "']");
    }
}
