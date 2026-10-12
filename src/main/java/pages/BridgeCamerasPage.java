package pages;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.ConfigReader;
import utils.PlatformLocator;
import utils.WaitUtils;

public class BridgeCamerasPage {

    private final AppiumDriver driver;
    private final CameraSearchHelper searchHelper;

    private final By camerasOption = PlatformLocator.byText("Cameras");
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: same DevicesMainScreen back button as DevicesPage.bridgesBackButton, no testTag yet.
    private final By backButton = PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View"),
        AppiumBy.accessibilityId("backButton")
    );
    private final By searchBar = PlatformLocator.byInputField(null);
    // Inline locator for context menu - only used once in verification
    private final By contextMenuCameraSettings = PlatformLocator.byText("Camera settings");
    private final By searchPlaceholder = PlatformLocator.byText(UiTextConstants.SEARCH_PLACEHOLDER);
    private final By cameraCounter = buildCameraCounterLocator();
    private final By firstCameraInList = buildFirstCameraLocator();
    private final By emptyStateMessage = PlatformLocator.byText(UiTextConstants.EMPTY_STATE_MESSAGE);

    private By buildFirstCameraLocator() {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeOther' AND visible == 1 AND " +
                "(label CONTAINS 'Online' OR label CONTAINS 'Offline' OR label CONTAINS 'error' OR " +
                "label CONTAINS 'Invalid' OR label CONTAINS 'Turned off')");
        }
        return AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]");
    }

    private By buildCameraCounterLocator() {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSNsPredicateString(
                String.format("type == 'XCUIElementTypeStaticText' AND label CONTAINS '%s' AND " +
                    "NOT label CONTAINS 'Filter' AND NOT label CONTAINS 'No matching'",
                    UiTextConstants.CAMERA_COUNTER_TEXT));
        }
        return AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]");
    }

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
}
