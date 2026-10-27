package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import org.testng.SkipException;
import utils.GestureUtils;
import utils.WaitUtils;

public class HistoryBrowserPage {

    private static final Logger logger = LogManager.getLogger(HistoryBrowserPage.class);

    private static final List<Integer> TIMELINE_LEVEL_MINUTES = List.of(1, 5, 15, 30, 60);
    private static final int MIN_LEVEL_MINUTES = TIMELINE_LEVEL_MINUTES.get(0);
    private static final int MAX_LEVEL_MINUTES = TIMELINE_LEVEL_MINUTES.get(TIMELINE_LEVEL_MINUTES.size() - 1);
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    private static final DateTimeFormatter CURSOR_POSITION_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<By> TIMELINE_CONTROLS = List.of(
            HistoryBrowserLocators.PREVIOUS_FRAME_BUTTON, HistoryBrowserLocators.PREVIOUS_EVENT_BUTTON,
            HistoryBrowserLocators.PLAY_PAUSE_BUTTON, HistoryBrowserLocators.NEXT_EVENT_BUTTON,
            HistoryBrowserLocators.NEXT_FRAME_BUTTON);

    /** The level is the smallest gap among the first rows, so a row without a label cannot double it. */
    private static final int LEVEL_SAMPLE_ROWS = 4;

    /** The controls test clicks "previous event" twice before "next event". */
    private static final int MIN_EVENTS_FOR_STEPPING = 2;

    /** One change of the cursor is tolerated while the player settles; a second one means it is playing. */
    private static final int PAUSE_CHECKS = 2;

    private static final int CONTROL_CLICK_ATTEMPTS = 2;

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
            WaitUtils.click(driver, HistoryBrowserLocators.NAVIGATION_BUTTON);
        }
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.LIVE_STREAM_SCREEN).isDisplayed(),
                "Live video of the selected camera should be displayed");
    }

    public void openHistoryBrowserFromLiveVideo() {
        WaitUtils.click(driver, HistoryBrowserLocators.NAVIGATION_BUTTON);
        verifyHistoryBrowserDisplayed();
    }

    /**
     * Cleanup step: the app reopens a camera in its last used mode, so a test that ends in the history browser
     * switches back to live video for whatever taps the camera next. Never asserts.
     */
    public void returnToLiveVideoIfInHistoryBrowser() {
        if (WaitUtils.isVisible(
                driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Switching the camera back to live video");
            WaitUtils.click(driver, HistoryBrowserLocators.NAVIGATION_BUTTON);
        }
    }

    public void verifyTimelineControlsDisplayed() {
        for (By control : TIMELINE_CONTROLS) {
            Assert.assertTrue(WaitUtils.waitForVisibility(driver, control).isDisplayed(),
                    "Timeline control " + control + " should be displayed below the timeline");
        }
    }

    /** Skips the test, with the reason, when the camera has too few events to step between. */
    public void requireEventsOnTimeline() {
        int events = 0;
        if (WaitUtils.isVisible(driver, HistoryBrowserLocators.TIMELINE_EVENT_CARDS, TimeoutConstants.SHORT_TIMEOUT)) {
            events = WaitUtils.findElements(driver, HistoryBrowserLocators.TIMELINE_EVENT_CARDS).size();
        }
        if (events < MIN_EVENTS_FOR_STEPPING) {
            throw new SkipException("The first camera needs at least " + MIN_EVENTS_FOR_STEPPING
                    + " events on its timeline to step between events but " + events + " are shown");
        }
    }

    /**
     * Stepping with the buttons only proves something when the cursor does not move by itself, so a cursor that keeps
     * advancing (playback running) fails here with that reason instead of failing a step check later.
     */
    public void verifyPlaybackPaused() {
        LocalDateTime position = readCursorPosition();
        for (int check = 1; check <= PAUSE_CHECKS; check++) {
            LocalDateTime observed = position;
            if (!WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT,
                    () -> !readCursorPosition().equals(observed))) {
                return;
            }
            position = readCursorPosition();
        }
        Assert.fail("Playback should be paused when the history browser opens but the cursor kept advancing (now at "
                + position + ")");
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
     * The timeline has no level label; its level is the time gap between two neighbouring rows. Rows are not
     * guaranteed to come back newest first, so the smaller of the two directions is the gap, and taking it modulo a
     * day keeps it right when the rows straddle midnight.
     */
    private static int gapMinutes(String firstRow, String secondRow) {
        int difference = parseTimeLabel(firstRow).toSecondOfDay() - parseTimeLabel(secondRow).toSecondOfDay();
        int gapSeconds = Math.min(
                Math.floorMod(difference, SECONDS_PER_DAY), Math.floorMod(-difference, SECONDS_PER_DAY));
        return gapSeconds / 60;
    }

    private static LocalTime parseTimeLabel(String label) {
        try {
            return LocalTime.parse(label.trim());
        } catch (DateTimeParseException e) {
            throw new AssertionError("Timeline row label '" + label + "' is not a 24-hour HH:mm:ss time; "
                    + "check the account's time format", e);
        }
    }

    private static LocalDateTime parseCursorLabel(String label) {
        try {
            return LocalDateTime.parse(label.trim(), CURSOR_POSITION_FORMAT);
        } catch (DateTimeParseException e) {
            throw new AssertionError("Cursor time '" + label + "' is not in the expected 24-hour "
                    + "'yyyy-MM-dd HH:mm:ss' format; check the account's time format", e);
        }
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
        return WaitUtils.retryOnStale(() -> parseCursorLabel(
                WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.CURRENT_POSITION).getText()));
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
                        + levels + "; if the app added or removed a level, update TIMELINE_LEVEL_MINUTES");
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
            int sampled = Math.min(rows.size(), LEVEL_SAMPLE_ROWS);
            List<String> labels = new ArrayList<>(sampled);
            for (int i = 0; i < sampled; i++) {
                labels.add(rows.get(i).getText());
            }
            int level = Integer.MAX_VALUE;
            for (int i = 1; i < sampled; i++) {
                level = Math.min(level, gapMinutes(labels.get(i - 1), labels.get(i)));
            }
            return level;
        });
    }
}
