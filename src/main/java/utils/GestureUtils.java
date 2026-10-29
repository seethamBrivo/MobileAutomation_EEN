package utils;

import constants.GestureConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

public final class GestureUtils {

    private static final Logger logger = LogManager.getLogger(GestureUtils.class);
    private static final int LONG_PRESS_ATTEMPTS = 3;

    private GestureUtils() {
    }

    public static void longPress(AppiumDriver driver, WebElement element) {
        longPress(driver, element, TimeoutConstants.LONG_PRESS);
    }

    public static void longPress(AppiumDriver driver, WebElement element, Duration holdDuration) {
        if (PlatformLocator.isIos()) {
            // XCUITest opens a long-press context menu with touchAndHold; the centre is kept in points (no int rounding).
            Rectangle rect = element.getRect();
            driver.executeScript("mobile: touchAndHold", Map.of(
                    "x", rect.x + rect.width / 2.0, "y", rect.y + rect.height / 2.0,
                    "duration", holdDuration.toMillis() / 1000.0));
            return;
        }
        Point center = getElementCenter(element);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPressSequence = new Sequence(finger, 1);
        longPressSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        longPressSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPressSequence.addAction(new Pause(finger, holdDuration));
        longPressSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(longPressSequence));
    }

    /**
     * Long presses the element found by {@code pressTarget} and waits for {@code expectedAfterPress} (for example a
     * context menu). A long press that lands while the screen is still settling can be missed (seen on both
     * platforms), so if nothing appears within a few seconds the target is found again and pressed once more, up to
     * {@code LONG_PRESS_ATTEMPTS} presses in all, with a short pause before each repeat. The caller still asserts the
     * result, so a menu that never opens still fails the test.
     */
    public static void longPressUntilVisible(AppiumDriver driver, By pressTarget, By expectedAfterPress) {
        longPress(driver, WaitUtils.waitForVisibility(driver, pressTarget));
        for (int attempt = 2; attempt <= LONG_PRESS_ATTEMPTS
                && !WaitUtils.isVisible(driver, expectedAfterPress, TimeoutConstants.FILTER_RESULT_TIMEOUT); attempt++) {
            logger.warn("Nothing appeared after the long press on {}; pressing again (press {} of {})",
                    pressTarget, attempt, LONG_PRESS_ATTEMPTS);
            pause(TimeoutConstants.SWIPE_PAUSE.multipliedBy(3));
            longPress(driver, WaitUtils.waitForVisibility(driver, pressTarget));
        }
    }

    private static void pause(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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

    /**
     * Taps the center of the screen.
     */
    public static void tapScreenCenter(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        int centerY = size.height / 2;
        tap(driver, centerX, centerY);
    }

    /**
     * Performs a tap gesture at the specified coordinates.
     */
    public static void tap(AppiumDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1);
        tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tapSequence));
    }

    /**
     * iOS only ({@code mobile: tap} is an XCUITest command and throws on Android; the name carries that, and the
     * callers check the platform): taps the dimmed area beside an open context menu, which closes it without choosing an option. The point is
     * derived from the screen size (a few percent in from the left edge, at mid height), where menus never reach.
     */
    public static void tapOutsideContextMenuIos(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        driver.executeScript("mobile: tap", Map.of(
                "x", Math.max(5.0, size.width * 0.03),
                "y", size.height * 0.5));
    }

    public static Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(location.x + (size.width / 2), location.y + (size.height / 2));
    }
}
