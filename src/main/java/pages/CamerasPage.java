package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.CamerasLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

public class CamerasPage {

    private static final Logger logger = LogManager.getLogger(CamerasPage.class);
    private final AppiumDriver driver;
    private final CameraSearchHelper searchHelper;

    public static final String STATUS_ONLINE = "Online";
    public static final String STATUS_OFFLINE = "Offline";
    public static final String STATUS_INVALID_CREDENTIALS = "Invalid Credentials";
    public static final String STATUS_DEVICE_ERROR = "Device error";
    public static final String STATUS_CONNECTIVITY_ISSUE = "Connectivity issue";
    public static final String STATUS_TURNED_OFF = "Turned off";
    public static final String STATUS_CAMERA_ERROR = "Camera error";
    // Status of the camera pressed by longPressFirstOfflineOrErrorCamera: true = "Offline", false = "Camera error".
    private boolean pressedCameraIsOffline;

    private static final String[] CAMERA_STATUSES = {
        STATUS_ONLINE, STATUS_OFFLINE, STATUS_INVALID_CREDENTIALS,
        STATUS_DEVICE_ERROR, STATUS_CONNECTIVITY_ISSUE, STATUS_TURNED_OFF, STATUS_CAMERA_ERROR
    };

    public CamerasPage(AppiumDriver driver) {
        this.driver = driver;
        this.searchHelper = new CameraSearchHelper(
            driver,
            CamerasLocators.SEARCH_BAR,
            CamerasLocators.SEARCH_PLACEHOLDER,
            CamerasLocators.CAMERA_COUNTER,
            CamerasLocators.FIRST_CAMERA_IN_LIST,
            CamerasLocators.EMPTY_STATE_MESSAGE
        );
    }

    public void clickCamerasItem() {
        WaitUtils.click(driver, CamerasLocators.CAMERAS_ITEM);
    }

