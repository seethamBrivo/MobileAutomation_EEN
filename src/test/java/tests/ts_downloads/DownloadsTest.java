package tests.ts_downloads;

import base.BaseTest;
import constants.TestDataConstants;
import constants.TimeoutConstants;
import locators.BottomNavigationLocators;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DownloadsPage;
import utils.WaitUtils;

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
            if (downloadsPage != null && WaitUtils.isVisible(driver, locators.DownloadsLocators.FOLDER_ACTION_ICON, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                downloadsPage.clickCancelDownloadIcon();
                if (WaitUtils.isVisible(driver, locators.ArchiveLocators.YES_CANCEL_BUTTON, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                    downloadsPage.clickYesCancelButton();
                }
            }

            // Navigate back to main screen with More tab visible
            // May need multiple backs if inside folder
            for (int i = 0; i < 3; i++) {
                if (WaitUtils.isVisible(driver, BottomNavigationLocators.MORE_TAB, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                    break; // Already at main screen
                }
                driver.navigate().back();
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
        downloadsPage.waitForDownloadComplete();

        // Folder cancellation flow
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.clickDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.verifyDownloadingSnackbar();
        downloadsPage.verifyDownloadInProgress();
        downloadsPage.clickCancelDownloadIcon();
        downloadsPage.clickYesCancelButton();
        downloadsPage.verifyDownloadCancelledMessage();
        downloadsPage.verifyDownloadCancelled();
    }

    @Test(description = "EEPD-TC-13207: POS-Downloads - downloading a video - Full video type in mobile app",
          groups = {"regression", "downloads"})
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
        downloadsPage.verifyDownloadProgressBar();
        downloadsPage.waitForVideoDownloadComplete();
        downloadsPage.navigateBack();

        // Video cancellation flow
        downloadsPage.verifyFilesTabIsVisible();
        downloadsPage.clickFirstFolder();
        downloadsPage.clickVideoDownloadIcon();
        downloadsPage.verifyDownloadDialogDisplayed();
        downloadsPage.clickDownloadButtonInDialog();
        downloadsPage.clickVideoCancelDownloadIcon();
        downloadsPage.clickYesCancelButton();
        downloadsPage.verifyDownloadCancelledMessage();
        downloadsPage.verifyDownloadCancelled();
    }
}
