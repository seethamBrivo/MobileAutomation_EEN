package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Locators for the Camera settings screen (Android only). */
public final class CameraSettingsLocators {

    private CameraSettingsLocators() {}

    // --- Screen and header -----------------------------------------------------

    public static final By GENERAL_SECTION = AppiumBy.xpath("//android.widget.TextView[@text='General']");

    public static final By HEADER_TITLE = AppiumBy.xpath(
            "//android.widget.ScrollView[.//android.widget.TextView[@text='General']]"
                    + "/following-sibling::android.view.View[1]/android.widget.TextView");

    // Positional: the clickable View inside the last child of the header.
    // TODO: Replace with testTag when mobile team adds an identifier to the preview icon.
    public static final By PREVIEW_ICON = AppiumBy.xpath(
            "//android.widget.ScrollView[.//android.widget.TextView[@text='General']]"
                    + "/following-sibling::android.view.View[1]/android.view.View[last()]"
                    + "/android.view.View[@clickable='true']");

    // --- Status ----------------------------------------------------------------

    public static final By STATUS_VALUE =
            AppiumBy.xpath("//android.widget.TextView[@text='Status']/following-sibling::android.widget.TextView[1]");

    // --- Name ------------------------------------------------------------------

    public static final By NAME_FIELD = AppiumBy.xpath("(//android.widget.EditText)[1]");

    // --- Enable switch ---------------------------------------------------------

    public static final By ENABLE_ROW = AppiumBy.xpath(
            "//android.widget.TextView[@text='Enable']/ancestor::android.view.View[@clickable='true'][1]");
}
