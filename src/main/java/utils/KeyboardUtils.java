package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriverException;

/**
 * Utility class for keyboard operations across different platforms.
 */
public final class KeyboardUtils {

    private static final Logger logger = LogManager.getLogger(KeyboardUtils.class);

    private KeyboardUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Attempts to hide the on-screen keyboard using multiple strategies.
     * First tries mobile:hideKeyboard command, then falls back to pressing BACK key on Android.
     * Both strategies are wrapped in try-catch to handle cases where keyboard is already hidden
     * or commands are not supported.
     *
     * @param driver the Appium driver instance
     */
    public static void hideKeyboard(AppiumDriver driver) {
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (WebDriverException e) {
            logger.debug("mobile: hideKeyboard failed: {}", e.getMessage());
        }
        try {
            if (driver instanceof AndroidDriver androidDriver && androidDriver.isKeyboardShown()) {
                androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
            }
        } catch (WebDriverException e) {
            logger.debug("Could not dismiss keyboard with BACK: {}", e.getMessage());
        }
    }
}
