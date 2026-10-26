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
     * XPath 1.0 string literal for {@code value}, which may contain quotes.
     * Handles single quotes, double quotes, and mixed quotes correctly.
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
