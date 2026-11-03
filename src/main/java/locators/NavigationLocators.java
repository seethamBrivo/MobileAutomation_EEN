package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Locators shared by several screens' locator classes, so that one update covers all of them. */
public final class NavigationLocators {

    private NavigationLocators() {}

    /**
     * iOS: a text button of the top navigation bar ("More", "Save", "Add" ...). The app gives all of them the same
     * name, {@code navigationBar_customButton}, so they are told apart by their label.
     */
    public static By iosTopBarButton(String label) {
        return AppiumBy.iOSNsPredicateString("name == 'navigationBar_customButton' AND label == '" + label + "'");
    }
}
