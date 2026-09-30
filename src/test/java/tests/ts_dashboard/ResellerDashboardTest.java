package tests.ts_dashboard;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ResellerDashboardPage;
import utils.GestureUtils;

public class ResellerDashboardTest extends BaseTest {

    private ResellerDashboardPage resellerDashboardPage;

    @BeforeMethod
    public void loginAndVerifyDashboard() {
        resellerDashboardPage = loginAs(TestDataConstants.RESELLER_CREDS);
        resellerDashboardPage.verifyDashboardPageDisplayed();
    }

    @Test(description = "EEPD-TC-4516: POS-Reseller Dashboard - (View Dashboard for Reseller) in mobile VMS app")
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

    @Test(description = "EEPD-TC-40220: POS-Reseller Dashboard - Verify the account creation for Admin OR Account access users")
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
        String customer_ID = getTestDataValue("customer_Id_Above_120_Characters");
        resellerDashboardPage.enterCustomerIdMoreThan120Characters(customer_ID);
        resellerDashboardPage.enterAccountName("NewAccountNia");
        resellerDashboardPage.clickAddOption();
        resellerDashboardPage.verifyMax120CharsErrorMessageDisplayed();
    }
}
