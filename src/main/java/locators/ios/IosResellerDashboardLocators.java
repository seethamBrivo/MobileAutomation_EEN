package locators.ios;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * iOS locators for the reseller dashboard: the account list with its cards, the card's context menu, the top
 * bar and the bottom navigation.
 */
public final class IosResellerDashboardLocators {

    private IosResellerDashboardLocators() {}

    public static final By DASHBOARD_TITLE = AppiumBy.accessibilityId("Reseller dashboard");

    /** "13 accounts" / "1 account". */
    public static final By TOTAL_ACCOUNTS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name MATCHES '^[0-9]+ accounts?$'");

    // --- Account card ----------------------------------------------------------

    public static final By LARGE_CARD_ICON = AppiumBy.accessibilityId("largeCardIcon");

    public static final By BRIDGE_ICON = AppiumBy.accessibilityId("ic_bridgeDevice");

    public static final By CAMERA_ICON = AppiumBy.accessibilityId("ic_camera");

    /** The account's name is a text named after itself. */
    public static By accountName(String accountName) {
        return AppiumBy.accessibilityId(accountName);
    }

    /**
     * The coloured status dots of the account's camera row, in order: 1 green (online), 2 red (offline),
     * 3 yellow (warning), 4 grey (other). The dots are unlabelled images placed after the card's camera icon, so
     * they are found by that position; their colour is not exposed to the accessibility tree.
     */
    public static By cameraStatusDot(String accountName, int position) {
        if (position < 1 || position > 4) {
            throw new IllegalArgumentException("Status dot position must be 1-4, got: " + position);
        }
        return AppiumBy.xpath("//XCUIElementTypeStaticText[@name=" + xpathLiteral(accountName) + "]"
                + "/following-sibling::XCUIElementTypeImage[@name='ic_camera'][1]"
                + "/following-sibling::XCUIElementTypeImage[not(@name) or @name=''][" + position + "]");
    }

    // --- Card context menu -----------------------------------------------------

    public static final By THREE_DOTS_BUTTON = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND name == 'ic_contextMenu' AND visible == 1");

    public static final By CONTEXT_MENU_SETTINGS = AppiumBy.accessibilityId("Settings");

    public static final By CONTEXT_MENU_DELETE_ACCOUNT = AppiumBy.accessibilityId("Delete account");

    // --- Top bar and bottom navigation -----------------------------------------

    public static final By FILTER_BY_ACCOUNT = AppiumBy.accessibilityId("Filter by account");

    public static final By ADD_ACCOUNT_BUTTON = AppiumBy.iOSNsPredicateString(
            "name == 'navigationBar_customButton' AND label == 'Add'");

    public static final By NAV_DASHBOARD = AppiumBy.accessibilityId("dashboard_button");

    public static final By NAV_USERS = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND name == 'Users'");

    public static final By NAV_BUSINESS_PORTAL = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND name == 'Business Portal'");

    /** An XPath 1.0 string literal for any text: XPath has no backslash escape, so a quote needs {@code concat()}. */
    private static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder result = new StringBuilder("concat(");
        String[] parts = value.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(", \"'\", ");
            }
            result.append("'").append(parts[i]).append("'");
        }
        return result.append(")").toString();
    }
}
