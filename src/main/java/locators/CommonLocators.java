package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/** Locators for UI that appears on several screens, so each screen does not define its own copy. */
public final class CommonLocators {

    private CommonLocators() {
    }

    /**
     * The text of a snackbar (Android) or toast (iOS): the short message that shows at the bottom of the screen.
     * On Android the app draws it two ways, a Compose text tagged {@code snackbar_een_text} and a View text with the id
     * {@code snackbar_text}; the union matches whichever the screen uses.
     */
    public static final By SNACKBAR_TEXT = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@content-desc='snackbar_een_text']"
                    + " | //*[@resource-id='com.een.vms.android:id/snackbar_text']"), // android
            AppiumBy.accessibilityId("toast_text")); // ios: the toast at the bottom of the screen

    /** iOS only: the button inside a search field that clears it. It exists only while the field holds text. */
    public static final By CLEAR_TEXT_BUTTON = AppiumBy.accessibilityId("Clear text");
}
