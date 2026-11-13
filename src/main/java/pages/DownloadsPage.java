package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import locators.ArchiveLocators;
import locators.BottomNavigationLocators;
import locators.CommonLocators;
import locators.DownloadsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.FileSizeUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Page object for Downloads feature.
 * Uses shared locators from ArchiveLocators and CommonLocators where applicable.
 */
public class DownloadsPage {

    private static final Logger logger = LogManager.getLogger(DownloadsPage.class);

    /** Big enough to still be downloading when the cancel icon is tapped. */
    private static final double LARGE_DOWNLOAD_MB = 10;

    /** Size of a download that is meant to finish: long enough to show its progress, short enough to end quickly. */
    private static final double MEDIUM_DOWNLOAD_MIN_MB = 3;

    private static final double MEDIUM_DOWNLOAD_MAX_MB = 50;

    /** How many folders are opened to find the test data before the test is skipped. */
    private static final int FOLDERS_TO_SCAN = 6;

    private final AppiumDriver driver;

    /** A folder row and a video row inside it. */
    private record Target(int folderRow, int videoRow) {}

    /** The targets found by {@link #findDownloadTargets()}, and the one in use. */
    private Target mediumTarget = new Target(0, 0);

    private Target largeTarget = new Target(0, 0);

    private Target currentTarget = new Target(0, 0);

