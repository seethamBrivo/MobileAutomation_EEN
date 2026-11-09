package pages;

import constants.FrameworkConstants;
import io.appium.java_client.AppiumDriver;
import locators.AccountSwitchingLocators;
import locators.BottomNavigationLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.GestureUtils;
import utils.WaitUtils;

/**
 * Page object for Account Switching feature.
 * Handles switching between multiple accounts (Resellers and End-Users).
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
     */
    public void requireBothAccountSections() {
        boolean resellersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.RESELLERS_SECTION,
                FrameworkConstants.EXPLICIT_WAIT
        );
        boolean endUsersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.END_USERS_SECTION,
                FrameworkConstants.EXPLICIT_WAIT
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
        WebElement profileSection = WaitUtils.waitForVisibility(
                driver,
                AccountSwitchingLocators.PROFILE_SECTION_IN_MORE,
                FrameworkConstants.EXPLICIT_WAIT
        );
        // Get first TextView in profile section which contains account name
        WebElement accountNameElement = profileSection.findElement(
                By.xpath(".//android.widget.TextView[1]"));
        return accountNameElement.getText().trim();
    }

    /**
     * Gets account name from the first account row.
     * Reads from the same row element that will be clicked to ensure consistency.
     */
    public String getFirstAccountNameFromRow() {
        WebElement accountElement = WaitUtils.waitForVisibility(
                driver,
                AccountSwitchingLocators.FIRST_ACCOUNT_NAME_IN_ROW,
                FrameworkConstants.EXPLICIT_WAIT
        );
        return accountElement.getText().trim();
    }

    public void clickFirstAccount() {
        WaitUtils.click(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW);
    }

    /**
     * Switches to a specific account by name.
     * Finds the account in the list and clicks it.
     */
    public void switchToAccountByName(String accountName) {
        By accountRow = AccountSwitchingLocators.accountRowByName(accountName);
        WaitUtils.click(driver, accountRow);
    }

    /**
     * Waits for account switching to complete.
     * Waits for loader to disappear and for a stable post-switch state.
     */
    public void waitForSwitchingToComplete() {
        // Wait for loader to disappear (it may already be gone on fast networks)
        WaitUtils.waitForInvisibility(
                driver,
                AccountSwitchingLocators.SWITCHING_LOADER,
                FrameworkConstants.EXPLICIT_WAIT
        );

        // Wait for More tab to be visible and stable (indicates app reloaded after switch)
        WaitUtils.waitForVisibility(
                driver,
                BottomNavigationLocators.MORE_TAB,
                FrameworkConstants.EXPLICIT_WAIT
        );
    }

    /**
     * Verifies that the account switched from originalAccountName to expectedAccountName.
     * Ensures the switch actually happened by comparing before and after.
     */
    public void verifyAccountSwitched(String originalAccountName, String expectedAccountName) {
        // Navigate to More menu if not already there
        if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                FrameworkConstants.EXPLICIT_WAIT)) {
            clickMoreTab();
        }

        // Get current profile name
        String currentProfileName = getCurrentProfileAccountName();

        // Verify it changed from original
        Assert.assertNotEquals(currentProfileName, originalAccountName,
                "Account should have changed from original account");

        // Verify it matches expected
        By expectedNameLocator = AccountSwitchingLocators.profileAccountName(expectedAccountName);
        WaitUtils.assertVisible(driver, expectedNameLocator, FrameworkConstants.EXPLICIT_WAIT,
                String.format("Profile should show account '%s' after switching", expectedAccountName));
    }

    /**
     * Cleanup helper: switches back to first account in the list.
     * Used in @AfterMethod to restore test state.
     * Clicks first account which should be the original account.
     */
    public void switchBackToFirstAccount() {
        clickOtherAccountsItem();
        clickFirstAccount();
        waitForSwitchingToComplete();
        logger.info("Switched back to first account in list");
    }
}
