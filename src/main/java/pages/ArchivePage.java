package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import locators.ArchiveLocators;
import locators.CommonLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.KeyboardUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

public class ArchivePage {

    private static final Logger logger = LogManager.getLogger(ArchivePage.class);
    /** iOS: the first folder holds a folder per archived clip, and the videos are in that: folder, clip folder, video. */
    private static final int MAX_NESTED_FOLDERS = 2;
    /** How often the cancel icon is tapped while the confirmation does not show (the row redraws as it downloads). */
    private static final int CANCEL_TAP_ATTEMPTS = 3;

    private final AppiumDriver driver;
    /** iOS: the photo-library prompt comes once, with the first download; after that it is not looked for. */
    private boolean photoLibraryPromptHandled;
    /** How many folders have been opened below the Archive root, so the same number of back taps return to it. */
    private int openedFolderDepth;

    public ArchivePage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
    }

    public void navigateToArchive() {
        BottomNavigationBar.clickMoreTab(driver);
        if (!PlatformLocator.isIos()) { // iOS lists Archive directly; "Files" there is only a section heading
            clickFilesMenu();
        }
        clickArchiveMenu();
        verifyArchiveScreenDisplayed();
    }

    /** Searches the Archive for an exported file. The list writes its time as 02-26-15, not 02:26:15. */
    public void verifyExportedFileListed(String exportedFileName) {
        navigateToArchive();
        findInSearch(exportedFileName.replace(':', '-'));
    }

    public void clickFilesMenu() {
        WaitUtils.click(driver, ArchiveLocators.FILES_MENU_ITEM);
    }

    public void clickArchiveMenu() {
        WaitUtils.click(driver, ArchiveLocators.ARCHIVE_MENU_ITEM);
    }

    private void verifyArchiveScreenDisplayed() {
        WaitUtils.assertVisible(driver, ArchiveLocators.FILES_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Archive screen should be displayed (Files tab visible)");
    }

    public void verifyFilesTabIsSelected() {
        WaitUtils.assertVisible(driver, ArchiveLocators.FILES_TAB, TimeoutConstants.SHORT_TIMEOUT,
                "Files tab should be visible (default tab)");
    }

    /**
     * Verifies that the Files tab contains at least one folder/file.
     * Per QMetry step 2 for TC-19818 and TC-19385, this prevents false positives
     * when test account has no archive content.
     */
    public void verifyFilesTabNotEmpty() {
        WaitUtils.assertVisible(driver, ArchiveLocators.FIRST_FOLDER, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Files tab should contain at least one folder/file - test account may have no archive content");
    }

    public void verifyFilterIconPresentOnFilesTab() {
        if (PlatformLocator.isIos()) {
            verifyFilterFieldPresentOnIos("Files");
            return;
        }
        WaitUtils.assertVisible(driver, ArchiveLocators.FILTER_ICON_FILES_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter icon should be present on Files tab");
    }

    public void verifyFilterIconPresentOnProgressTab() {
        if (PlatformLocator.isIos()) {
            verifyFilterFieldPresentOnIos("Progress");
            return;
        }
        WaitUtils.assertVisible(driver, ArchiveLocators.FILTER_ICON_PROGRESS_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter icon should be present on Progress tab");
    }

    /** iOS: the filter icon is drawn inside the search field and is not an element of its own. */
    private void verifyFilterFieldPresentOnIos(String tabName) {
        WaitUtils.assertVisible(driver, ArchiveLocators.SEARCH_BAR, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter field should be present on " + tabName + " tab");
    }

    public void clickFilterIcon() {
        if (PlatformLocator.isIos()) { // tapping the filter field is what opens the search
            WaitUtils.click(driver, ArchiveLocators.SEARCH_BAR);
            return;
        }
        boolean filesTabFilterVisible = WaitUtils.isVisible(
                driver,
                ArchiveLocators.FILTER_ICON_FILES_TAB,
                TimeoutConstants.SHORT_TIMEOUT
        );

        if (filesTabFilterVisible) {
            WaitUtils.click(driver, ArchiveLocators.FILTER_ICON_FILES_TAB);
        } else {
            WaitUtils.click(driver, ArchiveLocators.FILTER_ICON_PROGRESS_TAB);
        }
    }

    public void navigateToProgressTab() {
        WaitUtils.click(driver, ArchiveLocators.PROGRESS_TAB);
    }

    public void navigateToFilesTab() {
        WaitUtils.click(driver, ArchiveLocators.FILES_TAB);
    }

    public void verifySearchBarPlaceholder() {
        WebElement searchBar = WaitUtils.waitForVisibility(driver, ArchiveLocators.SEARCH_BAR);
        // This is a TextView displaying the placeholder text, not an EditText with hint
        String placeholder = PlatformLocator.isIos() ? searchBar.getAttribute("label") : searchBar.getText();
        String expected = "Filter for files or folders";
        Assert.assertEquals(placeholder, expected,
            "Search bar placeholder should be 'Filter for files or folders'");
    }

    public void verifyScreenBlankBeforeSearch() {
        if (PlatformLocator.isIos()) {
            // iOS keeps the full list while the field is empty and filters it as text is typed, so there is no
            // blank state; what is checked instead is that the whole list is showing.
            WaitUtils.assertVisible(driver, ArchiveLocators.FIRST_FOLDER, TimeoutConstants.SHORT_TIMEOUT,
                    "The full list should show while the search field is empty");
            return;
        }
        WaitUtils.assertNotVisible(driver, ArchiveLocators.FILE_ITEM, TimeoutConstants.SHORT_TIMEOUT,
                "Screen should be blank before typing any search terms");
    }

    public void verifyBackArrowButtonPresent() {
        // Back arrow position changes based on search results, check either locator
        boolean withResults = WaitUtils.isVisible(
                driver,
                ArchiveLocators.BACK_ARROW_BUTTON_WITH_RESULTS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        boolean emptyState = !withResults && WaitUtils.isVisible(
                driver,
                ArchiveLocators.BACK_ARROW_BUTTON_EMPTY_STATE,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(withResults || emptyState,
            PlatformLocator.isIos() ? "Close button should be present next to the search field"
                    : "Back arrow button should be present in the left side of search bar");
    }

    public void enterSearchText(String searchText) {
        // Use SEARCH_INPUT (EditText) not SEARCH_BAR (TextView placeholder)
        WebElement searchInput = WaitUtils.waitForVisibility(driver, ArchiveLocators.SEARCH_INPUT);
        if (PlatformLocator.isIos()) {
            // iOS: the "Clear text" button exists only while the field holds text; it is used instead of clear().
            if (WaitUtils.isPresent(driver, CommonLocators.CLEAR_TEXT_BUTTON)) {
                WaitUtils.click(driver, CommonLocators.CLEAR_TEXT_BUTTON);
            }
            searchInput.click();
        } else {
            searchInput.clear();
        }
        searchInput.sendKeys(searchText);
        // Note: Search filters automatically as you type (no Enter needed)
    }

    public void verifySearchResultsDisplayed(String searchText) {
        WaitUtils.assertVisible(driver, ArchiveLocators.searchResultByFileName(searchText),
                TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Search results should display files containing '" + searchText + "'");
    }

    public void verifyThreeDotsButtonPresent() {
        WaitUtils.assertVisible(driver, ArchiveLocators.THREE_DOTS_MENU, TimeoutConstants.SHORT_TIMEOUT,
                "3dots button should be present within results items");
    }

    public void verifyNoResultsDisplayed() {
        if (PlatformLocator.isIos()) {
            // iOS: the message sits just above the keyboard and can be reported as not visible although it is on
            // screen, so presence is what counts; the wait is for the list to finish filtering.
            boolean shown = WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT,
                    () -> WaitUtils.isPresent(driver, ArchiveLocators.EMPTY_STATE));
            Assert.assertTrue(shown,
                    "Empty state 'No results found' should be displayed when search returns no results");
            return;
        }
        KeyboardUtils.hideKeyboard(driver);
        WaitUtils.assertVisible(driver, ArchiveLocators.EMPTY_STATE, TimeoutConstants.SHORT_TIMEOUT,
                "Empty state 'No results found' should be displayed when search returns no results");
    }

    public void navigateBack() {
        if (PlatformLocator.isIos()) { // closes the search; there is a single Close button
            WaitUtils.click(driver, ArchiveLocators.BACK_ARROW_BUTTON_WITH_RESULTS);
            return;
        }
        boolean backWithResultsVisible = WaitUtils.isVisible(
                driver,
                ArchiveLocators.BACK_ARROW_BUTTON_WITH_RESULTS,
                TimeoutConstants.SHORT_TIMEOUT
        );

        if (backWithResultsVisible) {
            WaitUtils.click(driver, ArchiveLocators.BACK_ARROW_BUTTON_WITH_RESULTS);
        } else {
            WaitUtils.click(driver, ArchiveLocators.BACK_ARROW_BUTTON_EMPTY_STATE);
        }
    }

    public void verifySearchBarClosed() {
        if (PlatformLocator.isIos()) { // the field stays on screen; closing the search removes the Close button
            WaitUtils.assertNotVisible(driver, ArchiveLocators.BACK_ARROW_BUTTON_WITH_RESULTS,
                    TimeoutConstants.SHORT_TIMEOUT, "Search should be closed after tapping Close");
            return;
        }
        WaitUtils.assertNotVisible(driver, ArchiveLocators.SEARCH_BAR, TimeoutConstants.SHORT_TIMEOUT,
                "Search bar should be closed after navigation back");
    }

    public void verifyFilesTabWithFilterIcon() {
        verifyFilesTabIsSelected();
        verifyFilterIconPresentOnFilesTab();
    }

    public void openSearchAndVerifyUI() {
        verifySearchBarPlaceholder();
        verifyScreenBlankBeforeSearch();
        verifyBackArrowButtonPresent();
    }

    public void searchAndVerifyResults(String searchText) {
        enterSearchText(searchText);
        verifySearchResultsDisplayed(searchText);
        verifyThreeDotsButtonPresent();
    }

    public void verifyCompleteArchiveSearchFlow(String validSearchTerm, String noResultsSearchTerm) {
        navigateToArchive();
        verifyFilesTabWithFilterIcon();
        navigateToProgressTab();
        verifyFilterIconPresentOnProgressTab();
        navigateToFilesTab();
        clickFilterIcon();
        openSearchAndVerifyUI();
        searchAndVerifyResults(validSearchTerm);
        enterSearchText(noResultsSearchTerm);
        verifyNoResultsDisplayed();
        navigateBack();
        verifySearchBarClosed();
    }

    public void clickFirstFolder() {
        WaitUtils.click(driver, ArchiveLocators.FIRST_FOLDER);
        openedFolderDepth = 1;
        if (PlatformLocator.isIos()) {
            openNestedFoldersUntilAVideoShows();
        }
        WaitUtils.waitForVisibility(
                driver,
                ArchiveLocators.FIRST_VIDEO_THREE_DOTS,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
    }

    /** iOS: the first folder can hold folders (one per archived clip) with the videos one level further down. */
    private void openNestedFoldersUntilAVideoShows() {
        for (int level = 0; level < MAX_NESTED_FOLDERS && !isVideoShowing() && isFolderShowing(); level++) {
            WaitUtils.click(driver, ArchiveLocators.FIRST_FOLDER);
            openedFolderDepth++;
        }
    }

    private boolean isVideoShowing() {
        return WaitUtils.isVisible(driver, ArchiveLocators.FIRST_VIDEO_THREE_DOTS, TimeoutConstants.SHORT_TIMEOUT);
    }

    private boolean isFolderShowing() {
        return WaitUtils.isPresent(driver, ArchiveLocators.FIRST_FOLDER);
    }

    /** Back out of the folders opened by {@link #clickFirstFolder()} to the Archive list. */
    public void navigateBackFromFolder() {
        if (!PlatformLocator.isIos()) {
            navigateBack();
            return;
        }
        for (; openedFolderDepth > 0; openedFolderDepth--) {
            WaitUtils.click(driver, ArchiveLocators.FOLDER_BACK_BUTTON);
        }
    }

    public void clickFirstFolderThreeDots() {
        WaitUtils.click(driver, ArchiveLocators.FIRST_FOLDER_THREE_DOTS);
    }

    public void clickFirstVideoThreeDots() {
        WaitUtils.click(driver, ArchiveLocators.FIRST_VIDEO_THREE_DOTS);
    }

    public void verifyDownloadOptionsPresent() {
        boolean detailsPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.DETAILS_OPTION,
                TimeoutConstants.SHORT_TIMEOUT
        );
        boolean downloadPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.DOWNLOAD_OPTION,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(detailsPresent && downloadPresent,
            "Details and Download options should be present in 3-dots menu");
    }

    public void clickDownloadOption() {
        WaitUtils.click(driver, ArchiveLocators.DOWNLOAD_OPTION);
    }

    public void verifyDownloadDialogDisplayed() {
        // Verify dialog by checking for DOWNLOAD button (more specific than generic ViewGroup)
        WaitUtils.assertVisible(driver, ArchiveLocators.DOWNLOAD_BUTTON_DIALOG, TimeoutConstants.SHORT_TIMEOUT,
                "Download confirmation dialog should display DOWNLOAD button");
    }

    public void clickDownloadButtonInDialog() {
        WaitUtils.click(driver, ArchiveLocators.DOWNLOAD_BUTTON_DIALOG);
        allowPhotoLibraryAccessWhenAsked();
    }

    /** iOS saves downloads to Photos, so the first download asks for access to the photo library. */
    private void allowPhotoLibraryAccessWhenAsked() {
        if (!PlatformLocator.isIos() || photoLibraryPromptHandled) {
            return; // asking again would only use up the seconds a small download needs to be cancelled
        }
        photoLibraryPromptHandled = true;
        WaitUtils.tryClickOptionalElement(driver, List.of(ArchiveLocators.ALLOW_PHOTO_LIBRARY_ACCESS_BUTTON),
                TimeoutConstants.PERMISSION_PROMPT_TIMEOUT);
    }

    /** The "Downloading..." message shows when the download starts. */
    public void verifyDownloadingSnackbar() {
        String snackbarText = Snackbar.verifyContainsAnyOf(
                driver, TimeoutConstants.SHORT_TIMEOUT, UiTextConstants.ARCHIVE_DOWNLOADING_MESSAGE);
        logger.info("Download started: {}", snackbarText);
    }

    /** While the item downloads, its row says "In progress" and offers the cancel icon. */
    public void verifyDownloadInProgress() {
        WaitUtils.assertVisible(driver, ArchiveLocators.IN_PROGRESS_STATUS, TimeoutConstants.SHORT_TIMEOUT,
                "The row should say 'In progress' while the item downloads");
        WaitUtils.assertVisible(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON, TimeoutConstants.SHORT_TIMEOUT,
                "The row should offer the cancel icon while the item downloads");
    }

    /**
     * NOTE: Downloaded files go directly to device local storage (Files app), NOT to Archive Progress tab.
     * Progress tab only tracks Archive-specific operations. Snackbar "Saved to Files" confirms download to device.
     */
    public void waitForDownloadComplete() {
        Snackbar.waitUntilSays(driver, savedMessage(), TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT);
    }

    /** Android saves downloads to Files, iOS to Photos, and each says so when the download is done. */
    private static String savedMessage() {
        return PlatformLocator.isIos() ? UiTextConstants.ARCHIVE_SAVED_MESSAGE_IOS
                : UiTextConstants.ARCHIVE_SAVED_MESSAGE_ANDROID;
    }

    public void verifyCancelConfirmationShown() {
        WaitUtils.assertVisible(driver, ArchiveLocators.YES_CANCEL_BUTTON, TimeoutConstants.SHORT_TIMEOUT,
                "Cancelling a download should ask for confirmation");
    }

    public void clickYesCancelButton() {
        WaitUtils.click(driver, ArchiveLocators.YES_CANCEL_BUTTON);
    }

    public void verifyDownloadCancelledMessage() {
        String snackbarText = Snackbar.verifyContainsAnyOf(
                driver, TimeoutConstants.SHORT_TIMEOUT, UiTextConstants.ARCHIVE_CANCELLED_MESSAGE);
        logger.info("Download cancelled successfully: {}", snackbarText);
    }

    public void verifyDownloadCancelled() {
        WaitUtils.assertNotVisible(driver, ArchiveLocators.IN_PROGRESS_STATUS, TimeoutConstants.SHORT_TIMEOUT,
                "Download should be cancelled - 'In progress' status should not be present");
    }

    /**
     * iOS only: after a cancel the row has no cancel icon. On Android the cancel icon has the position of the row's
     * 3-dots button, which is always on screen, so this check cannot be made there.
     */
    private void verifyCancelIconIsGone() {
        WaitUtils.assertNotVisible(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON, TimeoutConstants.SHORT_TIMEOUT,
                "Download should be cancelled - the cancel icon should not be present");
    }

    /** Download of the first folder: options, confirmation, then the saved message. */
    public void verifyCompleteFolderDownloadFlow() {
        navigateToArchive();
        verifyFilesTabNotEmpty();
        clickFirstFolderThreeDots();
        verifyDownloadOptionsPresent();
        clickDownloadOption();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        waitForDownloadComplete();
    }

    /**
     * iOS: cancels the download of a large folder while it runs. Android keeps its earlier check
     * ({@link #cancelFirstFolderDownloadIfStillRunning()}). The folder is found by search, because the first folder
     * changes whenever archives are added, and it is a large one so the download is still running when it is
     * cancelled. Every step of the cancellation is checked.
     */
    public void verifyFolderDownloadCancellation(String folderSearchTerm) {
        if (!PlatformLocator.isIos()) {
            cancelFirstFolderDownloadIfStillRunning();
            return;
        }
        findInSearch(folderSearchTerm);
        clickThreeDotsOfFirstSearchResult();
        clickDownloadOption();
        clickDownloadButtonInDialog();
        // A large folder keeps downloading for several seconds, so the start message and the row can be checked first.
        verifyDownloadingSnackbar();
        verifyDownloadInProgress();
        cancelTheRunningDownload();
        closeSearch();
    }

    public void verifyCompleteVideoDownloadFlow() {
        navigateToArchive();
        verifyFilesTabNotEmpty();
        clickFirstFolder();
        clickFirstVideoThreeDots();
        verifyDownloadOptionsPresent();
        clickDownloadOption();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        waitForDownloadComplete();
        navigateBackFromFolder();
    }

    /**
     * iOS: cancels the download of a large video while it runs. Android keeps its earlier check
     * ({@link #cancelFirstVideoDownloadIfStillRunning()}). A video of a few MB is downloaded in one to two seconds, so
     * nothing may come between confirming the download and tapping the cancel icon: no other check, no extra wait.
     */
    public void verifyVideoDownloadCancellation(String fileSearchTerm) {
        if (!PlatformLocator.isIos()) {
            cancelFirstVideoDownloadIfStillRunning();
            return;
        }
        findInSearch(fileSearchTerm);
        clickThreeDotsOfFirstSearchResult();
        clickDownloadOption();
        clickDownloadButtonInDialog();
        cancelTheRunningDownload();
        closeSearch();
    }

    private void findInSearch(String searchTerm) {
        clickFilterIcon();
        enterSearchText(searchTerm);
        verifySearchResultsDisplayed(searchTerm);
    }

    private void clickThreeDotsOfFirstSearchResult() {
        WaitUtils.click(driver, ArchiveLocators.THREE_DOTS_MENU);
    }

    private void closeSearch() {
        navigateBack();
        verifySearchBarClosed();
    }

    // ---- Android: the cancel check as it was before the iOS work ---------------------------------------------
    // Until the Android row is mapped (the cancel icon, and a download that lasts long enough to cancel), Android
    // cancels the first item if the download is still running and otherwise only verifies the finished download.
    // That check can pass without cancelling anything; the strict version above is iOS only for now.

    private void cancelFirstFolderDownloadIfStillRunning() {
        clickFirstFolderThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();
        cancelIfStillRunningOtherwiseVerifyCompleted("Folder", () -> { });
    }

    private void cancelFirstVideoDownloadIfStillRunning() {
        clickFirstFolder();
        clickFirstVideoThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();
        cancelIfStillRunningOtherwiseVerifyCompleted("Video", this::navigateBackFromFolder);
    }

    private void cancelIfStillRunningOtherwiseVerifyCompleted(String what, Runnable afterCompletedDownload) {
        boolean cancelIconPresent = WaitUtils.isVisible(
                driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON, TimeoutConstants.FAST_DOWNLOAD_CANCEL_CHECK);
        if (cancelIconPresent) {
            WaitUtils.click(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON);
            if (WaitUtils.isVisible(driver, ArchiveLocators.YES_CANCEL_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
                clickYesCancelButton();
                verifyDownloadCancelledMessage();
                verifyDownloadCancelled();
                logger.info("{} download cancellation verified successfully", what);
                return;
            }
            logger.warn("{} downloaded too fast - cancel dialog didn't appear, verifying successful download", what);
        } else {
            logger.warn("{} downloaded instantly - cancel icon never appeared, verifying successful download", what);
        }
        waitForDownloadComplete();
        afterCompletedDownload.run();
    }

    /**
     * Taps the cancel icon and goes through the cancellation. The icon is on the row only while the item downloads, so
     * finding it is the check that the download is running; if the download is already over, the test fails here.
     *
     * <p>While it downloads, the row redraws as the progress changes, so the icon can go stale between finding it and
     * tapping it, or the tap can land on the old row. The tap is repeated (at most {@value #CANCEL_TAP_ATTEMPTS}
     * times) until the confirmation shows; if it never does, the check below fails.
     */
    private void cancelTheRunningDownload() {
        for (int attempt = 1; attempt <= CANCEL_TAP_ATTEMPTS; attempt++) {
            boolean tapped = WaitUtils.retryOnStale(() -> WaitUtils.tryClickOptionalElement(driver,
                    List.of(ArchiveLocators.CANCEL_DOWNLOAD_ICON), TimeoutConstants.SHORT_TIMEOUT));
            if (attempt == 1) {
                Assert.assertTrue(tapped,
                        "The cancel icon should show while the item downloads, so that it can be cancelled");
            }
            if (!tapped || WaitUtils.isVisible(driver, ArchiveLocators.YES_CANCEL_BUTTON,
                    TimeoutConstants.CANCEL_CONFIRMATION_CHECK)) {
                break;
            }
        }
        verifyCancelConfirmationShown();
        clickYesCancelButton();
        verifyDownloadCancelledMessage();
        verifyDownloadCancelled();
        verifyCancelIconIsGone();
    }
}
