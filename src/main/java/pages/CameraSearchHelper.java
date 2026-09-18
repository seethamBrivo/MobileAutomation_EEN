package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/**
 * Shared helper for camera search and filter functionality used by both
 * CamerasPage and BridgeCamerasPage to eliminate code duplication.
 */
public class CameraSearchHelper {

    private static final Logger logger = LogManager.getLogger(CameraSearchHelper.class);
    private final AppiumDriver driver;
    private final By searchBar;
    private final By searchPlaceholder;
    private final By cameraCounter;
    private final By firstCameraInList;
    private final By emptyStateMessage;

    public CameraSearchHelper(AppiumDriver driver,
                              By searchBar,
                              By searchPlaceholder,
                              By cameraCounter,
                              By firstCameraInList,
                              By emptyStateMessage) {
        this.driver = driver;
        this.searchBar = searchBar;
        this.searchPlaceholder = searchPlaceholder;
        this.cameraCounter = cameraCounter;
        this.firstCameraInList = firstCameraInList;
        this.emptyStateMessage = emptyStateMessage;
    }

    public void clickSearchBar() {
        WaitUtils.click(driver, searchBar);
    }

    public void verifySearchPlaceholderDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, searchPlaceholder).isDisplayed(),
                "Search placeholder '" + UiTextConstants.SEARCH_PLACEHOLDER + "' should be displayed");
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
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, cameraCounter).isDisplayed(),
                "Camera counter should be displayed");
    }

    public String getCameraCounterText() {
        return WaitUtils.waitForVisibility(driver, cameraCounter).getText();
    }

    public void clearSearchIfNeeded() {
        WebElement searchField = WaitUtils.waitForVisibility(driver, searchBar);
        // Use getAttribute("text") to get actual field value, not placeholder
        String fieldValue = searchField.getAttribute("text");

        // Check if field has actual text
        if (fieldValue != null && !fieldValue.isEmpty()) {
            WaitUtils.clear(driver, searchBar);
            logger.info("Cleared search field");
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
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(\\d+)\\s+[Cc]ameras?");
        java.util.regex.Matcher matcher = pattern.matcher(counterText);

        Assert.assertTrue(matcher.find(),
                "Counter text should match format 'X cameras': " + counterText);

        return Integer.parseInt(matcher.group(1));
    }

    public void verifyFilteredCountLessThanInitial(String filteredCount, String initialCount) {
        int filteredNum = extractCountFromCounterText(filteredCount);
        int initialNum = extractCountFromCounterText(initialCount);
        Assert.assertTrue(filteredNum < initialNum,
                "Filtered count (" + filteredNum + ") should be < initial count (" + initialNum + ") - filter must reduce results");
        Assert.assertTrue(filteredNum > 0,
                "Filtered count (" + filteredNum + ") should be > 0 - filter must find results");
    }

    public void verifyCountersMatch(String actualCount, String expectedCount) {
        Assert.assertEquals(actualCount, expectedCount,
                "Counter should restore to initial value");
    }

    public void longPressFirstCamera() {
        WebElement camera = WaitUtils.waitForVisibility(driver, firstCameraInList);
        GestureUtils.longPress(driver, camera);
    }

    public boolean isEmptyStateDisplayed() {
        return WaitUtils.isVisible(driver, emptyStateMessage, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void verifyEmptyStateDisplayed() {
        Assert.assertTrue(isEmptyStateDisplayed(),
                "Empty state message should be displayed when no cameras match search");
    }
}
