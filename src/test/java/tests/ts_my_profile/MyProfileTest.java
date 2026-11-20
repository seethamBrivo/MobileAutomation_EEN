package tests.ts_my_profile;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DevicesPage;
import pages.MyProfilePage;
import pages.TimeZonePage;
import pages.VSPEventsPage;
import pages.VideoSearchPage;
import utils.JsonDataReader;

public class MyProfileTest extends BaseTest {

    /** The time zone the account is reset to after the test. */
    private static final String ACCOUNT_TIMEZONE = "Asia/Kolkata";
    /** The time zone the test sets (EEPD-TC-3920), and the text that finds it in the list. */
    private static final String TEST_TIMEZONE = "Etc/GMT+12";
    private static final String TIMEZONE_SEARCH_TERM = "Etc";
    private static final String INVALID_TIMEZONE_SEARCH_TERM = "XYZ_INVALID_TIMEZONE_12345";
    /** Wrong password for negative testing (EEPD-TC-1326). */
    private static final String WRONG_PASSWORD = "WrongPassword123";
    /** Test email for login email change test (EEPD-TC-1326, EEPD-TC-1327). */
    private static final String TEST_NEW_EMAIL = "test.new@example.com";
    /** Wrong authentication code for negative testing (EEPD-TC-1327). */
    private static final String WRONG_AUTH_CODE = "0000";

    private DevicesPage devicesPage;
    private MyProfilePage myProfilePage;
    private TimeZonePage timeZonePage;
    private VSPEventsPage vspEventsPage;
    private VideoSearchPage videoSearchPage;

    private String initialVSPTimestamp;
    private String initialVideoSearchDate;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToMyProfile() {
        devicesPage = loginAndCreatePage(TestDataConstants.TS02_CREDS, DevicesPage::new);
        myProfilePage = devicesPage.navigateToMyProfile();
    }

    @Test(description = "EEPD-TC-1254: POS - My Profile - swiping down to refresh the data under the My Profile screen in VMS",
          groups = {"regression", "profile"})
    public void verifyPullToRefreshInMyProfile() {
        myProfilePage.verifyMyProfileScreenDisplayed();
        myProfilePage.clickProfileSection();
        myProfilePage.pullToRefresh();
        myProfilePage.verifyLoadingIndicatorAppears();
        myProfilePage.verifyDataRefreshed();
    }

    @Test(description = "EEPD-TC-1326: NEG-My Profile - Try to change login email when wrong verification password is provided in mobile VMS app",
          groups = {"regression", "profile"})
    public void verifyLoginEmailChangeWithWrongPassword() {
        myProfilePage.verifyMyProfileScreenDisplayed();
        myProfilePage.clickProfileSection();
        myProfilePage.clickProfileDetailsOption();
        myProfilePage.clickLoginEmailOption();
        myProfilePage.verifyNextButtonDisabled();
        myProfilePage.enterVerifyPassword(WRONG_PASSWORD);
        myProfilePage.enterNewEmail(TEST_NEW_EMAIL);
        myProfilePage.verifyNextButtonEnabled();
        myProfilePage.clickNextButton();
        myProfilePage.verifyPasswordErrorMessage();
    }

    @Test(description = "EEPD-TC-1327: NEG-My Profile - Try to change login email when wrong authentication code is provided in mobile VMS app",
          groups = {"regression", "profile"})
    public void verifyLoginEmailChangeWithWrongAuthCode() {
        String correctPassword = JsonDataReader.getCredentialValue(TestDataConstants.TS02_CREDS, "password");

        myProfilePage.verifyMyProfileScreenDisplayed();
        myProfilePage.clickProfileSection();
        myProfilePage.clickProfileDetailsOption();
        myProfilePage.clickLoginEmailOption();
        myProfilePage.verifyNextButtonDisabled();
        myProfilePage.enterVerifyPassword(correctPassword);
        myProfilePage.enterNewEmail(TEST_NEW_EMAIL);
        myProfilePage.verifyNextButtonEnabled();
        myProfilePage.clickNextButton();
        myProfilePage.enterAuthenticationCode(WRONG_AUTH_CODE);
        myProfilePage.clickConfirmButton();
        myProfilePage.verifyAuthCodeErrorMessage();
    }

