package pages.ios;

import io.appium.java_client.AppiumDriver;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

/** Touch helpers shared by the iOS pages. Coordinates are in points, derived from the element or the screen size. */
final class IosGestures {

    private static final double HOLD_SECONDS = 2.0;
    /** Dismiss tap: a few percent in from the left edge, at mid height. Context menus sit away from the left edge. */
    private static final double OUTSIDE_TAP_X_FRACTION = 0.03;
    private static final double OUTSIDE_TAP_Y_FRACTION = 0.5;
    private static final double OUTSIDE_TAP_MIN_X = 5.0;

    private IosGestures() {}

    /** Presses and holds the centre of the element, which opens a long-press context menu. */
    static void longPress(AppiumDriver driver, WebElement element) {
        Rectangle rect = element.getRect();
        driver.executeScript("mobile: touchAndHold", Map.of(
                "x", rect.x + rect.width / 2.0, "y", rect.y + rect.height / 2.0, "duration", HOLD_SECONDS));
    }

    /** Taps the dimmed area beside an open context menu, which closes it without choosing an option. */
    static void tapOutsideContextMenu(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        driver.executeScript("mobile: tap", Map.of(
                "x", Math.max(OUTSIDE_TAP_MIN_X, size.width * OUTSIDE_TAP_X_FRACTION),
                "y", size.height * OUTSIDE_TAP_Y_FRACTION));
    }
}
