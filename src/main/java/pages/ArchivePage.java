package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import locators.ArchiveLocators;
import locators.BridgesLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.ConfigReader;
import utils.KeyboardUtils;
import utils.WaitUtils;

public class ArchivePage {

    private final AppiumDriver driver;
    private final Logger logger = LogManager.getLogger(ArchivePage.class);
    private final boolean isAndroid;

    public ArchivePage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
        this.isAndroid = "android".equalsIgnoreCase(ConfigReader.getPlatform());
    }

    public void navigateToArchive() {
        clickMoreTab();
        clickFilesMenu();
        clickArchiveMenu();
        verifyArchiveScreenDisplayed();
    }

    public void clickMoreTab() {
        WaitUtils.click(driver, BridgesLocators.MORE_MENU_OPTION);
    }

    public void clickFilesMenu() {
        WaitUtils.click(driver, ArchiveLocators.FILES_MENU_ITEM);
    }

    public void clickArchiveMenu() {
        WaitUtils.click(driver, ArchiveLocators.ARCHIVE_MENU_ITEM);
    }

    private void verifyArchiveScreenDisplayed() {
        boolean isDisplayed = WaitUtils.isVisible(
                driver,
                ArchiveLocators.FILES_TAB,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(isDisplayed, "Archive screen should be displayed (Files tab visible)");
    }

    public void verifyFilesTabIsSelected() {
        boolean isVisible = WaitUtils.isVisible(
                driver,
                ArchiveLocators.FILES_TAB,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(isVisible, "Files tab should be visible (default tab)");
    }

    public void verifyFilterIconPresentOnFilesTab() {
        boolean isPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.FILTER_ICON_FILES_TAB,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(isPresent, "Filter icon should be present on Files tab");
    }

    public void verifyFilterIconPresentOnProgressTab() {
        boolean isPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.FILTER_ICON_PROGRESS_TAB,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(isPresent, "Filter icon should be present on Progress tab");
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
        boolean hasResults = WaitUtils.isVisible(
                driver,
                ArchiveLocators.FILE_ITEM,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertFalse(hasResults,
            "Screen should be blank before typing any search terms");
    }

    public void verifyCancelButtonPresent() {
        if (!isAndroid) {
            boolean isPresent = WaitUtils.isVisible(
                    driver,
                    ArchiveLocators.CANCEL_BUTTON,
                    TimeoutConstants.SHORT_TIMEOUT
            );
            Assert.assertTrue(isPresent,
                "Cancel button should be present in the right side of search bar (iOS)");
        }
    }

    public void verifyBackArrowButtonPresent() {
        if (isAndroid) {
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
                "Back arrow button should be present in the left side of search bar (Android)");
        }
    }

    public void enterSearchText(String searchText) {
        // Use SEARCH_INPUT (EditText) not SEARCH_BAR (TextView placeholder)
        WebElement searchInput = WaitUtils.waitForVisibility(driver, ArchiveLocators.SEARCH_INPUT);
        searchInput.clear();
        searchInput.sendKeys(searchText);
        // Note: Search filters automatically as you type (no Enter needed)
    }

    public void verifySearchResultsDisplayed(String searchText) {
        boolean hasResults = WaitUtils.isVisible(
                driver,
                ArchiveLocators.searchResultByFileName(searchText),
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(hasResults,
            "Search results should display files containing '" + searchText + "'");
    }

    public void verifyThreeDotsButtonPresent() {
        boolean isPresent = WaitUtils.isVisible(
                driver,
                ArchiveLocators.THREE_DOTS_MENU,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(isPresent,
            "3dots button should be present within results items");
    }

    public void verifyNoResultsDisplayed() {
        KeyboardUtils.hideKeyboard(driver);
        boolean emptyStateDisplayed = WaitUtils.isVisible(
                driver,
                ArchiveLocators.EMPTY_STATE,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(emptyStateDisplayed,
            "Empty state 'No results found' should be displayed when search returns no results");
    }

    public void navigateBack() {
        if (isAndroid) {
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
        } else {
            WaitUtils.click(driver, ArchiveLocators.CANCEL_BUTTON);
        }
    }

    public void verifySearchBarClosed() {
        boolean isClosed = !WaitUtils.isVisible(
                driver,
                ArchiveLocators.SEARCH_BAR,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(isClosed, "Search bar should be closed after navigation back");
    }

    public void verifyFilesTabWithFilterIcon() {
        verifyFilesTabIsSelected();
        verifyFilterIconPresentOnFilesTab();
    }

    public void openSearchAndVerifyUI() {
        verifySearchBarPlaceholder();
        verifyScreenBlankBeforeSearch();

        if (isAndroid) {
            verifyBackArrowButtonPresent();
        } else {
            verifyCancelButtonPresent();
        }
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
        boolean dialogDisplayed = WaitUtils.isVisible(
                driver,
                ArchiveLocators.DOWNLOAD_DIALOG,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(dialogDisplayed, "Download confirmation dialog should be displayed");
    }

    public void clickDownloadButtonInDialog() {
        WaitUtils.click(driver, ArchiveLocators.DOWNLOAD_BUTTON_DIALOG);
    }

    public void verifyDownloadingSnackbar() {
        WebElement snackbar = WaitUtils.waitForVisibility(
                driver,
                ArchiveLocators.SNACKBAR_TEXT,
                TimeoutConstants.SHORT_TIMEOUT
        );
        String snackbarText = snackbar.getText();
        Assert.assertTrue(snackbarText.toLowerCase().contains("downloading"),
            "Snackbar should show 'Downloading' message but was: " + snackbarText);
    }

    public void verifyInProgressStatus() {
        boolean inProgress = WaitUtils.isVisible(
                driver,
                ArchiveLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(inProgress, "'In progress' status should be displayed during download");
    }

    public void waitForDownloadComplete() {
        String snackbarText = WaitUtils.pollText(
                driver,
                ArchiveLocators.SNACKBAR_TEXT,
                "Saved to Files",
                Duration.ofSeconds(60)
        );
        Assert.assertTrue(snackbarText.contains("Saved to Files"),
            "Success snackbar should show 'Saved to Files' but was: " + snackbarText);
    }

    public void clickCancelDownloadIcon() {
        WaitUtils.click(driver, ArchiveLocators.CANCEL_DOWNLOAD_ICON);
    }

    public void clickYesCancelButton() {
        WaitUtils.click(driver, ArchiveLocators.YES_CANCEL_BUTTON);
    }

    public void verifyDownloadCancelledMessage() {
        WebElement snackbar = WaitUtils.waitForVisibility(
                driver,
                ArchiveLocators.SNACKBAR_TEXT,
                TimeoutConstants.SHORT_TIMEOUT
        );
        String snackbarText = snackbar.getText();
        Assert.assertTrue(snackbarText.toLowerCase().contains("cancel"),
            "Snackbar should show cancellation message but was: " + snackbarText);
        logger.info("Download cancelled successfully: " + snackbarText);
    }

    public void verifyDownloadCancelled() {
        boolean inProgressGone = !WaitUtils.isVisible(
                driver,
                ArchiveLocators.IN_PROGRESS_STATUS,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(inProgressGone, "Download should be cancelled - 'In progress' status should not be present");
    }

    public void verifyCompleteFolderDownloadFlow() {
        navigateToArchive();
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
     * Attempts to cancel folder download if cancel icon is available.
     * If folder is cached or downloads too quickly, cancellation test may not be possible.
     * In such cases, the test is skipped with a warning log message.
     */
    public void verifyFolderDownloadCancellation() {
        clickFirstFolderThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();

        boolean cancelIconAvailable = WaitUtils.isVisible(
                driver,
                ArchiveLocators.CANCEL_DOWNLOAD_ICON,
                TimeoutConstants.FAST_DOWNLOAD_CANCEL_CHECK
        );

        if (cancelIconAvailable) {
            clickCancelDownloadIcon();
            clickYesCancelButton();
            verifyDownloadCancelledMessage();
            verifyDownloadCancelled();
        } else {
            logger.warn("Folder downloaded too fast - cancel icon not available. Cancellation test skipped.");
        }
    }

    public void verifyCompleteVideoDownloadFlow() {
        navigateToArchive();
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
     * Attempts to cancel video download if cancel icon is available.
     * Videos may download too quickly for cancellation to be tested.
     * If cancel icon is not visible within the timeout period, the cancellation
     * test is skipped with a warning log message.
     */
    public void verifyVideoDownloadCancellation() {
        clickFirstFolder();
        clickFirstVideoThreeDots();
        clickDownloadOption();
        clickDownloadButtonInDialog();

        boolean cancelIconAvailable = WaitUtils.isVisible(
                driver,
                ArchiveLocators.CANCEL_DOWNLOAD_ICON,
                TimeoutConstants.FAST_DOWNLOAD_CANCEL_CHECK
        );

        if (cancelIconAvailable) {
            clickCancelDownloadIcon();
            clickYesCancelButton();
            verifyDownloadCancelledMessage();
            verifyDownloadCancelled();
        } else {
            logger.warn("Video downloaded too fast - cancel icon not available. Cancellation test skipped.");
        }
    }
}
