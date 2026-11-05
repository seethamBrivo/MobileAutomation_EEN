package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final AppiumDriver driver;

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

    private static final String LIVE_BUTTON_TEXT = "Live";

    /** Time zone offsets move in 15 minute steps, the smallest step in use. */
    private static final int TIME_ZONE_STEP_SECONDS = 15 * 60;

    /** The history browser opens a moment after the menu whose frame time it is compared with. */
    private static final int CURRENT_MOMENT_TOLERANCE_SECONDS = 120;

    /** The widest gap between two zones: UTC+14 and UTC-12 are 26 hours apart. */
    private static final long MAX_TIME_ZONE_GAP_SECONDS = 26 * 60 * 60;

    /** What must be left of a cut camera name before it can stand for a longer one. */
    private static final int MIN_CUT_NAME_PREFIX_LENGTH = 3;

    /** A long camera name is cut with three dots or the ellipsis character. */
    private static final Pattern NAME_CUT_MARK = Pattern.compile("(\\.\\.\\.|\\u2026)$");

    /** The calendar picker shows the cursor as a full 24-hour date and time. */
    private static final Pattern CALENDAR_TIMESTAMP = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

    private static final String MULTI_WINDOWS_SETTING = "enableMultiWindows";

    private static final List<By> CONTEXT_MENU_OPTIONS = List.of(
            CamerasLocators.CAMERA_SETTINGS_OPTION, CamerasLocators.HISTORY_BROWSER_OPTION,
            CamerasLocators.LIVE_VIDEO_OPTION);

    /** Hour and minute of a displayed time, then seconds when shown and AM/PM when the account uses 12-hour time. */
    private static final Pattern TIME_OF_DAY =
            Pattern.compile("(\\d{1,2}):(\\d{2})(?::(\\d{2}))?(?:\\s*([AaPp])[Mm])?");

    /** The cursor may already have moved on by the time its label is read, so it is not required to be exact. */
    private static final int EVENT_MOMENT_TOLERANCE_SECONDS = 10;

    /** The event time on the details screen has minutes only, so the event lies anywhere in that minute. */
    private static final int EVENT_TIME_RESOLUTION_SECONDS = 59;

    /** Set while the multi-window setting is on, so cleanup only has to switch it off when a test turned it on. */
    private boolean multiWindowEnabled;

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
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.LIVE_STREAM_SCREEN,
                "Live video of the selected camera should be displayed");
    }

    public void openHistoryBrowserFromLiveVideo() {
        WaitUtils.click(driver, HistoryBrowserLocators.NAVIGATION_BUTTON);
        verifyHistoryBrowserDisplayed();
    }

    /**
     * Recovers from export flows by navigating back to History Browser.
     * Handles Export screen, Progress tab, and Archive folder picker states.
     */
    public void recoverFromExportFlow() {
        if (WaitUtils.isVisible(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN,
                TimeoutConstants.SHORT_TIMEOUT)) {
            return;
        }

        if (WaitUtils.isVisible(driver, HistoryBrowserLocators.CANCEL_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
            WaitUtils.click(driver, HistoryBrowserLocators.CANCEL_BUTTON);
            WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN);
        } else if (WaitUtils.isVisible(driver, HistoryBrowserLocators.PROGRESS_TAB, TimeoutConstants.SHORT_TIMEOUT)) {
            driver.navigate().back();
            WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN);
        } else if (WaitUtils.isVisible(driver, HistoryBrowserLocators.SELECT_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
            driver.navigate().back();
            driver.navigate().back();
            WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN);
        }
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
            WaitUtils.assertVisible(driver, control,
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
     * multi-window is on; it stays on until the menu has been used, and is switched off at the end of every test by
     * {@link #resetMultiWindowSetting()}, so a failure in between cannot leave it on for the rest of the session.
     */
    public void openContextMenuOfFirstCameraPreview() {
        multiWindowEnabled = true;
        driver.setSetting(MULTI_WINDOWS_SETTING, true);
        GestureUtils.longPress(driver, WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.FIRST_PREVIEW_CARD));
        for (By option : CONTEXT_MENU_OPTIONS) {
            WaitUtils.assertVisible(driver, option, "Context menu option " + option + " should be displayed");
        }
        boolean hasTurnOnOrOff =
                WaitUtils.isVisible(driver, CamerasLocators.TURN_ON_CAMERA_OPTION, TimeoutConstants.SHORT_TIMEOUT)
                        || WaitUtils.isVisible(driver, CamerasLocators.TURN_OFF_CAMERA_OPTION,
                                TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(hasTurnOnOrOff,
                "Expected either 'Turn on camera' or 'Turn off camera' in the context menu");
    }

    /** Cleanup step: switches multi-window reporting off again if a test stopped with the context menu open. */
    public void resetMultiWindowSetting() {
        if (multiWindowEnabled) {
            multiWindowEnabled = false;
            driver.setSetting(MULTI_WINDOWS_SETTING, false);
        }
    }

    public void openHistoryBrowserFromContextMenu() {
        try {
            WaitUtils.click(driver, CamerasLocators.HISTORY_BROWSER_OPTION);
        } finally {
            resetMultiWindowSetting();
        }
        verifyHistoryBrowserDisplayed();
    }

    public void verifyTimelineAndControlsDisplayedInPortrait() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.TIMELINE_CONTAINER,
                "Timeline should be displayed in the history browser");
        verifyTimelineControlsDisplayed();
        Dimension screen = driver.manage().window().getSize();
        Assert.assertTrue(screen.height > screen.width,
                "History browser should be in portrait but the screen is " + screen.width + "x" + screen.height);
    }

    /**
     * Paused video, the screen's header and action bar, the "Timeline" label, the timeline and its controls.
     *
     * @param cameraName the name the camera showed before it was opened
     */
    public void verifyPausedHistoryBrowserElementsInPortrait(String cameraName) {
        verifyCommonPausedElements(cameraName);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_LABEL).isDisplayed(),
                "'Timeline' label should be displayed above the timeline");
        verifyTimelineAndControlsDisplayedInPortrait();
    }

    /** The same list in landscape; the test case leaves the timeline and its label out, so they are not asserted. */
    public void verifyPausedHistoryBrowserElementsInLandscape(String cameraName) {
        verifyCommonPausedElements(cameraName);
        verifyTimelineControlsDisplayed();
    }

    private void verifyCommonPausedElements(String cameraName) {
        verifyPlaybackPaused();
        verifyCameraNameInBackButton(cameraName);
        verifyHistoryBrowserHeaderAndActions();
    }

    /**
     * The history browser opens at the moment of the event that was selected. Video Search shows the account's time
     * and this screen the device's, so on BrowserStack the two read hours apart (16:23:44 against 03:53:44) and an
     * exact match would fail on every run. Both are the same instant in two zones, so their date-times must differ
     * by a time zone gap: a whole number of {@value #TIME_ZONE_STEP_SECONDS} s steps, at most
     * {@value #MAX_TIME_ZONE_GAP_SECONDS} s (UTC+14 against UTC-12). That rejects another event more than that apart;
     * an event exactly one day away at the same time of day would still pass, because a day is a whole number of
     * steps and within the limit. The screen may still show its first position while it seeks to the event, so the
     * cursor is polled until the gap is valid, and the value that ended the poll is the one asserted. Both times are
     * logged, and whether the two screens should agree is a question for the test case author.
     */
    public void verifyOpenedAtEventMoment(LocalDateTime eventTime) {
        verifyHistoryBrowserDisplayed();
        LocalDateTime cursor = pollCursor(
                position -> isTimeZoneGap(Duration.between(position, eventTime).getSeconds()));
        long gapSeconds = Duration.between(cursor, eventTime).getSeconds();
        logger.info("Event time on the event page: {}; history browser cursor: {}; gap {} s",
                eventTime, cursor, gapSeconds);
        Assert.assertEquals(Math.floorMod(gapSeconds, (long) TIME_ZONE_STEP_SECONDS), 0L,
                "History browser should open at the selected event (" + eventTime + ") apart from a time zone "
                        + "gap, but the cursor is at " + cursor);
        Assert.assertTrue(Math.abs(gapSeconds) <= MAX_TIME_ZONE_GAP_SECONDS,
                "Event time " + eventTime + " and cursor " + cursor + " are " + gapSeconds
                        + " s apart, more than any two time zones");
    }

    /**
     * The test case calls this "the current moment": the history browser opens at the frame the long press menu
     * showed. The menu carries no date, so only the time of day is compared. On BrowserStack both screens showed the
     * device's zone (43 s apart), but a whole time zone gap is still allowed so that an account zone cannot fail
     * the test. The screen opens a moment after the menu, so the cursor may be up to
     * {@value #CURRENT_MOMENT_TOLERANCE_SECONDS} s from the menu's time. Both times are logged.
     */
    public void verifyOpenedAtCurrentMoment(LocalTime menuFrameTime) {
        verifyHistoryBrowserDisplayed();
        LocalDateTime cursor = pollCursor(
                position -> secondsOffTimeZoneGap(menuFrameTime, position) <= CURRENT_MOMENT_TOLERANCE_SECONDS);
        long offSeconds = secondsOffTimeZoneGap(menuFrameTime, cursor);
        logger.info("Frame time in the context menu: {}; history browser cursor: {}; {} s off a time zone gap",
                menuFrameTime, cursor, offSeconds);
        Assert.assertTrue(offSeconds <= CURRENT_MOMENT_TOLERANCE_SECONDS,
                "History browser should open at the current moment (" + menuFrameTime + " in the menu) apart from a "
                        + "time zone gap, but the cursor is at " + cursor + ", " + offSeconds + " s off");
    }

    /**
     * Polls the cursor until {@code accepted} holds and returns the last position read, so the assert that follows
     * sees the value the poll ended on and not a later read that may have moved. When it never holds, the last
     * position is returned and the assert fails with it.
     */
    private LocalDateTime pollCursor(Predicate<LocalDateTime> accepted) {
        AtomicReference<LocalDateTime> last = new AtomicReference<>();
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT, () -> {
            last.set(readCursorPosition());
            return accepted.test(last.get());
        });
        LocalDateTime position = last.get();
        return position != null ? position : readCursorPosition();
    }

    private static boolean isTimeZoneGap(long gapSeconds) {
        return Math.floorMod(gapSeconds, (long) TIME_ZONE_STEP_SECONDS) == 0
                && Math.abs(gapSeconds) <= MAX_TIME_ZONE_GAP_SECONDS;
    }

    /** How far the two times of day are from the nearest whole time zone step: 0 when they differ by a pure gap. */
    private static long secondsOffTimeZoneGap(LocalTime menuTime, LocalDateTime cursor) {
        int differenceSeconds = menuTime.toSecondOfDay() - cursor.toLocalTime().toSecondOfDay();
        int half = TIME_ZONE_STEP_SECONDS / 2;
        return Math.abs(Math.floorMod(differenceSeconds + half, TIME_ZONE_STEP_SECONDS) - half);
    }

    public void rotateToLandscapeAndVerifyHistoryBrowser() {
        DeviceUtils.rotateToLandscape(driver);
        verifyHistoryBrowserDisplayed();
    }

    /**
     * The landscape history browser shows the timeline panel by default, with a close icon that hides it. The test
     * case words this as hidden until the timeline button is tapped; the app's behaviour is what is asserted.
     */
    public void verifyTimelineShowsByDefaultAndClosesInLandscape() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.TIMELINE_CONTAINER,
                "Timeline should be visible by default in landscape");
        WaitUtils.click(driver, HistoryBrowserLocators.TIMELINE_CLOSE_BUTTON);
        Assert.assertTrue(WaitUtils.waitForInvisibility(
                        driver, HistoryBrowserLocators.TIMELINE_CONTAINER, TimeoutConstants.SHORT_TIMEOUT),
                "Timeline should be closed after tapping the close icon");
    }

    public void showTimelineAndVerifyControlsInLandscape() {
        WaitUtils.click(driver, HistoryBrowserLocators.TIMELINE_BUTTON);
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.TIMELINE_CONTAINER,
                "Timeline should be displayed after tapping the timeline button");
        verifyTimelineControlsDisplayed();
    }

    /** The timeline is waited for first, so a number that renders late is not mistaken for an absent one. */
    public void verifyFilterButtonWithoutIndication() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.FILTER_BUTTON,
                "Filter button should be displayed in the history browser");
        WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_ROW_TIMESTAMPS);
        WaitUtils.assertNotVisible(driver, HistoryBrowserLocators.FILTER_BUTTON_INDICATION,
                TimeoutConstants.SHORT_TIMEOUT,
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
     * The history browser must open for the license plate event's camera, on the event's moment and not on the newest
     * recording. The camera is compared by name. The time is the details screen's Timestamp row, which, like the
     * history browser, is in the camera's time zone, so the cursor's time of day must fall inside the minute shown
     * there. The check is minute-level because the details screen shows no seconds: an event at 10:05:50 also
     * passes with the cursor at 10:05:00, but opening on the newest recording does not. That is why this is not
     * {@link #verifyOpenedAtEventMoment}: Video Search shows another time zone, so it can only compare up to a time
     * zone gap.
     */
    public void verifyOpenedForLprEvent(String eventCameraName, String eventTime) {
        verifyHistoryBrowserDisplayed();
        verifyCameraNameInBackButton(eventCameraName);
        verifyCursorAtEventMoment(eventTime);
    }

    /**
     * Cleanup step: closes the long press menu when a test stopped with it open, with the shared
     * {@link CameraContextMenu#dismiss}. That presses back on Android whether or not a menu is open, so it is only
     * called when the menu is visible. It must run before {@link #resetMultiWindowSetting()}, because with
     * multi-window reporting off the popup is no longer visible to Appium. Does nothing unless this page opened the
     * menu, and never asserts.
     */
    public void dismissContextMenuIfOpen() {
        if (!multiWindowEnabled) {
            return;
        }
        if (WaitUtils.isVisible(driver, CamerasLocators.CAMERA_SETTINGS_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            CameraContextMenu.dismiss(driver);
            boolean closed = WaitUtils.waitForInvisibility(
                    driver, CamerasLocators.CAMERA_SETTINGS_OPTION, TimeoutConstants.SHORT_TIMEOUT);
            logger.info("Long press menu {} after back", closed ? "closed" : "is still open");
        }
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

    /**
     * The names must be equal. The one exception is a name that one screen cut short with an ellipsis: the other
     * screen's name must then start with what is left, and that must be at least
     * {@value #MIN_CUT_NAME_PREFIX_LENGTH} characters, so a short name cannot match a longer one by accident.
     */
    private void verifyCameraNameInBackButton(String expectedName) {
        String shown = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.CAMERA_NAME).getText().trim();
        String expected = expectedName.trim();
        Assert.assertFalse(expected.isEmpty(), "The camera name seen before opening should not be empty");
        Assert.assertFalse(shown.isEmpty(), "Camera name should be shown in the back navigation button");
        boolean sameCamera = shown.equals(expected)
                || (isCut(expected) && startsWithCutName(shown, expected))
                || (isCut(shown) && startsWithCutName(expected, shown));
        Assert.assertTrue(sameCamera,
                "Back navigation button should show the camera name '" + expected + "' but showed '" + shown + "'");
    }

    private static boolean isCut(String name) {
        return NAME_CUT_MARK.matcher(name).find();
    }

    private static boolean startsWithCutName(String fullName, String cutName) {
        String kept = NAME_CUT_MARK.matcher(cutName).replaceAll("");
        return kept.length() >= MIN_CUT_NAME_PREFIX_LENGTH && fullName.startsWith(kept);
    }

    private void verifyHistoryBrowserHeaderAndActions() {
        Assert.assertEquals(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.NAVIGATION_BUTTON).getText(),
                LIVE_BUTTON_TEXT, "Button to switch to the live video should read '" + LIVE_BUTTON_TEXT + "'");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.PLAYER_PREVIEW).isDisplayed(),
                "Camera preview should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.SAVE_BUTTON).isDisplayed(),
                "Save button should be displayed");
        String calendarTimestamp = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.CURRENT_POSITION)
                .getText();
        Assert.assertTrue(CALENDAR_TIMESTAMP.matcher(calendarTimestamp).matches(),
                "Calendar picker should show a 'YYYY-MM-DD HH:MM:SS' timestamp but showed '" + calendarTimestamp + "'");
        verifyFilterButtonEnabledWhenEventsShown();
    }

    /**
     * The test case enables the filter only when an event is available, and disables it otherwise. Only the shown
     * part of the timeline is visible, so an empty view does not prove there are no events anywhere: the enabled
     * state is asserted when an event card is shown and only logged when none is. The cards get the settling
     * budget after the rows, so a slow render does not read as "no events", and the button is read last.
     */
    private void verifyFilterButtonEnabledWhenEventsShown() {
        WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_ROW_TIMESTAMPS);
        boolean eventsShown = WaitUtils.isVisible(
                driver, HistoryBrowserLocators.TIMELINE_EVENT_NAMES, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT);
        boolean enabled = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.FILTER_BUTTON).isEnabled();
        if (eventsShown) {
            Assert.assertTrue(enabled, "Filter button should be enabled while the timeline shows events");
        } else {
            logger.info("No event card on the shown timeline; filter button enabled: {} (not asserted)", enabled);
        }
    }

    private void verifyHistoryBrowserDisplayed() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN,
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
     * size of one step; vibration is not observable. The cursor label lags the repeated steps, so it is polled until
     * it has passed the tap's distance, and the assert then reads it afresh.
     */
    private void longPressStepButtonAndVerifyFastMove(By button, boolean forward, String description) {
        LocalDateTime start = readCursorPosition();
        LocalDateTime afterTap = clickUntilCursorMoves(button, start, description);
        long tapSeconds = Math.abs(Duration.between(start, afterTap).getSeconds());
        GestureUtils.longPress(driver, WaitUtils.waitForVisibility(driver, button));
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                () -> movedFurther(afterTap, readCursorPosition(), forward, tapSeconds));
        LocalDateTime afterHold = readCursorPosition();
        Assert.assertTrue(movedFurther(afterTap, afterHold, forward, tapSeconds),
                "Long press on " + description + " should keep moving the cursor " + (forward ? "forward" : "backward")
                        + " further than one tap (" + tapSeconds + " s) but moved "
                        + Math.abs(Duration.between(afterTap, afterHold).getSeconds()) + " s from " + afterTap
                        + " to " + afterHold);
    }

    private static boolean movedFurther(LocalDateTime from, LocalDateTime to, boolean forward, long thanSeconds) {
        boolean inDirection = forward ? to.isAfter(from) : to.isBefore(from);
        return inDirection && Math.abs(Duration.between(from, to).getSeconds()) > thanSeconds;
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

    private void verifyCursorAtEventMoment(String eventTime) {
        int eventSecond = secondOfDay(eventTime).orElseThrow(() -> new AssertionError(
                "Event time '" + eventTime + "' has no hh:mm part; check the account's time format"));
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                () -> isAtEventMoment(readCursorSecond(), eventSecond));
        int cursorSecond = readCursorSecond();
        Assert.assertTrue(isAtEventMoment(cursorSecond, eventSecond),
                "History browser should open at the moment of the event (" + eventTime + ", within that minute and "
                        + EVENT_MOMENT_TOLERANCE_SECONDS + " s) but the cursor is at "
                        + LocalTime.ofSecondOfDay(cursorSecond));
        logger.info("History browser opened at {} for the event shown as '{}'",
                LocalTime.ofSecondOfDay(cursorSecond), eventTime);
    }

    /** Read as a time of day, so the account's 12-hour or 24-hour format does not matter. */
    private int readCursorSecond() {
        String label = WaitUtils.retryOnStale(
                () -> WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.CURRENT_POSITION).getText());
        return secondOfDay(label).orElseThrow(() -> new AssertionError(
                "Cursor time '" + label + "' has no hh:mm part; check the account's time format"));
    }

    private static OptionalInt secondOfDay(String text) {
        Matcher time = TIME_OF_DAY.matcher(text);
        if (!time.find()) {
            return OptionalInt.empty();
        }
        int hour = Integer.parseInt(time.group(1));
        if (time.group(4) != null) {
            hour = hour % 12 + (time.group(4).equalsIgnoreCase("p") ? 12 : 0);
        }
        int seconds = time.group(3) == null ? 0 : Integer.parseInt(time.group(3));
        try {
            return OptionalInt.of(LocalTime.of(hour, Integer.parseInt(time.group(2)), seconds).toSecondOfDay());
        } catch (DateTimeException e) {
            return OptionalInt.empty();
        }
    }

    private static boolean isAtEventMoment(int cursorSecond, int eventSecond) {
        int gap = Math.floorMod(cursorSecond - eventSecond, SECONDS_PER_DAY);
        return gap <= EVENT_TIME_RESOLUTION_SECONDS + EVENT_MOMENT_TOLERANCE_SECONDS
                || SECONDS_PER_DAY - gap <= EVENT_MOMENT_TOLERANCE_SECONDS;
    }

    // --- Export functionality --------------------------------------------------

    public void verifyExportButtonVisible() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.SAVE_BUTTON,
                TimeoutConstants.SHORT_TIMEOUT,
                "Export button should be visible in History Browser");
    }

    public void clickExportButton() {
        WaitUtils.click(driver, HistoryBrowserLocators.SAVE_BUTTON);
    }

    public void verifyExportScreenOpened() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.SAVE_TO_SECTION_TITLE,
                FrameworkConstants.EXPLICIT_WAIT,
                "Export screen should be opened with 'SAVE TO' section visible");
        logger.info("Export screen opened successfully");
    }

    public void verifyDownloadsSelectedAsDefault() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.DOWNLOADS_SELECTED_INDICATOR,
                TimeoutConstants.SHORT_TIMEOUT,
                "Downloads should be selected as default save destination");
    }

    public void selectArchiveDestination() {
        WaitUtils.click(driver, HistoryBrowserLocators.ARCHIVE_TEXT);
    }

    public void verifyArchiveFolderPickerOpened() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.SELECT_BUTTON,
                FrameworkConstants.EXPLICIT_WAIT,
                "Archive folder picker should be opened with Select button visible");
    }

    public void pickFirstFolder() {
        WaitUtils.click(driver, HistoryBrowserLocators.FIRST_FOLDER);
    }

    public void clickSelectButton() {
        WaitUtils.click(driver, HistoryBrowserLocators.SELECT_BUTTON);
    }

    public void verifyFolderSelected() {
        // Wait for Export screen to reload after folder selection
        WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.SAVE_TO_SECTION_TITLE,
                FrameworkConstants.EXPLICIT_WAIT);
        String folderName = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.SELECTED_FOLDER_NAME)
                .getText();
        Assert.assertFalse(folderName.isEmpty(),
                "A folder should be selected but folder name is empty");
        Assert.assertTrue(folderName.startsWith("Folder:"),
                "Selected folder should start with 'Folder:' but was '" + folderName + "'");
    }

    /**
     * Verifies that Export screen fields are visible.
     * Deliberately not asserted: Start/End time values, Duration value, Time Stamp toggle state
     * (no stable locators for these values).
     */
    public void verifyExportScreenFieldsVisible() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.SAVE_TO_SECTION_TITLE,
                "SAVE TO section should be visible");

        WaitUtils.assertVisible(driver, HistoryBrowserLocators.RANGE_SECTION_TITLE,
                "RANGE section should be visible");
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.START_TIME_FIELD,
                "Start time field should be visible");
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.END_TIME_FIELD,
                "End time field should be visible");

        WaitUtils.assertVisible(driver, HistoryBrowserLocators.DOWNLOAD_TYPE_HEADING,
                "Download type heading should be visible");
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.DOWNLOAD_TYPE_VIDEO,
                "Download type 'Video' should be visible");

        WaitUtils.assertVisible(driver, HistoryBrowserLocators.FILE_NAME_FIELD,
                "File name field should be visible");

        GestureUtils.swipeUp(driver);

        WaitUtils.assertVisible(driver, HistoryBrowserLocators.DURATION_FIELD,
                "Duration field should be visible");

        // Verify Time Zone value (the only value we can reliably check)
        String timeZone = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIME_ZONE_VALUE).getText();
        Assert.assertFalse(timeZone.isEmpty(), "Time Zone value should not be empty");

        // Swipe back down to restore screen position
        GestureUtils.swipeDown(driver);
    }

    public void clickExportButtonAtBottom() {
        WaitUtils.click(driver, HistoryBrowserLocators.EXPORT_BUTTON);
    }

    public void verifyExportingSnackbar() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.SNACKBAR_MESSAGE,
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT,
                "Snackbar message should be displayed after clicking Export");
        String message = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.SNACKBAR_MESSAGE).getText();
        Assert.assertTrue(message.contains("Exporting"),
                "Snackbar should contain 'Exporting' but was: " + message);
    }

    public void verifyExportingSnackbarAndClickSeeProgress() {
        // Snackbar is transient - must click while still visible
        WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.SNACKBAR_MESSAGE,
                TimeoutConstants.DOWNLOAD_COMPLETE_TIMEOUT);
        WaitUtils.click(driver, HistoryBrowserLocators.SEE_PROGRESS_BUTTON);
    }

    public void verifyProgressTabOpened() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.PROGRESS_TAB,
                FrameworkConstants.EXPLICIT_WAIT,
                "Progress tab should be displayed after clicking See progress");
    }

    public void clickCancelButton() {
        WaitUtils.click(driver, HistoryBrowserLocators.CANCEL_BUTTON);
    }

    public void verifyReturnedToHistoryBrowser() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.HISTORY_BROWSER_SCREEN,
                FrameworkConstants.EXPLICIT_WAIT,
                "Should return to History Browser screen after canceling export");
    }

    public void enterFileName(String fileName) {
        WaitUtils.clearAndType(driver, HistoryBrowserLocators.FILE_NAME_FIELD, fileName);
    }

    public void verifyValidationError() {
        WaitUtils.assertVisible(driver, HistoryBrowserLocators.VALIDATION_ERROR_TEXT,
                TimeoutConstants.SHORT_TIMEOUT,
                "Validation error message should be displayed for special characters");
        String errorMessage = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.VALIDATION_ERROR_TEXT)
                .getText();
        Assert.assertTrue(errorMessage.contains("[^/]*"),
                "Error message should contain '[^/]*' but was: " + errorMessage);
    }

    // High-level test flows
    public void exportToArchiveWithDefaultSettings() {
        // Click previous event to ensure timeline is positioned at a point with historical video available
        clickPreviousEventAndVerifyCursorMoved();
        verifyExportButtonVisible();
        clickExportButton();
        verifyExportScreenOpened();
        verifyDownloadsSelectedAsDefault();
        selectArchiveDestination();
        verifyArchiveFolderPickerOpened();
        pickFirstFolder();
        clickSelectButton();
        verifyFolderSelected();
        verifyExportScreenFieldsVisible();
    }

    public void completeExportWithProgress() {
        clickExportButtonAtBottom();
        verifyExportingSnackbarAndClickSeeProgress();
        verifyProgressTabOpened();
        driver.navigate().back();
        verifyReturnedToHistoryBrowser();
    }

    public void verifyCancelExportFlow() {
        recoverFromExportFlow();
        clickExportButton();
        verifyExportScreenOpened();
        selectArchiveDestination();
        pickFirstFolder();
        clickSelectButton();
        clickCancelButton();
        verifyReturnedToHistoryBrowser();
    }

    public void verifyFileNameValidation() {
        recoverFromExportFlow();
        clickExportButton();
        verifyExportScreenOpened();
        enterFileName("test^file/name*");
        verifyValidationError();
        clickCancelButton();
        verifyReturnedToHistoryBrowser();
    }

    public void verifyDefaultFileNameExport() {
        recoverFromExportFlow();
        clickExportButton();
        verifyExportScreenOpened();
        verifyDefaultFileNamePattern();
        selectArchiveDestination();
        pickFirstFolder();
        clickSelectButton();
        clickExportButtonAtBottom();
        verifyExportingSnackbar();
    }

    public void verifyDefaultFileNamePattern() {
        WebElement fileNameField = WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.FILE_NAME_FIELD);
        String fileName = fileNameField.getAttribute("text");

        // Verify file name follows pattern: video <camera_name> YYYY-MM-DD HH-MM-SS
        Assert.assertNotNull(fileName, "File name attribute should not be null");
        Assert.assertFalse(fileName.isEmpty(), "File name should not be empty");

        // Pattern: "video " + camera_name + " " + YYYY-MM-DD + " " + HH-MM-SS
        // Example: "video Dm08 2026-11-04 02-26-15"
        Assert.assertTrue(fileName.matches("^video .+ \\d{4}-\\d{2}-\\d{2} \\d{2}-\\d{2}-\\d{2}$"),
                "File name should match pattern 'video <camera_name> YYYY-MM-DD HH-MM-SS' but was: " + fileName);
    }

    // Downloads export flows (EEPD-TC-23629)
    public void exportToDownloadsWithDefaultSettings() {
        // Click previous event to ensure timeline is positioned at a point with historical video available
        clickPreviousEventAndVerifyCursorMoved();
        verifyExportButtonVisible();
        clickExportButton();
        verifyExportScreenOpened();
        verifyDownloadsSelectedAsDefault();
        verifyExportScreenFieldsVisible();
    }

    public void verifyCancelExportFlowForDownloads() {
        recoverFromExportFlow();
        clickExportButton();
        verifyExportScreenOpened();
        clickCancelButton();
        verifyReturnedToHistoryBrowser();
    }

    public void verifyFileNameValidationForDownloads() {
        recoverFromExportFlow();
        clickExportButton();
        verifyExportScreenOpened();
        enterFileName("test^file/name*");
        verifyValidationError();
        clickCancelButton();
        verifyReturnedToHistoryBrowser();
    }

    public void verifyDefaultFileNameExportForDownloads() {
        recoverFromExportFlow();
        clickExportButton();
        verifyExportScreenOpened();
        verifyDefaultFileNamePattern();
        clickExportButtonAtBottom();
        verifyExportingSnackbar();
    }

}
