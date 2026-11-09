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
            AppiumBy.xpath("//android.widget.TextView[@text=\"Other accounts\"]");
    // endregion

    // region Account Lists
    public static final By RESELLERS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Resellers']");

    public static final By END_USERS_SECTION =
            AppiumBy.xpath("//android.widget.TextView[@text='End-users']");

    // TODO: Ask app team for stable IDs - positional XPath will break if view hierarchy changes
    public static final By FIRST_ACCOUNT_ROW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"SwitchAccount\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View");

    // NOTE: Uses contains(@text, '-') pattern - assumes account names have hyphen (e.g., "MK-Reseller")
    // TODO: Consider more robust locator if account names without hyphens are added
    public static final By FIRST_ACCOUNT_NAME =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"SwitchAccount\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]//android.widget.TextView[contains(@text, '-')]");
    // endregion

    // region Switching Indicators
    public static final By SWITCHING_LOADER =
            AppiumBy.xpath("//android.view.View[@content-desc=\"Loading\"]");
    // endregion
}
