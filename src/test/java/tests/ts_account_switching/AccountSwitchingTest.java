package tests.ts_account_switching;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AccountSwitchingPage;
import utils.PlatformLocator;

/**
 * Test suite for Account Switching functionality.
 * NOTE: These tests use Android-only locators and are only verified on Android platform.
 */
public class AccountSwitchingTest extends BaseTest {

    private AccountSwitchingPage accountSwitchingPage;
    private String originalAccountName;

    @BeforeMethod(alwaysRun = true)
    public void setupAccountSwitchingTest() {
        accountSwitchingPage = null;
        originalAccountName = null;

        if (PlatformLocator.isIos()) {
            throw new SkipException("Account switching tests are Android-only (no iOS locators implemented)");
        }

        accountSwitchingPage = loginAndCreatePage(TestDataConstants.RESELLER3_CREDS, AccountSwitchingPage::new);
        accountSwitchingPage.verifyLandedOnMoreScreen();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null || accountSwitchingPage == null) {
            return;
        }
        try {
            // First, recover to dashboard in case test died on a different screen
            accountSwitchingPage.recoverToDashboard();

            // If no account switch occurred, nothing to restore
            if (originalAccountName == null) {
                logger.info("Cleanup: no account switch occurred, nothing to restore");
                return;
            }

            // Navigate to More menu and switch back to first account
            accountSwitchingPage.clickMoreTab();
            accountSwitchingPage.switchBackToFirstAccount(originalAccountName);
        } catch (Throwable t) {
            logger.warn("Cleanup exception: {}", t.getMessage());
        }
    }

    @Test(description = "EEPD-TC-46239: POS-Account Switch - Switch to another account in VMS mobile app",
          groups = {"regression", "account_switching"})
    public void test01_switchToAnotherAccount() {
        accountSwitchingPage.clickMoreTab();

        // Capture original account name before navigating away
        originalAccountName = accountSwitchingPage.getCurrentProfileAccountName();
        logger.info("Original account: {}", originalAccountName);

        accountSwitchingPage.verifyOtherAccountsItemIsVisible();
        accountSwitchingPage.clickOtherAccountsItem();

        // Check test data is sufficient before switching
        accountSwitchingPage.requireBothAccountSections();

        accountSwitchingPage.clickFirstAccount();
        accountSwitchingPage.waitForSwitchingToComplete(originalAccountName);
        accountSwitchingPage.verifyAccountSwitched(originalAccountName);

        logger.info("Account switch verified successfully");
    }
}
