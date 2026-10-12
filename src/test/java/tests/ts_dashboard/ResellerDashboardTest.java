package tests.ts_dashboard;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ResellerDashboardPage;
import utils.GestureUtils;
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
          groups = {"regression", "dashboard"})
    public void verifyResellerDashboard() {
        resellerDashboardPage.verifyTotalAccountsDisplayed();
        resellerDashboardPage.verifyLargeModelTransformationIconDisplayed();
        String bridgeAccountName = getTestDataValue("bridge-account-name");
        resellerDashboardPage.verifyAccountNameDisplayed(bridgeAccountName);
        resellerDashboardPage.verifyBridgeResourceIconDisplayed();
        resellerDashboardPage.verifyGreenDotDisplayed();
        resellerDashboardPage.verifyRedDotDisplayed();
        resellerDashboardPage.verifyYellowDotDisplayed();
        resellerDashboardPage.verifyGreyDotDisplayed();
        resellerDashboardPage.verifyCameraResourceIconDisplayed();
        resellerDashboardPage.clickThreeDotsOption();
        resellerDashboardPage.verifySettingsOptionDisplayed();
        resellerDashboardPage.verifyDeleteAccountOptionDisplayed();
        resellerDashboardPage.dismissContextMenu();
        resellerDashboardPage.verifyFilterByAccountNameDisplayed();
        resellerDashboardPage.verifyAddAccountButtonDisplayed();
        resellerDashboardPage.verifyDashboardOptionDisplayed();
        resellerDashboardPage.verifyUsersOptionDisplayed();
        resellerDashboardPage.verifyBusinessPortalOptionDisplayed();
        resellerDashboardPage.verifyMoreOptionDisplayed();
    }

    @Test(description = "EEPD-TC-40220: POS-Reseller Dashboard - Verify the account creation for Admin OR Account access users",
          groups = {"regression", "dashboard"})
    public void verifyAccountCreationForAdminOrAccountAccessUsers() {
        resellerDashboardPage.verifyAddAccountButtonDisplayed();
        resellerDashboardPage.clickAddAccountButton();
        resellerDashboardPage.verifyAccountNameFieldDisplayed();
        resellerDashboardPage.verifyAddButtonDisplayed();
        resellerDashboardPage.verifyAddCustomerIdFieldDisplayed();
        resellerDashboardPage.verifyEmailFieldDisplayed();
        resellerDashboardPage.verifyEditionFieldDisplayed();
        resellerDashboardPage.verifyStandardOptionDisplayed();
        resellerDashboardPage.clickEditionSectionNavigationIcon();
        resellerDashboardPage.verifyProfessionalOptionDisplayed();
        resellerDashboardPage.verifyEnterpriseOptionDisplayed();
        resellerDashboardPage.clickEditionSectionBackNavigationIcon();
        resellerDashboardPage.verifyCameraDefaultsSectionDisplayed();
        resellerDashboardPage.verifyCloudRetentionOptionDisplayed();
        resellerDashboardPage.verifyThirtyDaysDefaultOptionDisplayed();
        resellerDashboardPage.cloudRetentionSectionNavigationIcon();
        resellerDashboardPage.verifyMinimumOneDayOptionDisplayed();
        GestureUtils.swipeUp(driver);
        resellerDashboardPage.verifyMax5YearsOptionDisplayed();
        resellerDashboardPage.clickCloudRetentionSectionBackNavigationIcon();
        resellerDashboardPage.verifyMinRetentionOptionDisplayed();
        resellerDashboardPage.verify14DaysDefaultOptionDisplayed();
        resellerDashboardPage.clickMinRetentionSectionViewIcon();
        resellerDashboardPage.verifyMinRetentionSectionMinValueNoneDisplayed();
        GestureUtils.swipeUp(driver);
        resellerDashboardPage.verifyMinRetentionSectionMaxValue180DaysDisplayed();
        resellerDashboardPage.clickMinRetentionSectionBackIcon();
        resellerDashboardPage.verifyMaxRetentionOptionDisplayed();
        resellerDashboardPage.verifySixtyDaysByDefaultOptionDisplayed();
        resellerDashboardPage.clickMaxRetentionSectionViewIcon();
        resellerDashboardPage.verifyMinimumValueOptionIsNoneInMaxRetentionDisplayed();
        GestureUtils.swipeUp(driver);
        resellerDashboardPage.verifyMaxValueOptionIs1YearInMaxRetentionDisplayed();
        resellerDashboardPage.clickMaxRetentionSectionBackIcon();
        resellerDashboardPage.verifyPreviewResolutionOptionDisplayed();
        resellerDashboardPage.verifyDefaultPreviewResolutionOptionDisplayed();
        resellerDashboardPage.verifyFullVideoResolutionOptionDisplayed();
        resellerDashboardPage.verifyFullVideoResolutionDefaultValueDisplayed();
        resellerDashboardPage.verifyCloudPreviewOnlyToggleButtonDisplayed();

        // Verify Customer ID character limit validation
        // Generated rather than read from testData: the CI-provided value was only 36 characters,
        // so it never crossed the 120-character limit and no error appeared.
        resellerDashboardPage.enterCustomerId("A".repeat(MAX_CUSTOMER_ID_LENGTH + 1));
        resellerDashboardPage.enterAccountName(INVALID_CUSTOMER_ID_ACCOUNT_NAME);
        resellerDashboardPage.clickAddOption();
        resellerDashboardPage.verifyMax120CharsErrorMessageDisplayed();

        // Create account with unique identifiers (error will clear when entering valid data)
        String accountName = TestDataGenerator.generateUniqueName("TC40220_");
        String customerId = TestDataGenerator.generateUniqueName("CustID_");

        resellerDashboardPage.enterAccountName(accountName);
        resellerDashboardPage.enterCustomerId(customerId);
        resellerDashboardPage.clickEmailField();
        String email = getTestDataValue(TestDataConstants.TEST_ACCOUNT_EMAIL);
        resellerDashboardPage.enterEmail(email);
        resellerDashboardPage.clickAddOption();
        String fullAccountName = accountName + " (" + customerId + ")";
        createdAccountDisplayName = fullAccountName;
        resellerDashboardPage.verifySuccessMessageDisplayed();

        resellerDashboardPage.scrollUntilAccountIsVisible(fullAccountName);
        resellerDashboardPage.verifyAccountNameDisplayed(fullAccountName);

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
        resellerDashboardPage.longPressOnAccount(accountDisplayName);
        resellerDashboardPage.clickDeleteAccountOption();
        resellerDashboardPage.clickDeleteConfirmButton();
        resellerDashboardPage.verifyDeletedSuccessMessageDisplayed();
    }
}
