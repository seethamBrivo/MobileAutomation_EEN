package tests.ios_ts_login;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ios.IosLandingPage;

/** iOS sign-in smoke test: signs in as the end user and checks the screen the app lands on. */
public class IosLoginTest extends BaseTest {

    private IosLandingPage landingPage;

    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        iosLoginAs(TestDataConstants.ENDUSER_CREDS);
        landingPage = new IosLandingPage(driver);
    }

    @Test(description = "iOS: sign in as the end user and verify the Layouts landing screen and navigation",
          groups = {"ios-smoke"})
    public void verifySignInLandsOnLayouts() {
        landingPage.verifyLandingScreenDisplayed();
        landingPage.verifyTabBar();
        landingPage.verifyAllCamerasListed();
        landingPage.openMoreTab();
        landingPage.openLayoutsTab();
    }
}
