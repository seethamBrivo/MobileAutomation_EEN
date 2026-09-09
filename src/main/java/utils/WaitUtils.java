package utils;
import constants.FrameworkConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
public final class WaitUtils {
    private WaitUtils() {
    }
    public static WebElement waitForVisibility(AppiumDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    public static WebElement waitForClickability(AppiumDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }
    public static boolean waitForInvisibility(AppiumDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    private static WebDriverWait getWait(AppiumDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(FrameworkConstants.EXPLICIT_WAIT_SECONDS));
    }

    /**
     * Executes an action with a temporarily reduced implicit wait timeout.
     * Automatically restores the original timeout after execution.
     *
     * @param driver The AppiumDriver instance
     * @param timeout The temporary timeout duration to use
     * @param action The action to execute with reduced timeout
     */
    public static void withReducedTimeout(AppiumDriver driver, Duration timeout, Runnable action) {
        Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        if (originalTimeout == null) {
            originalTimeout = Duration.ofSeconds(10); // Default fallback
        }
        try {
            driver.manage().timeouts().implicitlyWait(timeout);
            action.run();
        } finally {
            driver.manage().timeouts().implicitlyWait(originalTimeout);
        }
    }

    /**
     * Executes a supplier with a temporarily reduced implicit wait timeout.
     * Automatically restores the original timeout after execution.
     *
     * @param driver The AppiumDriver instance
     * @param timeout The temporary timeout duration to use
     * @param supplier The supplier to execute with reduced timeout
     * @return The result from the supplier
     */
    public static <T> T withReducedTimeout(AppiumDriver driver, Duration timeout, Supplier<T> supplier) {
        Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        if (originalTimeout == null) {
            originalTimeout = Duration.ofSeconds(10); // Default fallback
        }
        try {
            driver.manage().timeouts().implicitlyWait(timeout);
            return supplier.get();
        } finally {
            driver.manage().timeouts().implicitlyWait(originalTimeout);
        }
    }

    /**
     * Clears an element and enters text after waiting for visibility.
     *
     * @param driver The AppiumDriver instance
     * @param locator The locator for the element
     * @param text The text to enter (skips if null or empty)
     */
    public static void clearAndEnterText(AppiumDriver driver, By locator, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        WebElement element = waitForVisibility(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Checks if an element is present and displayed quickly (200ms timeout).
     * Use this for optional popups/dialogs to avoid wasting time when they don't exist.
     *
     * @param driver The AppiumDriver instance
     * @param locator The locator for the element
     * @return true if element is present and displayed, false otherwise
     */
    public static boolean isElementPresentQuickly(AppiumDriver driver, By locator) {
        return withReducedTimeout(driver, Duration.ofMillis(200), () -> {
            try {
                List<WebElement> elements = driver.findElements(locator);
                return !elements.isEmpty() && elements.get(0).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Tries multiple locator strategies to find and click an optional element (like a popup).
     * Returns immediately if element not found instead of wasting time on full timeouts.
     * Uses 200ms timeout - suitable for immediate popups (permissions).
     *
     * @param driver The AppiumDriver instance
     * @param locators List of locators to try in order
     * @return true if element was found and clicked, false otherwise
     */
    public static boolean tryClickOptionalElement(AppiumDriver driver, List<By> locators) {
        return tryClickOptionalElement(driver, locators, Duration.ofMillis(200));
    }

    /**
     * Tries multiple locator strategies to find and click an optional element (like a popup).
     * Returns if element not found after the specified timeout.
     *
     * @param driver The AppiumDriver instance
     * @param locators List of locators to try in order
     * @param timeout How long to wait for the popup to appear
     * @return true if element was found and clicked, false otherwise
     */
    public static boolean tryClickOptionalElement(AppiumDriver driver, List<By> locators, Duration timeout) {
        return withReducedTimeout(driver, timeout, () -> {
            for (By locator : locators) {
                try {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        elements.get(0).click();
                        return true;
                    }
                } catch (Exception e) {
                    // Continue to next strategy
                }
            }
            return false;
        });
    }
}