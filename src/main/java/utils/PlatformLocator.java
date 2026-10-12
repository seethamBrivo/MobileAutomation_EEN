package utils;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Utility for managing platform-specific locators.
 * Automatically returns the correct locator based on the current platform (Android/iOS).
 */
public final class PlatformLocator {

    private PlatformLocator() {
    }

    /**
     * Returns the appropriate locator based on the current platform.
     *
     * @param androidLocator The locator to use for Android
     * @param iosLocator The locator to use for iOS
     * @return Platform-specific locator
     */
    public static By get(By androidLocator, By iosLocator) {
        String platform = ConfigReader.getPlatform();
        return "ios".equalsIgnoreCase(platform) ? iosLocator : androidLocator;
    }

    /**
     * Creates a text-based locator for both platforms.
     * Android uses XPath with @text attribute.
     * iOS uses accessibility ID or predicate string.
     *
     * @param text The text to search for
     * @return Platform-specific text locator
     */
    public static By byText(String text) {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSNsPredicateString(String.format("label == '%s' OR name == '%s'", text, text));
        }
        return AppiumBy.xpath(String.format("//android.widget.TextView[@text=\"%s\"]", text));
    }

    /**
     * Creates a text-contains locator for both platforms.
     *
     * @param text The partial text to search for
     * @return Platform-specific partial text locator
     */
    public static By byTextContains(String text) {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSNsPredicateString(String.format("label CONTAINS '%s' OR name CONTAINS '%s'", text, text));
        }
        return AppiumBy.xpath(String.format("//android.widget.TextView[contains(@text, '%s')]", text));
    }

    /**
     * Creates an accessibility ID / content-desc locator.
     * This is the preferred cross-platform locator strategy.
     *
     * @param id The accessibility identifier
     * @return Cross-platform accessibility locator
     */
    public static By byAccessibilityId(String id) {
        return AppiumBy.accessibilityId(id);
    }

    /**
     * Creates a button locator with text for both platforms.
     *
     * @param buttonText The button text
     * @return Platform-specific button locator
     */
    public static By byButton(String buttonText) {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSNsPredicateString(
                String.format("type == 'XCUIElementTypeButton' AND (label == '%s' OR name == '%s')",
                    buttonText, buttonText));
        }
        return AppiumBy.xpath(String.format("//android.widget.Button[@text=\"%s\"]", buttonText));
    }

    /**
     * Creates a text input field locator for both platforms.
     *
     * @param placeholder Optional placeholder text (can be null)
     * @return Platform-specific input field locator
     */
    public static By byInputField(String placeholder) {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            if (placeholder != null && !placeholder.isEmpty()) {
                return AppiumBy.iOSNsPredicateString(
                    String.format("type == 'XCUIElementTypeTextField' AND placeholderValue == '%s'", placeholder));
            }
            return AppiumBy.iOSClassChain("**/XCUIElementTypeTextField");
        }
        if (placeholder != null && !placeholder.isEmpty()) {
            return AppiumBy.xpath(
                String.format("//android.widget.EditText[@text=\"%s\" or @hint=\"%s\"]", placeholder, placeholder));
        }
        return AppiumBy.className("android.widget.EditText");
    }

    /**
     * Creates a secure text field (password) locator for both platforms.
     *
     * @return Platform-specific secure text field locator
     */
    public static By bySecureTextField() {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSClassChain("**/XCUIElementTypeSecureTextField");
        }
        return AppiumBy.xpath("//android.widget.EditText[@password='true']");
    }

    /**
     * Creates a resource ID locator for Android and accessibility ID for iOS.
     *
     * @param androidResourceId The Android resource ID (e.g., "com.app:id/button")
     * @param iosAccessibilityId The iOS accessibility identifier
     * @return Platform-specific resource/accessibility locator
     */
    public static By byId(String androidResourceId, String iosAccessibilityId) {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.accessibilityId(iosAccessibilityId);
        }
        return AppiumBy.id(androidResourceId);
    }

    /**
     * Creates a class name locator for both platforms.
     *
     * @param androidClassName Android widget class (e.g., "android.widget.Button")
     * @param iosElementType iOS element type (e.g., "XCUIElementTypeButton")
     * @return Platform-specific class locator
     */
    public static By byClass(String androidClassName, String iosElementType) {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.className(iosElementType);
        }
        return AppiumBy.className(androidClassName);
    }
}
