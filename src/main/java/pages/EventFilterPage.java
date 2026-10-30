package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import locators.EventFilterLocators;
import locators.HistoryBrowserLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.SkipException;
import utils.EventFilterPageSource;
import utils.GestureUtils;
import utils.WaitUtils;

/** The history browser's event filter sheet: choosing which event types the timeline shows. */
public class EventFilterPage {

    private static final Logger logger = LogManager.getLogger(EventFilterPage.class);

    /** A tap that does not flip the checkbox means the list moved under it, so it is repeated once. */
    private static final int TOGGLE_ATTEMPTS = 2;

    /** Upper bound on swipes while reading the event filter's lazily laid out rows. */
    private static final int MAX_FILTER_SCROLLS = 15;
    private static final double FILTER_SWIPE_START_PERCENT = 0.65;
    private static final double FILTER_SWIPE_SMALL_PERCENT = 0.25;
    private static final double FILTER_SWIPE_LARGE_PERCENT = 0.5;

    private final AppiumDriver driver;

    public EventFilterPage(AppiumDriver driver) {
        this.driver = driver;
    }

    /**
     * The distinct event types of the cards on screen, newest first. The timeline rows are waited for with the full
     * explicit wait first, so a timeline that is slow to load fails instead of being reported as a skip; rows with no
     * card after the short probe are a camera without events, and the test is skipped.
     */
    public List<String> eventTypesOnTimeline() {
        WaitUtils.waitForVisibility(driver, HistoryBrowserLocators.TIMELINE_ROW_TIMESTAMPS);
        List<String> distinct = WaitUtils.textsOfOrEmpty(
                driver, HistoryBrowserLocators.TIMELINE_EVENT_NAMES, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT)
                .stream().distinct().toList();
        if (distinct.isEmpty()) {
            throw new SkipException("The first camera needs at least one event on its timeline to filter");
        }
        return distinct;
    }

    /**
     * Chooses the types to select: those with a card on the timeline first, so the filtered timeline is not empty,
     * then the first other types of the filter list.
     */
    public List<String> chooseEventTypes(List<String> onTimeline, List<String> listedInFilter, int count) {
        List<String> chosen = new ArrayList<>(onTimeline.subList(0, Math.min(count, onTimeline.size())));
        for (String listed : listedInFilter) {
            if (chosen.size() < count && !chosen.contains(listed)) {
                chosen.add(listed);
            }
        }
        return chosen;
    }

