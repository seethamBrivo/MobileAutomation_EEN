package utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.util.List;
import org.testng.annotations.Test;

public class CameraCardPageSourceTest {

    private static String text(String name, boolean visible) {
        return "<XCUIElementTypeStaticText name='" + name + "' visible='" + visible + "'/>";
    }

    private static String menu() {
        return "<XCUIElementTypeButton name='ic_contextMenu' visible='true'/>";
    }

    private static String image(String name) {
        return "<XCUIElementTypeImage name='" + name + "' visible='true'/>";
    }

    private static String tags(String... names) {
        StringBuilder row = new StringBuilder("<XCUIElementTypeScrollView visible='true'><XCUIElementTypeOther>");
        for (String name : names) {
            row.append(text(name, true));
        }
        return row.append("</XCUIElementTypeOther></XCUIElementTypeScrollView>").toString();
    }

    private static String snapshot(String... nodes) {
        return "<XCUIElementTypeApplication><XCUIElementTypeOther>" + String.join("", nodes)
                + "</XCUIElementTypeOther></XCUIElementTypeApplication>";
    }

    @Test
    public void cardKeepsItsNameStatusAndTagsTogether() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(snapshot(
                text("Camera A", true), menu(), text("Online", true), tags("lobby", "door"),
                text("Camera B", true), menu(), text("Offline", true)));

        assertEquals(cards, List.of(List.of("Camera A", "Online", "lobby", "door"), List.of("Camera B", "Offline")),
                "Each card should hold its own name, status and tags");
    }

    @Test
    public void sharedIconBetweenTheMenuAndTheStatusIsSkipped() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(snapshot(
                image("ic_no_permissions_status"), text("Camera A", true), menu(), image("ic_shared"),
                text("No permission", true)));

        assertEquals(cards, List.of(List.of("Camera A", "No permission")),
                "The icons around the card should not be read as its status");
    }

    @Test
    public void cardsScrolledOutOfViewOrInHalfwayAreLeftOut() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(snapshot(
                text("Camera A", true), menu(), text("Online", true),
                text("Camera B", true), menu(), text("Online", false),
                text("Camera C", false), menu(), text("Online", false)));

        assertEquals(cards, List.of(List.of("Camera A", "Online")),
                "Only the card whose name and status are both visible should be read");
    }

    @Test
    public void textsThatAreNotCardNamesAreIgnored() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(snapshot(
                text("684 cameras", true), text("Connection", true), text("Camera A", true), menu(),
                text("Online", true)));

        assertEquals(cards, List.of(List.of("Camera A", "Online")),
                "The counter and the filter chips are not cards");
    }

    @Test
    public void unreadableSnapshotFailsWithAMessageNamingTheCameraList() {
        IllegalStateException failure = expectThrows(IllegalStateException.class,
                () -> CameraCardPageSource.visibleCards("<hierarchy>"));

        assertTrue(failure.getMessage().contains("camera list"),
                "Message should name the camera list but was '" + failure.getMessage() + "'");
    }
}
