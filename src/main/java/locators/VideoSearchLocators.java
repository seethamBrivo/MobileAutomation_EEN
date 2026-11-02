package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the Video Search screen and the back navigation from the VSP Events screen.
 */
public final class VideoSearchLocators {

    private VideoSearchLocators() {}

    // --- Navigation ------------------------------------------------------------------

    // Stable: Back button at fixed position [6] in VspMain screen
    public static final By BACK_BUTTON_FROM_VSP_EVENTS = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View/android.view.View");

    // --- Search ----------------------------------------------------------------------

    public static final By DATE_FIELD = AppiumBy.id("com.een.vms.android:id/date");
}
