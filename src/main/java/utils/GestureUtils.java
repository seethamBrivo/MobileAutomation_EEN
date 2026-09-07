package utils;

import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

public final class GestureUtils {

    private GestureUtils() {
    }

    public static void tap(AppiumDriver driver, Point point) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1);

        tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), point.x, point.y));
        tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(java.util.List.of(tapSequence));
    }

    public static void longPress(AppiumDriver driver, WebElement element, Duration holdDuration) {
        Point center = getElementCenter(element);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPressSequence = new Sequence(finger, 1);

        longPressSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        longPressSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPressSequence.addAction(new org.openqa.selenium.interactions.Pause(finger, holdDuration));
        longPressSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(java.util.List.of(longPressSequence));
    }

    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);

        swipe(driver, centerX, startY, centerX, endY);
    }

    public static void swipeElementRightToLeft(AppiumDriver driver, WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        Point location;
        Dimension size;

        try {
            location = element.getLocation();
            size = element.getSize();
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            throw new org.openqa.selenium.StaleElementReferenceException(
                "Element became stale before swipe could be performed: " + e.getMessage());
        }

        // Calculate coordinates relative to element bounds, not screen
        int centerY = location.y + (size.height / 2);
        int elementRight = location.x + size.width;
        int elementLeft = location.x;

        // Start 90% to the right within element, end 10% from left edge
        int startX = (int) (elementRight - (size.width * 0.1));
        int endX = (int) (elementLeft + (size.width * 0.1));

        swipe(driver, startX, centerY, endX, centerY);
    }

    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 1);

        swipeSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipeSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeSequence.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(300)));
        swipeSequence.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), endX, endY));
        swipeSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(java.util.List.of(swipeSequence));
    }

    private static Point getElementCenter(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        try {
            Point location = element.getLocation();
            Dimension size = element.getSize();
            return new Point(location.x + (size.width / 2), location.y + (size.height / 2));
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            throw new org.openqa.selenium.StaleElementReferenceException(
                "Element became stale before center could be calculated: " + e.getMessage());
        }
    }
}