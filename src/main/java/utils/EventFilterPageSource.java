package utils;

import constants.UiTextConstants;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads the event type rows of the history browser's event filter sheet out of one page source snapshot, so every
 * title is paired with its own checkbox even while the list is still moving.
 */
public final class EventFilterPageSource {

    private EventFilterPageSource() {}

    /**
     * The event type rows in the snapshot, in list order, with whether each one is ticked. While the TIMELINE EVENTS
     * header is in the snapshot the overlay rows above it are skipped; once it has scrolled away every row left is
     * an event type.
     */
    public static Map<String, Boolean> eventTypeRows(String pageSource) {
        NodeList nodes = parse(pageSource).getElementsByTagName("*");
        boolean headerInSnapshot = false;
        for (int i = 0; i < nodes.getLength(); i++) {
            headerInSnapshot |= isTimelineEventsHeader((Element) nodes.item(i));
        }
        Map<String, Boolean> rows = new LinkedHashMap<>();
        boolean inEventTypes = !headerInSnapshot;
        String pendingTitle = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element node = (Element) nodes.item(i);
            String id = node.getAttribute("resource-id");
            if (isTimelineEventsHeader(node)) {
                inEventTypes = true;
            } else if (inEventTypes && isRowTitle(node, id)) {
                pendingTitle = node.getAttribute("text");
            } else if (inEventTypes && id.endsWith(":id/checkBox") && pendingTitle != null) {
                rows.put(pendingTitle, isTicked(node.getAttribute("checked")));
                pendingTitle = null;
            }
        }
        return rows;
    }

    /**
     * The checkbox state from its checked attribute. The app's checkboxes report their state only there:
     * isSelected() reads the selected attribute, which does not change when a row is ticked.
     */
    public static boolean isTicked(String checkedAttribute) {
        return "true".equals(checkedAttribute);
    }

    private static boolean isRowTitle(Element node, String id) {
        return id.endsWith(":id/title") && !node.getAttribute("text").equals(UiTextConstants.EVENT_FILTERS_TITLE);
    }

    private static boolean isTimelineEventsHeader(Element node) {
        return node.getAttribute("resource-id").endsWith(":id/header")
                && node.getAttribute("text").equals(UiTextConstants.TIMELINE_EVENTS_HEADER);
    }

    private static Document parse(String xml) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Could not read the page source of the event filter", e);
        }
    }
}
