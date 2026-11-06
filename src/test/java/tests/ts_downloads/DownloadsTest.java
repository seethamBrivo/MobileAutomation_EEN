package tests.ts_downloads;

import base.BaseTest;
import constants.TestDataConstants;
import constants.TimeoutConstants;
import locators.BottomNavigationLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DownloadsPage;
import utils.WaitUtils;

public class DownloadsTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(DownloadsTest.class);
    private DownloadsPage downloadsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupDownloadsTest() {
        loginAs(TestDataConstants.TS02_CREDS);
        downloadsPage = new DownloadsPage(driver);
        // Verify we landed on the expected screen with More tab visible
        WaitUtils.assertVisible(driver, BottomNavigationLocators.MORE_TAB,
                TimeoutConstants.SHORT_TIMEOUT, "Should land on screen with More tab visible after login");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null) {
            return;
        }
        try {
            // Navigate back from Downloads screen to main screen with More tab visible
            driver.navigate().back();
        } catch (Throwable t) {
            logger.warn("Cleanup failed: {}", t.getMessage());
        }
    }

    @Test(description = "EEPD-TC-13330: POS-Downloads - downloading a folder in mobile app",
          groups = {"regression", "downloads"})
    public void test01_folderDownloadWithCancellation() {
        downloadsPage.verifyCompleteFolderDownloadFlow();
        downloadsPage.verifyFolderDownloadCancellation();
    }

    @Test(description = "EEPD-TC-13207: POS-Downloads - downloading a video - Full video type in mobile app",
          groups = {"regression", "downloads"})
    public void test02_videoDownloadWithCancellation() {
        downloadsPage.verifyCompleteVideoDownloadFlow();
        downloadsPage.verifyVideoDownloadCancellation();
    }
}
