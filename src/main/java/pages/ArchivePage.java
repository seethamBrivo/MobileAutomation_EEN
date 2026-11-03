package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
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
    private static final int MAX_NESTED_FOLDERS = 2;

    private final AppiumDriver driver;
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
        if (PlatformLocator.isIos()) { // the filter icon is drawn inside the search field and is not an element of its own
            WaitUtils.assertVisible(driver, ArchiveLocators.SEARCH_BAR, TimeoutConstants.LIST_LOAD_TIMEOUT,
                    "Filter field should be present on Files tab");
            return;
        }
        WaitUtils.assertVisible(driver, ArchiveLocators.FILTER_ICON_FILES_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter icon should be present on Files tab");
    }

    public void verifyFilterIconPresentOnProgressTab() {
        if (PlatformLocator.isIos()) { // same filter field on both tabs
            WaitUtils.assertVisible(driver, ArchiveLocators.SEARCH_BAR, TimeoutConstants.LIST_LOAD_TIMEOUT,
                    "Filter field should be present on Progress tab");
            return;
        }
        WaitUtils.assertVisible(driver, ArchiveLocators.FILTER_ICON_PROGRESS_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter icon should be present on Progress tab");
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
            // iOS keeps the full list while the field is empty and filters it as text is typed, so there is no blank state.
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
            // screen, so it counts when it is visible or present.
            boolean shown = WaitUtils.isVisible(driver, ArchiveLocators.EMPTY_STATE, TimeoutConstants.SHORT_TIMEOUT)
                    || WaitUtils.isPresent(driver, ArchiveLocators.EMPTY_STATE);
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
        if (PlatformLocator.isIos() && WaitUtils.isVisible(driver, ArchiveLocators.ALLOW_PHOTO_LIBRARY_ACCESS_BUTTON,
                TimeoutConstants.PERMISSION_PROMPT_TIMEOUT)) {
            WaitUtils.click(driver, ArchiveLocators.ALLOW_PHOTO_LIBRARY_ACCESS_BUTTON);
        }
    }

    /**
     * Either the "downloading" message (in progress) or the "saved" message (a fast download already finished).
     * Returns true when it was the saved message: that message may be gone by the time the caller waits for it, so
     * the caller passes the result to {@link #waitForDownloadComplete(boolean)}.
     */
    public boolean verifyDownloadingSnackbar() {
        String savedMessage = UiTextConstants.ARCHIVE_DOWNLOAD_SAVED_MESSAGE.toLowerCase();
        String snackbarText = Snackbar.verifyContainsAnyOf(
                driver, TimeoutConstants.SHORT_TIMEOUT, UiTextConstants.ARCHIVE_DOWNLOADING_MESSAGE, savedMessage);
        boolean alreadySaved = snackbarText.toLowerCase().contains(savedMessage);
        if (alreadySaved) {
            logger.info("Download completed very quickly - skipping intermediate status checks");
        }
        return alreadySaved;
    }

    /**
     * Logs whether the "In progress" status is showing; it asserts nothing, because fast downloads skip it. It must
     * not wait: the "saved" message shows for only a few seconds, and waiting here for a status that never comes
     * makes the next step miss it.
     */
    public void verifyInProgressStatus() {
        if (!WaitUtils.isPresent(driver, ArchiveLocators.IN_PROGRESS_STATUS)) {
            logger.info("'In progress' status not visible - download may have completed very quickly");
        }
    }

    /**
     * NOTE: Downloaded files go directly to device local storage (Files app), NOT to Archive Progress tab.
     * Progress tab only tracks Archive-specific operations. Snackbar "Saved to Files" confirms download to device.
     */
    public void waitForDownloadComplete(boolean savedMessageAlreadySeen) {
        if (savedMessageAlreadySeen) {
            return;
        }
        Snackbar.waitUntilSays(driver, UiTextConstants.ARCHIVE_DOWNLOAD_SAVED_MESSAGE,
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT);
    }

    public void clickCancelDownloadIcon() {
        WaitUtils.click(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON);
    }

    public void clickYesCancelButton() {
        WaitUtils.click(driver, ArchiveLocators.YES_CANCEL_BUTTON);
    }

    public void verifyDownloadCancelledMessage() {
        String snackbarText = Snackbar.verifyContainsAnyOf(
                driver, TimeoutConstants.SHORT_TIMEOUT, UiTextConstants.ARCHIVE_CANCELLED_MESSAGE);
        logger.info("Download cancelled successfully: " + snackbarText);
    }

    public void verifyDownloadCancelled() {
        WaitUtils.assertNotVisible(driver, ArchiveLocators.IN_PROGRESS_STATUS, TimeoutConstants.SHORT_TIMEOUT,
                "Download should be cancelled - 'In progress' status should not be present");
    }

    public void verifyCompleteFolderDownloadFlow() {
        navigateToArchive();
        verifyFilesTabNotEmpty();
        clickFirstFolderThreeDots();
        verifyDownloadOptionsPresent();
        clickDownloadOption();
        verifyDownloadDialogDisplayed();
        clickDownloadButtonInDialog();
        boolean savedMessageSeen = verifyDownloadingSnackbar();
        verifyInProgressStatus();
        waitForDownloadComplete(savedMessageSeen);
    }

    /**
     * Attempts to cancel the folder download immediately after starting it; see
     * {@link #verifyCancellationOrCompletion(String, Runnable)}.
     */
    public void verifyFolderDownloadCancellation() {
        clickFirstFolderThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();
        verifyCancellationOrCompletion("Folder", () -> { });
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
        boolean savedMessageSeen = verifyDownloadingSnackbar();
        waitForDownloadComplete(savedMessageSeen);
        navigateBackFromFolder();
    }

    /**
     * Attempts to cancel the video download immediately after starting it; see
     * {@link #verifyCancellationOrCompletion(String, Runnable)}.
     */
    public void verifyVideoDownloadCancellation() {
        clickFirstFolder();
        clickFirstVideoThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();
        verifyCancellationOrCompletion("Video", this::navigateBackFromFolder);
    }

    /**
     * Clicks the cancel icon right after the download starts (without waiting for the progress check), so the
     * cancellation is tested reliably even on fast CI/BrowserStack sessions. If the download finishes before the
     * cancel icon or the confirmation dialog appears, the successful download is verified instead and
     * {@code afterCompletedDownload} runs (for a video: back out of its folder).
     *
     * <p>The flow is the same on both platforms: the cancel icon on the row, a confirmation dialog, then a
     * "cancelled" message. A small download can finish before the dialog is answered, which is the fall-back.
     */
    private void verifyCancellationOrCompletion(String what, Runnable afterCompletedDownload) {
        boolean cancelIconPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.CANCEL_DOWNLOAD_ICON,
                TimeoutConstants.FAST_DOWNLOAD_CANCEL_CHECK
        );
        if (!cancelIconPresent) {
            logger.warn("{} downloaded instantly - cancel icon never appeared, verifying successful download", what);
            verifyDownloadCompletedWith(afterCompletedDownload);
            return;
        }
        clickCancelDownloadIcon();
        boolean cancelDialogPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.YES_CANCEL_BUTTON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        if (!cancelDialogPresent) {
            logger.warn("{} downloaded too fast - cancel dialog didn't appear, verifying successful download", what);
            verifyDownloadCompletedAfterMissedCancel(afterCompletedDownload);
            return;
        }
        clickYesCancelButton();
        verifyDownloadCancelledMessage();
        verifyDownloadCancelled();
        logger.info("{} download cancellation verified successfully", what);
    }

    /**
     * The cancel attempt took the few seconds the "saved" message is shown, so on iOS that message is already gone;
     * the finished download is shown by the row instead: its cancel icon and "In progress" status are gone. Android
     * keeps waiting for the message.
     */
    private void verifyDownloadCompletedAfterMissedCancel(Runnable afterCompletedDownload) {
        if (!PlatformLocator.isIos()) {
            verifyDownloadCompletedWith(afterCompletedDownload);
            return;
        }
        WaitUtils.assertNotVisible(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON,
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT,
                "The download should finish: the cancel icon on its row should disappear");
        WaitUtils.assertNotVisible(driver, ArchiveLocators.IN_PROGRESS_STATUS, TimeoutConstants.SHORT_TIMEOUT,
                "The download should finish: the row should no longer say 'In progress'");
        afterCompletedDownload.run();
    }

    private void verifyDownloadCompletedWith(Runnable afterCompletedDownload) {
        waitForDownloadComplete(false);
        afterCompletedDownload.run();
    }
}