    public void verifyCameraListDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_LIST_CONTAINER).isDisplayed(),
                "Camera list should be displayed");
        // Verify we're on cameras screen (not bridges screen) using search placeholder
        Assert.assertTrue(WaitUtils.isVisible(driver, CamerasLocators.SEARCH_PLACEHOLDER, TimeoutConstants.SHORT_TIMEOUT),
                "Camera search placeholder should be visible on cameras screen");
        Assert.assertTrue(scrollToFirstCamera(), "Could not find any camera in the list");
    }

    public boolean scrollToFirstOfflineOrErrorCamera() {
        logger.info("Looking for first offline or error camera");
        return scrollUntilElementIsVisible(CamerasLocators.FIRST_OFFLINE_OR_ERROR_CAMERA);
    }

    public void longPressFirstOfflineOrErrorCamera() {
        boolean found = scrollToFirstOfflineOrErrorCamera();
        Assert.assertTrue(found, "Could not find an offline or error camera even after scrolling");
        pressedCameraIsOffline = isOfflineCamera(
                WaitUtils.waitForVisibility(driver, CamerasLocators.FIRST_OFFLINE_OR_ERROR_CAMERA));
        GestureUtils.longPressUntilVisible(driver, CamerasLocators.FIRST_OFFLINE_OR_ERROR_CAMERA,
                CamerasLocators.CONTEXT_MENU_CONTAINER);
        logger.info("Long pressed on offline/error camera");
    }

    /**
     * The list item found by FIRST_OFFLINE_OR_ERROR_CAMERA is either an "Offline" or a "Camera error" camera,
     * whichever comes first in the list. Their menus differ (see verifyAllContextMenuOptions), so the status is
     * read before the press. On iOS that locator is the status label itself; on Android it is the whole card.
     */
    private boolean isOfflineCamera(WebElement camera) {
        if (PlatformLocator.isIos()) {
            return STATUS_OFFLINE.equals(camera.getAttribute("name"));
        }
        return !camera.findElements(CamerasLocators.OFFLINE_STATUS_IN_CAMERA_CARD).isEmpty();
    }

    public boolean scrollToFirstOnlineCamera() {
        logger.info("Looking for first online camera");
        return scrollUntilElementIsVisible(CamerasLocators.FIRST_ONLINE_CAMERA);
    }

    /**
     * Android scrolls until the row is in the hierarchy. iOS keeps rows just outside the screen in the hierarchy, so
     * it scrolls until the row is actually visible (and nudges it away from the screen edge).
     */
    private boolean scrollUntilElementIsVisible(By locator) {
        return PlatformLocator.isIos()
                ? GestureUtils.scrollUntilVisible(driver, locator)
                : GestureUtils.scrollUntilPresent(driver, locator);
    }

    public boolean scrollToFirstCamera() {
        // The list first renders skeleton rows with "0 cameras"; swiping through that placeholder
        // exhausts the scroll budget before any data arrives (BrowserStack build #9).
        if (WaitUtils.isVisible(driver, CamerasLocators.FIRST_CAMERA_IN_LIST, TimeoutConstants.LIST_LOAD_TIMEOUT)) {
            return true;
        }
        return GestureUtils.scrollUntilPresent(driver, CamerasLocators.FIRST_CAMERA_IN_LIST);
    }

    public void verifyFirstOnlineCameraFound() {
        GestureUtils.scrollToTop(driver);
        Assert.assertTrue(scrollToFirstOnlineCamera(),
                "Could not find an online camera even after scrolling");
    }

    public void longPressFirstOnlineCamera() {
        GestureUtils.longPressUntilVisible(driver, CamerasLocators.FIRST_ONLINE_CAMERA,
                CamerasLocators.CONTEXT_MENU_CONTAINER);
        logger.info("Long pressed on online camera");
    }

    public void verifyContextMenuDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.CONTEXT_MENU_CONTAINER).isDisplayed(),
                "Context menu should be displayed in the foreground with blurred background");
    }

    public void dismissContextMenu() {
        searchHelper.dismissContextMenu();
    }

    public void verifyCameraSettingsOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_SETTINGS_OPTION).isDisplayed(),
                "Camera Settings option should be displayed in context menu");
    }

    public void verifyHistoryBrowserOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.HISTORY_BROWSER_OPTION).isDisplayed(),
                "History Browser option should be displayed in context menu");
    }

    public void verifyLiveVideoOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.LIVE_VIDEO_OPTION).isDisplayed(),
                "Live Video option should be displayed in context menu");
    }

    public void verifyCameraPreviewDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_PREVIEW).isDisplayed(),
                "Camera preview should be displayed for the selected camera");
    }

    public void verifyCameraNameDisplayed() {
        Assert.assertTrue(WaitUtils.isVisible(driver, CamerasLocators.CAMERA_NAME_IN_PREVIEW, TimeoutConstants.SHORT_TIMEOUT),
                "Camera name should be visible in the preview");
        String cameraName = WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_NAME_IN_PREVIEW).getText();
        Assert.assertFalse(cameraName.isEmpty(), "Camera name should not be empty");
        if (cameraName.contains("...")) {
            logger.info("Camera name contains ellipsis (lengthy name): {}", cameraName);
        } else {
            logger.info("Camera name displayed: {}", cameraName);
        }
    }

    public void verifyTimestampDisplayed() {
        if (PlatformLocator.isIos()) {
            // iOS: the preview floats in its own layer, so its timestamp is in the hierarchy but not "visible".
            Assert.assertTrue(WaitUtils.isPresent(driver, CamerasLocators.TIMESTAMP),
                    "Timestamp should be displayed in camera preview");
            return;
        }
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.TIMESTAMP).isDisplayed(),
                "Timestamp should be displayed in camera preview");
    }

    public boolean isDeviceStatusDisplayed(String status) {
        By statusLocator;
        switch (status) {
            case STATUS_ONLINE:
                statusLocator = CamerasLocators.ONLINE_STATUS;
                break;
            case STATUS_OFFLINE:
                statusLocator = CamerasLocators.OFFLINE_STATUS;
                break;
            case STATUS_INVALID_CREDENTIALS:
                statusLocator = CamerasLocators.INVALID_CREDENTIALS_STATUS;
                break;
            case STATUS_DEVICE_ERROR:
                statusLocator = CamerasLocators.DEVICE_ERROR_STATUS;
                break;
            case STATUS_CONNECTIVITY_ISSUE:
                statusLocator = CamerasLocators.CONNECTIVITY_ISSUE_STATUS;
                break;
            case STATUS_TURNED_OFF:
                statusLocator = CamerasLocators.TURNED_OFF_STATUS;
                break;
            case STATUS_CAMERA_ERROR:
                statusLocator = CamerasLocators.CAMERA_ERROR_STATUS;
                break;
            default:
                return false;
        }
        // iOS: the rows behind an open context menu are in the hierarchy but not reported as visible. This is a
        // weaker check than on Android: the label could belong to another row, not the one that was pressed.
        return PlatformLocator.isIos()
                ? WaitUtils.isPresent(driver, statusLocator)
                : WaitUtils.isVisible(driver, statusLocator, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void verifyCameraStatusAndIndicators() {
        boolean statusFound = false;
        for (String status : CAMERA_STATUSES) {
            if (isDeviceStatusDisplayed(status)) {
                logger.info("Camera status displayed: {}", status);
                statusFound = true;
                break;
            }
        }
        Assert.assertTrue(statusFound, "Expected at least one status indicator to be displayed");
    }

    /**
     * "Camera settings", "History browser" and "Live video" are in every camera's menu. "Turn on camera" and
     * "Turn off camera" are only there for a camera that can be switched, which an "Offline" camera is and a
     * "Camera error" camera is not (read from the menus of both on iOS: an offline camera's menu also has
     * "Replace camera"; a camera in error shows only the three basic options).
     */
    public void verifyAllContextMenuOptions(boolean turnOnOrOffExpected) {
        verifyCameraSettingsOptionDisplayed();
        verifyHistoryBrowserOptionDisplayed();
        verifyLiveVideoOptionDisplayed();
        if (!turnOnOrOffExpected) {
            logger.info("Camera is in error: the 'Turn on/off camera' options are not expected in its menu");
            return;
        }
        boolean hasTurnOnOrOff = WaitUtils.isVisible(driver, CamerasLocators.TURN_ON_CAMERA_OPTION, TimeoutConstants.SHORT_TIMEOUT) ||
                                 WaitUtils.isVisible(driver, CamerasLocators.TURN_OFF_CAMERA_OPTION, TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(hasTurnOnOrOff, "Expected either 'Turn on camera' or 'Turn off camera' option to be displayed");
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

    // ---- step-level groups ----------------------------------------------------------------

    /** Step "Tap on Cameras item": opens the cameras list and waits for it. */
    public void openCamerasList() {
        clickCamerasItem();
        verifyCameraListDisplayed();
    }

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

    /** Long presses the first offline or error camera in the list and checks the context menu: options, preview, name and status. */
    public void verifyFirstOfflineOrErrorCameraContextMenu() {
        longPressFirstOfflineOrErrorCamera();
        verifyContextMenuDisplayed();
        verifyAllContextMenuOptions(pressedCameraIsOffline);
        verifyCameraPreviewDisplayed();
        verifyCameraNameDisplayed();
        verifyCameraStatusAndIndicators();
    }

    /** Closes the menu, finds the first online camera, long presses it and checks the timestamp. */
    public void verifyFirstOnlineCameraContextMenu() {
        dismissContextMenu();
        openContextMenuOfFirstOnlineCamera();
        verifyTimestampDisplayed();
    }

    /** Long presses the first online camera and checks the context menu: foreground menu, options and preview. */
    public void longPressFirstOnlineCameraAndVerifyContextMenu() {
        openContextMenuOfFirstOnlineCamera();
        verifyAllContextMenuOptions(true);
        verifyCameraPreviewDisplayed();
    }

    private void openContextMenuOfFirstOnlineCamera() {
        verifyFirstOnlineCameraFound();
        longPressFirstOnlineCamera();
        verifyContextMenuDisplayed();
    }

    /** The camera's name as the open context menu shows it, which the history browser must show again. */
    public String readCameraNameInContextMenu() {
        return WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_NAME_IN_PREVIEW).getText();
    }

    public void verifyCanInteractWithFilteredCamera() {
        longPressFirstCamera();
        verifyContextMenuDisplayed();
        dismissContextMenu();
    }

    public boolean isEmptyStateDisplayed() {
        return searchHelper.isEmptyStateDisplayed();
    }

    public void verifyEmptyStateDisplayed() {
        searchHelper.verifyEmptyStateDisplayed();
    }
}
