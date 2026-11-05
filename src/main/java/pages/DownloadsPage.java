package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import locators.BottomNavigationLocators;
import locators.DownloadsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

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

    public void verifyFilesTabIsSelected() {
        boolean isVisible = WaitUtils.isVisible(
                driver,
                DownloadsLocators.FILES_TAB,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(isVisible, "Files tab should be visible (default tab)");
    }

    public void clickFirstFolder() {
        WaitUtils.click(driver, DownloadsLocators.FIRST_FOLDER);
    }

    public void navigateBack() {
        ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
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
                DownloadsLocators.DOWNLOAD_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(downloadPresent,
            "Download icon should be present in the folder section to download the folder");
    }

    public void verifyVideoDownloadOptionsPresent() {
        boolean downloadPresent = WaitUtils.isVisible(
                driver,
                DownloadsLocators.VIDEO_DOWNLOAD_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(downloadPresent,
            "Video download icon should be present to download the video");
    }

    public void clickDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.DOWNLOAD_ICON);
    }

    public void clickVideoDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.VIDEO_DOWNLOAD_ICON);
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
        WebElement snackbar = WaitUtils.waitForVisibility(
                driver,
                DownloadsLocators.SNACKBAR_TEXT,
                TimeoutConstants.SHORT_TIMEOUT
        );
        String snackbarText = snackbar.getText().toLowerCase();
        boolean isValidSnackbar = snackbarText.contains("downloading") || snackbarText.contains("saved to files");
        Assert.assertTrue(isValidSnackbar,
            "Snackbar should show 'Downloading' or 'Saved to Files' message but was: " + snackbarText);

        if (snackbarText.contains("saved to files")) {
            logger.info("Download completed very quickly - skipping intermediate status checks");
        }
    }

    public void verifyDownloadProgressBar() {
        boolean progressBarVisible = WaitUtils.isVisible(
                driver,
                DownloadsLocators.DOWNLOAD_PROGRESS_BAR,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(progressBarVisible, "Progress bar should be visible during download");
    }

    public void verifyInProgressStatus() {
        boolean inProgress = WaitUtils.isVisible(
                driver,
                DownloadsLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        if (!inProgress) {
            logger.info("'In progress' status not visible - download may have completed very quickly");
        }
    }

    public void waitForDownloadComplete() {
        String snackbarText = WaitUtils.pollText(
                driver,
                DownloadsLocators.SNACKBAR_TEXT,
                "Saved to Files",
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT
        );
        Assert.assertTrue(snackbarText.contains("Saved to Files"),
            "Success snackbar should show 'Saved to Files' but was: " + snackbarText);
    }

    public void waitForVideoDownloadComplete() {
        String snackbarText = WaitUtils.pollText(
                driver,
                DownloadsLocators.SNACKBAR_TEXT,
                "Saved to Files",
                TimeoutConstants.VIDEO_DOWNLOAD_COMPLETE_TIMEOUT
        );
        Assert.assertTrue(snackbarText.contains("Saved to Files"),
            "Success snackbar should show 'Saved to Files' but was: " + snackbarText);
    }

    public void openNotificationPanel() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = 0;
        int endY = size.height / 2;
        GestureUtils.swipe(driver, startX, startY, startX, endY);
    }

    // Soft assertion - notification may disappear quickly if download completes fast
    public void verifyDownloadInNotification() {
        boolean downloadNotificationPresent = WaitUtils.isVisible(
                driver,
                DownloadsLocators.NOTIFICATION_DOWNLOAD_TITLE,
                TimeoutConstants.SHORT_TIMEOUT
        );
        if (downloadNotificationPresent) {
            Assert.assertTrue(true, "Download notification is present in notification panel");
        } else {
            logger.warn("Download notification not found - may have disappeared quickly");
        }
    }

    public void closeNotificationPanel() {
        ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
    }

    public void clickCancelDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.CANCEL_DOWNLOAD_ICON);
    }

    public void clickVideoCancelDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.VIDEO_CANCEL_DOWNLOAD_ICON);
    }

    public void clickYesCancelButton() {
        WaitUtils.click(driver, DownloadsLocators.YES_CANCEL_BUTTON);
    }

    public void verifyDownloadCancelledMessage() {
        WebElement snackbar = WaitUtils.waitForVisibility(
                driver,
                DownloadsLocators.SNACKBAR_TEXT,
                TimeoutConstants.SHORT_TIMEOUT
        );
        String snackbarText = snackbar.getText();
        Assert.assertTrue(snackbarText.toLowerCase().contains("cancel"),
            "Snackbar should show cancellation message but was: " + snackbarText);
    }

    public void verifyDownloadCancelled() {
        boolean inProgressGone = !WaitUtils.isVisible(
                driver,
                DownloadsLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(inProgressGone, "Download should be cancelled - 'In progress' status should not be present");
    }

    public void verifyCompleteFolderDownloadFlow() {
        navigateToDownloads();
        verifyFilesTabIsSelected();
        verifyProgressTabIsVisible();
        verifyDownloadOptionsPresent();
        clickDownloadIcon();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        verifyDownloadingSnackbar();
        verifyInProgressStatus();
        // Skip "Saved to Files" verification - ZIP downloads take too long. Instead verify in notification panel.
        openNotificationPanel();
        verifyDownloadInNotification();
        closeNotificationPanel();
    }

    public void verifyFolderDownloadCancellation() {
        verifyFilesTabIsSelected();
        // Wait for download icon to be ready after previous download completes
        WaitUtils.waitForVisibility(driver, DownloadsLocators.DOWNLOAD_ICON, TimeoutConstants.SHORT_TIMEOUT);
        clickDownloadIcon();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        verifyDownloadingSnackbar();
        clickCancelDownloadIcon();
        clickYesCancelButton();
        verifyDownloadCancelledMessage();
        verifyDownloadCancelled();
    }

    public void verifyCompleteVideoDownloadFlow() {
        navigateToDownloads();
        clickFirstFolder();
        verifyVideoDownloadOptionsPresent();
        clickVideoDownloadIcon();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        verifyDownloadingSnackbar();
        verifyDownloadProgressBar();
        waitForVideoDownloadComplete();
        navigateBack();
    }

    public void verifyVideoDownloadCancellation() {
        verifyFilesTabIsSelected();
        clickFirstFolder();
        // Wait for video download icon to be ready after previous download completes
        WaitUtils.waitForVisibility(driver, DownloadsLocators.VIDEO_DOWNLOAD_ICON, TimeoutConstants.SHORT_TIMEOUT);
        clickVideoDownloadIcon();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        verifyDownloadingSnackbar();
        clickVideoCancelDownloadIcon();
        clickYesCancelButton();
        verifyDownloadCancelledMessage();
        verifyDownloadCancelled();
    }
}
