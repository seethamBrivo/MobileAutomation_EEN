package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.AccountSwitchingLocators;
import locators.BottomNavigationLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import utils.GestureUtils;
import utils.WaitUtils;

/**
 * Page object for Account Switching feature.
 * Handles switching between multiple accounts (Resellers and End-Users).
 * NOTE: Android-only locators - not verified on iOS.
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
        WaitUtils.click(driver, BottomNavigationLocators.MORE_TAB);
        WaitUtils.waitForVisibility(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                FrameworkConstants.EXPLICIT_WAIT);
    }

    public void verifyOtherAccountsItemIsVisible() {
        if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                FrameworkConstants.EXPLICIT_WAIT)) {
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
     * Uses short timeout since these are absence probes.
     */
    public void requireBothAccountSections() {
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
     */
    public String getCurrentProfileAccountName() {
        return WaitUtils.waitForVisibility(
                driver,
                AccountSwitchingLocators.PROFILE_ACCOUNT_NAME,
                FrameworkConstants.EXPLICIT_WAIT
        ).getText().trim();
    }

    /**
     * Gets account name from the first account row.
     * Reads from the same row element that will be clicked to ensure consistency.
     */
    public String getFirstAccountNameFromRow() {
        return WaitUtils.waitForVisibility(
                driver,
                AccountSwitchingLocators.FIRST_ACCOUNT_NAME_IN_ROW,
                FrameworkConstants.EXPLICIT_WAIT
        ).getText().trim();
    }

    public void clickFirstAccount() {
        WaitUtils.click(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW);
    }

    /**
     * Waits for account switching to complete by observing profile name change.
     * @param originalAccountName the account name before the switch
     */
    public void waitForSwitchingToComplete(String originalAccountName) {
        // Wait for More tab to be visible (indicates app reloaded after switch)
        WaitUtils.waitForVisibility(
                driver,
                BottomNavigationLocators.MORE_TAB,
                FrameworkConstants.EXPLICIT_WAIT
        );

        // Poll for profile name to change (observable change, not just loader disappearing)
        clickMoreTab();
        for (int attempt = 0; attempt < 5; attempt++) {
            String currentName = getCurrentProfileAccountName();
            if (!currentName.equals(originalAccountName)) {
                logger.info("Account switch complete: profile changed from '{}' to '{}'",
                        originalAccountName, currentName);
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for account switch", e);
            }
        }
        Assert.fail(String.format(
                "Account name did not change after switch. Still showing: '%s'", originalAccountName));
    }

    /**
     * Verifies that the account switched from originalAccountName to expectedAccountName.
     * Ensures the switch actually happened by comparing before and after.
     */
    public void verifyAccountSwitched(String originalAccountName, String expectedAccountName) {
        // Navigate to More menu if not already there
        if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                TimeoutConstants.SHORT_TIMEOUT)) {
            clickMoreTab();
        }

        // Get current profile name
        String currentProfileName = getCurrentProfileAccountName();

        // Verify it changed from original
        Assert.assertNotEquals(currentProfileName, originalAccountName,
                String.format("Account should have changed from original. Expected: != '%s', Actual: '%s'",
                        originalAccountName, currentProfileName));

        // Verify it matches expected (scoped to profile section, not Other Accounts list)
        WaitUtils.assertVisible(driver,
                AccountSwitchingLocators.profileAccountName(expectedAccountName),
                FrameworkConstants.EXPLICIT_WAIT,
                String.format("Profile should show account '%s' after switching. Current: '%s'",
                        expectedAccountName, currentProfileName));
    }

    /**
     * Attempts to navigate back to the More tab/Dashboard from any screen.
     * Tries pressing back up to 3 times until More tab is visible.
     */
    public void recoverToDashboard() {
        int maxAttempts = 3;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (WaitUtils.isVisible(driver, BottomNavigationLocators.MORE_TAB,
                    TimeoutConstants.SHORT_TIMEOUT)) {
                logger.info("Already on dashboard");
                return;
            }
            logger.info("Navigating back to dashboard (attempt {})", attempt + 1);
            driver.navigate().back();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.warn("Could not recover to dashboard after {} attempts", maxAttempts);
    }

    /**
     * Cleanup helper: switches back by clicking the first account in Other Accounts list.
     * Simple approach - just clicks the first account row.
     *
     * @param originalAccountName the original account name (for logging only)
     */
    public void switchBackToFirstAccount(String originalAccountName) {
        try {
            String currentAccount = getCurrentProfileAccountName();

            // If already on original account, no need to switch
            if (currentAccount.equals(originalAccountName)) {
                logger.info("Cleanup: already on original account '{}'", originalAccountName);
                return;
            }

            logger.info("Cleanup: switching from '{}' back by clicking first account", currentAccount);

            // Click Other Accounts
            clickOtherAccountsItem();

            // Click first account row
            WaitUtils.click(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW);
            waitForSwitchingToComplete(currentAccount);

            String restoredAccount = getCurrentProfileAccountName();
            logger.info("Cleanup: switched to account '{}'", restoredAccount);

        } catch (Exception e) {
            logger.info("Cleanup: failed to switch back: {}", e.getMessage());
        }
    }
}
