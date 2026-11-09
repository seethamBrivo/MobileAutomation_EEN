package tests.ts_account_switching;

import base.BaseTest;
import constants.TestDataConstants;
import constants.TimeoutConstants;
import locators.AccountSwitchingLocators;
import locators.BottomNavigationLocators;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AccountSwitchingPage;
import utils.PlatformLocator;
import utils.WaitUtils;

/**
 * Test suite for Account Switching functionality.
 * NOTE: These tests use Android-only locators and are only verified on Android platform.
 */
public class AccountSwitchingTest extends BaseTest {

    private AccountSwitchingPage accountSwitchingPage;

    @BeforeMethod(alwaysRun = true)
    public void setupAccountSwitchingTest() {
        if (PlatformLocator.isIos()) {
            throw new SkipException("Account switching tests are Android-only (no iOS locators implemented)");
        }
        loginAs(TestDataConstants.RESELLER3_CREDS);
        accountSwitchingPage = new AccountSwitchingPage(driver);
        WaitUtils.assertVisible(driver, BottomNavigationLocators.MORE_TAB,
                TimeoutConstants.SHORT_TIMEOUT, "Should land on screen with More tab visible after login");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null || accountSwitchingPage == null) {
            return;
        }
        try {
            if (!WaitUtils.isVisible(driver, BottomNavigationLocators.MORE_TAB,
                    TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                return;
            }

            WaitUtils.click(driver, BottomNavigationLocators.MORE_TAB);

            if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                    TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                return;
            }

            accountSwitchingPage.clickOtherAccountsItem();
            accountSwitchingPage.clickFirstAccount();
            accountSwitchingPage.waitForSwitchingToComplete();
        } catch (Throwable t) {
            logger.warn("Cleanup failed: {}", t.getMessage());
        }
    }

    @Test(description = "EEPD-TC-46239: POS-Account Switch - Switch to another account in VMS mobile app",
          groups = {"regression", "account_switching"})
    public void test01_switchToAnotherAccount() {
        accountSwitchingPage.clickMoreTab();
        accountSwitchingPage.verifyOtherAccountsItemIsVisible();
        accountSwitchingPage.clickOtherAccountsItem();
        accountSwitchingPage.verifyOtherAccountsScreenIsVisible();
        accountSwitchingPage.verifyAccountListSections();

        String targetAccountName = accountSwitchingPage.getFirstAccountName();
        accountSwitchingPage.clickFirstAccount();
        accountSwitchingPage.verifySwitchingLoaderIsDisplayed();
        accountSwitchingPage.verifyBackNavigationIsDisabled();
        accountSwitchingPage.waitForSwitchingToComplete();
        accountSwitchingPage.verifyAccountSwitchedSuccessfully(targetAccountName);
    }
}
