package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.ArchiveLocators;
import locators.BottomNavigationLocators;
import locators.CommonLocators;
import locators.DownloadsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.WaitUtils;

/**
 * Page object for Downloads feature.
 * Uses shared locators from ArchiveLocators and CommonLocators where applicable.
 */
public class DownloadsPage {

    private static final Logger logger = LogManager.getLogger(DownloadsPage.class);
    private final AppiumDriver driver;

    public DownloadsPage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
    }

    public void navigateToDownloads() {
        clickMoreTab();
        clickFilesMenu();
        clickDownloadsMenu();
        verifyDownloadsScreenDisplayed();
    }

    // Shared pattern with ArchivePage - kept duplicated for page object encapsulation
    public void clickMoreTab() {
        WaitUtils.click(driver, BottomNavigationLocators.MORE_TAB);
    }

    public void clickFilesMenu() {
        WaitUtils.click(driver, DownloadsLocators.FILES_MENU_ITEM);
    }

    public void clickDownloadsMenu() {
        WaitUtils.click(driver, DownloadsLocators.DOWNLOADS_MENU_ITEM);
    }

    private void verifyDownloadsScreenDisplayed() {
        boolean isDisplayed = WaitUtils.isVisible(
                driver,
                DownloadsLocators.FILES_TAB,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(isDisplayed, "Downloads screen should be displayed (Files tab visible)");
    }

    public void verifyFilesTabIsVisible() {
        boolean isVisible = WaitUtils.isVisible(
                driver,
                DownloadsLocators.FILES_TAB,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(isVisible, "Files tab should be visible (default tab)");
    }

    /**
     * Checks if test data exists (folders and videos in Downloads).
     * @throws SkipException if no folders found
     */
    public void verifyTestDataExists() {
        boolean folderExists = WaitUtils.isVisible(
                driver,
                DownloadsLocators.FIRST_FOLDER,
                TimeoutConstants.SHORT_TIMEOUT
        );
        if (!folderExists) {
            throw new SkipException("Test data missing: No folders found in Downloads for this account");
        }
    }

    public void clickFirstFolder() {
        WaitUtils.click(driver, DownloadsLocators.FIRST_FOLDER);
    }

    public void navigateBack() {
        driver.navigate().back();
    }

    public void verifyProgressTabIsVisible() {
        boolean isVisible = WaitUtils.isVisible(
                driver,
                DownloadsLocators.PROGRESS_TAB,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(isVisible, "Progress tab should be visible");
    }

    public void verifyDownloadOptionsPresent() {
        boolean downloadPresent = WaitUtils.isVisible(
                driver,
                DownloadsLocators.FOLDER_ACTION_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(downloadPresent,
            "Download icon should be present in the folder section to download the folder");
    }

    public void verifyVideoDownloadOptionsPresent() {
        boolean downloadPresent = WaitUtils.isVisible(
                driver,
                DownloadsLocators.VIDEO_ACTION_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(downloadPresent,
            "Video download icon should be present to download the video");
    }

    public void clickDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.FOLDER_ACTION_ICON);
    }

    public void clickVideoDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.VIDEO_ACTION_ICON);
    }

    public void verifyDownloadDialogDisplayed() {
        WaitUtils.assertVisible(
                driver,
                DownloadsLocators.DOWNLOAD_BUTTON_DIALOG,
                "DOWNLOAD button should be visible in dialog"
        );
        WaitUtils.assertVisible(
                driver,
                DownloadsLocators.CANCEL_BUTTON_DIALOG,
                "CANCEL button should be visible in dialog"
        );
    }

    public void clickDownloadButtonInDialog() {
        WaitUtils.click(driver, DownloadsLocators.DOWNLOAD_BUTTON_DIALOG);
    }

    public void verifyDownloadingSnackbar() {
        // Poll until snackbar shows "downloading" or "saved" (fast download)
        // Handles case where stale "Saved to Files" from previous download is still visible
        boolean found = WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
            if (!WaitUtils.isPresent(driver, CommonLocators.SNACKBAR_TEXT)) {
                return false;
            }
            String text = driver.findElement(CommonLocators.SNACKBAR_TEXT).getText().toLowerCase();
            // Accept "downloading" (in progress) or "saved" (completed very fast)
            return text.contains("downloading") || text.contains("saved");
        });
        Assert.assertTrue(found,
                "Snackbar should show download-related message (Downloading or Saved to Files)");
    }

    public void verifyDownloadProgressBar() {
        boolean progressBarVisible = WaitUtils.isVisible(
                driver,
                DownloadsLocators.DOWNLOAD_PROGRESS_BAR,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(progressBarVisible, "Progress bar should be visible during download");
    }

    /**
     * Verifies the download is in progress by checking the "In progress" status text.
     * This must be called before attempting to cancel.
     */
    public void verifyDownloadInProgress() {
        WaitUtils.assertVisible(driver, ArchiveLocators.IN_PROGRESS_STATUS, TimeoutConstants.SHORT_TIMEOUT,
                "The row should say 'In progress' while the item downloads");
    }

    public void waitForDownloadComplete() {
        String snackbarText = WaitUtils.pollText(
                driver,
                CommonLocators.SNACKBAR_TEXT,
                "Saved to Files",
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT
        );
        Assert.assertTrue(snackbarText.contains("Saved to Files"),
            "Success snackbar should show 'Saved to Files' but was: " + snackbarText);
    }

    public void waitForVideoDownloadComplete() {
        String snackbarText = WaitUtils.pollText(
                driver,
                CommonLocators.SNACKBAR_TEXT,
                "Saved to Files",
                TimeoutConstants.VIDEO_DOWNLOAD_COMPLETE_TIMEOUT
        );
        Assert.assertTrue(snackbarText.contains("Saved to Files"),
            "Success snackbar should show 'Saved to Files' but was: " + snackbarText);
    }

    /**
     * Clicks the cancel icon for folder download.
     * Note: FOLDER_ACTION_ICON is the same locator as download icon - the icon changes state during download.
     */
    public void clickCancelDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.FOLDER_ACTION_ICON);
    }

    /**
     * Clicks the cancel icon for video download.
     * Note: VIDEO_ACTION_ICON is the same locator as download icon - the icon changes state during download.
     */
    public void clickVideoCancelDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.VIDEO_ACTION_ICON);
    }

    public void clickYesCancelButton() {
        WaitUtils.click(driver, ArchiveLocators.YES_CANCEL_BUTTON);
    }

    public void verifyDownloadCancelledMessage() {
        WebElement snackbar = WaitUtils.waitForVisibility(
                driver,
                CommonLocators.SNACKBAR_TEXT,
                TimeoutConstants.SHORT_TIMEOUT
        );
        String snackbarText = snackbar.getText();
        Assert.assertTrue(snackbarText.toLowerCase().contains("cancel"),
            "Snackbar should show cancellation message but was: " + snackbarText);
    }

    public void verifyDownloadCancelled() {
        boolean disappeared = WaitUtils.waitForInvisibility(
                driver,
                ArchiveLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(disappeared,
                "'In progress' status should disappear after cancelling the download");
    }

    /**
     * Cleanup method to cancel any active downloads.
     * Checks if a download is in progress before attempting to cancel.
     * Handles both folder and video downloads.
     * Waits for cancellation to complete to ensure clean state for next test.
     */
    public void cancelActiveDownloads() {
        // Check if download is in progress
        boolean inProgress = WaitUtils.isVisible(
                driver,
                ArchiveLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT
        );

        if (!inProgress) {
            return; // No active download
        }

        // Try to cancel folder download
        if (WaitUtils.isVisible(driver, DownloadsLocators.FOLDER_ACTION_ICON,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            clickCancelDownloadIcon();
            if (WaitUtils.isVisible(driver, ArchiveLocators.YES_CANCEL_BUTTON,
                    TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                clickYesCancelButton();
                // Wait for "In progress" to disappear (cancel succeeded)
                WaitUtils.waitForInvisibility(driver, ArchiveLocators.IN_PROGRESS_STATUS,
                        TimeoutConstants.SHORT_TIMEOUT);
            }
            return;
        }

        // Try to cancel video download
        if (WaitUtils.isVisible(driver, DownloadsLocators.VIDEO_ACTION_ICON,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            clickVideoCancelDownloadIcon();
            if (WaitUtils.isVisible(driver, ArchiveLocators.YES_CANCEL_BUTTON,
                    TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                clickYesCancelButton();
                // Wait for "In progress" to disappear (cancel succeeded)
                WaitUtils.waitForInvisibility(driver, ArchiveLocators.IN_PROGRESS_STATUS,
                        TimeoutConstants.SHORT_TIMEOUT);
            }
        }
    }
}
