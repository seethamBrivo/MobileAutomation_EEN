package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the My Profile screen (profile section, time zone and time format options).
 */
public final class MyProfileLocators {

    private MyProfileLocators() {}

    // --- Profile section -------------------------------------------------------------

    public static final By PROFILE_DETAILS_HEADING = AppiumBy.xpath("//android.widget.TextView[@text='Profile details']");

    // Stable: Profile section is always first [1] child in ScrollView on My Profile screen
    public static final By PROFILE_SECTION = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View");

    // --- Time zone -------------------------------------------------------------------

    public static final By TIMEZONE_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']");
    public static final By CURRENT_TIMEZONE_VALUE = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']/following-sibling::android.widget.TextView[1]");

    // --- Time format -----------------------------------------------------------------

    public static final By TIME_FORMAT_OPTION = AppiumBy.xpath("//android.widget.TextView[@text='Time format']");
    public static final By CURRENT_TIME_FORMAT_VALUE = AppiumBy.xpath("//android.widget.TextView[@text='Time format']/following-sibling::android.widget.TextView[1]");

    public static By timeFormatOption(String format) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + format + "']");
    }
}
