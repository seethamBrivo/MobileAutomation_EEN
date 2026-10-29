package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.DeviceUtils;
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

    private static final int CONTROL_CLICK_ATTEMPTS = 2;


    private static final String MULTI_WINDOWS_SETTING = "enableMultiWindows";

    private static final List<By> CONTEXT_MENU_OPTIONS = List.of(
            CamerasLocators.CAMERA_SETTINGS_OPTION, CamerasLocators.HISTORY_BROWSER_OPTION,
            CamerasLocators.LIVE_VIDEO_OPTION);

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

    /**
     * Skips the test, with the reason, when too few events lie before the cursor to step back through. Only the
     * events shown count, and the history browser opens at the newest recording, so the ones shown precede the cursor.
     */
    public void requireEventsOnTimeline() {
        LocalDateTime cursor = readCursorPosition();
        int earlierEvents = 0;
        if (WaitUtils.isVisible(driver, HistoryBrowserLocators.TIMELINE_EVENT_TIMES, TimeoutConstants.SHORT_TIMEOUT)) {
            earlierEvents = WaitUtils.retryOnStale(() -> {
                int count = 0;
                for (WebElement event : WaitUtils.findElements(driver, HistoryBrowserLocators.TIMELINE_EVENT_TIMES)) {
                    if (parseCursorLabel(event.getText()).isBefore(cursor)) {
                        count++;
                    }
                }
                return count;
            });
        }
        if (earlierEvents < MIN_EVENTS_FOR_STEPPING) {
            throw new SkipException("The first camera needs at least " + MIN_EVENTS_FOR_STEPPING
                    + " events before the cursor to step between events but " + earlierEvents + " are shown");
        }
    }

    /**
     * Stepping with the buttons only proves something when the cursor does not move by itself. One change is
     * tolerated while the player settles; a cursor that is still moving afterwards means playback is running, so it
     * is paused with the play/pause button and must then stay put.
     */
    public void ensurePlaybackPaused() {
        if (cursorStaysStill() || cursorStaysStill()) {
            return;
        }
        logger.info("Playback is running; pausing it");
        WaitUtils.click(driver, HistoryBrowserLocators.PLAY_PAUSE_BUTTON);
        Assert.assertTrue(cursorStaysStill(),
                "Playback should stop after pressing pause but the cursor kept advancing");
    }

    /**
     * Long press on the first camera preview: the context menu with the camera's options appears. The menu is a
     * popup window, and UiAutomator2 reports only the app window (so the menu is invisible on BrowserStack) unless
     * multi-window is on; it stays on until the menu has been used.
     */
    public void openContextMenuOfFirstCameraPreview() {
        driver.setSetting(MULTI_WINDOWS_SETTING, true);
        GestureUtils.longPress(driver, WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.FIRST_PREVIEW_CARD));
        for (By option : CONTEXT_MENU_OPTIONS) {
            Assert.assertTrue(WaitUtils.waitForVisibility(driver, option).isDisplayed(),
                    "Context menu option " + option + " should be displayed");
        }
        boolean hasTurnOnOrOff =
                WaitUtils.isVisible(driver, CamerasLocators.TURN_ON_CAMERA_OPTION, TimeoutConstants.SHORT_TIMEOUT)
                        || WaitUtils.isVisible(driver, CamerasLocators.TURN_OFF_CAMERA_OPTION,
                                TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(hasTurnOnOrOff,
                "Expected either 'Turn on camera' or 'Turn off camera' in the context menu");
    }

    public void openHistoryBrowserFromContextMenu() {
        try {
            WaitUtils.click(driver, CamerasLocators.HISTORY_BROWSER_OPTION);
        } finally {
            driver.setSetting(MULTI_WINDOWS_SETTING, false);
        }
        verifyHistoryBrowserDisplayed();
    }

    public void verifyTimelineAndControlsDisplayedInPortrait() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_CONTAINER).isDisplayed(),
                "Timeline should be displayed in the history browser");
        verifyTimelineControlsDisplayed();
        Dimension screen = driver.manage().window().getSize();
        Assert.assertTrue(screen.height > screen.width,
                "History browser should be in portrait but the screen is " + screen.width + "x" + screen.height);
    }

    public void rotateToLandscapeAndVerifyHistoryBrowser() {
        DeviceUtils.rotateToLandscape(driver);
        verifyHistoryBrowserDisplayed();
    }

    /**
     * The app opens the landscape history browser with the timeline side panel already showing, so the panel is
     * closed first; the timeline must then be hidden until the timeline button is tapped.
     */
    public void verifyTimelineHiddenInLandscape() {
        if (WaitUtils.isVisible(driver, HistoryBrowserLocators.TIMELINE_CLOSE_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Landscape history browser opened with the timeline showing; closing it");
            WaitUtils.click(driver, HistoryBrowserLocators.TIMELINE_CLOSE_BUTTON);
        }
        Assert.assertTrue(WaitUtils.waitForInvisibility(
                        driver, HistoryBrowserLocators.TIMELINE_CONTAINER, TimeoutConstants.SHORT_TIMEOUT),
                "Timeline should not be visible in landscape until the timeline button is tapped");
    }

    /** The landscape history browser shows the timeline panel by default, with a close icon that hides it. */
    public void verifyTimelineShowsByDefaultAndClosesInLandscape() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_CONTAINER).isDisplayed(),
                "Timeline should be visible by default in landscape");
        WaitUtils.click(driver, HistoryBrowserLocators.TIMELINE_CLOSE_BUTTON);
        Assert.assertTrue(WaitUtils.waitForInvisibility(
                        driver, HistoryBrowserLocators.TIMELINE_CONTAINER, TimeoutConstants.SHORT_TIMEOUT),
                "Timeline should be closed after tapping the close icon");
    }

    public void showTimelineAndVerifyControlsInLandscape() {
        WaitUtils.click(driver, HistoryBrowserLocators.TIMELINE_BUTTON);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_CONTAINER).isDisplayed(),
                "Timeline should be displayed after tapping the timeline button");
        verifyTimelineControlsDisplayed();
    }

    public void verifyFilterButtonWithoutIndication() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.FILTER_BUTTON).isDisplayed(),
                "Filter button should be displayed in the history browser");
        Assert.assertFalse(WaitUtils.isVisible(driver, HistoryBrowserLocators.FILTER_BUTTON_INDICATION,
                        TimeoutConstants.OPTIONAL_FIELD_TIMEOUT),
                "Filter button should show no indication number");
    }

    /**
     * Every event card must sit on the part of the timeline that is shown: its time is within one timeline level of a
     * visible row label. Row labels carry no date, so the distance is taken on the time of day, around midnight.
     */
    public void verifyEventCardsMatchTimelineRange() {
        int levelSeconds = readLevelMinutes() * 60;
        List<Integer> rowSeconds = timesOfDay(HistoryBrowserLocators.TIMELINE_ROW_TIMESTAMPS, false);
        List<Integer> cardSeconds = timesOfDay(HistoryBrowserLocators.TIMELINE_EVENT_TIMES, true);
        Assert.assertFalse(cardSeconds.isEmpty(), "Timeline should show at least one event card");
        for (int card : cardSeconds) {
            boolean onTimeline = rowSeconds.stream().anyMatch(row -> circularDistance(card, row) <= levelSeconds);
            Assert.assertTrue(onTimeline, "Event card at second " + card + " of the day should be within "
                    + levelSeconds + " s of a timeline row but the rows are at " + rowSeconds);
        }
    }

    public void verifyPlaybackPaused() {
        Assert.assertTrue(cursorStaysStill(), "Player should be in paused state but the cursor kept advancing");
    }

    public void tapPreviousFrameAndVerifyMoment() {
        tapStepButtonAndVerifyMoment(HistoryBrowserLocators.PREVIOUS_FRAME_BUTTON, false, "previous frame");
    }

    public void tapNextFrameAndVerifyMoment() {
        tapStepButtonAndVerifyMoment(HistoryBrowserLocators.NEXT_FRAME_BUTTON, true, "next frame");
    }

    public void longPressPreviousFrameAndVerifyFastMove() {
        longPressStepButtonAndVerifyFastMove(HistoryBrowserLocators.PREVIOUS_FRAME_BUTTON, false, "previous frame");
    }

    public void longPressNextFrameAndVerifyFastMove() {
        longPressStepButtonAndVerifyFastMove(HistoryBrowserLocators.NEXT_FRAME_BUTTON, true, "next frame");
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

    private List<Integer> timesOfDay(By locator, boolean withDate) {
        List<Integer> seconds = new ArrayList<>();
        for (String text : WaitUtils.textsOf(driver, locator)) {
            seconds.add(withDate ? parseCursorLabel(text).toLocalTime().toSecondOfDay()
                    : parseTimeLabel(text).toSecondOfDay());
        }
        return seconds;
    }

    private static int circularDistance(int firstSecond, int secondSecond) {
        int difference = Math.floorMod(firstSecond - secondSecond, SECONDS_PER_DAY);
        return Math.min(difference, SECONDS_PER_DAY - difference);
    }

    private boolean cursorStaysStill() {
        LocalDateTime observed = readCursorPosition();
        return !WaitUtils.pollUntil(driver, TimeoutConstants.PLAYBACK_PROBE_TIMEOUT,
                () -> !readCursorPosition().equals(observed));
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
        LocalDateTime after = clickUntilCursorMoves(button, before, description);
        verifyCursorMoved(before, after, forward, description);
    }

    /** One tap on a frame button: the cursor, the time above the video and so the shown frame all move together. */
    private void tapStepButtonAndVerifyMoment(By button, boolean forward, String description) {
        LocalDateTime cursorBefore = readCursorPosition();
        LocalDateTime previewBefore = readPreviewTime();
        LocalDateTime cursorAfter = clickUntilCursorMoves(button, cursorBefore, description);
        verifyCursorMoved(cursorBefore, cursorAfter, forward, description);
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                () -> !readPreviewTime().equals(previewBefore));
        LocalDateTime previewAfter = readPreviewTime();
        Assert.assertNotEquals(previewAfter, previewBefore,
                "Preview time above the video should update after tapping " + description + " but stayed at "
                        + previewBefore);
    }

    /**
     * A long press keeps stepping, so it must travel further than the single tap that precedes it. The tap gives the
     * size of one step; vibration is not observable.
     */
    private void longPressStepButtonAndVerifyFastMove(By button, boolean forward, String description) {
        LocalDateTime start = readCursorPosition();
        LocalDateTime afterTap = clickUntilCursorMoves(button, start, description);
        long tapSeconds = Math.abs(Duration.between(start, afterTap).getSeconds());
        GestureUtils.longPress(driver, WaitUtils.waitForVisibility(driver, button));
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                () -> !readCursorPosition().equals(afterTap));
        LocalDateTime afterHold = readSettledCursorPosition();
        long heldSeconds = Math.abs(Duration.between(afterTap, afterHold).getSeconds());
        boolean inDirection = forward ? afterHold.isAfter(afterTap) : afterHold.isBefore(afterTap);
        Assert.assertTrue(inDirection && heldSeconds > tapSeconds,
                "Long press on " + description + " should keep moving the cursor " + (forward ? "forward" : "backward")
                        + " further than one tap (" + tapSeconds + " s) but moved " + heldSeconds + " s from "
                        + afterTap + " to " + afterHold);
    }

    private void verifyCursorMoved(LocalDateTime before, LocalDateTime after, boolean forward, String description) {
        boolean moved = forward ? after.isAfter(before) : after.isBefore(before);
        Assert.assertTrue(moved,
                "Red cursor should move to the " + description + " (" + (forward ? "later" : "earlier")
                        + " than " + before + ") but is at " + after);
    }

    private LocalDateTime clickUntilCursorMoves(By button, LocalDateTime before, String description) {
        for (int attempt = 1; attempt <= CONTROL_CLICK_ATTEMPTS; attempt++) {
            WaitUtils.click(driver, button);
            if (WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                    () -> !readCursorPosition().equals(before))) {
                break;
            }
            logger.warn("Cursor did not move after clicking {} (attempt {} of {})",
                    description, attempt, CONTROL_CLICK_ATTEMPTS);
        }
        return readCursorPosition();
    }

    private LocalDateTime readPreviewTime() {
        return WaitUtils.retryOnStale(() -> parseCursorLabel(
                WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.PREVIEW_TIME).getText()));
    }

    /** The cursor label lags the repeated steps of a long press, so it is read once two reads in a row agree. */
    private LocalDateTime readSettledCursorPosition() {
        LocalDateTime[] last = {null};
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT, () -> {
            LocalDateTime now = readCursorPosition();
            boolean settled = now.equals(last[0]);
            last[0] = now;
            return settled;
        });
        return readCursorPosition();
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
                int gap = gapMinutes(labels.get(i - 1), labels.get(i));
                if (gap > 0) {
                    level = Math.min(level, gap);
                }
            }
            if (level == Integer.MAX_VALUE) {
                throw new NoSuchElementException("Timeline rows should show distinct times to read the level but "
                        + "showed " + labels);
            }
            return level;
        });
    }
}
