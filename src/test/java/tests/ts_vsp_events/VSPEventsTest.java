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
    private String originalTimeFormat = null; // BLOCKER FIX B3: Store original value to restore, not hardcode

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
        // BLOCKER FIX B6: Set flag BEFORE rotation so cleanup runs even if rotation fails
        rotationWasUsed = true;
        vspEventsPage.rotateToLandscape();
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
        // BLOCKER FIX B3: Read original value to restore later, not hardcode
        originalTimeFormat = myProfilePage.getCurrentTimeFormat();
        myProfilePage.clickTimeFormatOption();
        // BLOCKER FIX B3: Set flag BEFORE selectTimeFormat so cleanup runs even if verifyTimeFormatChanged fails
        timeFormatWasChanged = true;
        myProfilePage.selectTimeFormat("12 hours");
        myProfilePage.verifyTimeFormatChanged("12 hours");
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

                if (timeFormatWasChanged && myProfilePage != null && originalTimeFormat != null) {
                    devicesPage.recoverToDashboard();
                    driver.navigate().back(); // Back to bottom nav
                    myProfilePage = devicesPage.navigateToMyProfile();
                    myProfilePage.clickProfileSection();
                    myProfilePage.scrollToTimeFormatOption();
                    myProfilePage.clickTimeFormatOption();
                    // BLOCKER FIX B3: Restore original value, not hardcode
                    myProfilePage.selectTimeFormat(originalTimeFormat);
                    myProfilePage.verifyTimeFormatChanged(originalTimeFormat);
                    timeFormatWasChanged = false;
                    logger.info("Time format reset to {}", originalTimeFormat);
                }
            }
        } catch (AssertionError | Exception e) { // BLOCKER FIX B5: Catch both - AssertionError escapes plain Exception
            logger.warn("Failed to cleanup: {}", e.getMessage());
        } finally {
            super.tearDown(testResult);
        }
    }
}
