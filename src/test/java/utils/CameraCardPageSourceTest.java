package utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.testng.annotations.Test;

/**
 * Most snapshots here are hand-built, in the order {@link CameraCardPageSource} relies on: iOS lists a card as loose
 * siblings, in the order name, 3-dots button ({@code ic_contextMenu}), optional icons, status, and then the tag row
 * as a scroll view. One test reads {@code fixtures/cameras-list-ios-page-source.xml}, the Cameras list of the TS02
 * account captured from a device and cut down to two cards and one card scrolled out of view.
 */
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
    public void realPageSourceFromADeviceIsReadCardByCard() throws IOException {
        String pageSource;
        try (var fixture = getClass().getResourceAsStream("/fixtures/cameras-list-ios-page-source.xml")) {
            pageSource = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertEquals(CameraCardPageSource.visibleCards(pageSource), List.of(
                List.of("000 Camera 90 days", "Online", "parking lot 1", "hikvision ds-2cd2045fwd-i", "test5", "000",
                        "analog camera"),
                List.of("000 Camera Edge-1", "Online", "000")),
                "The real page source should give the two cards on screen and leave out the card scrolled away");
    }

    @Test
    public void unreadableSnapshotFailsWithAMessageNamingTheCameraList() {
        IllegalStateException failure = expectThrows(IllegalStateException.class,
                () -> CameraCardPageSource.visibleCards("<hierarchy>"));

        assertTrue(failure.getMessage().contains("camera list"),
                "Message should name the camera list but was '" + failure.getMessage() + "'");
    }
}
