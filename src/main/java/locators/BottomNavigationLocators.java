package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * The tabs of the bar at the bottom of the app. An end user sees Layouts, Tags, Search and More; a reseller sees
 * Dashboard, Users, Business Portal and More.
 */
public final class BottomNavigationLocators {

    /** Android: the bar's container, so a tab is never confused with a screen title of the same name. */
    private static final String ANDROID_BAR = "//*[@resource-id='com.een.vms.android:id/bottom_navigation']";

    // --- End user ----------------------------------------------------------------------

    public static final By LAYOUTS_TAB = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_BAR + "//android.widget.TextView[@text='Layouts']"), // android
            AppiumBy.accessibilityId("layouts_button")); // ios

    public static final By TAGS_TAB = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_BAR + "//android.widget.TextView[@text='Tags']"), // android
            iosTabBarButton("Tags")); // ios

    public static final By SEARCH_TAB = PlatformLocator.get(
            AppiumBy.xpath(ANDROID_BAR + "//android.widget.TextView[@text='Search']"), // android
            iosTabBarButton("Search")); // ios

    // --- Reseller ----------------------------------------------------------------------
    // Not scoped to the bar like the end-user tabs: the reseller screens draw the bar as plain Compose views with no
    // id or container to scope to (the end user's bar is the native bottom_navigation widget). MORE_TAB is shared by
    // both bars and is unscoped on Android for the same reason.

    public static final By DASHBOARD_TAB = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Dashboard']"), // android
            AppiumBy.accessibilityId("dashboard_button")); // ios

    public static final By USERS_TAB = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Users']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Users'")); // ios

    public static final By BUSINESS_PORTAL_TAB = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Business Portal']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Business Portal'")); // ios

    // --- Both ----------------------------------------------------------------------------

    /**
     * More. Shown on every signed-in landing screen and never on the sign-in screen, so it also marks "signed in".
     * iOS: the top bar has its own "More" button named {@code navigationBar_customButton}, which must not match,
     * so that name is excluded.
     */
    public static final By MORE_TAB = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='More']"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton'"
                    + " AND label == 'More' AND name != 'navigationBar_customButton'")); // ios

    private BottomNavigationLocators() {}

    /**
     * iOS: a button of the tab bar, found by its label. Only Layouts has a stable id ({@code layouts_button}); the
     * others are named after their label, and the top bar has buttons with similar labels, hence the tab bar scope.
     */
    private static By iosTabBarButton(String label) {
        return AppiumBy.iOSClassChain(
                "**/XCUIElementTypeTabBar/**/XCUIElementTypeButton[`label == \"" + label + "\"`]");
    }
}
