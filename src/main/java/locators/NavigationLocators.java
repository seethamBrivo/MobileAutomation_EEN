package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Locators shared by several screens' locator classes, so that one update covers all of them. */
public final class NavigationLocators {

    private NavigationLocators() {}

    /**
     * iOS: the "More" button of the bottom tab bar. The tab bar button's name varies, and the top bar has its own
     * "More" button named {@code navigationBar_customButton}, which must not match, so that name is excluded.
     * Used by {@code LoginLocators.SIGNED_IN_MARKER}, {@code BridgesLocators.MORE_MENU_OPTION} and
     * {@code ResellerDashboardLocators.NAV_MORE}.
     */
    public static final By IOS_TAB_BAR_MORE_BUTTON = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton'"
                    + " AND label == 'More' AND name != 'navigationBar_customButton'");

    /**
     * iOS: a text button of the top navigation bar ("More", "Save", "Add" ...). The app gives all of them the same
     * name, {@code navigationBar_customButton}, so they are told apart by their label.
     */
    public static By iosTopBarButton(String label) {
        return AppiumBy.iOSNsPredicateString("name == 'navigationBar_customButton' AND label == '" + label + "'");
    }
}
