package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
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

    /** The menu shows the frame time in the account's time format: 12 hour with AM/PM, or 24 hour. */
    private static final List<DateTimeFormatter> FRAME_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH), DateTimeFormatter.ofPattern("HH:mm:ss"));

    private final AppiumDriver driver;
    private final CameraSearchFilter cameraSearchFilter;

    public CamerasPage(AppiumDriver driver) {
        this.driver = driver;
        this.cameraSearchFilter = new CameraSearchFilter(
            driver,
            CamerasLocators.SEARCH_BAR,
            CamerasLocators.SEARCH_PLACEHOLDER,
            CamerasLocators.CAMERA_COUNTER,
            CamerasLocators.FIRST_CAMERA_IN_LIST,
            CamerasLocators.EMPTY_STATE_MESSAGE
        );
    }

    /** Closes the camera context menu; does nothing when none is open. */
    public void dismissContextMenu() {
        CameraContextMenu.dismiss(driver);
    }

    /** The search bar, camera counter and filtered list of this screen. */
    public CameraSearchFilter cameraSearchFilter() {
        return cameraSearchFilter;
    }

    public void clickCamerasItem() {
        WaitUtils.click(driver, CamerasLocators.CAMERAS_ITEM);
    }

    public void verifyCameraListDisplayed() {
        WaitUtils.assertVisible(driver, CamerasLocators.CAMERA_LIST_CONTAINER, "Camera list should be displayed");
        // Verify we're on cameras screen (not bridges screen) using search placeholder
        WaitUtils.assertVisible(driver, CamerasLocators.SEARCH_PLACEHOLDER, TimeoutConstants.SHORT_TIMEOUT,
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
        WaitUtils.assertVisible(driver, CamerasLocators.CONTEXT_MENU_CONTAINER,
                "Context menu should be displayed in the foreground with blurred background");
    }

    public void verifyCameraSettingsOptionDisplayed() {
        WaitUtils.assertVisible(driver, CamerasLocators.CAMERA_SETTINGS_OPTION,
                "Camera Settings option should be displayed in context menu");
    }

    public void verifyHistoryBrowserOptionDisplayed() {
        WaitUtils.assertVisible(driver, CamerasLocators.HISTORY_BROWSER_OPTION,
                "History Browser option should be displayed in context menu");
    }

    public void verifyLiveVideoOptionDisplayed() {
        WaitUtils.assertVisible(driver, CamerasLocators.LIVE_VIDEO_OPTION,
                "Live Video option should be displayed in context menu");
    }

    public void verifyCameraPreviewDisplayed() {
        WaitUtils.assertVisible(driver, CamerasLocators.CAMERA_PREVIEW,
                "Camera preview should be displayed for the selected camera");
    }

    public void verifyCameraNameDisplayed() {
        WaitUtils.assertVisible(driver, CamerasLocators.CAMERA_NAME_IN_PREVIEW, TimeoutConstants.SHORT_TIMEOUT,
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
        WaitUtils.assertVisible(driver, CamerasLocators.TIMESTAMP, "Timestamp should be displayed in camera preview");
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

    // ---- step-level groups ----------------------------------------------------------------

    /** Step "Tap on Cameras item": opens the cameras list and waits for it. */
    public void openCamerasList() {
        clickCamerasItem();
        verifyCameraListDisplayed();
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

    /**
     * The time of the frame the open context menu of an online camera shows, in the account's 12 or 24 hour format
     * ("07:15:10 AM" or "19:15:10").
     */
    public LocalTime readFrameTimeInContextMenu() {
        String shown = WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_FRAME_TIME_IN_PREVIEW)
                .getText().trim();
        for (DateTimeFormatter format : FRAME_TIME_FORMATS) {
            try {
                return LocalTime.parse(shown, format);
            } catch (DateTimeParseException e) {
                logger.debug("Frame time '{}' is not in format {}", shown, format);
            }
        }
        throw new AssertionError("Frame time '" + shown + "' in the context menu is neither 'hh:mm:ss a' nor "
                + "'HH:mm:ss'; check the account's time format");
    }

    public void verifyCanInteractWithFilteredCamera() {
        CameraContextMenu.longPressFirstCamera(driver, CamerasLocators.FIRST_CAMERA_IN_LIST);
        verifyContextMenuDisplayed();
        dismissContextMenu();
    }

    public void clickCameraSettingsOption() {
        WaitUtils.click(driver, CamerasLocators.CAMERA_SETTINGS_OPTION);
    }

    public void openContextMenuOfCameraNamed(String name) {
        By card = CamerasLocators.cameraCardWithName(name);
        Assert.assertTrue(scrollToCameraRow(card), "Camera '" + name + "' should be in the cameras list");
        GestureUtils.longPressUntilVisible(driver, card, CamerasLocators.CONTEXT_MENU_CONTAINER);
        verifyContextMenuDisplayed();
    }

    /**
     * iOS keeps the list's scroll position when the screen is left and reopened, and the search only scrolls down, so
     * it starts from the top. A row that is already on screen is left where it is: the scroll that centres a found
     * row (see scrollUntilElementIsVisible) would push a row near the top out of view.
     */
    private boolean scrollToCameraRow(By card) {
        if (!PlatformLocator.isIos()) {
            return scrollUntilElementIsVisible(card);
        }
        GestureUtils.scrollToTop(driver);
        return WaitUtils.isVisible(driver, card, TimeoutConstants.SHORT_TIMEOUT) || scrollUntilElementIsVisible(card);
    }

    public boolean hasOnlineCamera() {
        GestureUtils.scrollToTop(driver);
        return scrollToFirstOnlineCamera();
    }

    /**
     * iOS keeps showing a camera's old name in the list after a rename, until the list is opened again, and the
     * renamed camera then sits at its new place in the sorted list, so the list is scrolled to find it.
     */
    public void verifyCameraListedWithName(String name) {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, CamerasLocators.LIST_BACK_BUTTON);
            openCamerasList();
        }
        Assert.assertTrue(scrollUntilElementIsVisible(CamerasLocators.cameraCardWithName(name)),
                "Cameras list should show the renamed camera '" + name + "'");
    }

    public boolean hasSecondOnlineCamera() {
        GestureUtils.scrollToTop(driver);
        return scrollUntilElementIsVisible(CamerasLocators.SECOND_ONLINE_CAMERA);
    }

    public void longPressSecondOnlineCameraAndVerifyContextMenu() {
        GestureUtils.longPressUntilVisible(driver, CamerasLocators.SECOND_ONLINE_CAMERA,
                CamerasLocators.CONTEXT_MENU_CONTAINER);
        verifyContextMenuDisplayed();
        verifyAllContextMenuOptions(true);
        verifyCameraPreviewDisplayed();
    }

}
