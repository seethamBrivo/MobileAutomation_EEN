package tests.ts_account_switching;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AccountSwitchingPage;

/**
 * Test suite for Account Switching functionality.
 * Supports both Android and iOS platforms.
 */
public class AccountSwitchingTest extends BaseTest {

    private AccountSwitchingPage accountSwitchingPage;
    private String originalAccountName;

    @BeforeMethod(alwaysRun = true)
    public void setupAccountSwitchingTest() {
        accountSwitchingPage = null;
        originalAccountName = null;

        accountSwitchingPage = loginAndCreatePage(TestDataConstants.RESELLER3_CREDS, AccountSwitchingPage::new);
        accountSwitchingPage.verifyLandedOnMoreScreen();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null || accountSwitchingPage == null) {
            return;
        }
        try {
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
          groups = {"regression", "account_switching", "ios"})
    public void test01_switchToAnotherAccount() {
        accountSwitchingPage.clickMoreTab();

        // Capture original account name before switching
        originalAccountName = accountSwitchingPage.getCurrentProfileAccountName();
        logger.info("Original account: {}", originalAccountName);

        accountSwitchingPage.verifyOtherAccountsItemIsVisible();
        accountSwitchingPage.clickOtherAccountsItem();

        // Check test data is sufficient before switching
        accountSwitchingPage.requireBothAccountSections();

        // Capture target account name before clicking (may be empty on iOS)
        String targetAccountName = accountSwitchingPage.getFirstAccountName();
        if (!targetAccountName.isEmpty()) {
            logger.info("Target account: {}", targetAccountName);
        }

        accountSwitchingPage.clickFirstAccount();
        accountSwitchingPage.waitForOtherAccountsScreenToClose();
        accountSwitchingPage.waitForSwitchingToComplete(originalAccountName);
        accountSwitchingPage.verifyAccountSwitched(originalAccountName, targetAccountName);

        logger.info("Account switch verified successfully");
    }
}
