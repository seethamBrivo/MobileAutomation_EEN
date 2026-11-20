package utils;

import static org.testng.Assert.assertEquals;

import java.util.List;
import org.testng.annotations.Test;

/**
 * Reads the iOS Cameras list from small page source snapshots (no device needed). The element order is the one seen
 * on an iPhone 17 Pro Max: thumbnail, name, status dot (and the shared icon), status, 3-dots button, tag row.
 */
public class CameraCardPageSourceTest {

    private static String thumbnail() {
        return "<XCUIElementTypeImage name='' visible='true' width='96' height='54'/>";
    }

    private static String dot() {
        return "<XCUIElementTypeImage name='' visible='true' width='8' height='9'/>";
    }

    private static String sharedIcon() {
        return "<XCUIElementTypeImage name='ic_shared' visible='true' width='20' height='20'/>";
    }

    private static String text(String name, boolean visible) {
        return "<XCUIElementTypeStaticText name='" + name + "' visible='" + visible + "' width='100'/>";
    }

    private static String menuButton() {
        return "<XCUIElementTypeButton name='ic_contextMenu' visible='true' width='44'/>";
    }

    private static String tagRow(String... tags) {
        StringBuilder row = new StringBuilder("<XCUIElementTypeScrollView visible='true'>");
        for (String tag : tags) {
            row.append(text(tag, true));
        }
        return row.append("</XCUIElementTypeScrollView>").toString();
    }

    private static String list(String... parts) {
        return "<XCUIElementTypeApplication><XCUIElementTypeOther>" + String.join("", parts)
                + "</XCUIElementTypeOther></XCUIElementTypeApplication>";
    }

    @Test
    public void aCardIsReadAsNameThenStatusThenItsTags() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(list(
                thumbnail(), text("Fisheye Camera", true), dot(), text("Online", true), menuButton(),
                tagRow("en-cduf-003", "ts55")));

        assertEquals(cards, List.of(List.of("Fisheye Camera", "Online", "en-cduf-003", "ts55")),
                "The name comes first, then the status, then the tags");
    }

    @Test
    public void theTextBeforeTheThreeDotsButtonIsTheStatusAndNotTheName() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(list(
                thumbnail(), text("Off Camera", true), dot(), text("Online", true), menuButton(),
                thumbnail(), text("Sim Camera", true), dot(), text("Offline", true), menuButton()));

        assertEquals(cards.stream().map(card -> card.get(0)).toList(), List.of("Off Camera", "Sim Camera"),
                "Names should be the texts after the thumbnails, not the statuses that precede the buttons");
        assertEquals(cards.stream().map(card -> card.get(1)).toList(), List.of("Online", "Offline"),
                "Each card should keep its own status");
    }

    @Test
    public void theSharedIconBetweenTheNameAndTheStatusDoesNotShiftTheStatus() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(list(
                thumbnail(), text("7216HQI (Camera 02) -Fixed", true), sharedIcon(), dot(), text("Online", true),
                menuButton(), tagRow("hik")));

        assertEquals(cards, List.of(List.of("7216HQI (Camera 02) -Fixed", "Online", "hik")),
                "Icons between the name and the status should be skipped");
    }

    @Test
    public void aCardWithoutATagRowHasOnlyANameAndAStatus() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(list(
                thumbnail(), text("009-3", true), dot(), text("Online", true), menuButton(),
                thumbnail(), text("Dm08", true), dot(), text("Turned off", true), menuButton()));

        assertEquals(cards, List.of(List.of("009-3", "Online"), List.of("Dm08", "Turned off")),
                "A card with no tag row should have two texts, and the next card should not take its tags");
    }

    @Test
    public void aCardScrolledOutOfViewIsLeftOut() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(list(
                thumbnail(), text("Hidden Camera", false), dot(), text("Online", true), menuButton(),
                thumbnail(), text("Shown Camera", true), dot(), text("Online", true), menuButton()));

        assertEquals(cards.stream().map(card -> card.get(0)).toList(), List.of("Shown Camera"),
                "A name that is not visible should not be listed");
    }

    @Test
    public void textsThatDoNotFollowAThumbnailAreNotCards() {
        List<List<String>> cards = CameraCardPageSource.visibleCards(list(
                text("696 cameras", true), tagRow("Connection", "Status"),
                thumbnail(), text("Sim Camera", true), dot(), text("Online", true), menuButton()));

        assertEquals(cards, List.of(List.of("Sim Camera", "Online")),
                "The camera counter and the filter chips should not be read as cards");
    }
}
