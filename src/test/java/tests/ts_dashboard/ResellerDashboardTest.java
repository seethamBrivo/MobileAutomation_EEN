package tests.ts_dashboard;

import base.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ResellerDashboardPage;
import utils.GestureUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResellerDashboardTest extends BaseTest {

    private ResellerDashboardPage resellerDashboardPage;

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
        String customer_ID = getTestDataValue("customer_Id_Above_120_Characters");
        resellerDashboardPage.enterCustomerId(customer_ID);
        resellerDashboardPage.enterAccountName("TestAccount_TC40220");
        resellerDashboardPage.clickAddOption();
        resellerDashboardPage.verifyMax120CharsErrorMessageDisplayed();

        // Create account with unique timestamp-based identifiers (error will clear when entering valid data)
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String timestamp = sdf.format(new Date());
        String accountName = "TC40220_" + timestamp;
        String customerId = "CustID_" + timestamp;

        resellerDashboardPage.enterAccountName(accountName);
        resellerDashboardPage.enterCustomerId(customerId);
        resellerDashboardPage.clickEmailField();
        String email = getTestDataValue("test_account_email");
        resellerDashboardPage.enterEmail(email);
        resellerDashboardPage.clickAddOption();
        resellerDashboardPage.verifySuccessMessageDisplayed();

        String fullAccountName = accountName + " (" + customerId + ")";
        resellerDashboardPage.scrollUntilAccountIsVisible(fullAccountName);
        resellerDashboardPage.verifyAccountNameDisplayed(fullAccountName);

        // Cleanup
        try {
            resellerDashboardPage.longPressOnAccount(fullAccountName);
            resellerDashboardPage.clickDeleteAccountOption();
            resellerDashboardPage.clickDeleteConfirmButton();
            resellerDashboardPage.verifyDeletedSuccessMessageDisplayed();
        } catch (Exception e) {
            logger.warn("Cleanup failed - account deletion unsuccessful: " + e.getMessage());
        }
    }
}
