package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import locators.CamerasLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.KeyboardUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

/**
 * Filtering the camera list with the search bar, as on Dashboard > Cameras and on a bridge's Cameras screen. Both
 * screens behave the same; each page creates one of these with its own locators and hands it out through
 * {@code cameraSearchFilter()}.
 *
 * <p>The public methods are the steps of the test cases (EEPD-TC-15581 and EEPD-TC-53382); everything else is
 * private detail of how a step is carried out.
 */
public final class CameraSearchFilter {

    private static final Logger logger = LogManager.getLogger(CameraSearchFilter.class);
    /** The counter above the list, for example "12 cameras". */
    private static final Pattern CAMERA_COUNT = Pattern.compile("^(\\d+)\\s+[Cc]ameras?");

    private final AppiumDriver driver;
    private final By searchBar;
    private final By searchPlaceholder;
    private final By cameraCounter;
    private final By firstCameraInList;
    private final By emptyStateMessage;

    public CameraSearchFilter(AppiumDriver driver, By searchBar, By searchPlaceholder, By cameraCounter,
                              By firstCameraInList, By emptyStateMessage) {
        this.driver = driver;
        this.searchBar = searchBar;
        this.searchPlaceholder = searchPlaceholder;
        this.cameraCounter = cameraCounter;
        this.firstCameraInList = firstCameraInList;
        this.emptyStateMessage = emptyStateMessage;
    }

    // ---- steps of the test cases ----------------------------------------------------------

    /** Reads the camera counter, checks its format and returns the text for later comparison. */
    public String captureCounterAndVerifyFormat() {
        String counterText = readCameraCounterText();
        cameraCountFrom(counterText);
        return counterText;
    }

    /** Step "Tap on search bar": the placeholder is shown and the field is editable. */
    public void tapSearchBarAndVerify() {
        WaitUtils.click(driver, searchBar);
        verifySearchPlaceholderDisplayed();
        WebElement searchField = WaitUtils.waitForVisibility(driver, searchBar);
        Assert.assertTrue(searchField.isDisplayed() && searchField.isEnabled(), "Search field should be editable");
    }

    /** Step "Provide a search term": the list is filtered and the counter shrinks against {@code initialCount}. */
    public void searchAndVerifyResults(String searchTerm, String initialCount) {
        enterSearchTerm(searchTerm);
        WaitUtils.assertVisible(driver, firstCameraInList, TimeoutConstants.FILTER_RESULT_TIMEOUT,
                "Camera should be found in filter results for: " + searchTerm);
        verifyAllVisibleCamerasContainSearchTerm(searchTerm);
        int filteredCount = cameraCountFrom(readCameraCounterText());
        int initialCountNumber = cameraCountFrom(initialCount);
        Assert.assertTrue(filteredCount < initialCountNumber, "Filtered count (" + filteredCount
                + ") should be < initial count (" + initialCountNumber + ") - filter must reduce results");
        Assert.assertTrue(filteredCount > 0, "Filtered count (" + filteredCount
                + ") should be > 0 - filter must find results");
    }

    /** Step "Cancel the filtering": the default list and the initial counter are back. */
    public void clearSearchAndVerifyRestored(String initialCount) {
        clearSearch();
        verifyDefaultCameraListRestored();
        String restoredCount = readCameraCounterText();
        cameraCountFrom(restoredCount);
        Assert.assertEquals(restoredCount, initialCount, "Counter should restore to initial value");
    }

    /** A term with no matches shows the empty state; clearing it brings the default list back. */
    public void searchWithNoResultsAndVerifyEmptyState(String searchTerm) {
        enterSearchTerm(searchTerm);
        verifyEmptyStateDisplayed();
        clearSearch();
        verifyDefaultCameraListRestored();
    }

    /** The counter's number. "1 camera" is singular and any other number is plural, as the test case says. */
    public int readCameraCountAndVerifyWording() {
        String counterText = readCameraCounterText().trim();
        int count = cameraCountFrom(counterText);
        String expectedText = count + (count == 1 ? " camera" : " cameras");
        Assert.assertEquals(counterText, expectedText,
                "Counter for " + count + " camera(s) should read '" + expectedText + "' but was '" + counterText + "'");
        return count;
    }

    // ---- how the steps are carried out ----------------------------------------------------

    private void verifySearchPlaceholderDisplayed() {
        if (PlatformLocator.isIos()) {
            // iOS: the placeholder is the empty field's value, not a separate element.
            String placeholder = WaitUtils.waitForVisibility(driver, searchBar).getAttribute("value");
            Assert.assertEquals(placeholder, UiTextConstants.SEARCH_PLACEHOLDER,
                    "Search placeholder '" + UiTextConstants.SEARCH_PLACEHOLDER + "' should be displayed");
            return;
        }
        WaitUtils.assertVisible(driver, searchPlaceholder,
                "Search placeholder '" + UiTextConstants.SEARCH_PLACEHOLDER + "' should be displayed");
    }

    private void enterSearchTerm(String searchTerm) {
        if (PlatformLocator.isIos() && WaitUtils.isPresent(driver, CamerasLocators.CLEAR_TEXT_BUTTON)) {
            // iOS: the "Clear text" button exists only while the field holds text, so leftover text is cleared first.
            WaitUtils.click(driver, CamerasLocators.CLEAR_TEXT_BUTTON);
        }
        String previousCount = currentCounterTextOrNull();
        boolean wasEmptyStateVisible = isEmptyStateVisibleNow();
        if (PlatformLocator.isIos()) {
            // iOS: the field is empty at this point, so it is tapped and typed into.
            WebElement field = WaitUtils.waitForVisibility(driver, searchBar);
            field.click();
            field.sendKeys(searchTerm);
        } else {
            WaitUtils.clearAndType(driver, searchBar, searchTerm);
        }
        Assert.assertTrue(waitForListToUpdate(previousCount, wasEmptyStateVisible, searchTerm),
                "Camera counter should change after entering search term '" + searchTerm
                        + "' - filter may not have re-rendered");
    }

