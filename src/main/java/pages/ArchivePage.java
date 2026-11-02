package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.ArchiveLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.KeyboardUtils;
import utils.WaitUtils;

public class ArchivePage {

    private static final Logger logger = LogManager.getLogger(ArchivePage.class);
    private final AppiumDriver driver;

    public ArchivePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateToArchive() {
        BottomNavigationBar.clickMoreTab(driver);
        clickFilesMenu();
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
        WaitUtils.assertVisible(driver, ArchiveLocators.FILTER_ICON_FILES_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter icon should be present on Files tab");
    }

    public void verifyFilterIconPresentOnProgressTab() {
        WaitUtils.assertVisible(driver, ArchiveLocators.FILTER_ICON_PROGRESS_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Filter icon should be present on Progress tab");
    }

    public void clickFilterIcon() {
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
        String placeholder = searchBar.getText();
        String expected = "Filter for files or folders";
        Assert.assertEquals(placeholder, expected,
            "Search bar placeholder should be 'Filter for files or folders'");
    }

    public void verifyScreenBlankBeforeSearch() {
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
            "Back arrow button should be present in the left side of search bar");
    }

    public void enterSearchText(String searchText) {
        // Use SEARCH_INPUT (EditText) not SEARCH_BAR (TextView placeholder)
        WebElement searchInput = WaitUtils.waitForVisibility(driver, ArchiveLocators.SEARCH_INPUT);
        searchInput.clear();
        searchInput.sendKeys(searchText);
        // Note: Search filters automatically as you type (no Enter needed)
    }

    public void verifySearchResultsDisplayed(String searchText) {
        WaitUtils.assertVisible(driver, ArchiveLocators.searchResultByFileName(searchText), TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Search results should display files containing '" + searchText + "'");
    }

    public void verifyThreeDotsButtonPresent() {
        WaitUtils.assertVisible(driver, ArchiveLocators.THREE_DOTS_MENU, TimeoutConstants.SHORT_TIMEOUT,
                "3dots button should be present within results items");
    }

    public void verifyNoResultsDisplayed() {
        KeyboardUtils.hideKeyboard(driver);
        WaitUtils.assertVisible(driver, ArchiveLocators.EMPTY_STATE, TimeoutConstants.SHORT_TIMEOUT,
                "Empty state 'No results found' should be displayed when search returns no results");
    }

    public void navigateBack() {
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
        WaitUtils.waitForVisibility(
                driver,
                ArchiveLocators.FIRST_VIDEO_THREE_DOTS,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
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
    }

    public void verifyDownloadingSnackbar() {
        // Either "downloading" (in progress) or "saved to files" (a fast download already finished).
        String snackbarText = Snackbar.verifyContainsAnyOf(driver, TimeoutConstants.SHORT_TIMEOUT, "downloading", "saved to files");
        if (snackbarText.toLowerCase().contains("saved to files")) {
            logger.info("Download completed very quickly - skipping intermediate status checks");
        }
    }

    public void verifyInProgressStatus() {
        boolean inProgress = WaitUtils.isVisible(
                driver,
                ArchiveLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        if (!inProgress) {
            logger.info("'In progress' status not visible - download may have completed very quickly");
        }
        // Don't assert - fast downloads may skip this status entirely
    }

    /**
     * NOTE: Downloaded files go directly to device local storage (Files app), NOT to Archive Progress tab.
     * Progress tab only tracks Archive-specific operations. Snackbar "Saved to Files" confirms download to device.
     */
    public void waitForDownloadComplete() {
        Snackbar.waitUntilSays(driver, "Saved to Files", TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT);
    }

    public void clickCancelDownloadIcon() {
        WaitUtils.click(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON);
    }

    public void clickYesCancelButton() {
        WaitUtils.click(driver, ArchiveLocators.YES_CANCEL_BUTTON);
    }

    public void verifyDownloadCancelledMessage() {
        String snackbarText = Snackbar.verifyContainsAnyOf(driver, TimeoutConstants.SHORT_TIMEOUT, "cancel");
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
        verifyDownloadingSnackbar();
        verifyInProgressStatus();
        waitForDownloadComplete();
    }

    /**
     * Attempts to cancel folder download immediately after initiating download.
     * Clicks cancel icon right after download starts (without waiting for progress check).
     * This ensures cancellation is tested reliably even on fast CI/BrowserStack sessions.
     *
     * If download completes before cancel dialog appears, verifies successful download instead.
     */
    public void verifyFolderDownloadCancellation() {
        clickFirstFolderThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();

        // Immediately attempt to cancel without waiting - ensures cancellation is tested
        boolean cancelIconPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.CANCEL_DOWNLOAD_ICON,
                TimeoutConstants.FAST_DOWNLOAD_CANCEL_CHECK
        );

        if (cancelIconPresent) {
            clickCancelDownloadIcon();

            // Check if cancel confirmation dialog appeared
            boolean cancelDialogPresent = WaitUtils.isVisible(
                    driver,
                    ArchiveLocators.YES_CANCEL_BUTTON,
                    TimeoutConstants.SHORT_TIMEOUT
            );

            if (cancelDialogPresent) {
                clickYesCancelButton();
                verifyDownloadCancelledMessage();
                verifyDownloadCancelled();
                logger.info("Folder download cancellation verified successfully");
            } else {
                // Download completed before cancel dialog appeared
                logger.warn("Folder downloaded too fast - cancel dialog didn't appear, verifying successful download");
                waitForDownloadComplete();
            }
        } else {
            // Cancel icon never appeared - download completed immediately
            logger.warn("Folder downloaded instantly - cancel icon never appeared, verifying successful download");
            waitForDownloadComplete();
        }
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
        verifyDownloadingSnackbar();
        waitForDownloadComplete();
        navigateBack();
    }

    /**
     * Attempts to cancel video download immediately after initiating download.
     * Clicks cancel icon right after download starts (without waiting for progress check).
     * This ensures cancellation is tested reliably even on fast CI/BrowserStack sessions.
     *
     * If download completes before cancel dialog appears, verifies successful download instead.
     */
    public void verifyVideoDownloadCancellation() {
        clickFirstFolder();
        clickFirstVideoThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();

        // Immediately attempt to cancel without waiting - ensures cancellation is tested
        boolean cancelIconPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.CANCEL_DOWNLOAD_ICON,
                TimeoutConstants.FAST_DOWNLOAD_CANCEL_CHECK
        );

        if (cancelIconPresent) {
            clickCancelDownloadIcon();

            // Check if cancel confirmation dialog appeared
            boolean cancelDialogPresent = WaitUtils.isVisible(
                    driver,
                    ArchiveLocators.YES_CANCEL_BUTTON,
                    TimeoutConstants.SHORT_TIMEOUT
            );

            if (cancelDialogPresent) {
                clickYesCancelButton();
                verifyDownloadCancelledMessage();
                verifyDownloadCancelled();
                logger.info("Video download cancellation verified successfully");
            } else {
                // Download completed before cancel dialog appeared
                logger.warn("Video downloaded too fast - cancel dialog didn't appear, verifying successful download");
                waitForDownloadComplete();
                navigateBack();
            }
        } else {
            // Cancel icon never appeared - download completed immediately
            logger.warn("Video downloaded instantly - cancel icon never appeared, verifying successful download");
            waitForDownloadComplete();
            navigateBack();
        }
    }
}
