package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/**
 * Shared helper for camera search and filter functionality used by both
 * CamerasPage and BridgeCamerasPage to eliminate code duplication.
 */
public class CameraSearchHelper {

    private static final Logger logger = LogManager.getLogger(CameraSearchHelper.class);
    private static final java.util.regex.Pattern COUNTER_PATTERN = java.util.regex.Pattern.compile("^(\\d+)\\s+[Cc]ameras?");
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
        String previousCount = getCameraCounterText();
        WaitUtils.clearAndType(driver, searchBar, searchTerm);
        Assert.assertTrue(waitForCounterToChangeFrom(previousCount),
                "Camera counter should change after entering search term '" + searchTerm + "' - filter may not have re-rendered");
    }

    private boolean waitForCounterToChangeFrom(String previousCount) {
        boolean wasEmptyStateVisibleInitially = driver.findElements(emptyStateMessage).stream()
                .anyMatch(WebElement::isDisplayed);

        try {
            new WebDriverWait(driver, TimeoutConstants.FILTER_RESULT_TIMEOUT)
                .until(d -> {
                    // Check if empty state is NOW visible (fast check, no nested wait)
                    boolean isEmptyStateVisible = driver.findElements(emptyStateMessage).stream()
                            .anyMatch(WebElement::isDisplayed);

                    // Only return true if empty state wasn't visible before but is now
                    if (isEmptyStateVisible && !wasEmptyStateVisibleInitially) {
                        return true;
                    }

                    String currentCount = getCameraCounterText();
                    return !currentCount.equals(previousCount);
                });
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
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
        String previousCount = getCameraCounterText();
        WaitUtils.clearAndType(driver, searchBar, "");
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (Exception e) {
            // Keyboard already hidden or not present
        }
        Assert.assertTrue(waitForCounterToChangeFrom(previousCount),
                "Camera counter should change after clearing search - filter may not have reset");
        logger.info("Cleared search field");
    }

    public void verifyDefaultCameraListRestored() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, firstCameraInList).isDisplayed(),
                "Default camera list should be restored after clearing search");
    }

    public void validateCounterFormat(String counterText) {
        Assert.assertTrue(COUNTER_PATTERN.matcher(counterText).find(),
                "Counter should show format like '10 cameras' but was: " + counterText);
    }

    public int extractCountFromCounterText(String counterText) {
        java.util.regex.Matcher matcher = COUNTER_PATTERN.matcher(counterText);
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
        return WaitUtils.isVisible(driver, emptyStateMessage, TimeoutConstants.FILTER_RESULT_TIMEOUT);
    }

    public void verifyEmptyStateDisplayed() {
        Assert.assertTrue(isEmptyStateDisplayed(),
                "Empty state message should be displayed when no cameras match search");
    }

    public void verifyAllVisibleCamerasContainSearchTerm(String searchTerm) {
        By allVisibleCameras = AppiumBy.xpath(
            "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]]//android.widget.TextView[1]"
        );
        List<WebElement> cameraElements = driver.findElements(allVisibleCameras);
        Assert.assertFalse(cameraElements.isEmpty(), "Should have at least one visible camera");
        for (WebElement element : cameraElements) {
            String cameraName = element.getText();
            Assert.assertTrue(cameraName.toLowerCase().contains(searchTerm.toLowerCase()),
                "Camera name '" + cameraName + "' should contain search term '" + searchTerm + "'");
        }
        logger.info("Verified {} visible cameras all contain search term '{}'", cameraElements.size(), searchTerm);
    }

    public void dismissContextMenu() {
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (Exception e) {
            // Keyboard already hidden or not present
        }
        driver.navigate().back();
        logger.info("Context menu dismissed");
    }
}
