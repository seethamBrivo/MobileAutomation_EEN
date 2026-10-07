package tests.ts_my_profile;

import base.BaseTest;
import constants.TestDataConstants;
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

    private DevicesPage devicesPage;
    private MyProfilePage myProfilePage;
    private TimeZonePage timeZonePage;
    private VSPEventsPage vspEventsPage;
    private VideoSearchPage videoSearchPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToMyProfile() {
        loginAs(TestDataConstants.TS02_CREDS);
        devicesPage = new DevicesPage(driver);
        myProfilePage = devicesPage.navigateToMyProfile();
    }

    @Test(description = "EEPD-TC-3920: POS-My Profile - setting 'Time zone' in VMS mobile app",
          groups = {"regression", "profile"})
    public void verifyTimezoneSettingFunctionality() {
        final String searchTerm = JsonDataReader.getTimezoneDataValue(TestDataConstants.TIMEZONE_SEARCH_TERM);
        final String invalidSearchTerm = JsonDataReader.getTimezoneDataValue(TestDataConstants.TIMEZONE_INVALID_SEARCH_TERM);
        final String testTimezone = JsonDataReader.getTimezoneDataValue(TestDataConstants.TEST_TIMEZONE);

        myProfilePage.clickProfileSection();
        myProfilePage.scrollToTimezoneOption();
        myProfilePage.clickTimezoneOption();
        timeZonePage = new TimeZonePage(driver);
        timeZonePage.verifyTimezonePopupDisplayed();
        timeZonePage.verifySearchPlaceholderDisplayed();
        timeZonePage.clickSearchBar();
        timeZonePage.enterSearchTerm(searchTerm);
        timeZonePage.verifyTimezoneListAdjustedToInput(searchTerm);
        timeZonePage.enterSearchTerm(invalidSearchTerm);
        timeZonePage.verifyEmptyStateDisplayed();
        timeZonePage.clearSearchField();
        timeZonePage.enterSearchTerm(testTimezone);
        timeZonePage.selectAndVerifyFirstTimezone(testTimezone);
        myProfilePage.verifyTimezoneChanged(testTimezone);
        vspEventsPage = new VSPEventsPage(driver);
        vspEventsPage.navigateBackFromProfile();
        vspEventsPage.verifyVSPSectionVisible();
        vspEventsPage.clickEvents();
        vspEventsPage.verifyVSPEventsScreenDisplayed();
        vspEventsPage.verifyVSPEventTimestampFormatDisplayed();
        videoSearchPage = new VideoSearchPage(driver);
        videoSearchPage.navigateBackFromVSPEvents();
        videoSearchPage.clickSearch();
        videoSearchPage.verifyDateFieldDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void resetTimezone() {
        if (driver == null) {
            return;
        }

        try {
            final String resetTimezone = JsonDataReader.getTimezoneDataValue(TestDataConstants.VALID_TIMEZONE);
            devicesPage = new DevicesPage(driver);
            devicesPage.recoverToDashboard();
            driver.navigate().back(); // Exit Dashboard view to return to main screen with bottom nav
            myProfilePage = devicesPage.navigateToMyProfile();
            myProfilePage.clickProfileSection();
            myProfilePage.scrollToTimezoneOption();
            myProfilePage.clickTimezoneOption();
            timeZonePage = new TimeZonePage(driver);
            timeZonePage.enterSearchTerm(resetTimezone);
            timeZonePage.selectAndVerifyFirstTimezone(resetTimezone);
            myProfilePage.verifyTimezoneChanged(resetTimezone);
            logger.info("Timezone reset to {} successfully", resetTimezone);
        } catch (Exception e) {
            logger.error("Failed to reset timezone for shared ts02 account. " +
                        "This may affect subsequent test runs. Error: {}", e.getMessage());
        }
    }
}
