package tests.ts_downloads;

import base.BaseTest;
import constants.TestDataConstants;
import constants.TimeoutConstants;
import locators.BottomNavigationLocators;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BottomNavigationBar;
import pages.DownloadsPage;
import utils.WaitUtils;

/**
 * Test suite for Downloads functionality.
 * The downloads that must finish use a medium folder and video, the cancel step a large video. Both tests look for
 * them first and skip, with the reason, when the account has none. A medium download can end before its progress is
 * visible, so "Downloading", "in progress" and the progress bar are checked in the cancel step, whose download is
 * still running.
 */
public class DownloadsTest extends BaseTest {

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
            // Cancel any active download first
            if (downloadsPage != null) {
                downloadsPage.cancelActiveDownloads();
            }

            // Navigate back until bottom navigation bar is showing (up to 3 backs)
            for (int i = 0; i < 3 && !BottomNavigationBar.isShowing(driver); i++) {
                downloadsPage.navigateBack();
            }

            // Log if we couldn't reach the main screen
            if (!BottomNavigationBar.isShowing(driver)) {
                logger.warn("Cleanup: Could not navigate back to main screen with bottom navigation");
            }
        } catch (Throwable t) {
            logger.warn("Cleanup failed: {}", t.getMessage());
        }
    }

    @Test(description = "EEPD-TC-13330: POS-Downloads - downloading a folder in mobile app",
          groups = {"regression", "downloads", "ios"})
    public void test01_folderDownloadWithCancellation() {
        downloadsPage.navigateToDownloads();
        downloadsPage.findDownloadTargets();
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.verifyProgressTabIsVisible();
        downloadsPage.verifyTestDataExists();
        downloadsPage.verifyDownloadOptionsPresent();
        downloadsPage.clickDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.waitForDownloadComplete();

        // Folder cancellation flow
        downloadsPage.selectLargeDownloadTarget();
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.clickDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadInProgress();  // Folders show "In progress", videos show progress bar
        downloadsPage.clickCancelDownloadIcon();
        downloadsPage.clickYesCancelButton();
        downloadsPage.verifyDownloadCancelledMessage();
        downloadsPage.verifyDownloadCancelled();
    }

    // Note: "Full video type" refers to the video format, not a user-selectable option in the app
    @Test(description = "EEPD-TC-13207: POS-Downloads - downloading a video - Full video type in mobile app",
          groups = {"regression", "downloads", "ios"})
    public void test02_videoDownloadWithCancellation() {
        downloadsPage.navigateToDownloads();
        downloadsPage.findDownloadTargets();
        downloadsPage.verifyTestDataExists();
        downloadsPage.openCurrentFolder();
        downloadsPage.verifyVideoDownloadOptionsPresent();
        downloadsPage.clickVideoDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.waitForVideoDownloadComplete();
        downloadsPage.navigateBack();

        // Video cancellation flow
        downloadsPage.selectLargeDownloadTarget();
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.openCurrentFolder();
        downloadsPage.clickVideoDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadProgressBar();
        downloadsPage.clickVideoCancelDownloadIcon();
        downloadsPage.clickYesCancelButton();
        downloadsPage.verifyDownloadCancelledMessage();
        downloadsPage.verifyDownloadCancelled();
    }
}
