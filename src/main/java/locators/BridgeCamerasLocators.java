package locators;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the cameras list reached from a bridge's context menu: the Cameras option, the back button,
 * the search/filter UI and the camera context menu.
 */
public final class BridgeCamerasLocators {

    private BridgeCamerasLocators() {}

    // --- Navigation ------------------------------------------------------------

    public static final By CAMERAS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: same DevicesMainScreen back button as BridgesLocators.BRIDGES_BACK_BUTTON, no testTag yet.
    public static final By BACK_BUTTON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View");

    // --- Search and filter -----------------------------------------------------

    public static final By SEARCH_BAR =
            AppiumBy.xpath("//android.widget.EditText");

    // Inline locator for context menu - only used once in verification
    public static final By CONTEXT_MENU_CAMERA_SETTINGS =
            AppiumBy.xpath("//android.widget.TextView[@text='Camera settings']");

    public static final By SEARCH_PLACEHOLDER =
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.SEARCH_PLACEHOLDER + "\"]");

    public static final By CAMERA_COUNTER =
            AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + UiTextConstants.CAMERA_COUNTER_TEXT + "') and not(contains(@text, 'Filter')) and not(contains(@text, 'No matching'))]");

    public static final By FIRST_CAMERA_IN_LIST =
            AppiumBy.xpath("(//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, 'Online') or contains(@text, 'Offline') or contains(@text, 'error') or contains(@text, 'Invalid') or contains(@text, 'Turned off')]])[1]");

    public static final By EMPTY_STATE_MESSAGE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"" + UiTextConstants.EMPTY_STATE_MESSAGE + "\"]");
}