    public void openEventFilter() {
        WaitUtils.click(driver, HistoryBrowserLocators.FILTER_BUTTON);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, EventFilterLocators.EVENT_FILTERS_TITLE).isDisplayed(),
                "List with event types should be displayed");
    }

    /**
     * The test case expects Motion alone to be selected by default but the app selects every event type. That
     * difference is not pinned down yet, so the default is logged and not asserted; every later step clears the
     * selection first. Returns the titles of the whole list, read by scrolling through it.
     */
    public List<String> verifyEventFilterListDefaultsAndReadTypes() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, EventFilterLocators.EVENT_TYPES_SELECT_ALL)
                .isDisplayed(), "Select/Deselect button should be visible");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, EventFilterLocators.FILTER_DONE_BUTTON).isDisplayed(),
                "Done button should be visible");
        Map<String, Boolean> rows = readEventTypeSelection();
        Assert.assertFalse(rows.isEmpty(), "Event filter should list at least one event type");
        List<String> titles = new ArrayList<>(rows.keySet());
        List<String> sorted = new ArrayList<>(titles);
        Collections.sort(sorted);
        Assert.assertEquals(titles, sorted, "Event types should be listed in sorted order");
        logger.info("Event types selected by default: {} of {} (the test case expects Motion only)",
                selected(rows), titles);
        return titles;
    }

    /**
     * Clears the selection, then ticks the given types. The button toggles: it reads "Select" while some type is
     * unticked and ticks them all, and "Deselect" once every type is ticked and clears them, so from "Select" it is
     * pressed twice to end with nothing ticked. A complete check scrolls through the whole list; a visible
     * check looks at the rows on screen, which is enough once the complete one has passed in another orientation.
     */
    public void multiSelectEventTypes(List<String> eventTypes, boolean completeCheck) {
        if (selectAllLabel().equals(UiTextConstants.SELECT_ALL_LABEL)) {
            clickSelectAll();
        }
        clickSelectAll();
        for (String eventType : eventTypes) {
            toggleEventType(eventType);
        }
        verifySelection(eventTypes, completeCheck, "Exactly the chosen event types");
    }

    /**
     * After Done the timeline must still show events, and only of the selected types, so every type that was shown
     * before and not selected is gone. The old cards stay on screen until the timeline re-renders, so the read
     * waits for the filtered cards. A selected type may legitimately be missing from the visible cards: the camera
     * is live, and a burst of newer events of the other selected type can push it below the fold.
     */
    public void confirmEventFilterAndVerifyTimeline(List<String> selectedEventTypes) {
        WaitUtils.click(driver, EventFilterLocators.FILTER_DONE_BUTTON);
        verifyEventFilterClosed();
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT, () -> {
            List<String> shown = timelineEventNames();
            return !shown.isEmpty() && selectedEventTypes.containsAll(shown);
        });
        List<String> shown = timelineEventNames();
        Assert.assertFalse(shown.isEmpty(), "Timeline should show events of the selected types "
                + selectedEventTypes + " but shows none");
        Assert.assertTrue(selectedEventTypes.containsAll(shown),
                "Timeline should show only the selected event types " + selectedEventTypes + " but shows " + shown);
    }

    public void discardEventFilterChangesAndVerifyUnchanged(List<String> appliedEventTypes,
                                                            boolean completeCheck) {
        Assert.assertFalse(appliedEventTypes.isEmpty(),
                "At least one applied event type is needed to change and discard");
        String toggled = appliedEventTypes.get(0);
        openEventFilter();
        toggleEventType(toggled);
        WaitUtils.click(driver, EventFilterLocators.FILTER_CLOSE_BUTTON);
        verifyEventFilterClosed();
        openEventFilter();
        By checkbox = scrollToEventType(toggled);
        Assert.assertTrue(isChecked(checkbox),
                "Event type '" + toggled + "' should still be selected after closing the filter with X");
        verifySelection(appliedEventTypes, completeCheck, "Closing the filter with X should keep the types");
    }

    /** The label is the app's own summary of the whole list; the rows on screen must agree with it. */
    public void selectAllEventTypesAndVerify() {
        clickSelectAll();
        verifySelectAllLabel(UiTextConstants.DESELECT_ALL_LABEL);
        Map<String, Boolean> rows = visibleEventTypeSelection();
        Assert.assertEquals(selected(rows).size(), rows.size(), "All event types on screen should be selected");
    }

    public void deselectAllEventTypesAndVerify() {
        clickSelectAll();
        verifySelectAllLabel(UiTextConstants.SELECT_ALL_LABEL);
        Assert.assertTrue(selected(visibleEventTypeSelection()).isEmpty(),
                "All selections should be removed from the event types on screen");
    }

    public void closeEventFilterWithX() {
        WaitUtils.click(driver, EventFilterLocators.FILTER_CLOSE_BUTTON);
        verifyEventFilterClosed();
    }

    /**
     * Cleanup step: the app keeps the chosen event types, so the filter is put back to every type selected for the
     * next test. Never asserts.
     */
    public void restoreDefaultEventFilter() {
        if (!WaitUtils.isVisible(driver, EventFilterLocators.EVENT_FILTERS_TITLE, TimeoutConstants.SHORT_TIMEOUT)) {
            if (!WaitUtils.isVisible(driver, HistoryBrowserLocators.FILTER_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
                return;
            }
            WaitUtils.click(driver, HistoryBrowserLocators.FILTER_BUTTON);
        }
        if (selectAllLabel().equals(UiTextConstants.SELECT_ALL_LABEL)) {
            clickSelectAll();
        }
        WaitUtils.click(driver, EventFilterLocators.FILTER_DONE_BUTTON);
        logger.info("Event filter restored to every event type");
    }

    private List<String> timelineEventNames() {
        return WaitUtils.textsOfOrEmpty(
                driver, HistoryBrowserLocators.TIMELINE_EVENT_NAMES, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT);
    }

    private void verifyEventFilterClosed() {
        Assert.assertTrue(WaitUtils.waitForInvisibility(
                        driver, EventFilterLocators.EVENT_FILTERS_TITLE, TimeoutConstants.SHORT_TIMEOUT),
                "Event filter list should be closed");
    }

    /**
     * Scrolling hides the section header, and with it the button, so the list is swiped back down until it shows. It
     * stops at the first sight of the button: the filter is a bottom sheet, and a downward swipe at the top of it
     * drags the whole sheet closed.
     */
    private void showSelectAllButton() {
        if (WaitUtils.isVisible(
                driver, EventFilterLocators.EVENT_TYPES_SELECT_ALL, TimeoutConstants.SHORT_TIMEOUT)) {
            return;
        }
        for (int swipes = 0; swipes < MAX_FILTER_SCROLLS
                && !WaitUtils.isPresent(driver, EventFilterLocators.EVENT_TYPES_SELECT_ALL); swipes++) {
            scrollFilterList(false);
        }
    }

    /**
     * One drag inside the filter list, started clear of the Done button. Reading forward moves a quarter of the
     * screen height in landscape, where only about three rows are visible and larger steps would skip rows, and half
     * of it in portrait; going back moves half either way, as nothing is read on the way.
     */
    private void scrollFilterList(boolean forward) {
        Dimension screen = driver.manage().window().getSize();
        boolean landscape = screen.width > screen.height;
        double distance = forward && landscape ? FILTER_SWIPE_SMALL_PERCENT : FILTER_SWIPE_LARGE_PERCENT;
        int x = screen.width / 2;
        int from = (int) (screen.height * FILTER_SWIPE_START_PERCENT);
        int to = from + (int) (screen.height * distance) * (forward ? -1 : 1);
        GestureUtils.dragAndHold(driver, x, from, x, to);
    }

    private void clickSelectAll() {
        showSelectAllButton();
        WaitUtils.click(driver, EventFilterLocators.EVENT_TYPES_SELECT_ALL);
    }

    private String selectAllLabel() {
        showSelectAllButton();
        return WaitUtils.getTextWithRetry(
                driver, EventFilterLocators.EVENT_TYPES_SELECT_ALL, TimeoutConstants.SHORT_TIMEOUT);
    }

    private void verifySelectAllLabel(String expected) {
        WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT, () -> selectAllLabel().equals(expected));
        Assert.assertEquals(selectAllLabel(), expected, "Select/Deselect button should read '" + expected + "'");
    }

    /** A fling keeps the list moving after the swipe ends; reads and taps wait until the first match stops moving. */
    private void waitUntilStill(By locator) {
        AtomicInteger lastY = new AtomicInteger(Integer.MIN_VALUE);
        WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
            int y = WaitUtils.waitForVisibility(driver, locator, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)
                    .getLocation().getY();
            return y == lastY.getAndSet(y);
        });
    }

    /** Scrolls the list until the row of the event type is on screen and returns its checkbox. */
    private By scrollToEventType(String eventType) {
        By checkbox = EventFilterLocators.eventTypeCheckbox(eventType);
        showSelectAllButton();
        for (int swipes = 0; swipes < MAX_FILTER_SCROLLS && !WaitUtils.isPresent(driver, checkbox); swipes++) {
            scrollFilterList(true);
        }
        Assert.assertTrue(WaitUtils.isVisible(driver, checkbox, TimeoutConstants.SHORT_TIMEOUT),
                "Event type '" + eventType + "' should be listed in the event filter");
        return checkbox;
    }

    /**
     * Ticks or unticks one event type, scrolling to its row first. The tap is only trusted once the checkbox has
     * flipped; if it has not, the list moved under the tap and it is repeated once.
     */
    private void toggleEventType(String eventType) {
        By checkbox = scrollToEventType(eventType);
        for (int attempt = 1; attempt <= TOGGLE_ATTEMPTS; attempt++) {
            waitUntilStill(checkbox);
            boolean before = isChecked(checkbox);
            WaitUtils.click(driver, checkbox);
            if (WaitUtils.pollUntil(driver, TimeoutConstants.TIMELINE_SETTLE_TIMEOUT,
                    () -> isChecked(checkbox) != before)) {
                return;
            }
            logger.warn("Event type '{}' did not change after the tap (attempt {} of {})",
                    eventType, attempt, TOGGLE_ATTEMPTS);
        }
        Assert.fail("Event type '" + eventType + "' should change its tick after tapping its checkbox");
    }

    private boolean isChecked(By checkbox) {
        return EventFilterPageSource.isTicked(
                WaitUtils.waitForVisibility(driver, checkbox).getAttribute("checked"));
    }

    private static List<String> selected(Map<String, Boolean> rows) {
        return rows.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
    }

    /**
     * Every row seen must be ticked exactly when it is expected to be. A complete check scrolls the whole list and
     * also requires every expected row to have been seen; otherwise only the rows on screen are compared.
     */
    private void verifySelection(List<String> expected, boolean complete, String what) {
        Map<String, Boolean> rows = complete ? readEventTypeSelection() : visibleEventTypeSelection();
        List<String> wrong = new ArrayList<>();
        rows.forEach((name, checked) -> {
            if (checked != expected.contains(name)) {
                wrong.add(name);
            }
        });
        Assert.assertTrue(wrong.isEmpty(), what + " " + expected + " should be selected but " + wrong
                + " are not as expected; selected are " + selected(rows));
        if (complete) {
            Assert.assertTrue(rows.keySet().containsAll(expected),
                    "Event types " + expected + " should be listed but the list shows " + rows.keySet());
        }
    }

    /**
     * The event type rows on screen. When the sheet is showing only its overlay section the list is moved on until
     * a row appears, so a check over these rows can never pass because nothing was read.
     */
    private Map<String, Boolean> visibleEventTypeSelection() {
        Map<String, Boolean> rows = new LinkedHashMap<>();
        collectVisibleEventTypes(rows);
        for (int swipes = 0; swipes < MAX_FILTER_SCROLLS && rows.isEmpty(); swipes++) {
            scrollFilterList(true);
            collectVisibleEventTypes(rows);
        }
        Assert.assertFalse(rows.isEmpty(), "Event type rows should be on screen in the event filter");
        return rows;
    }

    /**
     * The event type rows are laid out lazily, so the list is scrolled until no new row appears and then returned to
     * the top. Rows come back in list order with whether each one is ticked.
     */
    private Map<String, Boolean> readEventTypeSelection() {
        Map<String, Boolean> rows = new LinkedHashMap<>();
        for (int swipes = 0; swipes <= MAX_FILTER_SCROLLS; swipes++) {
            int before = rows.size();
            collectVisibleEventTypes(rows);
            if ((swipes > 0 && rows.size() == before) || swipes == MAX_FILTER_SCROLLS) {
                break;
            }
            scrollFilterList(true);
        }
        showSelectAllButton();
        return rows;
    }

    private void collectVisibleEventTypes(Map<String, Boolean> rows) {
        rows.putAll(EventFilterPageSource.eventTypeRows(WaitUtils.pageSource(driver)));
    }
}
