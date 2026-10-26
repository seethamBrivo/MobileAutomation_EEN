package pages;

import io.appium.java_client.AppiumDriver;
import locators.BridgeCamerasLocators;
import org.testng.Assert;
import utils.WaitUtils;

public class BridgeCamerasPage {

    private final AppiumDriver driver;
    private final CameraSearchHelper searchHelper;


    public BridgeCamerasPage(AppiumDriver driver) {
        this.driver = driver;
        this.searchHelper = new CameraSearchHelper(
            driver,
            BridgeCamerasLocators.SEARCH_BAR,
            BridgeCamerasLocators.SEARCH_PLACEHOLDER,
            BridgeCamerasLocators.CAMERA_COUNTER,
            BridgeCamerasLocators.FIRST_CAMERA_IN_LIST,
            BridgeCamerasLocators.EMPTY_STATE_MESSAGE
        );
    }

    public void clickCamerasOption() {
        WaitUtils.click(driver, BridgeCamerasLocators.CAMERAS_OPTION);
    }

    public void verifyCamerasOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, BridgeCamerasLocators.CAMERAS_OPTION).isDisplayed(),
                "Cameras option should be displayed in bridge context menu");
    }

    public void verifyCameraListDisplayed() {
        // First verify we're on camera screen (search placeholder is unique to camera screen)
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, BridgeCamerasLocators.SEARCH_PLACEHOLDER).isDisplayed(),
                "Camera screen should be displayed (search placeholder visible)");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, BridgeCamerasLocators.FIRST_CAMERA_IN_LIST).isDisplayed(),
                "Camera list should be displayed");
    }

    public void clickSearchBar() {
        searchHelper.clickSearchBar();
    }

    public void verifySearchPlaceholderDisplayed() {
        searchHelper.verifySearchPlaceholderDisplayed();
    }

    public void verifySearchFieldEditable() {
        searchHelper.verifySearchFieldEditable();
    }

    public void enterSearchTerm(String searchTerm) {
        searchHelper.enterSearchTerm(searchTerm);
    }

    public boolean isCameraFoundInFilter() {
        return searchHelper.isCameraFoundInFilter();
    }

    public void verifyCameraFoundInFilter(String searchTerm) {
        searchHelper.verifyCameraFoundInFilter(searchTerm);
    }

    public void verifyFilteredListDisplayed() {
        searchHelper.verifyFilteredListDisplayed();
    }

    public void verifyAllVisibleCamerasContainSearchTerm(String searchTerm) {
        searchHelper.verifyAllVisibleCamerasContainSearchTerm(searchTerm);
    }

    public void verifyCameraCounterDisplayed() {
        searchHelper.verifyCameraCounterDisplayed();
    }

    public String getCameraCounterText() {
        return searchHelper.getCameraCounterText();
    }

    public void clearSearchIfNeeded() {
        searchHelper.clearSearchIfNeeded();
    }

    public void verifyDefaultCameraListRestored() {
        searchHelper.verifyDefaultCameraListRestored();
    }

    public void validateCounterFormat(String counterText) {
        searchHelper.validateCounterFormat(counterText);
    }

    public int extractCountFromCounterText(String counterText) {
        return searchHelper.extractCountFromCounterText(counterText);
    }

    public void verifyFilteredCountLessThanInitial(String filteredCount, String initialCount) {
        searchHelper.verifyFilteredCountLessThanInitial(filteredCount, initialCount);
    }

    public void verifyCountersMatch(String actualCount, String expectedCount) {
        searchHelper.verifyCountersMatch(actualCount, expectedCount);
    }

    public void longPressFirstCamera() {
        searchHelper.longPressFirstCamera();
    }

    public void dismissContextMenu() {
        searchHelper.dismissContextMenu();
    }

    // ---- step-level groups ----------------------------------------------------------------

    public String captureCounterAndVerifyFormat() {
        return searchHelper.captureCounterAndVerifyFormat();
    }

    public void tapSearchBarAndVerify() {
        searchHelper.tapSearchBarAndVerify();
    }

    public void searchAndVerifyResults(String searchTerm, String initialCount) {
        searchHelper.searchAndVerifyResults(searchTerm, initialCount);
    }

    public void clearSearchAndVerifyRestored(String initialCount) {
        searchHelper.clearSearchAndVerifyRestored(initialCount);
    }

    public void searchWithNoResultsAndVerifyEmptyState(String searchTerm) {
        searchHelper.searchWithNoResultsAndVerifyEmptyState(searchTerm);
    }

    public void verifyCanInteractWithFilteredCamera() {
        longPressFirstCamera();
        // Verify context menu opened - use normal wait, not SHORT_TIMEOUT
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, BridgeCamerasLocators.CONTEXT_MENU_CAMERA_SETTINGS).isDisplayed(),
                "Context menu should be displayed after long press");
        dismissContextMenu();
    }

    public boolean isEmptyStateDisplayed() {
        return searchHelper.isEmptyStateDisplayed();
    }

    public void verifyEmptyStateDisplayed() {
        searchHelper.verifyEmptyStateDisplayed();
    }

    public void clickBackButton() {
        WaitUtils.click(driver, BridgeCamerasLocators.BACK_BUTTON);
    }
}
