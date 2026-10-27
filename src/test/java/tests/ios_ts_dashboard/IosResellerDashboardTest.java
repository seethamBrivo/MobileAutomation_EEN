package tests.ios_ts_dashboard;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ios.IosResellerDashboardPage;

/**
 * iOS counterpart of {@code tests.ts_dashboard.ResellerDashboardTest}. Only the read-only view test is ported so
 * far; the account creation test (EEPD-TC-40220) creates and deletes an account and is still to do.
 */
public class IosResellerDashboardTest extends BaseTest {

    private IosResellerDashboardPage resellerDashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndVerifyDashboard() {
        iosLoginAs(TestDataConstants.RESELLER_CREDS);
        resellerDashboardPage = new IosResellerDashboardPage(driver);
        resellerDashboardPage.verifyDashboardPageDisplayed();
    }

    @Test(description = "EEPD-TC-4516: POS-Reseller Dashboard - (View Dashboard for Reseller) in mobile VMS app",
          groups = {"ios", "dashboard"})
    public void verifyResellerDashboard() {
        resellerDashboardPage.verifyDashboardSummary();
        resellerDashboardPage.verifyAccountCardContents(getTestDataValue("bridge-account-name"));
        resellerDashboardPage.verifyContextMenuOptions();
        resellerDashboardPage.verifyDashboardToolbar();
        resellerDashboardPage.verifyBottomNavigationBar();
    }
}
