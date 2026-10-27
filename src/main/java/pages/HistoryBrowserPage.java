package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import locators.CamerasLocators;
import locators.HistoryBrowserLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

public class HistoryBrowserPage {

    private static final Logger logger = LogManager.getLogger(HistoryBrowserPage.class);

    private static final List<Integer> TIMELINE_LEVEL_MINUTES = List.of(1, 5, 15, 30, 60);
    private static final int MIN_LEVEL_MINUTES = TIMELINE_LEVEL_MINUTES.get(0);
    private static final int MAX_LEVEL_MINUTES = TIMELINE_LEVEL_MINUTES.get(TIMELINE_LEVEL_MINUTES.size() - 1);
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    private static final int CONTROL_CLICK_ATTEMPTS = 2;
    private static final DateTimeFormatter CURSOR_POSITION_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<By> TIMELINE_CONTROLS = List.of(
            HistoryBrowserLocators.PREVIOUS_FRAME_BUTTON, HistoryBrowserLocators.PREVIOUS_EVENT_BUTTON,
            HistoryBrowserLocators.PLAY_PAUSE_BUTTON, HistoryBrowserLocators.NEXT_EVENT_BUTTON,
            HistoryBrowserLocators.NEXT_FRAME_BUTTON);

    private final AppiumDriver driver;

    public HistoryBrowserPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void openHistoryBrowserFromFirstCameraPreview() {
        WaitUtils.click(driver, HistoryBrowserLocators.FIRST_PREVIEW_THREE_DOTS);
        WaitUtils.click(driver, CamerasLocators.HISTORY_BROWSER_OPTION);
        verifyHistoryBrowserDisplayed();
    }

    public void verifyDefaultTimelineLevel() {
        verifyTimelineLevel(MIN_LEVEL_MINUTES);
    }

    public void pinchInAndVerifyMaxTimelineLevel() {
        List<Integer> levels = pinchUntilLevel(true, MAX_LEVEL_MINUTES);
        verifyLevelsMovedInOneDirection(levels, true);
        verifyTimelineLevel(MAX_LEVEL_MINUTES);
    }

    public void pinchOutAndVerifyMinTimelineLevel() {
        List<Integer> levels = pinchUntilLevel(false, MIN_LEVEL_MINUTES);
        verifyLevelsMovedInOneDirection(levels, false);
        verifyTimelineLevel(MIN_LEVEL_MINUTES);
    }

