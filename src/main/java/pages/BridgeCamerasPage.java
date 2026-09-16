package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

public class BridgeCamerasPage {

    private static final Logger logger = LogManager.getLogger(BridgeCamerasPage.class);
    private final AppiumDriver driver;

    private final By camerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    private final By backButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View");
    private final By searchBar = AppiumBy.xpath("//android.widget.EditText");
    private final By searchPlaceholder = AppiumBy.xpath("//android.widget.TextView[@text=\"Filter by camera\"]");
    private final By cameraCounter = AppiumBy.xpath("//android.widget.TextView[contains(@text, ' camera') and not(contains(@text, 'Filter'))]");
    private final By firstCameraInList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]");
    private final By emptyStateMessage = AppiumBy.xpath("//android.widget.TextView[@text=\"No matching cameras\"]");

    public BridgeCamerasPage(AppiumDriver driver) {
        this.driver = driver;
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
        WaitUtils.click(driver, searchBar);
    }

    public void verifySearchPlaceholderDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, searchPlaceholder).isDisplayed(),
                "Search placeholder 'Filter by camera' should be displayed");
    }

    public void verifySearchFieldEditable() {
        WebElement searchField = WaitUtils.waitForVisibility(driver, searchBar);
        Assert.assertTrue(searchField.isDisplayed() && searchField.isEnabled(),
                "Search field should be editable");
    }

    public void enterSearchTerm(String searchTerm) {
        WaitUtils.clearAndType(driver, searchBar, searchTerm);
    }

    public boolean isCameraFoundInFilter() {
        return WaitUtils.isVisible(driver, firstCameraInList, TimeoutConstants.FILTER_RESULT_TIMEOUT);
    }

    public void verifyCameraFoundInFilter(String searchTerm) {
        Assert.assertTrue(isCameraFoundInFilter(),
                "Camera should be found in filter results for: " + searchTerm);
    }

    public void verifyFilteredListDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, firstCameraInList).isDisplayed(),
                "Filtered camera list should be displayed");
    }

    public void verifyCameraCounterDisplayed() {
        Assert.assertTrue(WaitUtils.isVisible(driver, cameraCounter, TimeoutConstants.SHORT_TIMEOUT),
                "Camera counter should be displayed");
    }

    public String getCameraCounterText() {
        return WaitUtils.waitForVisibility(driver, cameraCounter).getText();
    }

    public void clearSearchIfNeeded() {
        WebElement searchField = WaitUtils.waitForVisibility(driver, searchBar);
        String currentText = searchField.getText();

        if (currentText != null && !currentText.isEmpty()) {
            By clearIcon = AppiumBy.xpath(
                "//android.widget.EditText[@text=\"" + currentText + "\"]/android.view.View[3]/android.view.View"
            );
            WaitUtils.click(driver, clearIcon);
            logger.info("Cleared search term: {}", currentText);
        } else {
            logger.info("Search field is already empty");
        }
    }

    public void verifyDefaultCameraListRestored() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, firstCameraInList).isDisplayed(),
                "Default camera list should be restored after clearing search");
    }

    public void validateCounterFormat(String counterText) {
        Assert.assertTrue(counterText.matches("\\d+ [Cc]ameras?"),
                "Counter should show format like '10 cameras' but was: " + counterText);
    }

    public int extractCountFromCounterText(String counterText) {
        String numberOnly = counterText.replaceAll("\\D+", "");
        Assert.assertFalse(numberOnly.isEmpty(),
                "Counter text should contain a numeric value: " + counterText);

        return Integer.parseInt(numberOnly);
    }

    public void verifyFilteredCountLessThanOrEqualInitial(String filteredCount, String initialCount) {
        int filteredNum = extractCountFromCounterText(filteredCount);
        int initialNum = extractCountFromCounterText(initialCount);
        Assert.assertTrue(filteredNum <= initialNum,
                "Filtered count (" + filteredNum + ") should be <= initial count (" + initialNum + ")");
    }

    public void verifyCountersMatch(String actualCount, String expectedCount) {
        Assert.assertEquals(actualCount, expectedCount,
                "Counter should restore to initial value");
    }

    public void longPressFirstCamera() {
        WebElement camera = WaitUtils.waitForVisibility(driver, firstCameraInList);
        GestureUtils.longPress(driver, camera);
    }

    public void dismissContextMenu() {
        driver.navigate().back();
        logger.info("Context menu dismissed");
    }

    public void verifyCanInteractWithFilteredCamera() {
        longPressFirstCamera();
        dismissContextMenu();
    }

    public boolean isEmptyStateDisplayed() {
        return WaitUtils.isVisible(driver, emptyStateMessage, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void verifyEmptyStateDisplayed() {
        Assert.assertTrue(isEmptyStateDisplayed(),
                "Empty state message should be displayed when no cameras match search");
    }

    public void clickBackButton() {
        WaitUtils.click(driver, backButton);
    }

}
