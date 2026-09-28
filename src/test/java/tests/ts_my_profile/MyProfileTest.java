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

    @BeforeMethod
    public void loginAndNavigateToMyProfile() {
        loginAs(TestDataConstants.TS02_CREDS);
        devicesPage = new DevicesPage(driver);
        myProfilePage = devicesPage.navigateToMyProfile();
    }

    @Test(description = "EEPD-TC-3920: POS-My Profile - setting 'Time zone' in VMS mobile app")
    public void verifyTimezoneSettingFunctionality() {
        final String searchTerm = JsonDataReader.getTimezoneDataValue(TestDataConstants.TIMEZONE_SEARCH_TERM);
        final String invalidSearchTerm = JsonDataReader.getTimezoneDataValue(TestDataConstants.TIMEZONE_INVALID_SEARCH_TERM);
        final String testTimezone = "Calcutta"; // Specific timezone for verification

        myProfilePage.clickProfileSection();
        myProfilePage.scrollToTimeSection();
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
        timeZonePage.selectFirstTimezone();
        myProfilePage.verifyTimezoneChanged(testTimezone);

        vspEventsPage = new VSPEventsPage(driver);
        vspEventsPage.navigateBackFromProfile();
        vspEventsPage.verifyVSPSectionVisible();
        vspEventsPage.clickEvents();
        vspEventsPage.verifyVSPEventsScreenDisplayed();
        vspEventsPage.verifyTimezoneAppliedInVSPEvents();

        videoSearchPage = new VideoSearchPage(driver);
        videoSearchPage.navigateBackFromVSPEvents();
        videoSearchPage.clickSearch();
        videoSearchPage.verifyTimezoneAppliedInVideoSearch();
    }

    @AfterMethod(alwaysRun = true)
    public void resetTimezone() {
        if (driver == null) {
            return;
        }

        try {
            devicesPage = new DevicesPage(driver);
            myProfilePage = devicesPage.navigateToMyProfile();
            myProfilePage.clickProfileSection();
            myProfilePage.scrollToTimeSection();
            myProfilePage.clickTimezoneOption();
            timeZonePage = new TimeZonePage(driver);
            timeZonePage.enterSearchTerm("Alaska");
            timeZonePage.selectFirstTimezone();
            logger.info("Timezone reset to US/Alaska successfully");
        } catch (Exception e) {
            logger.warn("Failed to reset timezone: {}. BaseTest teardown will handle cleanup.", e.getMessage());
        }
    }
}
