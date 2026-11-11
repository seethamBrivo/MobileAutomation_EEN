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
     * Must read account name from within this same container to ensure name and click target match.
     */
    public static final By FIRST_ACCOUNT_ROW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[@clickable='true']");

    /**
     * Account name within the first account row.
     * Gets the TextView at index=1 which is the direct child of the clickable row (index=0 is avatar View).
     */
    public static final By FIRST_ACCOUNT_NAME_IN_ROW =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='SwitchAccount']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[@clickable='true']/android.widget.TextView[@index='1']");
    // endregion

    /**
     * Returns a locator for finding an account name in the More menu profile section.
     * Scoped to first View of ScrollView to avoid matching names in Other Accounts list below.
     * Uses concat() for XPath 1.0 compatibility when the name contains single quotes.
     */
    public static By profileAccountName(String accountName) {
        String xpathText;
        if (accountName.contains("'")) {
            // XPath 1.0 doesn't support escape sequences, so use concat()
            String[] parts = accountName.split("'", -1);
            StringBuilder concat = new StringBuilder("concat(");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    concat.append(",\"'\",");
                }
                concat.append("'").append(parts[i]).append("'");
            }
            concat.append(")");
            xpathText = concat.toString();
        } else {
            xpathText = "'" + accountName + "'";
        }
        return AppiumBy.xpath(
                "//android.widget.ScrollView/android.view.View[1]" +
                "//android.widget.TextView[@text=" + xpathText + "]");
    }
}
