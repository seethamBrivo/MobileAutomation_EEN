package utils;

import constants.FrameworkConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * All element lookups go through here. The driver runs with implicit wait = 0, so the
 * {@link Duration} passed to these methods is the whole budget for the lookup.
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    // ---- elements that must appear -------------------------------------------------------

    public static WebElement waitForVisibility(AppiumDriver driver, By locator) {
        return waitForVisibility(driver, locator, FrameworkConstants.EXPLICIT_WAIT);
    }

    public static WebElement waitForVisibility(AppiumDriver driver, By locator, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(AppiumDriver driver, By locator) {
        return waitForClickability(driver, locator, FrameworkConstants.EXPLICIT_WAIT);
    }

    public static WebElement waitForClickability(AppiumDriver driver, By locator, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Waits for the element to be clickable, then clicks it. */
    public static void click(AppiumDriver driver, By locator) {
        waitForClickability(driver, locator).click();
    }

    /**
     * Waits for at least one element matching the locator to be visible, then returns all
     * matching elements. Encapsulates driver.findElements so page objects never call driver directly.
     */
    public static List<WebElement> findElements(AppiumDriver driver, By locator) {
        return findElements(driver, locator, FrameworkConstants.EXPLICIT_WAIT);
    }

    /**
     * Waits for at least one element matching the locator to be visible (with custom timeout),
     * then returns all matching elements.
     */
    public static List<WebElement> findElements(AppiumDriver driver, By locator, Duration timeout) {
        // Wait for at least one element to be visible and return that checked list
        return wait(driver, timeout).until(d -> {
            List<WebElement> elements = d.findElements(locator);
            boolean anyVisible = elements.stream().anyMatch(e -> {
                try {
                    return e.isDisplayed();
                } catch (StaleElementReferenceException ex) {
                    return false;
                }
            });
            // Return the list when at least one is visible, null to continue waiting
            return anyVisible ? elements : null;
        });
    }

    /**
     * Waits for the field, clears it and types. Retries once on the transient
     * stale/invalid-state errors Compose text fields throw while re-rendering.
     */
    public static void clearAndType(AppiumDriver driver, By locator, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text to type cannot be null");
        }
        for (int attempt = 1; ; attempt++) {
            try {
                WebElement field = waitForVisibility(driver, locator);
                field.clear();
                field.sendKeys(text);
                return;
            } catch (StaleElementReferenceException | InvalidElementStateException e) {
                if (attempt == 2) {
                    if (e instanceof InvalidElementStateException) {
                        // Some devices refuse the "set text" command on this field (seen on a Realme phone;
                        // the BrowserStack Pixel accepts it). Typing key by key still works there.
                        typeWithKeyEvents(driver, locator, text);
                        return;
                    }
                    throw e;
                }
            } catch (WebDriverException e) {
                // Appium restores the input method after typing with `adb shell ime set <id>`; on a
                // device where the original IME is unknown that runs as a bare `ime set` and fails
                // (build #11, BridgesTest rename). Retry the whole clear+type instead of failing.
                String message = e.getMessage() == null ? "" : e.getMessage();
                if (attempt == 3 || !message.contains("shell ime set")) {
                    throw e;
                }
            }
        }
    }


    /**
     * Fallback for fields that reject "set text": focuses the field, clears it and types the text through Appium's
     * own input method ({@code mobile: type}). Slower than setting the text, so only used after that was refused twice.
     */
    private static void typeWithKeyEvents(AppiumDriver driver, By locator, String text) {
        WebElement field = waitForVisibility(driver, locator);
        field.click();
        try {
            field.clear();
        } catch (WebDriverException e) {
            // the typing below still replaces nothing if this fails; the caller reads the field back
        }
        driver.executeScript("mobile: type", java.util.Map.of("text", text));
    }

    // ---- elements that may legitimately be absent ---------------------------------------

    /**
     * True if the element becomes visible within {@code timeout}. Only a timeout is treated
     * as "not visible"; a dead session or any other WebDriver failure still propagates.
     */
    public static boolean isVisible(AppiumDriver driver, By locator, Duration timeout) {
        try {
            waitForVisibility(driver, locator, timeout);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Instant presence check (implicit wait is zero). */
    public static boolean isPresent(AppiumDriver driver, By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /**
     * Waits up to {@code timeout} for the first of several alternative locators to become
     * visible. The budget is shared across all locators, not paid once per locator.
     */
    public static Optional<WebElement> waitForAny(AppiumDriver driver, List<By> locators, Duration timeout) {
        try {
            WebElement found = wait(driver, timeout).until(d -> {
                for (By locator : locators) {
                    for (WebElement candidate : d.findElements(locator)) {
                        if (candidate.isDisplayed()) {
                            return candidate;
                        }
                    }
                }
                return null;
            });
            return Optional.ofNullable(found);
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }

    /**
     * Like {@link #waitForAny} but reports which locator matched, for callers that react
     * differently depending on what showed up first (e.g. a popup versus the screen behind it).
     * Empty when none became visible within {@code timeout}.
     */
    public static Optional<By> waitForFirstVisible(AppiumDriver driver, List<By> locators, Duration timeout) {
        try {
            By found = wait(driver, timeout).until(d -> {
                for (By locator : locators) {
                    for (WebElement candidate : d.findElements(locator)) {
                        if (candidate.isDisplayed()) {
                            return locator;
                        }
                    }
                }
                return null;
            });
            return Optional.ofNullable(found);
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }

    /**
     * Clicks an optional element (permission dialog, biometrics prompt) if any of the given
     * locators shows up within {@code timeout}. Returns whether something was clicked.
     */
    public static boolean tryClickOptionalElement(AppiumDriver driver, List<By> locators, Duration timeout) {
        Optional<WebElement> element = waitForAny(driver, locators, timeout);
        element.ifPresent(WebElement::click);
        return element.isPresent();
    }

    private static WebDriverWait wait(AppiumDriver driver, Duration timeout) {
        return new WebDriverWait(driver, timeout);
    }
}
