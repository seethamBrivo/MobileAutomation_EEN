package tests.ts_dashboard;

import base.BaseTest;
import org.openqa.selenium.Point;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import utils.GestureUtils;
import utils.JsonDataReader;
import pages.ResellerDashboardPage;

public class ResellerDashboardTest extends BaseTest {

    @Test(description = "EEPD-TC-4516: POS-Reseller Dashboard - (View Dashboard for Reseller) in mobile VMS app")
    public void verifyResellerDashboard() {
    	ResellerDashboardPage resellerDashboardPage = loginAs("reseller_user_creds");
        Assert.assertTrue(
        		resellerDashboardPage.isDashboardPageDisplayed(),
                "Dashboard page should be visible after reseller login"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isTotalAccountsDisplayed(),
                "total accounts with count should be visible after reseller login"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isLargeModelTransformationIconDisplayed(),
                "Large model transformation icon should be visible in dashboard"
        );
        JsonObject testData = JsonDataReader.readJsonObject("testData.json");
        String bridgeAccountName = testData
                .getAsJsonObject("credentials")
                .getAsJsonObject("reseller_user_creds")
                .get("bridge-account-name")
                .getAsString();

		Assert.assertTrue(
		resellerDashboardPage.isAccountNameDisplayed(bridgeAccountName)
		);
        Assert.assertTrue(
        		resellerDashboardPage.isBridgeResourceIconDisplayed(),
                "Bridge resource icon should be disaplyed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isGreenDotIsDisplayed(),
                "Green dot is displayed for online status"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isRedDotIsDisplayed(),
                "Green dot is displayed for offline status"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isyellowDotIsDisplayed(),
                "yellow dot is displayed for connectivity issue status"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isGreyDotIsDisplayed(),
                "Grey dot is displayed for turned off status"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isCameraResourceIconIsDisplayed(),
                "Camera resource icon is displayed"
        );
        resellerDashboardPage.clickThreeDotsOption();
        Assert.assertTrue(
        		resellerDashboardPage.isSettingsOptionDisplayed(),
                "Settings option is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isDeleteAccountOptionDisplayed(),
                "Delete account option is displayed"
        );
        GestureUtils.tap(driver, new Point(500, 1000));
        Assert.assertTrue(
        		resellerDashboardPage.isFilterByAccountNameFilterDisplayed(),
                "Filter by account name filter is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isAddAccountButtonDisplayed(),
                "Add account button is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isDashboardOptionDisplayed(),
                "Dashboard option is dislayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isUsersOptionDisplayed(),
                "Users option is dislayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isBusinessPortalOptionDisplayed(),
                "Business portal option is dislayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isMoreOptionDisplayed(),
                "More option is dislayed"
        );
    }
    
