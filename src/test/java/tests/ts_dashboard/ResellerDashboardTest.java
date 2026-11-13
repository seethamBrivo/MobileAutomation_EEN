package tests.ts_dashboard;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ResellerDashboardPage;
import utils.TestDataGenerator;

public class ResellerDashboardTest extends BaseTest {

    private static final int MAX_CUSTOMER_ID_LENGTH = 120;

    private static final String INVALID_CUSTOMER_ID_ACCOUNT_NAME = "TestAccount_TC40220";

    private ResellerDashboardPage resellerDashboardPage;
    // Set once the account exists, cleared once it is deleted; @AfterMethod removes any leftover.
    private String createdAccountDisplayName;

    @BeforeMethod(alwaysRun = true)
    public void loginAndVerifyDashboard() {
        resellerDashboardPage = loginAsResellerAndVerifyDashboard();
    }

    @Test(description = "EEPD-TC-4516: POS-Reseller Dashboard - (View Dashboard for Reseller) in mobile VMS app",
          groups = {"regression", "dashboard", "ios"})
    public void verifyResellerDashboard() {
        resellerDashboardPage.verifyDashboardSummary();
        resellerDashboardPage.verifyAccountCardContents(getTestDataValue("bridge-account-name"));
        resellerDashboardPage.verifyContextMenuOptions();
        resellerDashboardPage.verifyDashboardToolbar();
        resellerDashboardPage.verifyBottomNavigationBar();
    }

    // Disabled: EPD-13231 - deleting an added account returns an internal server error, so the test cannot
    // clean up after itself and leaves a TC40220_ account behind on every run. Re-enable once that is fixed.
    @Test(description = "EEPD-TC-40220: POS-Reseller Dashboard - Verify the account creation for Admin OR Account access users",
          groups = {"regression", "dashboard"}, enabled = false)
    public void verifyAccountCreationForAdminOrAccountAccessUsers() {
        resellerDashboardPage.verifyAddAccountFormFields();
        resellerDashboardPage.verifyEditionOptions();
        resellerDashboardPage.verifyCloudRetentionRange();
        resellerDashboardPage.verifyMinRetentionRange();
        resellerDashboardPage.verifyMaxRetentionRange();
        resellerDashboardPage.verifyResolutionAndToggleDefaults();

        // Generated rather than read from testData: the CI-provided value was only 36 characters,
        // so it never crossed the 120-character limit and no error appeared.
        resellerDashboardPage.verifyCustomerIdTooLongIsRejected(
                "A".repeat(MAX_CUSTOMER_ID_LENGTH + 1), INVALID_CUSTOMER_ID_ACCOUNT_NAME);

        String accountName = TestDataGenerator.generateUniqueName("TC40220_");
        String customerId = TestDataGenerator.generateUniqueName("CustID_");
        String fullAccountName = accountName + " (" + customerId + ")";
        createdAccountDisplayName = fullAccountName;
        resellerDashboardPage.submitNewAccount(accountName, customerId, getTestDataValue(TestDataConstants.TEST_ACCOUNT_EMAIL));
        resellerDashboardPage.verifyAccountCreatedAndVisible(fullAccountName);

        deleteAccount(fullAccountName);
        createdAccountDisplayName = null;
    }

    // ---- lifecycle helpers ----------------------------------------------------------------

    /**
     * Removes the account created by the test even when the test failed part-way. Runs before
     * BaseTest's teardown (subclass @AfterMethod first), so the driver is still alive. Must not
     * throw: an exception here would skip the remaining tests in the class. Catches
     * AssertionError too because the page verify* methods throw it.
     */
    @AfterMethod(alwaysRun = true)
    public void cleanupCreatedAccount() {
        if (driver == null || createdAccountDisplayName == null) {
            return;
        }
        String accountToDelete = createdAccountDisplayName;
        try {
            resellerDashboardPage.recoverToAccountList();
            deleteAccount(accountToDelete);
            logger.info("@AfterMethod: deleted test account '{}'", accountToDelete);
        } catch (Exception | AssertionError e) {
            logger.error("@AfterMethod: could not delete test account '{}': {}", accountToDelete, e.toString());
        } finally {
            createdAccountDisplayName = null;
        }
    }

    private void deleteAccount(String accountDisplayName) {
        resellerDashboardPage.scrollUntilAccountIsVisible(accountDisplayName);
        int accountsBefore = resellerDashboardPage.getAccountCount();
        resellerDashboardPage.longPressOnAccount(accountDisplayName);
        resellerDashboardPage.clickDeleteAccountOption();
        resellerDashboardPage.clickDeleteConfirmButton();
        resellerDashboardPage.verifyAccountDeleted(accountsBefore);
    }
}
