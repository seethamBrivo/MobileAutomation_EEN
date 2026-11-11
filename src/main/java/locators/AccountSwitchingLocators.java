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
    /**
     * Profile account name in More menu.
     * Scoped to first clickable View in ScrollView to avoid matching names in Other Accounts list.
     */
    public static final By PROFILE_ACCOUNT_NAME =
            AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View[@clickable='true']//android.widget.TextView[1]");
    // endregion

    // region Account Lists
    public static final By RESELLERS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Resellers']");

    public static final By END_USERS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='End-users']");

    // TODO: Ask app team for stable testTag IDs - positional XPath breaks if view hierarchy changes
    /**
     * First account row in the list. Positional because Compose doesn't provide stable IDs.
     */
    public static final By FIRST_ACCOUNT_ROW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[@clickable='true']");
    // endregion
}
