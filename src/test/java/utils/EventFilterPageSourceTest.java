package utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

/** Reads event filter rows from small page source snapshots (no device needed). */
public class EventFilterPageSourceTest {

    private static final String ID = "com.een.vms.android:id/";

    private static String node(String id, String text, String checked) {
        return "<android.view.View resource-id='" + ID + id + "' text='" + text + "' checked='" + checked + "'/>";
    }

    private static String snapshot(String... nodes) {
        return "<hierarchy>" + String.join("", nodes) + "</hierarchy>";
    }

    @Test
    public void rowsBelowTheTimelineEventsHeaderAreReadInListOrder() {
        Map<String, Boolean> rows = EventFilterPageSource.eventTypeRows(snapshot(
                node("title", "Event filters", "false"),
                node("header", "OVERLAYS", "false"),
                node("title", "Zones", "false"),
                node("checkBox", "", "true"),
                node("header", "TIMELINE EVENTS", "false"),
                node("title", "Motion", "false"),
                node("checkBox", "", "true"),
                node("title", "Tamper", "false"),
                node("checkBox", "", "false")));

        assertEquals(List.copyOf(rows.keySet()), List.of("Motion", "Tamper"),
                "Overlay rows above the TIMELINE EVENTS header should be skipped");
        assertEquals(rows, Map.of("Motion", true, "Tamper", false), "Each row should keep its own tick state");
    }

    @Test
    public void everyRowIsReadOnceTheHeaderHasScrolledAway() {
        Map<String, Boolean> rows = EventFilterPageSource.eventTypeRows(snapshot(
                node("title", "Motion", "false"),
                node("checkBox", "", "false"),
                node("title", "Tamper", "false"),
                node("checkBox", "", "true")));

        assertEquals(rows, Map.of("Motion", false, "Tamper", true),
                "Without the header in view every row left is an event type");
    }

    @Test
    public void noRowIsReadWhileOnlyTheOverlaySectionIsShown() {
        Map<String, Boolean> rows = EventFilterPageSource.eventTypeRows(snapshot(
                node("header", "OVERLAYS", "false"),
                node("title", "Zones", "false"),
                node("checkBox", "", "true"),
                node("header", "TIMELINE EVENTS", "false")));

        assertTrue(rows.isEmpty(), "The overlay section is not an event type but rows were read: " + rows);
    }

    @Test
    public void onlyTrueCountsAsTicked() {
        assertTrue(EventFilterPageSource.isTicked("true"), "'true' should be ticked");
        assertFalse(EventFilterPageSource.isTicked("false"), "'false' should not be ticked");
        assertFalse(EventFilterPageSource.isTicked(""), "A missing attribute should not be ticked");
        assertFalse(EventFilterPageSource.isTicked(null), "A null attribute should not be ticked");
    }

    @Test
    public void unreadableSnapshotFailsWithAMessageNamingTheFilter() {
        IllegalStateException failure = expectThrows(IllegalStateException.class,
                () -> EventFilterPageSource.eventTypeRows("<hierarchy>"));

        assertTrue(failure.getMessage().contains("event filter"),
                "Message should name the event filter but was '" + failure.getMessage() + "'");
    }
}
