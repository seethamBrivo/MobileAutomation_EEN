package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

/**
 * Locators for Live View feature including mechanical zoom controls.
 * Supports both Android and iOS platforms.
 */
public final class LiveViewLocators {

    private LiveViewLocators() {}

    public static final By SEARCH_FILTER_ICON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/img_search"), // android
            AppiumBy.accessibilityId("navigationBar_searchButton")); // ios - Layouts navigation bar search button

    public static final By SEARCH_BAR = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/search_bar"), // android
            AppiumBy.accessibilityId("navigationBar_searchField")); // ios - search text field

    /**
     * Locator for a camera card by its name.
     * iOS: Camera names appear in StaticText elements with name pattern "cell_at_X_preview_view_name"
     */
    public static By cameraCardByName(String cameraName) {
        return PlatformLocator.get(
                AppiumBy.xpath("//android.widget.TextView[contains(@text, " + XPathUtils.xpathLiteral(cameraName) + ")]"), // android
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS 'preview_view_name' AND label == " + XPathUtils.xpathLiteral(cameraName))); // ios
    }

    /**
     * Mechanical zoom button on live view screen.
     * Android: Uses last() to avoid matching menu icons when menu is open
     * iOS: varifocalIcon button
     */
    public static final By MECHANICAL_ZOOM_BUTTON = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[last()]"), // android
            AppiumBy.accessibilityId("varifocalIcon")); // ios

    /**
     * Zoom controls icons.
     * Android: Brittle positional indices - mobile team should add unique content-desc attributes
     * iOS: Verified from mechanical zoom menu
     */
    public static final By ZOOM_IN_ICON = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[2]"), // android
            AppiumBy.accessibilityId("ic plus")); // ios - zoom in button

    public static final By ZOOM_OUT_ICON = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[3]"), // android
            AppiumBy.accessibilityId("ic minus")); // ios - zoom out button

    public static final By AUTO_FOCUS_ICON = PlatformLocator.get(
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[1]"), // android
            AppiumBy.accessibilityId("autofocusIcon")); // ios - auto focus button

    /**
     * Loading indicator that appears while camera stream is loading.
     */
    public static final By LOADING_SPINNER = PlatformLocator.get(
            AppiumBy.className("android.widget.ProgressBar"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeActivityIndicator")); // ios
}
