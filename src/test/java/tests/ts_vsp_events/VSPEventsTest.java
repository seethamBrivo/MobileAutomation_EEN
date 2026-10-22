package tests.ts_vsp_events;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DevicesPage;
import pages.MyProfilePage;
import pages.VSPEventsPage;

/**
 * VSP Events test suite covering card display, pagination, orientation, and detail page functionality.
 */
public class VSPEventsTest extends BaseTest {

    private DevicesPage devicesPage;
    private MyProfilePage myProfilePage;
    private VSPEventsPage vspEventsPage;
    private boolean rotationWasUsed = false;
    private boolean timeFormatWasChanged = false;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        loginAs(TestDataConstants.TS02_CREDS);
        devicesPage = new DevicesPage(driver);
        vspEventsPage = new VSPEventsPage(driver);
    }

    @Test(description = "EEPD-TC-37646: POS-VSP-Verify Event Cards in the VSP events page",
          groups = {"regression", "vsp", "events"})
    public void verifyEventCardsInVSPEventsPage() {
        vspEventsPage.navigateToVSPEvents();
        vspEventsPage.verifyEventCardsDisplayed();
        vspEventsPage.verifyCardsPerRow(1);
        vspEventsPage.verifyEventCardInformation();
        vspEventsPage.verifyToolbarHidesOnScroll();
        vspEventsPage.verifyPaginationWorks();
        vspEventsPage.rotateToLandscape();
        rotationWasUsed = true;
        vspEventsPage.verifyCardsPerRow(2);
        vspEventsPage.verifyEventCardInformation();
        vspEventsPage.verifyPaginationWorks();
    }

    @Test(description = "EEPD-TC-37714: POS-VSP-Verify the More info detail page",
          groups = {"regression", "vsp", "events"})
    public void verifyVSPEventDetailPage() {
        myProfilePage = devicesPage.navigateToMyProfile();
        myProfilePage.clickProfileSection();
        myProfilePage.scrollToTimeFormatOption();
        myProfilePage.clickTimeFormatOption();
        myProfilePage.selectTimeFormat("12 hours");
        myProfilePage.verifyTimeFormatChanged("12 hours");
        timeFormatWasChanged = true;
        vspEventsPage.navigateBackFromProfile();
        vspEventsPage.navigateToVSPEvents();
        vspEventsPage.verifyEventCardsDisplayed();
        vspEventsPage.clickFirstEventCard();
        vspEventsPage.verifyDetailPageDisplayed();
        vspEventsPage.verifyDetailPageInformation();
        vspEventsPage.closeDetailPage();
        vspEventsPage.verifyEventCardsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(ITestResult testResult) {
        try {
            if (driver != null) {
                if (rotationWasUsed && vspEventsPage != null) {
                    vspEventsPage.rotateToPortrait();
                    rotationWasUsed = false;
                }

                if (timeFormatWasChanged && myProfilePage != null) {
                    // Navigate directly to My Profile without intermediate back() - more robust than assuming navigation state
                    devicesPage.recoverToDashboard();
                    myProfilePage = devicesPage.navigateToMyProfile();
                    myProfilePage.clickProfileSection();
                    myProfilePage.scrollToTimeFormatOption();
                    myProfilePage.clickTimeFormatOption();
                    myProfilePage.selectTimeFormat("24 hours");
                    myProfilePage.verifyTimeFormatChanged("24 hours");
                    timeFormatWasChanged = false;
                    logger.info("Time format reset to 24 hours");
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to cleanup: {}", e.getMessage());
        } finally {
            super.tearDown(testResult);
        }
    }
}
