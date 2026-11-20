package tests.ts_my_profile;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DevicesPage;
import pages.MyProfilePage;
import utils.GestureUtils;

/**
 * Test class for My Profile functionality specific to Reseller accounts.
 * Uses RESELLER_CREDS instead of TS02_CREDS.
 */
public class MyProfileResellerTest extends BaseTest {

    private MyProfilePage myProfilePage;

    @BeforeMethod
    public void setupTest() {
        // Login with reseller account and navigate to My Profile
        DevicesPage devicesPage = loginAndCreatePage(TestDataConstants.RESELLER_CREDS, DevicesPage::new);
        myProfilePage = devicesPage.navigateToMyProfile();
        logger.info("Test setup completed for {}.{}", getClass().getSimpleName(),
                   java.lang.Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Test(description = "EPD-TC-1329: POS-My Profile - Call support as a Reseller in mobile VMS app",
          groups = {"regression", "profile"})
    public void verifyCallSupportAsReseller() {
        myProfilePage.verifyMyProfileScreenDisplayed();
        myProfilePage.clickProfileSection();
        myProfilePage.scrollDownInProfile();
        myProfilePage.clickSupportPin();
        driver.navigate().back();
        GestureUtils.swipeUp(driver);
        myProfilePage.clickCallSupport();
        myProfilePage.verifyPhonePermissionDialogDisplayed();
    }
}
