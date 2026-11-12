package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.AccountSwitchingLocators;
import locators.BottomNavigationLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.SkipException;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

/**
 * Page object for Account Switching feature.
 * Handles switching between multiple accounts (Resellers and End-Users).
 * Supports both Android and iOS platforms.
 */
public class AccountSwitchingPage {

    private static final Logger logger = LogManager.getLogger(AccountSwitchingPage.class);
    private final AppiumDriver driver;

    public AccountSwitchingPage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
    }

    public void verifyLandedOnMoreScreen() {
        WaitUtils.assertVisible(driver, BottomNavigationLocators.MORE_TAB,
                FrameworkConstants.EXPLICIT_WAIT, "Should land on screen with More tab visible after login");
    }

    public void clickMoreTab() {
        BottomNavigationBar.clickMoreTab(driver);
        WaitUtils.waitForVisibility(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                FrameworkConstants.EXPLICIT_WAIT);
    }

    public void verifyOtherAccountsItemIsVisible() {
        if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                TimeoutConstants.SHORT_TIMEOUT)) {
            boolean found = GestureUtils.scrollUntilVisible(driver,
                    AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM);
            Assert.assertTrue(found,
                    "Other Accounts item should be visible after scrolling in More menu");
        }
    }

    public void clickOtherAccountsItem() {
        WaitUtils.click(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM);
    }

    /**
     * Requires both Resellers and End-Users sections to be present.
     * Throws SkipException if test data is incomplete.
     * Waits for list to load first, then probes for both sections.
     */
    public void requireBothAccountSections() {
        // Wait for list to load first
        WaitUtils.waitForVisibility(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW,
                TimeoutConstants.LIST_LOAD_TIMEOUT);

        // Now probe for both sections with short timeout
        boolean resellersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.RESELLERS_SECTION,
                TimeoutConstants.SHORT_TIMEOUT
        );
        boolean endUsersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.END_USERS_SECTION,
                TimeoutConstants.SHORT_TIMEOUT
        );

        if (!resellersSectionVisible || !endUsersSectionVisible) {
            throw new SkipException(
                    "Test data incomplete: Account must have both Resellers and End-Users sections");
        }
    }

    /**
     * Gets the current account name from the More menu profile section.
     * Must be called when More menu is open.
     * Uses getTextWithRetry to handle stale elements during UI re-renders.
     */
    public String getCurrentProfileAccountName() {
        return WaitUtils.getTextWithRetry(
                driver,
                AccountSwitchingLocators.PROFILE_ACCOUNT_NAME,
                FrameworkConstants.EXPLICIT_WAIT
        );
    }

    /**
     * Gets the name of the first account in the Other accounts list.
     * Note: On iOS, Cell.getText() returns empty string (name is in child element).
     * On Android, may return abbreviated name.
     */
    public String getFirstAccountName() {
        return WaitUtils.waitForVisibility(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW).getText();
    }

    public void clickFirstAccount() {
        WaitUtils.click(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW);
    }

    /**
     * Waits for the "Other accounts" screen to close after clicking an account.
     * iOS takes longer to process the navigation, so we wait for the screen to disappear
     * before checking the profile name change.
     */
    public void waitForOtherAccountsScreenToClose() {
        boolean closed = WaitUtils.waitForInvisibility(driver, AccountSwitchingLocators.RESELLERS_SECTION,
                FrameworkConstants.EXPLICIT_WAIT);
        if (!closed) {
            throw new AssertionError("Other accounts screen did not close after switching account");
        }
    }

    /**
     * Waits for account switching to complete by observing profile name change.
     * Polls until the profile name differs from the original account name.
     * Does not verify exact match to target (profile may show abbreviated name).
     * @param originalAccountName the account name before the switch
     */
    public void waitForSwitchingToComplete(String originalAccountName) {
        // Navigate to More to observe profile name change
        BottomNavigationBar.clickMoreTab(driver);

        // Poll for profile name to change from original (profile may show abbreviated name)
        boolean switched = WaitUtils.pollUntil(driver, FrameworkConstants.EXPLICIT_WAIT, () -> {
            String currentName = getCurrentProfileAccountName();
            return !currentName.equals(originalAccountName);
        });

        if (!switched) {
            throw new TimeoutException(
                    String.format("Account switch timed out after %s - profile still shows '%s'",
                            FrameworkConstants.EXPLICIT_WAIT, originalAccountName));
        }

        String newAccountName = getCurrentProfileAccountName();
        logger.info("Account switch complete: profile changed from '{}' to '{}'",
                originalAccountName, newAccountName);
    }

    /**
     * Verifies that account switching occurred by checking the profile name changed.
     * Both platforms verify name changed (cannot reliably assert specific target name
     * due to Android abbreviation and iOS Cell.getText() returning empty).
     * Assumes More menu is already open (waitForSwitchingToComplete navigates there).
     */
    public void verifyAccountSwitched(String originalAccountName, String targetAccountName) {
        String currentProfileName = getCurrentProfileAccountName();

        // Verify the name changed from original (both platforms)
        Assert.assertNotEquals(currentProfileName, originalAccountName,
                String.format("Profile should show different account. Was: '%s', Now: '%s'",
                        originalAccountName, currentProfileName));

        logger.info("Account switch verified: profile changed from '{}' to '{}'",
                originalAccountName, currentProfileName);
    }

    /**
     * Attempts to navigate back to the dashboard from any screen.
     * Delegates to DevicesPage.recoverToDashboard() for consistent recovery behavior.
     */
    public void recoverToDashboard() {
        DevicesPage devicesPage = new DevicesPage(driver);
        devicesPage.recoverToDashboard();
    }

    /**
     * Cleanup helper: switches back by clicking the first account in Other Accounts list.
     * Simple approach - just clicks the first account row.
     *
     * @param originalAccountName the original account name to restore to
     */
    public void switchBackToFirstAccount(String originalAccountName) {
        try {
            String currentAccount = getCurrentProfileAccountName();

            // If already on original account, no need to switch
            if (currentAccount.equals(originalAccountName)) {
                logger.info("Cleanup: already on original account '{}'", originalAccountName);
                return;
            }

            logger.info("Cleanup: switching from '{}' back to '{}'", currentAccount, originalAccountName);

            // Click Other Accounts
            clickOtherAccountsItem();

            // Click first account row and wait for switch from currentAccount
            WaitUtils.click(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW);
            waitForSwitchingToComplete(currentAccount);

            // Verify restoration (warning only - cleanup is best-effort)
            String restoredAccount = getCurrentProfileAccountName();
            if (!restoredAccount.equals(originalAccountName)) {
                logger.warn("Cleanup: restored to '{}' but expected '{}' - first account may not be the original",
                        restoredAccount, originalAccountName);
            } else {
                logger.info("Cleanup: successfully restored to account '{}'", restoredAccount);
            }
        } catch (Throwable t) {
            logger.warn("Cleanup: failed to switch back: {}", t.getMessage());
        }
    }
}
