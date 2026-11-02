package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Locators for UI that appears on several screens, so each screen does not define its own copy. */
public final class CommonLocators {

    private CommonLocators() {
    }

    /**
     * The text of a snackbar (the short message that slides in at the bottom). Android only: iOS shows none.
     * The app draws it two ways, a Compose text tagged {@code snackbar_een_text} and a View text with the id
     * {@code snackbar_text}; the union matches whichever the screen uses.
     */
    public static final By SNACKBAR_TEXT = AppiumBy.xpath(
            "//android.widget.TextView[@content-desc='snackbar_een_text']"
                    + " | //*[@resource-id='com.een.vms.android:id/snackbar_text']");
}
