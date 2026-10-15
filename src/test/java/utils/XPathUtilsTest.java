package utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** Evaluates the generated XPath against a small Android-style hierarchy (no device needed). */
public class XPathUtilsTest {

    private static final String HIERARCHY =
            "<hierarchy>"
                    + "<android.widget.TextView text='CAMERAS DEFAULTS'/>"
                    + "<android.widget.TextView text='Cameras defaults'/>"
                    + "<android.widget.TextView text='  cameras defaults '/>"
                    + "<android.widget.TextView text='Cameras defaults extra'/>"
                    + "<android.widget.Button text='Cameras defaults'/>"
                    + "</hierarchy>";

    private static int matches(String xpath) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(HIERARCHY)));
        NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, doc, XPathConstants.NODESET);
        return nodes.getLength();
    }

    @Test(description = "matches either casing and surrounding whitespace, but only exact TextViews")
    public void matchesAnyCasingOfExactText() throws Exception {
        assertEquals(matches(XPathUtils.textViewIgnoreCase("Cameras defaults")), 3);
        assertEquals(matches(XPathUtils.textViewIgnoreCase("CAMERAS DEFAULTS")), 3);
    }

    @Test(description = "does not match a different label")
    public void doesNotMatchOtherText() throws Exception {
        assertEquals(matches(XPathUtils.textViewIgnoreCase("Layouts")), 0);
    }

    @Test(description = "an apostrophe would break the expression, so it is rejected up front")
    public void rejectsApostrophe() {
        assertThrows(IllegalArgumentException.class, () -> XPathUtils.textViewIgnoreCase("Don't"));
    }
}
