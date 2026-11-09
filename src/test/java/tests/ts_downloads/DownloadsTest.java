package tests.ts_downloads;

import base.BaseTest;
import constants.TestDataConstants;
import constants.TimeoutConstants;
import locators.BottomNavigationLocators;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BottomNavigationBar;
import pages.DownloadsPage;
import utils.PlatformLocator;
import utils.WaitUtils;

/**
 * Test suite for Downloads functionality.
 * NOTE: These tests use Android-only locators and are only verified on Android platform.
 */
public class DownloadsTest extends BaseTest {

    private DownloadsPage downloadsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupDownloadsTest() {
        if (PlatformLocator.isIos()) {
            throw new SkipException("Downloads tests are Android-only (no iOS locators implemented)");
        }
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
                driver.navigate().back();
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
          groups = {"regression", "downloads"})
    public void test01_folderDownloadWithCancellation() {
        // Complete folder download flow
        downloadsPage.navigateToDownloads();
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.verifyProgressTabIsVisible();
        downloadsPage.verifyTestDataExists();
        downloadsPage.verifyDownloadOptionsPresent();
        downloadsPage.clickDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadInProgress();
        downloadsPage.waitForDownloadComplete();

        // Folder cancellation flow
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.clickDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadInProgress();  // Folders show "In progress", videos show progress bar while downloading
        downloadsPage.clickCancelDownloadIcon();
        downloadsPage.clickYesCancelButton();
        downloadsPage.verifyDownloadCancelledMessage();
        downloadsPage.verifyDownloadCancelled();
    }

    @Test(description = "EEPD-TC-13207: POS-Downloads - downloading a video - Full video type in mobile app",
          groups = {"regression", "downloads"})
    // Note: "Full video type" refers to the video format, not a user-selectable option in the app
    public void test02_videoDownloadWithCancellation() {
        // Complete video download flow
        downloadsPage.navigateToDownloads();
        downloadsPage.verifyTestDataExists();
        downloadsPage.clickFirstFolder();
        downloadsPage.verifyVideoDownloadOptionsPresent();
        downloadsPage.clickVideoDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadProgressBar();  // Folders show "In progress", videos show progress bar while downloading
        downloadsPage.waitForVideoDownloadComplete();
        downloadsPage.navigateBack();

        // Video cancellation flow
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.clickFirstFolder();
        downloadsPage.clickVideoDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadProgressBar(); // Folders show "In progress", videos show progress bar
        downloadsPage.clickVideoCancelDownloadIcon();
        downloadsPage.clickYesCancelButton();
        downloadsPage.verifyDownloadCancelledMessage();
        downloadsPage.verifyDownloadCancelled();
    }
}
