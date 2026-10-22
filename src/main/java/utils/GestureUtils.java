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

    private static final long SETTLE_POLL_MILLIS = 500;

    /**
     * Swipes up until {@code locator} is present, at most {@link GestureConstants#MAX_SCROLL_SWIPES}
     * times. Does nothing if it is already on screen. Returns whether it was found.
     *
     * <p>Screens often report themselves as open (title visible) while their content is still on
     * loading skeletons; four swipes over a skeleton find nothing. So when the first round fails the
     * screen is scrolled back to the top, given {@link TimeoutConstants#SCROLL_RETRY_SETTLE} to finish
     * loading, and swiped once more. That second round only costs time on a failing lookup.
     */
    public static boolean scrollUntilPresent(AppiumDriver driver, By locator) {
        if (swipeUntilPresent(driver, locator)) {
            return true;
        }
        scrollToTop(driver);
        // Same settle budget as before, but stop waiting the moment the target shows up on its own
        // (the content finished loading at the top) instead of always sleeping the full time.
        long deadline = System.nanoTime() + TimeoutConstants.SCROLL_RETRY_SETTLE.toNanos();
        while (System.nanoTime() < deadline) {
            if (WaitUtils.isPresent(driver, locator)) {
                return true;
            }
            try {
                Thread.sleep(SETTLE_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return swipeUntilPresent(driver, locator);
    }

    private static boolean swipeUntilPresent(AppiumDriver driver, By locator) {
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

    /**
     * Scrolls until element is visible and clickable, then performs adjustment scroll
     * to move it away from screen edge. Use when element needs to be clicked after scrolling.
     */
    public static boolean scrollUntilVisible(AppiumDriver driver, By locator) {
        for (int swipes = 0; swipes <= GestureConstants.MAX_SCROLL_SWIPES; swipes++) {
            if (WaitUtils.isVisible(driver, locator, TimeoutConstants.SHORT_TIMEOUT)) {
                Dimension size = driver.manage().window().getSize();
                int centerX = size.width / 2;
                int startY = (int) (size.height * 0.7);
                int endY = (int) (size.height * 0.5);
                swipe(driver, centerX, startY, centerX, endY);
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
