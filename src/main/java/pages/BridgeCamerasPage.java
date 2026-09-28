package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

public class BridgeCamerasPage {

    private final AppiumDriver driver;
    private final CameraSearchHelper searchHelper;

    private final By camerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    // Stable: Back button at fixed position [6][1] in DevicesMainScreen Compose hierarchy
    // TODO: replace with testTag/content-desc once app exposes one for this back button
    private final By backButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View");
    private final By searchBar = AppiumBy.xpath("//android.widget.EditText");
    // Inline locator for context menu - only used once in verification
    private final By contextMenuCameraSettings = AppiumBy.xpath("//android.widget.TextView[@text='Camera settings']");
    private final By searchPlaceholder = AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]");
    private final By cameraCounter = AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]");
    private final By firstCameraInList = AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]");
    private final By emptyStateMessage = AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.EMPTY_STATE_MESSAGE + "\"]");

    public BridgeCamerasPage(AppiumDriver driver) {
        this.driver = driver;
        this.searchHelper = new CameraSearchHelper(
            driver,
            searchBar,
            searchPlaceholder,
            cameraCounter,
            firstCameraInList,
            emptyStateMessage
        );
    }

    public void clickCamerasOption() {
        WaitUtils.click(driver, camerasOption);
    }

    public void verifyCamerasOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, camerasOption).isDisplayed(),
                "Cameras option should be displayed in bridge context menu");
    }

    public void verifyCameraListDisplayed() {
        // First verify we're on camera screen (search placeholder is unique to camera screen)
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, searchPlaceholder).isDisplayed(),
                "Camera screen should be displayed (search placeholder visible)");
        // Then verify cameras are present
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, firstCameraInList).isDisplayed(),
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

    public void verifyCanInteractWithFilteredCamera() {
        longPressFirstCamera();
        // Verify context menu opened - use normal wait, not SHORT_TIMEOUT
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, contextMenuCameraSettings).isDisplayed(),
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
        WaitUtils.click(driver, backButton);
    }

    public boolean isOnCameraScreen() {
        return WaitUtils.isVisible(driver, searchPlaceholder, TimeoutConstants.SHORT_TIMEOUT);
    }

}
