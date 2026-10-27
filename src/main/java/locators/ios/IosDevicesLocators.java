package locators.ios;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** iOS locators for the bottom tab bar's More tab, the More menu and the Dashboard ("All devices") screen. */
public final class IosDevicesLocators {

    private IosDevicesLocators() {}

    // The tab-bar button's name varies ("More" / "more_navigation_button") and the top bar has its own
    // "More" button (navigationBar_customButton), so it is picked by label, excluding the top bar one.
    public static final By MORE_TAB = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeButton' AND label == 'More' AND name != 'navigationBar_customButton'");

    public static final By DASHBOARD_BUTTON = AppiumBy.accessibilityId("dashboard_button");

    public static final By ALL_DEVICES_TITLE = AppiumBy.accessibilityId("All devices");

    /** The "Cameras" tile on the Dashboard. */
    public static final By CAMERAS_TILE = AppiumBy.iOSNsPredicateString(
            "type == 'XCUIElementTypeStaticText' AND name == 'Cameras'");
}
