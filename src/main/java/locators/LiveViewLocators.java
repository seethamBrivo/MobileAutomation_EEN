package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public final class LiveViewLocators {

    private LiveViewLocators() {}

    public static final By SEARCH_FILTER_ICON =
            AppiumBy.id("com.een.vms.android:id/img_search");

    public static final By SEARCH_BAR =
            AppiumBy.id("com.een.vms.android:id/search_bar");

    public static By cameraCardByName(String cameraName) {
        // Escape both single and double quotes to prevent XPath injection
        String escapedName = cameraName.replace("'", "\\'").replace("\"", "&quot;");
        return AppiumBy.xpath("//android.widget.TextView[contains(@text, '" + escapedName + "')]");
    }

    // Uses last() to avoid matching menu icons when menu is open
    public static final By MECHANICAL_ZOOM_BUTTON =
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[last()]");

    // Brittle: positional indices - mobile team should add unique content-desc attributes
    public static final By ZOOM_IN_ICON =
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[2]");

    public static final By ZOOM_OUT_ICON =
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[3]");

    public static final By AUTO_FOCUS_ICON =
            AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/icon\"])[1]");
}
