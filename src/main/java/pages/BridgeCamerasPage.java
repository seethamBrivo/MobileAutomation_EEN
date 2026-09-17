package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

public class BridgeCamerasPage {

    private static final Logger logger = LogManager.getLogger(BridgeCamerasPage.class);
    private final AppiumDriver driver;
    private final CameraSearchHelper searchHelper;

    private final By camerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    private final By backButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View");
    private final By searchBar = AppiumBy.xpath("//android.widget.EditText");
    private final By searchPlaceholder = AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]");
    private final By cameraCounter = AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]");
    private final By firstCameraInList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]");
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

    public void verifyFilteredCountLessThanOrEqualInitial(String filteredCount, String initialCount) {
        searchHelper.verifyFilteredCountLessThanOrEqualInitial(filteredCount, initialCount);
    }

    public void verifyCountersMatch(String actualCount, String expectedCount) {
        searchHelper.verifyCountersMatch(actualCount, expectedCount);
    }

    public void longPressFirstCamera() {
        searchHelper.longPressFirstCamera();
    }

    public void dismissContextMenu() {
        driver.navigate().back();
        logger.info("Context menu dismissed");
    }

    public void verifyCanInteractWithFilteredCamera() {
        longPressFirstCamera();
        // Verify context menu opened (same pattern as CamerasPage)
        Assert.assertTrue(WaitUtils.isVisible(driver, AppiumBy.xpath("//android.widget.TextView[@text='Camera settings']"), TimeoutConstants.SHORT_TIMEOUT),
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
