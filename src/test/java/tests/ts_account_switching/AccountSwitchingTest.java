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

        loginAs(TestDataConstants.RESELLER3_CREDS);
        accountSwitchingPage = new AccountSwitchingPage(driver);
        accountSwitchingPage.verifyLandedOnMoreScreen();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null || accountSwitchingPage == null) {
            return;
        }
        try {
            // Navigate back to More menu and switch to first account (original)
            accountSwitchingPage.clickMoreTab();
            accountSwitchingPage.switchBackToFirstAccount();
        } catch (Throwable t) {
            logger.warn("Cleanup failed: {}", t.getMessage());
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
        accountSwitchingPage.requireBothAccountSections();

        String targetAccountName = accountSwitchingPage.getFirstAccountNameFromRow();
        logger.info("Switching to account: {}", targetAccountName);

        accountSwitchingPage.clickFirstAccount();
        accountSwitchingPage.waitForSwitchingToComplete();
        accountSwitchingPage.verifyAccountSwitched(originalAccountName, targetAccountName);

        logger.info("Account switch verified successfully");
    }
}