    /**
     * The app reopens a camera in the mode it was last viewed in, and sessions run noReset, so a previous test that
     * ended in the history browser makes this tap open the history browser instead of the live video.
     */
    public void openLiveVideoOfFirstCameraPreview() {
        WaitUtils.click(driver, HistoryBrowserLocators.FIRST_PREVIEW_CARD);
        List<By> screens = List.of(
                HistoryBrowserLocators.LIVE_STREAM_SCREEN, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN);
        By opened = WaitUtils.waitForFirstVisible(driver, screens, FrameworkConstants.EXPLICIT_WAIT)
                .orElseThrow(() -> new AssertionError(
                        "Neither the live video nor the history browser opened after tapping the camera preview"));
        if (opened.equals(HistoryBrowserLocators.HISTORY_BROWSER_SCREEN)) {
            logger.info("Camera reopened in the history browser (its last used mode); switching to live video");
            WaitUtils.click(driver, HistoryBrowserLocators.LIVE_BUTTON);
        }
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.LIVE_STREAM_SCREEN).isDisplayed(),
                "Live video of the selected camera should be displayed");
    }

    public void openHistoryBrowserFromLiveVideo() {
        WaitUtils.click(driver, HistoryBrowserLocators.HISTORY_BUTTON);
        verifyHistoryBrowserDisplayed();
    }

    public void verifyTimelineControlsDisplayed() {
        for (By control : TIMELINE_CONTROLS) {
            Assert.assertTrue(WaitUtils.waitForVisibility(driver, control).isDisplayed(),
                    "Timeline control " + control + " should be displayed below the timeline");
        }
    }

    public void clickNextFrameAndVerifyCursorMoved() {
        clickAndVerifyCursorMoved(HistoryBrowserLocators.NEXT_FRAME_BUTTON, true, "next frame");
    }

    public void clickPreviousFrameAndVerifyCursorMoved() {
        clickAndVerifyCursorMoved(HistoryBrowserLocators.PREVIOUS_FRAME_BUTTON, false, "previous frame");
    }

    public void clickNextEventAndVerifyCursorMoved() {
        clickAndVerifyCursorMoved(HistoryBrowserLocators.NEXT_EVENT_BUTTON, true, "next event");
    }

    public void clickPreviousEventAndVerifyCursorMoved() {
        clickAndVerifyCursorMoved(HistoryBrowserLocators.PREVIOUS_EVENT_BUTTON, false, "previous event");
    }

    /**
     * The timeline has no level label; its level is the time gap between two neighbouring rows. Rows run from
     * newest (top) to oldest, so the first row minus the second is the gap.
     */
    private static int gapMinutes(String newerRow, String olderRow) {
        int gapSeconds = Math.floorMod(
                LocalTime.parse(newerRow.trim()).toSecondOfDay() - LocalTime.parse(olderRow.trim()).toSecondOfDay(),
                SECONDS_PER_DAY);
        return gapSeconds / 60;
    }

    private static boolean isMonotonic(List<Integer> levels, boolean ascending) {
        for (int i = 1; i < levels.size(); i++) {
            boolean ordered = ascending ? levels.get(i) >= levels.get(i - 1) : levels.get(i) <= levels.get(i - 1);
            if (!ordered) {
                return false;
            }
        }
        return true;
    }

    private void verifyHistoryBrowserDisplayed() {
        Assert.assertTrue(
                WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN).isDisplayed(),
                "History browser of the selected camera should be displayed");
    }

    /**
     * The controls are visible and enabled before the player has finished loading, and a click in that window does
     * not seek, so the click is repeated once when the cursor has not moved. A direction assert still follows.
     */
    private void clickAndVerifyCursorMoved(By button, boolean forward, String description) {
        LocalDateTime before = readCursorPosition();
        for (int attempt = 1; attempt <= CONTROL_CLICK_ATTEMPTS; attempt++) {
            WaitUtils.click(driver, button);
            if (WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                    () -> !readCursorPosition().equals(before))) {
                break;
            }
            logger.warn("Cursor did not move after clicking {} (attempt {} of {})",
                    description, attempt, CONTROL_CLICK_ATTEMPTS);
        }
        LocalDateTime after = readCursorPosition();
        boolean moved = forward ? after.isAfter(before) : after.isBefore(before);
        Assert.assertTrue(moved,
                "Red cursor should move to the " + description + " (" + (forward ? "later" : "earlier")
                        + " than " + before + ") but is at " + after);
    }

    private LocalDateTime readCursorPosition() {
        return WaitUtils.retryOnStale(() -> LocalDateTime.parse(
                WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.CURRENT_POSITION).getText().trim(),
                CURSOR_POSITION_FORMAT));
    }

    private void verifyTimelineLevel(int expectedMinutes) {
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                () -> readLevelMinutes() == expectedMinutes);
        int actualMinutes = readLevelMinutes();
        Assert.assertEquals(actualMinutes, expectedMinutes,
                "Timeline level should be " + expectedMinutes + " minute(s) between rows but was " + actualMinutes);
    }

    private void verifyLevelsMovedInOneDirection(List<Integer> levels, boolean pinchIn) {
        Assert.assertTrue(TIMELINE_LEVEL_MINUTES.containsAll(levels),
                "Every timeline level read should be one of " + TIMELINE_LEVEL_MINUTES + " but the levels read were "
                        + levels);
        Assert.assertTrue(isMonotonic(levels, pinchIn),
                "Pinching " + (pinchIn ? "in" : "out") + " should only move the timeline "
                        + (pinchIn ? "up" : "down") + " a level but the levels read were " + levels);
    }

    /**
     * One pinch can move the timeline by more than one level, so the gesture is repeated until the target shows,
     * at most once per level. Returns every level read, starting with the one before the first pinch.
     */
    private List<Integer> pinchUntilLevel(boolean pinchIn, int targetMinutes) {
        List<Integer> levels = new ArrayList<>();
        int current = readLevelMinutes();
        levels.add(current);
        for (int pinches = 0; current != targetMinutes && pinches < TIMELINE_LEVEL_MINUTES.size(); pinches++) {
            WebElement timeline = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_LIST);
            if (pinchIn) {
                GestureUtils.pinchIn(driver, timeline);
            } else {
                GestureUtils.pinchOut(driver, timeline);
            }
            int previous = current;
            WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT, () -> readLevelMinutes() != previous);
            current = readLevelMinutes();
            levels.add(current);
        }
        logger.info("Timeline levels read while pinching {}: {}", pinchIn ? "in" : "out", levels);
        return levels;
    }

    private int readLevelMinutes() {
        return WaitUtils.retryOnStale(() -> {
            List<WebElement> rows = WaitUtils.findElements(driver, HistoryBrowserLocators.TIMELINE_ROW_TIMESTAMPS);
            if (rows.size() < 2) {
                throw new NoSuchElementException(
                        "Timeline should show at least two time rows to read its level but showed " + rows.size());
            }
            return gapMinutes(rows.get(0).getText(), rows.get(1).getText());
        });
    }
}
