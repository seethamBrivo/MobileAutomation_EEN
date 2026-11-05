package utils;

import constants.FrameworkConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

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

    /**
     * Waits for the element to be clickable, then clicks it. A list or Compose screen that re-renders between finding
     * the element and tapping it makes the element stale; the lookup is then repeated (up to {@link #STALE_ATTEMPTS}).
     */
    public static void click(AppiumDriver driver, By locator) {
        retryOnStale(() -> {
            waitForClickability(driver, locator).click();
            return null;
        });
    }

    private static final int STALE_ATTEMPTS = 3;

    /**
     * Runs {@code action}, repeating it when the element it used went stale because the screen re-rendered. The action
     * must look the element up itself each time. The third stale failure is rethrown.
     */
    public static <T> T retryOnStale(java.util.function.Supplier<T> action) {
        for (int attempt = 1; ; attempt++) {
            try {
                return action.get();
            } catch (StaleElementReferenceException e) {
                if (attempt == STALE_ATTEMPTS) {
                    throw e;
                }
            }
        }
    }

    /**
     * Polls the text of the first element matching {@code locator} (trimmed) until it equals {@code expected}, for up to
     * {@code timeout}, and returns the text last seen ("" if none was readable). A missing or re-rendering element is
     * not an error here; the caller compares the result with what it expects.
     */
    public static String pollText(AppiumDriver driver, By locator, String expected, Duration timeout) {
        java.util.concurrent.atomic.AtomicReference<String> seen = new java.util.concurrent.atomic.AtomicReference<>("");
        try {
            new WebDriverWait(driver, timeout)
                    .pollingEvery(Duration.ofMillis(300))
                    .ignoring(WebDriverException.class)
                    .until(d -> {
                        seen.set(d.findElement(locator).getText().trim());
                        return expected.equals(seen.get()) ? Boolean.TRUE : null;
                    });
        } catch (TimeoutException ignored) {
            // the caller compares the last text seen with what it expected
        }
        return seen.get();
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
            // the typing below still replaces nothing if this fails; the field is read back right after
        }
        driver.executeScript("mobile: type", java.util.Map.of("text", text));
        String held = pollText(driver, locator, text.trim(), Duration.ofSeconds(3));
        if (!text.trim().equals(held)) {
            // Typed key by key without confirmation can leave a partial or empty field; do not pass that on silently.
            // Lengths only: the text may be a password.
            throw new InvalidElementStateException("The field holds " + held.length() + " characters after typing "
                    + text.length() + " key by key");
        }
    }

    /**
     * Sleeps for a fixed time. Only for the few places where the app offers nothing to wait on (a retry back-off, the
     * moment between two gesture presses); wherever a condition exists, wait on that instead.
     */
    public static void pauseFor(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---- visibility assertions --------------------------------------------------------------

    /** Fails with {@code failureMessage} unless the element is visible within the default explicit wait. */
    public static void assertVisible(AppiumDriver driver, By locator, String failureMessage) {
        assertVisible(driver, locator, FrameworkConstants.EXPLICIT_WAIT, failureMessage);
    }

    /** Fails with {@code failureMessage} unless the element becomes visible within {@code timeout}. */
    public static void assertVisible(AppiumDriver driver, By locator, Duration timeout, String failureMessage) {
        Assert.assertTrue(isVisible(driver, locator, timeout), failureMessage);
    }

    /** Fails with {@code failureMessage} if the element is still visible after {@code timeout}. */
    public static void assertNotVisible(AppiumDriver driver, By locator, Duration timeout, String failureMessage) {
        Assert.assertFalse(isVisible(driver, locator, timeout), failureMessage);
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

    /**
     * Waits for an element to become invisible or not present within the given timeout.
     * Returns true if the element became invisible, false if timeout occurred.
     */
    public static boolean waitForInvisibility(AppiumDriver driver, By locator, Duration timeout) {
        try {
            wait(driver, timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Gets text from an element with automatic retry on StaleElementReferenceException.
     * Handles UI re-renders that occur after user interaction (e.g., dropdown selection).
     */
    public static String getTextWithRetry(AppiumDriver driver, By locator, Duration timeout) {
        final int maxRetries = 2;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return waitForVisibility(driver, locator, timeout).getText();
            } catch (StaleElementReferenceException e) {
                if (attempt == maxRetries) {
                    throw e;
                }
            }
        }
        throw new AssertionError("Unreachable");
    }

    /**
     * Polls {@code condition} until it returns true or {@code timeout} passes, and returns whether it did. WebDriver
     * errors raised by the condition (an element gone or re-rendering) count as "not yet", not as failures.
     */
    public static boolean pollUntil(AppiumDriver driver, Duration timeout, Supplier<Boolean> condition) {
        try {
            wait(driver, timeout).ignoring(WebDriverException.class).until(d -> condition.get());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** The text of every match once at least one is visible; repeated if the list re-renders under it. */
    public static List<String> textsOf(AppiumDriver driver, By locator) {
        return retryOnStale(() -> {
            List<String> texts = new ArrayList<>();
            for (WebElement element : findElements(driver, locator)) {
                texts.add(element.getText());
            }
            return texts;
        });
    }

    /**
     * Like {@link #textsOf}, but an empty list when nothing matches within the timeout instead of waiting the full
     * explicit wait and throwing, for lists that may legitimately have no entries.
     */
    public static List<String> textsOfOrEmpty(AppiumDriver driver, By locator, Duration timeout) {
        return isVisible(driver, locator, timeout) ? textsOf(driver, locator) : List.of();
    }

    /** Everything on screen in one round trip, instead of one call per element, and from a single moment. */
    public static String pageSource(AppiumDriver driver) {
        return driver.getPageSource();
    }

    private static WebDriverWait wait(AppiumDriver driver, Duration timeout) {
        return new WebDriverWait(driver, timeout);
    }
}
