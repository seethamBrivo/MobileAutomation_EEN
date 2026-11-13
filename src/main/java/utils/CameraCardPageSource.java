package utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads the cards of the iOS Cameras list from one page source. iOS gives no element per card: a card is loose
 * siblings in the order name, 3-dots button ({@code ic_contextMenu}), optional icons, status, then the tag row as a
 * scroll view. The 3-dots button is what marks a name as a card, so a change in that order breaks this parser.
 */
public final class CameraCardPageSource {

    private static final String STATIC_TEXT = "XCUIElementTypeStaticText";
    private static final String BUTTON = "XCUIElementTypeButton";
    private static final String IMAGE = "XCUIElementTypeImage";
    private static final String SCROLL_VIEW = "XCUIElementTypeScrollView";
    private static final String MENU_BUTTON = "ic_contextMenu";

    private CameraCardPageSource() {}

    public static List<List<String>> visibleCards(String pageSource) {
        NodeList texts = parse(pageSource).getElementsByTagName(STATIC_TEXT);
        List<List<String>> cards = new ArrayList<>();
        for (int i = 0; i < texts.getLength(); i++) {
            Element name = (Element) texts.item(i);
            Element menu = nextElement(name);
            if (!isVisible(name) || menu == null || !isMenuButton(menu)) {
                continue;
            }
            Element status = nextElement(menu);
            while (status != null && IMAGE.equals(status.getTagName())) {
                status = nextElement(status);
            }
            if (status == null || !STATIC_TEXT.equals(status.getTagName()) || !isVisible(status)) {
                continue;
            }
            List<String> card = new ArrayList<>(List.of(label(name), label(status)));
            Element tags = nextElement(status);
            if (tags != null && SCROLL_VIEW.equals(tags.getTagName())) {
                NodeList tagTexts = tags.getElementsByTagName(STATIC_TEXT);
                for (int t = 0; t < tagTexts.getLength(); t++) {
                    card.add(label((Element) tagTexts.item(t)));
                }
            }
            cards.add(card);
        }
        return cards;
    }

    private static boolean isMenuButton(Element element) {
        return BUTTON.equals(element.getTagName()) && MENU_BUTTON.equals(element.getAttribute("name"));
    }

    private static boolean isVisible(Element element) {
        return "true".equals(element.getAttribute("visible"));
    }

    private static String label(Element element) {
        return element.getAttribute("name").trim();
    }

    private static Element nextElement(Element element) {
        Node sibling = element.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
    }

    private static Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // A device page source has no DOCTYPE; refusing one keeps external entities out.
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Could not read the page source of the camera list", e);
        }
    }
}