    @Test(description = "EEPD-TC-3920: POS-My Profile - setting 'Time zone' in VMS mobile app",
          groups = {"regression", "profile", "ios", "timezone"})
    public void verifyTimezoneSettingFunctionality() {
        vspEventsPage = new VSPEventsPage(driver);
        vspEventsPage.verifyVSPSectionVisible();
        vspEventsPage.clickEvents();
        vspEventsPage.verifyVSPEventsScreenDisplayed();
        initialVSPTimestamp = vspEventsPage.captureEventTimestamp();
        logger.info("Initial VSP timestamp (before timezone change): {}", initialVSPTimestamp);
        videoSearchPage = new VideoSearchPage(driver);
        videoSearchPage.navigateBackFromVSPEvents();
        videoSearchPage.clickSearch();
        initialVideoSearchDate = videoSearchPage.captureDateFieldText();
        logger.info("Initial Video Search date (before timezone change): {}", initialVideoSearchDate);
        devicesPage = new DevicesPage(driver);
        myProfilePage = devicesPage.navigateToMyProfile();
        // Step 2: Go to the TIME section and click the Time zone item
        myProfilePage.openTimezonePicker();
        timeZonePage = new TimeZonePage(driver);
        timeZonePage.verifyPopupAndSearchPlaceholder();
        // Step 3: Tap the search bar and type a search term
        timeZonePage.searchTimezonesAndVerify(TIMEZONE_SEARCH_TERM, INVALID_TIMEZONE_SEARCH_TERM);
        // Step 4: Set the time zone by tapping on it
        timeZonePage.selectTimezone(TEST_TIMEZONE);
        myProfilePage.verifyTimezoneChanged(TEST_TIMEZONE);
        vspEventsPage.navigateBackFromProfile();
        vspEventsPage.verifyVSPSectionVisible();
        vspEventsPage.clickEvents();
        vspEventsPage.verifyVSPEventsScreenDisplayed();
        vspEventsPage.verifyTimestampChangedAfterTimezoneChange(initialVSPTimestamp);
        videoSearchPage.navigateBackFromVSPEvents();
        videoSearchPage.clickSearch();
        videoSearchPage.verifyDateFieldMatchesTimezone(TEST_TIMEZONE);
    }

    @AfterMethod(alwaysRun = true, onlyForGroups = {"timezone"})
    public void resetTimezone(ITestResult testResult) {
        try {
            if (driver != null) {
                final String resetTimezone = ACCOUNT_TIMEZONE;
                devicesPage = new DevicesPage(driver);
                devicesPage.recoverToDashboard();
                devicesPage.goBack(); // Exit Dashboard view to return to main screen with bottom nav
                myProfilePage = devicesPage.navigateToMyProfile();
                myProfilePage.clickProfileSection();
                myProfilePage.scrollToTimezoneOption();
                myProfilePage.clickTimezoneOption();
                timeZonePage = new TimeZonePage(driver);
                timeZonePage.enterSearchTerm(resetTimezone);
                timeZonePage.selectAndVerifyFirstTimezone(resetTimezone);
                myProfilePage.verifyTimezoneChanged(resetTimezone);
                logger.info("Timezone reset to {} successfully", resetTimezone);
            }
        } catch (Throwable e) { // the page checks throw AssertionError, which is not an Exception
            logger.error("Failed to reset timezone for shared ts02 account. " +
                        "This may affect subsequent test runs. Error: {}", e.getMessage());
        } finally {
            super.tearDown(testResult);
        }
    }
}
