package utils;

import constants.GestureConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

public final class GestureUtils {

    private GestureUtils() {
    }

    public static void longPress(AppiumDriver driver, WebElement element) {
        longPress(driver, element, TimeoutConstants.LONG_PRESS);
    }

    public static void longPress(AppiumDriver driver, WebElement element, Duration holdDuration) {
        Point center = getElementCenter(element);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPressSequence = new Sequence(finger, 1);
        longPressSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        longPressSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPressSequence.addAction(new Pause(finger, holdDuration));
        longPressSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(longPressSequence));
    }

    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        int startY = (int) (size.height * GestureConstants.SWIPE_START_PERCENT_VERTICAL);
        int endY = (int) (size.height * GestureConstants.SWIPE_END_PERCENT_VERTICAL);
        swipe(driver, centerX, startY, centerX, endY);
    }

    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        int startY = (int) (size.height * GestureConstants.SWIPE_END_PERCENT_VERTICAL);
        int endY = (int) (size.height * GestureConstants.SWIPE_START_PERCENT_VERTICAL);
        swipe(driver, centerX, startY, centerX, endY);
    }

    public static void scrollToTop(AppiumDriver driver) {
        for (int i = 0; i < GestureConstants.MAX_SCROLL_SWIPES; i++) {
            swipeDown(driver);
        }
    }

    /**
     * Swipes up until {@code locator} is present, at most {@link GestureConstants#MAX_SCROLL_SWIPES}
     * times. Does nothing if it is already on screen. Returns whether it was found.
     */
    public static boolean scrollUntilPresent(AppiumDriver driver, By locator) {
        for (int swipes = 0; swipes <= GestureConstants.MAX_SCROLL_SWIPES; swipes++) {
            if (WaitUtils.isPresent(driver, locator)) {
                return true;
            }
            if (swipes < GestureConstants.MAX_SCROLL_SWIPES) {
                swipeUp(driver);
            }
        }
        return false;
    }

    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 1);
        swipeSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipeSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeSequence.addAction(new Pause(finger, TimeoutConstants.SWIPE_PAUSE));
        swipeSequence.addAction(finger.createPointerMove(TimeoutConstants.SWIPE_MOVE, PointerInput.Origin.viewport(), endX, endY));
        swipeSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipeSequence));
    }

    public static Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(location.x + (size.width / 2), location.y + (size.height / 2));
    }
}
