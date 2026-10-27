package pages.ios;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import locators.ios.IosCamerasLocators;
import locators.ios.IosDevicesLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/** iOS counterpart of {@code pages.CamerasPage} (with {@code CameraSearchHelper}): same steps on the iOS screens. */
public class IosCamerasPage {

    private static final Logger logger = LogManager.getLogger(IosCamerasPage.class);
    private static final Pattern COUNTER_PATTERN = Pattern.compile("^(\\d+)\\s+[Cc]ameras?");
    private final AppiumDriver driver;

    public IosCamerasPage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ---- step-level groups ---------------------------------------------------------------

    /** Step "Tap on Cameras item": opens the cameras list and waits for it. */
    public void openCamerasList() {
        WaitUtils.click(driver, IosDevicesLocators.CAMERAS_TILE);
        verifyCameraListDisplayed();
    }

    public void verifyCameraListDisplayed() {
        // The list first renders "0 cameras" while it loads, so wait for the first camera row itself.
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.FIRST_CAMERA, TimeoutConstants.LIST_LOAD_TIMEOUT),
                "Could not find any camera in the list");
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.SEARCH_FIELD, TimeoutConstants.SHORT_TIMEOUT),
                "Camera search field should be visible on cameras screen");
    }

    /** Reads the camera counter, checks its format and returns the text for later comparison. */
    public String captureCounterAndVerifyFormat() {
        String count = getCameraCounterText();
        validateCounterFormat(count);
        return count;
    }

    /** Step "Tap on search bar": placeholder shown and the field is editable. */
    public void tapSearchBarAndVerify() {
        WaitUtils.click(driver, IosCamerasLocators.SEARCH_FIELD);
        WebElement field = WaitUtils.waitForVisibility(driver, IosCamerasLocators.SEARCH_FIELD);
        Assert.assertEquals(field.getAttribute("value"), UiTextConstants.SEARCH_PLACEHOLDER,
                "Search placeholder '" + UiTextConstants.SEARCH_PLACEHOLDER + "' should be displayed");
        Assert.assertTrue(field.isDisplayed() && field.isEnabled(), "Search field should be editable");
    }

    /** Step "Provide a search term": the list is filtered and the counter shrinks against {@code initialCount}. */
    public void searchAndVerifyResults(String searchTerm, String initialCount) {
        enterSearchTerm(searchTerm);
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.FIRST_CAMERA, TimeoutConstants.FILTER_RESULT_TIMEOUT),
                "Camera should be found in filter results for: " + searchTerm);
        verifyAllVisibleCamerasContainSearchTerm(searchTerm);
        String filteredCount = getCameraCounterText();
        validateCounterFormat(filteredCount);
        int filteredNum = extractCount(filteredCount);
        int initialNum = extractCount(initialCount);
        Assert.assertTrue(filteredNum < initialNum,
                "Filtered count (" + filteredNum + ") should be < initial count (" + initialNum + ") - filter must reduce results");
        Assert.assertTrue(filteredNum > 0,
                "Filtered count (" + filteredNum + ") should be > 0 - filter must find results");
    }

    /** Step "Cancel the filtering": the default list and the initial counter are back. */
    public void clearSearchAndVerifyRestored(String initialCount) {
        clearSearch();
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.FIRST_CAMERA, TimeoutConstants.LIST_LOAD_TIMEOUT),
                "Default camera list should be restored after clearing search");
        String restoredCount = getCameraCounterText();
        validateCounterFormat(restoredCount);
        Assert.assertEquals(restoredCount, initialCount, "Counter should restore to initial value");
    }

    /** A term with no matches shows the empty state; clearing it brings the default list back. */
    public void searchWithNoResultsAndVerifyEmptyState(String searchTerm) {
        enterSearchTerm(searchTerm);
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.EMPTY_STATE_IMAGE, TimeoutConstants.FILTER_RESULT_TIMEOUT),
                "Empty state should be displayed when no cameras match search");
        Assert.assertEquals(extractCount(getCameraCounterText()), 0, "Counter should show 0 cameras in the empty state");
        clearSearch();
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.FIRST_CAMERA, TimeoutConstants.LIST_LOAD_TIMEOUT),
                "Default camera list should be restored after clearing search");
    }

    public void verifyCanInteractWithFilteredCamera() {
        closeKeyboard();
        longPressFirstCamera();
        verifyContextMenuDisplayed();
        dismissContextMenu();
    }

    // ---- search internals ----------------------------------------------------------------

    private void enterSearchTerm(String searchTerm) {
        String previousCount = getCameraCounterText();
        WebElement field = WaitUtils.waitForVisibility(driver, IosCamerasLocators.SEARCH_FIELD);
        field.click();
        field.sendKeys(searchTerm);
        Assert.assertTrue(waitForCounterToChangeFrom(previousCount),
                "Camera counter should change after entering search term '" + searchTerm + "' - filter may not have re-rendered");
    }

    private void clearSearch() {
        String previousCount = getCameraCounterText();
        WaitUtils.click(driver, IosCamerasLocators.CLEAR_TEXT_BUTTON);
        Assert.assertTrue(waitForCounterToChangeFrom(previousCount),
                "Camera counter should change after clearing search - filter may not have reset");
        logger.info("Cleared search field");
    }

    private boolean waitForCounterToChangeFrom(String previousCount) {
        try {
            new WebDriverWait(driver, TimeoutConstants.FILTER_RESULT_TIMEOUT)
                    .until(d -> !previousCount.equals(currentCounterText()));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    private String currentCounterText() {
        return driver.findElements(IosCamerasLocators.CAMERA_COUNTER).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .map(WebElement::getText)
                .orElse(null);
    }

    private String getCameraCounterText() {
        return WaitUtils.waitForVisibility(driver, IosCamerasLocators.CAMERA_COUNTER).getText();
    }

    private void validateCounterFormat(String counterText) {
        Assert.assertTrue(COUNTER_PATTERN.matcher(counterText).find(),
                "Counter should show format like '10 cameras' but was: " + counterText);
    }

    private int extractCount(String counterText) {
        Matcher matcher = COUNTER_PATTERN.matcher(counterText);
        Assert.assertTrue(matcher.find(), "Counter text should match format 'X cameras': " + counterText);
        return Integer.parseInt(matcher.group(1));
    }

    private void verifyAllVisibleCamerasContainSearchTerm(String searchTerm) {
        List<WebElement> cameraElements = driver.findElements(IosCamerasLocators.CAMERA_NAMES);
        Assert.assertFalse(cameraElements.isEmpty(), "Should have at least one visible camera");
        for (WebElement element : cameraElements) {
            String cameraName = element.getText();
            Assert.assertTrue(cameraName.toLowerCase().contains(searchTerm.toLowerCase()),
                    "Camera name '" + cameraName + "' should contain search term '" + searchTerm + "'");
        }
        logger.info("Verified {} visible cameras all contain search term '{}'", cameraElements.size(), searchTerm);
    }

    /** Closes the keyboard with its "search" key (disabled while the field is empty); it covers part of the list. */
    private void closeKeyboard() {
        if (WaitUtils.isVisible(driver, IosCamerasLocators.KEYBOARD_SEARCH_KEY, TimeoutConstants.SHORT_TIMEOUT)) {
            WaitUtils.click(driver, IosCamerasLocators.KEYBOARD_SEARCH_KEY);
        }
    }

    // ---- context menu --------------------------------------------------------------------

    public void longPressFirstCamera() {
        longPress(WaitUtils.waitForVisibility(driver, IosCamerasLocators.FIRST_CAMERA));
    }

    private void longPress(WebElement element) {
        Rectangle rect = element.getRect();
        driver.executeScript("mobile: touchAndHold", Map.of(
                "x", (double) (rect.x + rect.width / 2), "y", (double) (rect.y + rect.height / 2), "duration", 2.0));
    }

    public void verifyContextMenuDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, IosCamerasLocators.CAMERA_SETTINGS_OPTION).isDisplayed(),
                "Context menu should be displayed in the foreground with blurred background");
    }

    /** Taps the dimmed area beside the menu, which closes it without choosing an option. No-op if no menu is open. */
    public void dismissContextMenu() {
        if (!WaitUtils.isVisible(driver, IosCamerasLocators.CAMERA_SETTINGS_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            return;
        }
        driver.executeScript("mobile: tap", Map.of("x", 12.0, "y", 440.0));
        Assert.assertTrue(WaitUtils.waitForInvisibility(driver, IosCamerasLocators.CAMERA_SETTINGS_OPTION,
                TimeoutConstants.SHORT_TIMEOUT), "Context menu should close");
        logger.info("Context menu dismissed");
    }

    // ---- context menu test steps (EEPD-TC-12355) -------------------------------------------

    /** Long presses the first offline or error camera in the list and checks the context menu: options, name and status. */
    public void verifyFirstOfflineOrErrorCameraContextMenu() {
        logger.info("Looking for first offline or error camera");
        Assert.assertTrue(GestureUtils.scrollUntilVisible(driver, IosCamerasLocators.FIRST_OFFLINE_OR_ERROR_STATUS),
                "Could not find an offline or error camera even after scrolling");
        longPress(WaitUtils.waitForVisibility(driver, IosCamerasLocators.FIRST_OFFLINE_OR_ERROR_STATUS));
        verifyContextMenuDisplayed();
        verifyAllContextMenuOptions();
        verifyMenuCameraNameDisplayed();
        Assert.assertTrue(WaitUtils.isPresent(driver, IosCamerasLocators.ANY_STATUS),
                "Expected at least one status indicator to be displayed");
    }

    /** Closes the menu, finds the first online camera, long presses it and checks the preview's timestamp. */
    public void verifyFirstOnlineCameraContextMenu() {
        dismissContextMenu();
        GestureUtils.scrollToTop(driver);
        Assert.assertTrue(GestureUtils.scrollUntilVisible(driver, IosCamerasLocators.FIRST_ONLINE_STATUS),
                "Could not find an online camera even after scrolling");
        longPress(WaitUtils.waitForVisibility(driver, IosCamerasLocators.FIRST_ONLINE_STATUS));
        verifyContextMenuDisplayed();
        Assert.assertTrue(WaitUtils.isPresent(driver, IosCamerasLocators.PREVIEW_TIMESTAMP),
                "Timestamp should be displayed in camera preview");
    }

    private void verifyAllContextMenuOptions() {
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.CAMERA_SETTINGS_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Camera Settings option should be displayed in context menu");
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.HISTORY_BROWSER_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "History Browser option should be displayed in context menu");
        Assert.assertTrue(WaitUtils.isVisible(driver, IosCamerasLocators.LIVE_VIDEO_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Live Video option should be displayed in context menu");
        boolean hasTurnOnOrOff = WaitUtils.isVisible(driver, IosCamerasLocators.TURN_ON_CAMERA_OPTION, TimeoutConstants.SHORT_TIMEOUT)
                || WaitUtils.isVisible(driver, IosCamerasLocators.TURN_OFF_CAMERA_OPTION, TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(hasTurnOnOrOff, "Expected either 'Turn on camera' or 'Turn off camera' option to be displayed");
    }

    private void verifyMenuCameraNameDisplayed() {
        String cameraName = WaitUtils.waitForVisibility(driver, IosCamerasLocators.MENU_CAMERA_NAME).getText();
        Assert.assertFalse(cameraName.isEmpty(), "Camera name should not be empty");
        logger.info("Camera name displayed: {}", cameraName);
    }
}