    private void clearSearch() {
        String previousCount = currentCounterTextOrNull();
        boolean wasEmptyStateVisible = isEmptyStateVisibleNow();
        if (PlatformLocator.isIos()) {
            // iOS: the field has a "Clear text" button, which is used instead of clearing the text by typing.
            // It only exists while the field holds text, so an already empty field has nothing to clear.
            if (!WaitUtils.isVisible(
                    driver, CamerasLocators.CLEAR_TEXT_BUTTON, TimeoutConstants.FILTER_RESULT_TIMEOUT)) {
                logger.info("Search field is already empty; nothing to clear");
                return;
            }
            WaitUtils.click(driver, CamerasLocators.CLEAR_TEXT_BUTTON);
        } else {
            WaitUtils.clearAndType(driver, searchBar, "");
            try {
                driver.executeScript("mobile: hideKeyboard");
            } catch (Exception e) {
                // Keyboard already hidden or not present
            }
        }
        Assert.assertTrue(waitForListToUpdate(previousCount, wasEmptyStateVisible, ""),
                "Camera counter should change after clearing search - filter may not have reset");
        logger.info("Cleared search field");
    }

    private void verifyDefaultCameraListRestored() {
        WaitUtils.assertVisible(driver, firstCameraInList,
                "Default camera list should be restored after clearing search");
    }

    private void verifyEmptyStateDisplayed() {
        // The keyboard stays up after typing a search term and covers the empty-state message (CI Build 103,
        // BrowserStack), so close it first. Then fall back to presence: with the IME resizing the window the message
        // can sit below the visible area even though it is rendered.
        if (!PlatformLocator.isIos()) { // the Android keyboard hides the message; on iOS it is not hidden
            KeyboardUtils.hideKeyboard(driver);
        }
        boolean shown = WaitUtils.isVisible(driver, emptyStateMessage, TimeoutConstants.FILTER_RESULT_TIMEOUT)
                || WaitUtils.isPresent(driver, emptyStateMessage);
        Assert.assertTrue(shown, "Empty state message should be displayed when no cameras match search");
    }

    private void verifyAllVisibleCamerasContainSearchTerm(String searchTerm) {
        List<WebElement> cameraElements = WaitUtils.findElements(driver, CamerasLocators.ALL_VISIBLE_CAMERAS);
        Assert.assertFalse(cameraElements.isEmpty(), "Should have at least one visible camera");
        for (WebElement element : cameraElements) {
            String cameraName = element.getText();
            Assert.assertTrue(cameraName.toLowerCase().contains(searchTerm.toLowerCase()),
                    "Camera name '" + cameraName + "' should contain search term '" + searchTerm + "'");
        }
        logger.info("Verified {} visible cameras all contain search term '{}'", cameraElements.size(), searchTerm);
    }

    // ---- the counter ------------------------------------------------------------------------

    private String readCameraCounterText() {
        WaitUtils.assertVisible(driver, cameraCounter, "Camera counter should be displayed");
        return WaitUtils.waitForVisibility(driver, cameraCounter).getText();
    }

    /** The number in a counter text such as "12 cameras"; fails when the text has another format. */
    private int cameraCountFrom(String counterText) {
        Matcher matcher = CAMERA_COUNT.matcher(counterText);
        Assert.assertTrue(matcher.find(), "Counter should show format like '10 cameras' but was: " + counterText);
        return Integer.parseInt(matcher.group(1));
    }

    // ---- waiting for the list to re-render --------------------------------------------------

    private boolean isEmptyStateVisibleNow() {
        return WaitUtils.retryOnStale(() -> driver.findElements(emptyStateMessage).stream()
                .anyMatch(WebElement::isDisplayed));
    }

    /** The counter text if one is showing, else null (the empty state hides the counter). */
    private String currentCounterTextOrNull() {
        return WaitUtils.retryOnStale(() -> driver.findElements(cameraCounter).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .map(WebElement::getText)
                .orElse(null));
    }

    /**
     * True once the list has visibly reacted to the typed (or cleared) text: the empty state appeared, or the counter
     * changed, or, when the empty state was already showing, the search field now holds {@code expectedSearchText}.
     */
    private boolean waitForListToUpdate(String previousCount, boolean wasEmptyStateVisible, String expectedSearchText) {
        try {
            new WebDriverWait(driver, TimeoutConstants.FILTER_RESULT_TIMEOUT).until(d -> {
                boolean isEmptyStateVisible = isEmptyStateVisibleNow();
                if (isEmptyStateVisible && !wasEmptyStateVisible) {
                    return true;
                }
                String currentCount = currentCounterTextOrNull();
                if (currentCount != null && !currentCount.equals(previousCount)) {
                    return true;
                }
                if (isEmptyStateVisible) {
                    // Empty state before and after, with a different term: the text in the field is the only sign.
                    WebElement searchField = d.findElement(searchBar);
                    String currentSearchText = searchField.getText();
                    if (currentSearchText == null) {
                        currentSearchText = searchField.getAttribute("text");
                    }
                    return expectedSearchText.equals(currentSearchText);
                }
                return false;
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
