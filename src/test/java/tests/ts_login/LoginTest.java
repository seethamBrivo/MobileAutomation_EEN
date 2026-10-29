package tests.ts_login;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LayoutsPage;

/**
 * Sign-in smoke test: signs in as the end user and checks the screen the app lands on. It has no Android
 * counterpart and no test-case ID, and only {@code testng-ios.xml} runs it (group "ios"); every other
 * Android test already signs in as part of its setup.
 */
public class LoginTest extends BaseTest {

    private LayoutsPage layoutsPage;

    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        loginAs(TestDataConstants.ENDUSER_CREDS);
        layoutsPage = new LayoutsPage(driver);
    }

    @Test(description = "Sign in as the end user and verify the Layouts landing screen",
          groups = {"login", "ios"})
    public void verifySignInLandsOnLayouts() {
        layoutsPage.verifyLayoutsScreenDisplayed();
    }
}
