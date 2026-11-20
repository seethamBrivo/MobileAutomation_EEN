package pages;

import constants.GestureConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import locators.CamerasLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.CameraCardPageSource;
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
    public static final String STATUS_UNKNOWN = "Unknown";
    public static final String STATUS_NO_PERMISSION = "No permission";
    // Status of the camera pressed by longPressFirstOfflineOrErrorCamera: true = "Offline", false = "Camera error".
    private boolean pressedCameraIsOffline;

    private static final String[] CAMERA_STATUSES = {
        STATUS_ONLINE, STATUS_OFFLINE, STATUS_INVALID_CREDENTIALS,
        STATUS_DEVICE_ERROR, STATUS_CONNECTIVITY_ISSUE, STATUS_TURNED_OFF, STATUS_CAMERA_ERROR
    };

    /**
     * The statuses a camera card may show: the ones above and "Unknown", which the test case lists too, and
     * "No permission", which the app shows for a camera the user may not see and the test case does not list.
     */
    private static final List<String> CARD_STATUSES = Stream.concat(Arrays.stream(CAMERA_STATUSES),
            Stream.of(STATUS_UNKNOWN, STATUS_NO_PERMISSION)).toList();

    private static final List<String> FILTER_CHIPS = List.of(
            "Connection", "Status", "Bridges", "Sites", "Layouts", "Tags", "Multi-cameras");

    /** The filter row holds 7 chips and about 5 fit on screen; one swipe reaches the end, more is headroom. */
    private static final int FILTER_ROW_MAX_SWIPES = 4;

    /** The filter row is swiped from 80% to 20% of the width: far from the edges, where Android's back gesture is. */
    private static final double FILTER_ROW_SWIPE_START = 0.8;
    private static final double FILTER_ROW_SWIPE_END = 0.2;

    /** The menu shows the frame time in the account's time format: 12 hour with AM/PM, or 24 hour. */
    private static final List<DateTimeFormatter> FRAME_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH), DateTimeFormatter.ofPattern("HH:mm:ss"));

    /** A tag row is a few tags wide; if its last tag is not reached after this many swipes, the row is not moving. */
    private static final int TAG_ROW_MAX_SWIPES = 3;

    /** The next page loads as the end of the list nears; if it has not after this many swipes, it is not coming. */
    private static final int NEXT_PAGE_MAX_SWIPES = 8;

    /**
     * Swiping back down covers 45% of the screen, a swipe up 60%, and a scroll's momentum differs between the two,
     * so going back may take about twice as many swipes as going down.
     */
    private static final int BACK_SWIPES_PER_SWIPE_UP = 2;

    /** Headroom for the list having moved further than the swipes counted, so the first camera is not missed. */
    private static final int BACK_SWIPES_SPARE = 2;
    private static final Pattern LOADED_PAGES = Pattern.compile("(\\d+) pages?");

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
        // iOS: the time is in the hierarchy but reported as not visible, so it is read without a visibility wait. It
        // is read from the name: on iOS 26 the element is an "Other" with no value, and the name is the time itself.
        String shown = (PlatformLocator.isIos()
                ? WaitUtils.attributeOfPresent(
                        driver, CamerasLocators.TIMESTAMP, "name", TimeoutConstants.SHORT_TIMEOUT)
                : WaitUtils.waitForVisibility(driver, CamerasLocators.CAMERA_FRAME_TIME_IN_PREVIEW).getText())
                .trim();
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

    /**
     * Why the named camera cannot be used, or empty when it is listed and "Online" or "Turned off" (an earlier run
     * may have left it off; the test switches it on first). "Not listed" (wrong name) and "listed with another
     * status" (Offline, an error) are told apart, because they are fixed in different places.
     */
    public Optional<String> reasonCameraUnusable(String name) {
        if (!scrollToCameraRow(CamerasLocators.cameraCardWithName(name))) {
            return Optional.of("Camera '" + name + "' is not in the cameras list; check the test-data camera name");
        }
        if (!scrollToCameraRow(CamerasLocators.onlineCameraCardWithName(name))
                && !scrollToCameraRow(CamerasLocators.turnedOffCameraCardWithName(name))) {
            return Optional.of("Camera '" + name + "' is listed but its status is neither '" + STATUS_ONLINE
                    + "' nor '" + STATUS_TURNED_OFF + "'; the End User cannot switch it from here");
        }
        return Optional.empty();
    }

    /** For a camera the End User can switch: its menu has Turn on/off, which is checked. */
    public void longPressCameraNamedAndVerifyContextMenu(String name) {
        openContextMenuOfCameraNamed(name);
        verifyAllContextMenuOptions(true);
        verifyCameraPreviewDisplayed();
    }

    /**
     * The Dashboard's Cameras tile shows the cameras per status, which load from "0" and may not all arrive at
     * once; returns their sum once two reads in a row agree.
     */
    public int readCamerasTileTotal() {
        AtomicInteger total = new AtomicInteger();
        boolean shown = WaitUtils.pollUntil(driver, TimeoutConstants.LIST_LOAD_TIMEOUT, () -> {
            int sum = WaitUtils.textsOfOrEmpty(driver, CamerasLocators.CAMERAS_TILE_COUNTS,
                    TimeoutConstants.SHORT_TIMEOUT).stream().mapToInt(Integer::parseInt).sum();
            return sum > 0 && sum == total.getAndSet(sum);
        });
        Assert.assertTrue(shown, "Cameras tile on the Dashboard should show the number of cameras");
        return total.get();
    }

    public void verifyCounterShowsCameraCount(int expectedCount) {
        int shownCount = cameraSearchFilter.readCameraCountAndVerifyWording();
        Assert.assertEquals(shownCount, expectedCount,
                "Camera counter should match the " + expectedCount + " cameras on the Dashboard tile");
    }

    /**
     * Checks every card on screen: a name, then one of the listed statuses. A card scrolled in only
     * halfway may not have its status yet, so cards with fewer than two texts are skipped, and at least one must
     * be checked. The preview image has no label to find, so it is not checked.
     */
    public void verifyVisibleCameraCardsShowNameAndStatus() {
        List<List<String>> cards = visibleCardTexts();
        Assert.assertFalse(cards.isEmpty(), "At least one camera card should show a name and a status");
        for (List<String> texts : cards) {
            String name = texts.get(0);
            String status = texts.get(1);
            Assert.assertFalse(name.isEmpty(), "Camera card should show the camera's name");
            Assert.assertTrue(CARD_STATUSES.contains(status),
                    "Camera '" + name + "' should show one of " + CARD_STATUSES + " but showed '" + status + "'");
        }
        logger.info("Checked the name and status of {} camera cards", cards.size());
    }

    /**
     * Reads the filter chips from left to right, scrolling the row until no new chip shows up, and puts it back.
     */
    public void verifyFilterChipsInOrder() {
        if (PlatformLocator.isIos()) {
            assertChipsInOrder(WaitUtils.textsOf(driver, CamerasLocators.FILTER_CHIP_NAME));
            return;
        }
        List<String> chips = new ArrayList<>();
        int rowY = GestureUtils.getElementCenter(
                WaitUtils.waitForVisibility(driver, CamerasLocators.FILTER_CHIP_NAME)).y;
        int swipes = 0;
        while (true) {
            List<String> shown = WaitUtils.textsOf(driver, CamerasLocators.FILTER_CHIP_NAME);
            boolean newChip = false;
            for (String chip : shown) {
                if (!chips.contains(chip)) {
                    chips.add(chip);
                    newChip = true;
                }
            }
            if (!newChip || swipes == FILTER_ROW_MAX_SWIPES) {
                break;
            }
            GestureUtils.swipeHorizontal(driver, rowY, FILTER_ROW_SWIPE_START, FILTER_ROW_SWIPE_END);
            swipes++;
            // The row has moved once its first chip changes; at the end of the row it does not move, and the
            // next read adds nothing, which ends the loop.
            String firstChipBefore = shown.get(0);
            WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
                List<String> now = WaitUtils.textsOfOrEmpty(driver, CamerasLocators.FILTER_CHIP_NAME,
                        TimeoutConstants.SHORT_TIMEOUT);
                return !now.isEmpty() && !now.get(0).equals(firstChipBefore);
            });
        }
        for (int i = 0; i < swipes; i++) {
            GestureUtils.swipeHorizontal(driver, rowY, FILTER_ROW_SWIPE_END, FILTER_ROW_SWIPE_START);
        }
        assertChipsInOrder(chips);
    }

    /** Whether some camera shows a tag, scrolling down the list to look for one. */
    public boolean hasCameraWithTag() {
        GestureUtils.scrollToTop(driver);
        if (!PlatformLocator.isIos()) {
            return scrollUntilElementIsVisible(CamerasLocators.FIRST_TAG);
        }
        // Looks for the tag that tapFirstTag will take, so a half-visible tag cannot end the search with no tag to tap.
        for (int swipes = 0; swipes <= GestureConstants.MAX_SCROLL_SWIPES; swipes++) {
            if (firstWholeTag().isPresent()) {
                return true;
            }
            if (swipes < GestureConstants.MAX_SCROLL_SWIPES) {
                GestureUtils.swipeUp(driver);
            }
        }
        return false;
    }

    /** Taps the first tag on screen and returns its name. */
    public String tapFirstTag() {
        if (PlatformLocator.isIos()) {
            return tapFirstWholeTag();
        }
        String tagName = WaitUtils.waitForVisibility(driver, CamerasLocators.FIRST_TAG_NAME).getText().trim();
        WaitUtils.click(driver, CamerasLocators.FIRST_TAG);
        logger.info("Tapped the tag '{}'", tagName);
        return tagName;
    }

    /** A tag cut off by a screen edge is tapped on its visible part only, which may miss; take a whole one. */
    private String tapFirstWholeTag() {
        WebElement tag = firstWholeTag().orElseGet(() -> {
            Assert.fail("A tag fully inside the screen should be listed under the cameras on screen");
            return null;
        });
        String tagName = tag.getText().trim();
        tag.click();
        logger.info("Tapped the tag '{}'", tagName);
        return tagName;
    }

    /** The first tag on screen that is inside the screen on every side and above the tab bar, if there is one. */
    private Optional<WebElement> firstWholeTag() {
        if (!WaitUtils.isVisible(driver, CamerasLocators.VISIBLE_TAGS, TimeoutConstants.SHORT_TIMEOUT)) {
            return Optional.empty();
        }
        int screenWidth = driver.manage().window().getSize().width;
        int bottomEdge = bottomUsableEdge();
        for (WebElement tag : WaitUtils.findElements(driver, CamerasLocators.VISIBLE_TAGS)) {
            Rectangle bounds = tag.getRect();
            if (bounds.x >= 0 && bounds.y >= 0 && bounds.x + bounds.width <= screenWidth
                    && bounds.y + bounds.height <= bottomEdge) {
                return Optional.of(tag);
            }
        }
        return Optional.empty();
    }

    /** The tab bar sits over the bottom of the list, so what is under it is on screen but cannot be touched. */
    private int bottomUsableEdge() {
        if (WaitUtils.isVisible(driver, CamerasLocators.TAB_BAR, TimeoutConstants.SHORT_TIMEOUT)) {
            return WaitUtils.waitForVisibility(driver, CamerasLocators.TAB_BAR).getRect().y;
        }
        return driver.manage().window().getSize().height;
    }

    /** After a tag was tapped: the counter shrank to some cameras and every card on screen carries the tag. */
    public void verifyCamerasFilteredByTag(String tagName, int camerasBeforeFilter) {
        // "Clear filter" appears with the filter; waiting for it keeps the old list from being read. The counter
        // cannot be the signal: it does not change when every camera carries the tag.
        WaitUtils.assertVisible(driver, CamerasLocators.CLEAR_FILTER_BUTTON, TimeoutConstants.FILTER_RESULT_TIMEOUT,
                "Clear filter should appear once the tag '" + tagName + "' is applied");
        int filtered = cameraSearchFilter.readCameraCountAndVerifyWording();
        Assert.assertTrue(filtered >= 1 && filtered <= camerasBeforeFilter,
                "Counter after filtering by tag '" + tagName + "' should be between 1 and " + camerasBeforeFilter
                        + " but was " + filtered);
        List<List<String>> cards = visibleCardTexts();
        Assert.assertFalse(cards.isEmpty(), "At least one camera card should be on screen after filtering by a tag");
        for (List<String> texts : cards) {
            List<String> tags = texts.subList(2, texts.size());
            Assert.assertTrue(tags.contains(tagName), "Camera '" + texts.get(0) + "' should carry the tag '"
                    + tagName + "' after filtering by it but has " + tags);
        }
    }

    /** The "Tags" chip moves to the front of the row and shows how many tags are selected. */
    public void verifyTagsFilterIsActive() {
        List<String> chips = WaitUtils.textsOf(driver, CamerasLocators.FILTER_CHIP_NAME);
        Assert.assertEquals(chips.get(0), "Tags",
                "The active Tags filter should be the first chip but the row was " + chips);
        String selected = WaitUtils.waitForVisibility(driver, CamerasLocators.ACTIVE_TAGS_FILTER_COUNT)
                .getText().trim();
        Assert.assertEquals(selected, "1", "The Tags filter should show 1 selected tag");
        logger.info("Filter chips with the tag filter applied: {}", chips);
    }

    /**
     * The texts of each card on screen: the name, the status, then the tags. A card scrolled in only
     * halfway may not have its status yet; it has fewer than two texts and is left out.
     */
    private List<List<String>> visibleCardTexts() {
        if (PlatformLocator.isIos()) {
            WaitUtils.waitForVisibility(driver, CamerasLocators.FIRST_CAMERA_IN_LIST);
            return CameraCardPageSource.visibleCards(WaitUtils.pageSource(driver));
        }
        List<List<String>> cards = new ArrayList<>();
        for (WebElement card : WaitUtils.findElements(driver, CamerasLocators.CAMERA_CARD)) {
            List<String> texts = card.findElements(CamerasLocators.TEXTS_IN_CAMERA_CARD).stream()
                    .map(text -> text.getText().trim()).toList();
            if (texts.size() >= 2) {
                cards.add(texts);
            }
        }
        return cards;
    }

    /** Cleanup: removes a tag filter if one is applied; asserts nothing. */
    public void clearFilterIfApplied() {
        if (WaitUtils.isVisible(driver, CamerasLocators.CLEAR_FILTER_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
            WaitUtils.click(driver, CamerasLocators.CLEAR_FILTER_BUTTON);
        }
    }

    /** Taps the 3-dots button of the first online camera and checks its menu. */
    public void openMenuWithThreeDotsOfFirstOnlineCameraAndVerifyOptions() {
        verifyFirstOnlineCameraFound();
        WaitUtils.click(driver, CamerasLocators.THREE_DOTS_OF_FIRST_ONLINE_CAMERA);
        verifyContextMenuDisplayed();
        verifyAllContextMenuOptions(true);
        if (PlatformLocator.isIos()) {
            verifyTurnOffIsTheActiveEntry();
        }
    }

    /**
     * iOS: swipes the tag row of a card whose tags run past the screen edge to its last tag and back. Returns
     * false when this platform automates the step but no card on screen has such a row, so the caller can say
     * the step did not run instead of reporting a pass; Android does not automate it and returns true.
     */
    public boolean verifyTagRowScrolls() {
        if (!PlatformLocator.isIos()) {
            logger.info("Scrolling a card's tags is automated on iOS only");
            return true;
        }
        int screenWidth = driver.manage().window().getSize().width;
        if (!WaitUtils.isVisible(driver, CamerasLocators.TAG_ROWS, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.warn("No card on screen shows tags; scrolling the tags was not checked");
            return false;
        }
        int bottomEdge = bottomUsableEdge();
        for (WebElement row : WaitUtils.findElements(driver, CamerasLocators.TAG_ROWS)) {
            // A swipe that starts over the tab bar scrolls nothing, so a row under it cannot be checked.
            if (rowCentreY(row) < bottomEdge && rightEdgeOfLastTag(row) > screenWidth) {
                scrollTagRowToEndAndBack(row, screenWidth);
                return true;
            }
        }
        logger.warn("No card on screen has tags beyond the screen edge; scrolling the tags was not checked");
        return false;
    }

    /**
     * iOS: scrolling down grows the list. The list's scroll bar states its height in pages ("3 pages"), so this
     * proves the list got taller, not how many cameras were added. Returns false, without scrolling, when the
     * whole list fits one screen: the account then has too few cameras for a next page, which is missing test
     * data, not a defect. Android does not automate the step and returns true. Scrolls back to the first camera.
     */
    public boolean verifyNextPageLoadsOnScroll() {
        if (!PlatformLocator.isIos()) {
            logger.info("Loading the next page of cameras is automated on iOS only");
            return true;
        }
        List<List<String>> cards = visibleCardTexts();
        Assert.assertFalse(cards.isEmpty(), "A camera card should be on screen before scrolling for the next page");
        String firstCamera = cards.get(0).get(0);
        int pagesBefore = loadedPages();
        if (pagesBefore <= 1) {
            logger.warn("The camera list fits one screen; loading the next page was not checked");
            return false;
        }
        int swipes = 0;
        boolean listGrew = false;
        while (!listGrew && swipes < NEXT_PAGE_MAX_SWIPES) {
            GestureUtils.swipeUp(driver);
            swipes++;
            // The next page arrives after the swipe, from the backend: wait for it before swiping on.
            listGrew = WaitUtils.pollUntil(driver, TimeoutConstants.FILTER_RESULT_TIMEOUT,
                    () -> readLoadedPages().orElse(0) > pagesBefore);
        }
        Assert.assertTrue(listGrew, "Scrolling down should load the next page of cameras, making the list taller "
                + "than " + pagesBefore + " screens, but it did not grow within " + swipes + " swipes");
        int pagesAfter = loadedPages();
        By firstCameraCard = CamerasLocators.cameraCardWithName(firstCamera);
        int backSwipes = 0;
        while (backSwipes < swipes * BACK_SWIPES_PER_SWIPE_UP + BACK_SWIPES_SPARE
                && !WaitUtils.isVisible(driver, firstCameraCard, TimeoutConstants.SHORT_TIMEOUT)) {
            GestureUtils.swipeDownInList(driver);
            backSwipes++;
        }
        WaitUtils.assertVisible(driver, firstCameraCard, TimeoutConstants.SHORT_TIMEOUT,
                "The list should be back at its first camera '" + firstCamera + "' after loading the next page");
        logger.info("The list grew from {} to {} screens after {} swipe(s)", pagesBefore, pagesAfter, swipes);
        return true;
    }

    private int loadedPages() {
        OptionalInt pages = readLoadedPages();
        Assert.assertTrue(pages.isPresent(), "The list's scroll bar should state its height in pages");
        return pages.getAsInt();
    }

    /** Empty while the scroll bar is not readable, e.g. mid-animation, so a poll can try again. */
    private OptionalInt readLoadedPages() {
        String name = WaitUtils.attributeOfPresent(driver, CamerasLocators.LIST_SCROLL_BAR, "name",
                TimeoutConstants.SHORT_TIMEOUT);
        Matcher pages = LOADED_PAGES.matcher(name);
        return pages.find() ? OptionalInt.of(Integer.parseInt(pages.group(1))) : OptionalInt.empty();
    }

    private void scrollTagRowToEndAndBack(WebElement row, int screenWidth) {
        int rowY = rowCentreY(row);
        String lastTag = WaitUtils.retryOnStale(() -> {
            List<WebElement> tags = row.findElements(CamerasLocators.TAGS_IN_ROW);
            return tags.get(tags.size() - 1).getText().trim();
        });
        int swipes = 0;
        boolean lastTagShown = false;
        while (!lastTagShown && swipes < TAG_ROW_MAX_SWIPES) {
            GestureUtils.swipeHorizontal(driver, rowY, FILTER_ROW_SWIPE_START, FILTER_ROW_SWIPE_END);
            swipes++;
            lastTagShown = WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT,
                    () -> rightEdgeOfLastTag(row) <= screenWidth);
        }
        Assert.assertTrue(lastTagShown, "The last tag '" + lastTag + "' of the card should scroll into view within "
                + TAG_ROW_MAX_SWIPES + " swipes of its tag row");
        for (int i = 0; i < swipes; i++) {
            GestureUtils.swipeHorizontal(driver, rowY, FILTER_ROW_SWIPE_END, FILTER_ROW_SWIPE_START);
        }
        Assert.assertTrue(WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
            List<WebElement> rowTags = row.findElements(CamerasLocators.TAGS_IN_ROW);
            return !rowTags.isEmpty() && rowTags.get(0).getRect().x >= 0;
        }),
                "The tag row should be back at its first tag after swiping it back");
        logger.info("Scrolled the tag row to its last tag '{}' in {} swipe(s) and back", lastTag, swipes);
    }

    private int rowCentreY(WebElement row) {
        return WaitUtils.retryOnStale(() -> {
            Rectangle bounds = row.getRect();
            return bounds.y + bounds.height / 2;
        });
    }

    private int rightEdgeOfLastTag(WebElement row) {
        return WaitUtils.retryOnStale(() -> {
            List<WebElement> tags = row.findElements(CamerasLocators.TAGS_IN_ROW);
            if (tags.isEmpty()) {
                return -1;
            }
            Rectangle bounds = tags.get(tags.size() - 1).getRect();
            return bounds.x + bounds.width;
        });
    }

    private void verifyTurnOffIsTheActiveEntry() {
        WaitUtils.assertVisible(driver, CamerasLocators.TURN_OFF_CAMERA_OPTION,
                "'Turn off camera' should be listed in the menu of an online camera");
        WaitUtils.assertVisible(driver, CamerasLocators.TURN_ON_CAMERA_OPTION,
                "'Turn on camera' should be listed, greyed out, in the menu of an online camera");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, CamerasLocators.TURN_OFF_CAMERA_OPTION).isEnabled(),
                "'Turn off camera' should be active in the menu of an online camera");
        Assert.assertFalse(WaitUtils.waitForVisibility(driver, CamerasLocators.TURN_ON_CAMERA_OPTION).isEnabled(),
                "'Turn on camera' should be greyed out in the menu of an online camera");
    }

    private void assertChipsInOrder(List<String> chips) {
        Assert.assertEquals(chips, FILTER_CHIPS,
                "Filter row should show the chips " + FILTER_CHIPS + " in that order but showed " + chips);
    }

}
