package utils;

/** Small helpers for building XPath expressions that survive cosmetic UI text changes. */
public final class XPathUtils {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private XPathUtils() {
    }

    /**
     * Matches a {@code TextView} whose text equals {@code text} regardless of case. The app switched
     * its section headers from "CAMERAS DEFAULTS" to "Cameras defaults" between builds, which broke
     * every locator written against one casing. Apostrophes are not supported in {@code text}.
     */
    public static String textViewIgnoreCase(String text) {
        if (text.contains("'")) {
            throw new IllegalArgumentException("text must not contain an apostrophe: " + text);
        }
        return "//android.widget.TextView[translate(normalize-space(@text),'" + LOWER + "','" + UPPER
                + "')='" + text.trim().toUpperCase(java.util.Locale.ROOT) + "']";
    }

    /**
     * Escapes a string value for safe use in an XPath expression.
     * XPath 1.0 has no escape mechanism, so strings containing both single and double quotes
     * must be built with concat().
     *
     * @param value the string to escape
     * @return an XPath string literal (quoted or concat expression)
     */
    public static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + String.join("',\"'\",'", value.split("'", -1)) + "')";
    }
}
