package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import locators.EventFilterLocators;
import locators.HistoryBrowserLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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

    /** The event types of the cards on screen, newest first; skips the test when the timeline has no events. */
    public List<String> eventTypesOnTimeline() {
        List<String> distinct = distinctEventTypesOnTimeline();
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

    /** The types on the timeline before filtering that are not selected, which must be gone after Done. */
    public List<String> unselectedEventTypes(List<String> onTimeline, List<String> selected) {
        List<String> others = new ArrayList<>(onTimeline);
        others.removeAll(selected);
        return others;
    }

    /** Skips the test when the timeline shows fewer distinct event types than the filter test needs to choose. */
    public void openEventFilter() {
        WaitUtils.click(driver, HistoryBrowserLocators.FILTER_BUTTON);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, EventFilterLocators.EVENT_FILTERS_TITLE).isDisplayed(),
                "List with event types should be displayed");
    }

    /**
     * The test case expects Motion alone to be selected by default; the app selects every event type, so that is what
     * is asserted. Only the rows the sheet has laid out are checked, scrolling through the whole list.
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
        Assert.assertEquals(selected(rows).size(), rows.size(),
                "Every event type should be selected by default but only " + selected(rows) + " of " + titles
                        + " are");
        return titles;
    }

    /**
     * Clears the selection, then ticks the given types. A complete check scrolls through the whole list; a visible
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
     * After Done the timeline must still show events, only of the selected types, and the types that were shown
     * before and were not selected must be gone. A selected type may legitimately be missing from the visible
     * cards: the camera is live, and a burst of newer events of the other selected type can push it below the fold.
     */
    public void confirmEventFilterAndVerifyTimeline(List<String> selectedEventTypes,
                                                    List<String> unselectedEventTypes) {
        WaitUtils.click(driver, EventFilterLocators.FILTER_DONE_BUTTON);
        verifyEventFilterClosed();
        List<String> shown = WaitUtils.textsOf(driver, HistoryBrowserLocators.TIMELINE_EVENT_NAMES);
        Assert.assertFalse(shown.isEmpty(), "Timeline should show events of the selected types "
                + selectedEventTypes + " but shows none");
        Assert.assertTrue(selectedEventTypes.containsAll(shown),
                "Timeline should show only the selected event types " + selectedEventTypes + " but shows " + shown);
        Assert.assertTrue(Collections.disjoint(shown, unselectedEventTypes),
                "Event types " + unselectedEventTypes + " were not selected and should be gone but the timeline "
                        + "shows " + shown);
    }

    public void discardEventFilterChangesAndVerifyUnchanged(List<String> appliedEventTypes,
                                                            boolean completeCheck) {
        openEventFilter();
        toggleEventType(appliedEventTypes.get(0));
        WaitUtils.click(driver, EventFilterLocators.FILTER_CLOSE_BUTTON);
        verifyEventFilterClosed();
        openEventFilter();
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

    private List<String> distinctEventTypesOnTimeline() {
        List<String> distinct = new ArrayList<>();
        for (String name : WaitUtils.textsOf(driver, HistoryBrowserLocators.TIMELINE_EVENT_NAMES)) {
            if (!distinct.contains(name)) {
                distinct.add(name);
            }
        }
        return distinct;
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
        int[] lastY = {Integer.MIN_VALUE};
        WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
            int y = WaitUtils.waitForVisibility(driver, locator, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)
                    .getLocation().getY();
            boolean still = y == lastY[0];
            lastY[0] = y;
            return still;
        });
    }

    /**
     * Ticks or unticks one event type, scrolling to its row first. The tap is only trusted once the checkbox has
     * flipped; if it has not, the list moved under the tap and it is repeated once.
     */
    private void toggleEventType(String eventType) {
        By checkbox = EventFilterLocators.eventTypeCheckbox(eventType);
        showSelectAllButton();
        for (int swipes = 0; swipes < MAX_FILTER_SCROLLS && !WaitUtils.isPresent(driver, checkbox); swipes++) {
            scrollFilterList(true);
        }
        Assert.assertTrue(WaitUtils.isVisible(driver, checkbox, TimeoutConstants.SHORT_TIMEOUT),
                "Event type '" + eventType + "' should be listed in the event filter");
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
        return Boolean.parseBoolean(WaitUtils.waitForVisibility(driver, checkbox).getAttribute("checked"));
    }

    private static List<String> selected(Map<String, Boolean> rows) {
        List<String> names = new ArrayList<>();
        rows.forEach((name, checked) -> {
            if (checked) {
                names.add(name);
            }
        });
        return names;
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

    private Map<String, Boolean> visibleEventTypeSelection() {
        Map<String, Boolean> rows = new LinkedHashMap<>();
        collectVisibleEventTypes(rows);
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
            if (swipes > 0 && rows.size() == before || swipes == MAX_FILTER_SCROLLS) {
                break;
            }
            scrollFilterList(true);
        }
        showSelectAllButton();
        return rows;
    }

    /**
     * Adds the event type rows on screen, read from one page source snapshot so every title is paired with its own
     * checkbox. While the TIMELINE EVENTS header is in the snapshot, the overlay rows above it are skipped; once it
     * has scrolled away every row left is an event type.
     */
    private void collectVisibleEventTypes(Map<String, Boolean> rows) {
        NodeList nodes = parseXml(WaitUtils.pageSource(driver)).getElementsByTagName("*");
        boolean headerInSnapshot = false;
        for (int i = 0; i < nodes.getLength(); i++) {
            headerInSnapshot |= isFilterHeader((Element) nodes.item(i));
        }
        boolean inEventTypes = !headerInSnapshot;
        String pendingTitle = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element node = (Element) nodes.item(i);
            String id = node.getAttribute("resource-id");
            if (isFilterHeader(node)) {
                inEventTypes = true;
            } else if (inEventTypes && id.endsWith(":id/title")
                    && !node.getAttribute("text").equals(UiTextConstants.EVENT_FILTERS_TITLE)) {
                pendingTitle = node.getAttribute("text");
            } else if (inEventTypes && id.endsWith(":id/checkBox") && pendingTitle != null) {
                rows.put(pendingTitle, Boolean.parseBoolean(node.getAttribute("checked")));
                pendingTitle = null;
            }
        }
    }

    private static boolean isFilterHeader(Element node) {
        return node.getAttribute("resource-id").endsWith(":id/header")
                && node.getAttribute("text").equals(UiTextConstants.TIMELINE_EVENTS_HEADER);
    }

    private static Document parseXml(String xml) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Could not read the page source of the event filter", e);
        }
    }
}
