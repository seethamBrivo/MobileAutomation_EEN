package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
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
    private static final String[] CAMERA_STATUSES = {
        STATUS_ONLINE, STATUS_OFFLINE, STATUS_INVALID_CREDENTIALS,
        STATUS_DEVICE_ERROR, STATUS_CONNECTIVITY_ISSUE, STATUS_TURNED_OFF
    };
    private final By camerasItem = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    private final By cameraListContainer = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView");
    private final By firstCameraInList = AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]");
    private final By firstOfflineOrErrorCamera = AppiumBy.xpath("(//android.widget.TextView[@text='Offline' or @text='Camera error'])[1]/ancestor::android.view.View[@clickable='true'][1]");
    private final By firstOnlineCamera = AppiumBy.xpath("(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[@clickable='true'][1]");
    private final By contextMenuContainer = AppiumBy.xpath("//android.view.ViewGroup[.//android.widget.TextView[@text='Camera settings']]");
    private final By cameraSettingsOption = AppiumBy.xpath("//android.widget.TextView[@text=\"Camera settings\"]");
    private final By historyBrowserOption = AppiumBy.xpath("//android.widget.TextView[@text='History browser']");
    private final By liveVideoOption = AppiumBy.xpath("//android.widget.TextView[@text='Live video']");
    private final By turnOnCameraOption = AppiumBy.xpath("//android.widget.TextView[@text='Turn on camera']");
    private final By turnOffCameraOption = AppiumBy.xpath("//android.widget.TextView[@text='Turn off camera']");
    // TODO: Replace with testTag when mobile team adds an identifier to the preview overlay.
    // Positional: the preview container and its name label carry no content-desc/testTag yet.
    private final By cameraPreview = AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[2]");
    private final By cameraNameInPreview = AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[1]//android.widget.TextView[1]");
    private final By timestamp = AppiumBy.xpath("//android.widget.TextView[contains(@text, ':')]");
    private final By onlineStatus = AppiumBy.xpath("//android.widget.TextView[@text='Online']");
    private final By offlineStatus = AppiumBy.xpath("//android.widget.TextView[@text='Offline']");
    private final By invalidCredentialsStatus = AppiumBy.xpath("//android.widget.TextView[@text='Invalid Credentials']");
    private final By deviceErrorStatus = AppiumBy.xpath("//android.widget.TextView[@text='Device error']");
    private final By connectivityIssueStatus = AppiumBy.xpath("//android.widget.TextView[@text='Connectivity issue']");
    private final By turnedOffStatus = AppiumBy.xpath("//android.widget.TextView[@text='Turned off']");
    private final By searchBar = AppiumBy.xpath("//android.widget.EditText");
    private final By searchPlaceholder = AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]");
    private final By cameraCounter = AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]");
    private final By emptyStateMessage = AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.EMPTY_STATE_MESSAGE + "\"]");

    public CamerasPage(AppiumDriver driver) {
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

    public void clickCamerasItem() {
        WaitUtils.click(driver, camerasItem);
    }

    public void verifyCameraListDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, cameraListContainer).isDisplayed(),
                "Camera list should be displayed");
        // Verify we're on cameras screen (not bridges screen) using search placeholder
        Assert.assertTrue(WaitUtils.isVisible(driver, searchPlaceholder, TimeoutConstants.SHORT_TIMEOUT),
                "Camera search placeholder should be visible on cameras screen");
        Assert.assertTrue(scrollToFirstCamera(), "Could not find any camera in the list");
    }

    public boolean scrollToOfflineOrErrorCamera() {
        logger.info("Looking for first offline or error camera");
        return GestureUtils.scrollUntilPresent(driver, firstOfflineOrErrorCamera);
    }

    public void longPressOnCamera() {
        boolean found = scrollToOfflineOrErrorCamera();
        Assert.assertTrue(found, "Could not find an offline or error camera even after scrolling");
        WebElement camera = WaitUtils.waitForVisibility(driver, firstOfflineOrErrorCamera);
        GestureUtils.longPress(driver, camera);
        logger.info("Long pressed on offline/error camera");
    }

    public boolean scrollToOnlineCamera() {
        logger.info("Looking for first online camera");
        return GestureUtils.scrollUntilPresent(driver, firstOnlineCamera);
    }

    public boolean scrollToFirstCamera() {
        // The list first renders skeleton rows with "0 cameras"; swiping through that placeholder
        // exhausts the scroll budget before any data arrives (BrowserStack build #9).
        if (WaitUtils.isVisible(driver, firstCameraInList, TimeoutConstants.LIST_LOAD_TIMEOUT)) {
            return true;
        }
        return GestureUtils.scrollUntilPresent(driver, firstCameraInList);
    }

    public void verifyOnlineCameraFound() {
        GestureUtils.scrollToTop(driver);
        Assert.assertTrue(scrollToOnlineCamera(),
                "Could not find an online camera even after scrolling");
    }

    public void longPressOnOnlineCamera() {
        WebElement onlineCamera = WaitUtils.waitForVisibility(driver, firstOnlineCamera);
        GestureUtils.longPress(driver, onlineCamera);
        logger.info("Long pressed on online camera");
    }

    public void verifyContextMenuDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, contextMenuContainer).isDisplayed(),
                "Context menu should be displayed in the foreground with blurred background");
    }

    public void dismissContextMenu() {
        searchHelper.dismissContextMenu();
    }

    public void verifyCameraSettingsOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, cameraSettingsOption).isDisplayed(),
                "Camera Settings option should be displayed in context menu");
    }

    public void verifyHistoryBrowserOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, historyBrowserOption).isDisplayed(),
                "History Browser option should be displayed in context menu");
    }

    public void verifyLiveVideoOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, liveVideoOption).isDisplayed(),
                "Live Video option should be displayed in context menu");
    }

    public void verifyCameraPreviewDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, cameraPreview).isDisplayed(),
                "Camera preview should be displayed for the selected camera");
    }

    public void verifyCameraNameDisplayed() {
        Assert.assertTrue(WaitUtils.isVisible(driver, cameraNameInPreview, TimeoutConstants.SHORT_TIMEOUT),
                "Camera name should be visible in the preview");
        String cameraName = WaitUtils.waitForVisibility(driver, cameraNameInPreview).getText();
        Assert.assertFalse(cameraName.isEmpty(), "Camera name should not be empty");
        if (cameraName.contains("...")) {
            logger.info("Camera name contains ellipsis (lengthy name): {}", cameraName);
        } else {
            logger.info("Camera name displayed: {}", cameraName);
        }
    }

    public void verifyTimestampDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, timestamp).isDisplayed(),
                "Timestamp should be displayed in camera preview");
    }

    public boolean isDeviceStatusDisplayed(String status) {
        By statusLocator;
        switch (status) {
            case STATUS_ONLINE:
                statusLocator = onlineStatus;
                break;
            case STATUS_OFFLINE:
                statusLocator = offlineStatus;
                break;
            case STATUS_INVALID_CREDENTIALS:
                statusLocator = invalidCredentialsStatus;
                break;
            case STATUS_DEVICE_ERROR:
                statusLocator = deviceErrorStatus;
                break;
            case STATUS_CONNECTIVITY_ISSUE:
                statusLocator = connectivityIssueStatus;
                break;
            case STATUS_TURNED_OFF:
                statusLocator = turnedOffStatus;
                break;
            default:
                return false;
        }
        return WaitUtils.isVisible(driver, statusLocator, TimeoutConstants.SHORT_TIMEOUT);
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

    public void verifyAllContextMenuOptions() {
        verifyCameraSettingsOptionDisplayed();
        verifyHistoryBrowserOptionDisplayed();
        verifyLiveVideoOptionDisplayed();
        boolean hasTurnOnOrOff = WaitUtils.isVisible(driver, turnOnCameraOption, TimeoutConstants.SHORT_TIMEOUT) ||
                                 WaitUtils.isVisible(driver, turnOffCameraOption, TimeoutConstants.SHORT_TIMEOUT);
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
