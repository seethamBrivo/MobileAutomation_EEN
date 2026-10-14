package utils;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

/**
 * Device-level utilities for orientation, settings, and low-level control.
 * <p>
 * Rotation methods use {@code mobile: shell} (requires {@code allowInsecure: ["adb_shell"]}
 * in capabilities) instead of {@code driver.rotate()} because standard Appium rotation
 * does not reliably persist across screen navigation in Android Compose apps - the OS
 * auto-rotation overrides it. Shell commands directly control system settings to ensure
 * the orientation holds until explicitly restored.
 */
public final class DeviceUtils {

    private static final Logger logger = LogManager.getLogger(DeviceUtils.class);

    private DeviceUtils() {
    }

    /**
     * Rotates device to landscape orientation and disables auto-rotation.
     * <p>
     * Uses shell commands to ensure orientation persists across navigation.
     * Call {@link #rotateToPortrait(AppiumDriver)} in test cleanup to restore.
     *
     * @param driver the Appium driver
     * @throws AssertionError if rotation fails
     */
    public static void rotateToLandscape(AppiumDriver driver) {
        executeRotation(driver, "LANDSCAPE", "0", "1");
        waitForOrientationStabilize(driver);
        logger.info("Rotated to LANDSCAPE");
    }

    /**
     * Rotates device back to portrait orientation and restores auto-rotation.
     * <p>
     * Logs a warning if restoration fails but does not throw - this is cleanup code
     * that runs from {@code @AfterMethod} and must not fail the test or skip remaining
     * tests in the class.
     *
     * @param driver the Appium driver
     */
    public static void rotateToPortrait(AppiumDriver driver) {
        try {
            executeRotation(driver, "PORTRAIT", "0", "0");
            waitForOrientationStabilize(driver);

            driver.executeScript("mobile: shell", Map.of(
                "command", "settings",
                "args", List.of("put", "system", "accelerometer_rotation", "1")
            ));
            logger.info("Rotated to PORTRAIT and restored auto-rotation");
        } catch (Exception e) {
            logger.warn("Failed to restore portrait orientation: {}", e.getMessage());
        }
    }

    /**
     * Executes orientation change via adb shell settings command.
     *
     * @param driver       the Appium driver
     * @param mode         orientation name for log messages (LANDSCAPE or PORTRAIT)
     * @param autoRotate   "0" to disable auto-rotation, "1" to enable
     * @param userRotation "0" for portrait, "1" for landscape
     * @throws RuntimeException if shell command fails
     */
    private static void executeRotation(AppiumDriver driver, String mode, String autoRotate, String userRotation) {
        try {
            driver.executeScript("mobile: shell", Map.of(
                "command", "settings",
                "args", List.of("put", "system", "accelerometer_rotation", autoRotate)
            ));
            driver.executeScript("mobile: shell", Map.of(
                "command", "settings",
                "args", List.of("put", "system", "user_rotation", userRotation)
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to rotate device to " + mode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Waits for device orientation to stabilize after rotation.
     * <p>
     * Uses explicit wait to detect when the window size has changed (indicating layout
     * completion) rather than a hard sleep. Falls back to timeout duration if size doesn't
     * change (already in target orientation or orientation-locked app).
     *
     * @param driver the Appium driver
     */
    private static void waitForOrientationStabilize(AppiumDriver driver) {
        int initialWidth = driver.manage().window().getSize().getWidth();
        try {
            new WebDriverWait(driver, TimeoutConstants.ROTATION_STABILIZE)
                .until(d -> d.manage().window().getSize().getWidth() != initialWidth);
        } catch (org.openqa.selenium.TimeoutException e) {
            logger.debug("Window size unchanged after rotation command");
        }
    }
}
