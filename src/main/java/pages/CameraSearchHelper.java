package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import java.util.List;
import locators.CamerasLocators;
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
        String previousCount = getCurrentCounterText();
        boolean wasEmptyStateVisible = isEmptyStateCurrentlyVisible();
        WaitUtils.clearAndType(driver, searchBar, searchTerm);
        Assert.assertTrue(waitForCounterToChangeFrom(previousCount, wasEmptyStateVisible, searchTerm),
                "Camera counter should change after entering search term '" + searchTerm + "' - filter may not have re-rendered");
    }

    private boolean isEmptyStateCurrentlyVisible() {
        for (int attempt = 1; ; attempt++) {
            try {
                return driver.findElements(emptyStateMessage).stream()
                        .anyMatch(WebElement::isDisplayed);
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                if (attempt == 2) {
                    // Throw on persistent staleness - let WebDriverWait handle retry
                    throw e;
                }
            }
        }
    }

    private String getCurrentCounterText() {
        for (int attempt = 1; ; attempt++) {
            try {
                return driver.findElements(cameraCounter).stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .map(WebElement::getText)
                        .orElse(null);
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                if (attempt == 2) {
                    // Throw on persistent staleness - let WebDriverWait handle retry
                    throw e;
                }
            }
        }
    }

    private boolean waitForCounterToChangeFrom(String previousCount, boolean wasEmptyStateVisibleInitially, String expectedSearchText) {
        try {
            new WebDriverWait(driver, TimeoutConstants.FILTER_RESULT_TIMEOUT)
                .until(d -> {
                    boolean isEmptyStateVisible = isEmptyStateCurrentlyVisible();

                    // Detect transition INTO empty state
                    if (isEmptyStateVisible && !wasEmptyStateVisibleInitially) {
                        return true;
                    }

                    // Detect counter change (non-empty results)
                    String currentCount = getCurrentCounterText();
                    if (currentCount != null && !currentCount.equals(previousCount)) {
                        return true;
                    }

                    // Handle empty→empty with different search term: verify search bar updated
                    if (isEmptyStateVisible && wasEmptyStateVisibleInitially) {
                        WebElement searchField = d.findElement(searchBar);
                        String currentSearchText = searchField.getText();
                        if (currentSearchText == null) {
                            currentSearchText = searchField.getAttribute("text");
                        }
                        // If search text matches what we typed, filter re-rendered
                        return expectedSearchText.equals(currentSearchText);
                    }

                    return false;
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
        String previousCount = getCurrentCounterText();
        boolean wasEmptyStateVisible = isEmptyStateCurrentlyVisible();
        WaitUtils.clearAndType(driver, searchBar, "");
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (Exception e) {
            // Keyboard already hidden or not present
        }
        Assert.assertTrue(waitForCounterToChangeFrom(previousCount, wasEmptyStateVisible, ""),
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
        // The keyboard stays up after typing a search term and covers the empty-state message (CI Build 103, BrowserStack),
        // so close it first. Then fall back to presence: with the IME resizing the window the message can sit below the
        // visible area even though it is rendered.
        hideKeyboard();
        boolean shown = isEmptyStateDisplayed() || WaitUtils.isPresent(driver, emptyStateMessage);
        Assert.assertTrue(shown, "Empty state message should be displayed when no cameras match search");
    }

    /**
     * Dismisses the soft keyboard. {@code mobile: hideKeyboard} is a silent no-op on this Compose search field, so fall
     * back to BACK, which only closes the IME while it is showing. Never throws.
     */
    private void hideKeyboard() {
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.debug("mobile: hideKeyboard failed: {}", e.getMessage());
        }
        try {
            if (driver instanceof AndroidDriver androidDriver && androidDriver.isKeyboardShown()) {
                androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
            }
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.debug("Could not dismiss keyboard with BACK: {}", e.getMessage());
        }
    }

    public void verifyAllVisibleCamerasContainSearchTerm(String searchTerm) {
        List<WebElement> cameraElements = driver.findElements(CamerasLocators.ALL_VISIBLE_CAMERAS);
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