    @Test(description = "EEPD-TC-40220: POS-Reseller Dashboard - Verify the account creation for Admin OR Account access users")
    public void verifyAccountCreationForAdminOrAccountAccessUsers() {
    	ResellerDashboardPage resellerDashboardPage = loginAs("reseller_user_creds");
        Assert.assertTrue(
        		resellerDashboardPage.isDashboardPageDisplayed(),
                "Dashboard page should be visible after reseller login"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isAddAccountButtonDisplayed(),
                "Add account button is displayed"
        );
        resellerDashboardPage.clickAddAccountButton();
        
        Assert.assertTrue(
        		resellerDashboardPage.isAccountNameFieldDisplayed(),
                "Account Name field is dislayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isAddButtonDisplayed(),
                "Add button is dislayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isAddCustomerIdFieldDisplayed(),
                "Add customer id field is dislayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isEmailFieldDisplayed(),
                "Email field displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isEditionFieldDisplayed(),
                "Edition field displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isStandardOptionIsDisplayed(),
                "Default 'Standard' option is displayed "
        );
        resellerDashboardPage.clickEditionSectionNavigationIcon();
        Assert.assertTrue(
        		resellerDashboardPage.isProfessionalOptionDisplayed(),
                "Professional option in edition section is visible"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isEnterPriseOptionDisplayed(),
                "Enterprise option in edition section is visible"
        );
        resellerDashboardPage.clickEditionSectionBackNavigationIcon();
        Assert.assertTrue(
        		resellerDashboardPage.isCameraDefaultsSectionDisplayed(),
                "Camera defaults section is visible"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isCloudRetentionOptionDisplayed(),
                "Cloud retention option is visible"
        );
        Assert.assertTrue(
        		resellerDashboardPage.isThirtyDaysDefaultOptionDisplayed(),
                "Thirty days default for cloud retention is visible"
        );
        resellerDashboardPage.cloudRetentionSectionNavigationIcon();
        Assert.assertTrue(
        		resellerDashboardPage.isMinimumOneDayOptionDisplayed(),
                "Minimum one day 1 day(M10) value is visible"
        );
        GestureUtils.swipeUp(driver);
        Assert.assertTrue(
        		resellerDashboardPage.isMax5YearsOptionDisplayed(),
                "Max 5 years value is visible"
        );
        resellerDashboardPage.clickCloudRetentionSectionBackNavigationIcon();
        Assert.assertTrue(
        		resellerDashboardPage.isMinRetentionOptionDisplayed(),
                "Minimum retention option is visible"
        );
        Assert.assertTrue(
        		resellerDashboardPage.is14DaysDefaultOptionDisplayed(),
                "14 days is the default value for minimum retention section"
        );
        resellerDashboardPage.clickMinRetentionSectionViewIcon();
        Assert.assertTrue(
        		resellerDashboardPage.isMinRetetionSectionMinValueIsNoneDisplayed(),
                "Minimum value in minimum retention section is 'none' displayed"
        );
        GestureUtils.swipeUp(driver);
        Assert.assertTrue(
        		resellerDashboardPage.isMinRetetionSectionMaxValueIs180DaysDisplayed(),
                "Maximum value in minimum retention section is '180days' is displayed"
        );
        resellerDashboardPage.clickMinRetentionSectionBackIcon();
        Assert.assertTrue(
        		resellerDashboardPage.isMaxRetentionOptionDisplayed(),
                "Max retention option is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.sixtyDaysByDefaultOptionDisplayed(),
                "60 days by default option is displayed in max retention"
        );
        resellerDashboardPage.clickMaxRetentionSectionViewIcon();
        Assert.assertTrue(
        		resellerDashboardPage.minimumValueOptionIsNoneInMaxRetentionDisplayed(),
                "Minimum value in minimum retention section is 'none' displayed"
        );
        GestureUtils.swipeUp(driver);
        Assert.assertTrue(
        		resellerDashboardPage.maxValueOptionIs1YearInMaxRetentionDisplayed(),
                "Maximum value in minimum retention section is '1 year' is displayed"
        );
        resellerDashboardPage.clickMaxRetentionSectionBackIcon();
        Assert.assertTrue(
        		resellerDashboardPage.previewResolutionOptionIsDisplayed(),
                "Preview resolution option is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.defaultpreviewResolutionOptionIsDisplayed(),
                "Default Preview resolution option 'cif (SD1 320*240)' is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.fullVideoResolutionOptionIsDisplayed(),
                "Full video resolution is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.fullVideoResolutionDefaultValueIsDisplayed(),
                "Full video resolution default value 'high (HD1 1280*720)' is displayed"
        );
        Assert.assertTrue(
        		resellerDashboardPage.cloudPreviewOnlyToggleButton(),
                "Cloud preview only toggle is displayed"
        );
        JsonObject testData =
                JsonDataReader.readJsonObject("testData.json");

        String customer_ID = testData
                .getAsJsonObject("credentials")
                .getAsJsonObject("reseller_user_creds")
                .get("custromer_Id_Above_120_Characters")
                .getAsString();
        resellerDashboardPage.enterCustomerIdMoreThan120Characters(customer_ID);
        resellerDashboardPage.enterAccountName("NewAccountNia");
        resellerDashboardPage.clickAddOption();
        Assert.assertTrue(
        		resellerDashboardPage.isMax120CharsErrorMessageDisplayed(),
                "Max 120 charaters error message should displayed if user try to enter more than 120 characters for customer id"
        );
    }
   
}