    public DownloadsPage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
    }

    public void navigateToDownloads() {
        clickMoreTab();
        if (!PlatformLocator.isIos()) { // iOS lists Downloads directly on the More screen
            clickFilesMenu();
        }
        clickDownloadsMenu();
        verifyDownloadsScreenDisplayed();
    }

    /** Waits until an exported file is listed in Downloads. The list writes its time as 02-26-15, not 02:26:15. */
    public void verifyExportedFileListed(String exportedFileName) {
        navigateToDownloads();
        String listedName = exportedFileName.replace(':', '-');
        WaitUtils.assertVisible(driver, DownloadsLocators.rowByFileName(listedName),
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT,
                "The exported file '" + listedName + "' should be listed in Downloads once the export is done");
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
                DownloadsLocators.folderRow(0),
                TimeoutConstants.SHORT_TIMEOUT
        );
        if (!folderExists) {
            throw new SkipException("Test data missing: No folders found in Downloads for this account");
        }
    }

    public void openCurrentFolder() {
        WaitUtils.click(driver, DownloadsLocators.folderRow(currentTarget.folderRow()));
    }

    public void navigateBack() {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, DownloadsLocators.FOLDER_BACK_BUTTON);
            return;
        }
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
                folderActionIcon(),
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(downloadPresent,
            "Download icon should be present in the folder section to download the folder");
    }

    public void verifyVideoDownloadOptionsPresent() {
        boolean downloadPresent = WaitUtils.isVisible(
                driver,
                videoActionIcon(),
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(downloadPresent,
            "Video download icon should be present to download the video");
    }

    public void clickDownloadIcon() {
        WaitUtils.click(driver, folderActionIcon());
    }

    public void clickVideoDownloadIcon() {
        WaitUtils.click(driver, videoActionIcon());
    }

    private By folderActionIcon() {
        return DownloadsLocators.folderDownloadIcon(currentTarget.folderRow());
    }

    private By videoActionIcon() {
        return DownloadsLocators.videoDownloadIcon(currentTarget.videoRow());
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

    /** iOS saves downloads to Photos and asks for access when the first one is saved, which can be at any moment. */
    private void dismissPhotoPromptIfShown() {
        if (PlatformLocator.isIos() && WaitUtils.isPresent(driver, ArchiveLocators.ALLOW_PHOTO_LIBRARY_ACCESS_BUTTON)) {
            WaitUtils.click(driver, ArchiveLocators.ALLOW_PHOTO_LIBRARY_ACCESS_BUTTON);
        }
    }
    public void verifyDownloadingSnackbar() {
        // Poll until snackbar text contains "downloading" (not exact match, handles case variations)
        boolean found = WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
            List<String> texts = WaitUtils.textsOf(driver, CommonLocators.SNACKBAR_TEXT);
            return !texts.isEmpty() && texts.get(0).toLowerCase().contains("downloading");
        });
        Assert.assertTrue(found, "Snackbar should show 'Downloading' message");
    }

    public void verifyDownloadProgressBar() {
        WaitUtils.assertVisible(driver, DownloadsLocators.DOWNLOAD_PROGRESS_BAR,
                TimeoutConstants.SHORT_TIMEOUT,
                "Progress bar should be visible during download");
    }

    /**
     * Verifies the download is in progress by checking the "In progress" status text.
     * This must be called before attempting to cancel.
     */
    public void verifyDownloadInProgress() {
        WaitUtils.assertVisible(driver, ArchiveLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT,
                "The row should say 'In progress' while the item downloads");
    }

    public void waitForDownloadComplete() {
        waitForSavedMessage(TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT);
    }

    public void waitForVideoDownloadComplete() {
        waitForSavedMessage(TimeoutConstants.VIDEO_DOWNLOAD_COMPLETE_TIMEOUT);
    }

    /** Android saves downloads to Files, iOS to Photos. */
    private void waitForSavedMessage(Duration timeout) {
        String savedMessage = PlatformLocator.isIos()
                ? UiTextConstants.ARCHIVE_SAVED_MESSAGE_IOS : UiTextConstants.ARCHIVE_SAVED_MESSAGE_ANDROID;
        AtomicReference<String> lastSeen = new AtomicReference<>("");
        boolean saved = WaitUtils.pollUntil(driver, timeout, () -> {
            dismissPhotoPromptIfShown();
            List<String> texts = WaitUtils.textsOf(driver, CommonLocators.SNACKBAR_TEXT);
            if (!texts.isEmpty()) {
                lastSeen.set(texts.get(0));
            }
            return !texts.isEmpty() && texts.get(0).contains(savedMessage);
        });
        Assert.assertTrue(saved,
            "Success snackbar should show '" + savedMessage + "' but the last message was: " + lastSeen.get());
    }

    public void clickCancelDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.folderCancelIcon(currentTarget.folderRow()));
    }

    public void clickVideoCancelDownloadIcon() {
        WaitUtils.click(driver, DownloadsLocators.videoCancelIcon(currentTarget.videoRow()));
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

        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, DownloadsLocators.ANY_CANCEL_ICON);
            if (WaitUtils.isVisible(driver, ArchiveLocators.YES_CANCEL_BUTTON,
                    TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
                clickYesCancelButton();
            }
            return;
        }

        // Try to cancel folder download
        if (WaitUtils.isVisible(driver, folderActionIcon(), TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
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
        if (WaitUtils.isVisible(driver, videoActionIcon(), TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
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

    /**
     * Finds a medium folder with a medium video, for the downloads that must finish, and a large video, for the cancel
     * step. Most items on the account are clips of 1 to 2 MB that finish in under two seconds. Sizes show only inside
     * a folder, so the first folders are opened, and only the rows the screen has rendered are counted, so the size of
     * a folder with many videos is a lower bound. Skips the test, with the reason, when either is missing. Selects the
     * medium target.
     */
    public void findDownloadTargets() {
        Target medium = null;
        Target large = null;
        for (int folder = 0; folder < FOLDERS_TO_SCAN && (medium == null || large == null); folder++) {
            if (!WaitUtils.isVisible(driver, DownloadsLocators.folderRow(folder), TimeoutConstants.SHORT_TIMEOUT)) {
                break;
            }
            WaitUtils.click(driver, DownloadsLocators.folderRow(folder));
            Assert.assertTrue(WaitUtils.waitForAny(driver,
                            List.of(DownloadsLocators.SIZE_LINES, DownloadsLocators.EMPTY_FOLDER_TEXT),
                            TimeoutConstants.LIST_LOAD_TIMEOUT).isPresent(),
                    "Folder row " + folder + " should show its videos or say that it is empty");
            List<String> sizeLines = WaitUtils.textsOfOrEmpty(driver, DownloadsLocators.SIZE_LINES,
                    TimeoutConstants.OPTIONAL_FIELD_TIMEOUT);
            double folderMb = 0;
            int mediumVideoRow = -1;
            for (int video = 0; video < sizeLines.size(); video++) {
                double megabytes = FileSizeUtils.toMegabytes(sizeLines.get(video));
                folderMb += megabytes;
                boolean isMedium = megabytes >= MEDIUM_DOWNLOAD_MIN_MB && megabytes <= MEDIUM_DOWNLOAD_MAX_MB;
                if (isMedium && mediumVideoRow < 0) {
                    mediumVideoRow = video;
                }
                if (megabytes >= LARGE_DOWNLOAD_MB && large == null) {
                    large = new Target(folder, video);
                }
            }
            if (medium == null && mediumVideoRow >= 0 && folderMb <= MEDIUM_DOWNLOAD_MAX_MB) {
                medium = new Target(folder, mediumVideoRow);
            }
            navigateBack();
        }
        if (medium == null || large == null) {
            throw new SkipException("Test data missing: the first " + FOLDERS_TO_SCAN + " Downloads folders need a"
                    + " folder of at most " + MEDIUM_DOWNLOAD_MAX_MB + " MB holding a video of "
                    + MEDIUM_DOWNLOAD_MIN_MB + " to " + MEDIUM_DOWNLOAD_MAX_MB + " MB, and a video of "
                    + LARGE_DOWNLOAD_MB + " MB or more; found medium=" + medium + ", large=" + large
                    + ". Items on the account expire after 14 days, so the data may need to be created again");
        }
        mediumTarget = medium;
        largeTarget = large;
        logger.info("Download targets: medium {}, large {}", mediumTarget, largeTarget);
        selectMediumDownloadTarget();
    }

    /** The next download steps use the medium target. */
    private void selectMediumDownloadTarget() {
        currentTarget = mediumTarget;
    }

    /** The next download steps use the large target. */
    public void selectLargeDownloadTarget() {
        currentTarget = largeTarget;
    }
}
