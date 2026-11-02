package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.CamerasLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

/** Opening and closing the context menu that a long press shows on a camera in a camera list. */
public final class CameraContextMenu {

    private static final Logger logger = LogManager.getLogger(CameraContextMenu.class);

    private CameraContextMenu() {
    }

    public static void longPressFirstCamera(AppiumDriver driver, By firstCameraInList) {
        if (PlatformLocator.isIos()) {
            // iOS: close the keyboard (left open after a search) before pressing a camera row.
            closeIosKeyboard(driver);
        }
        GestureUtils.longPress(driver, WaitUtils.waitForVisibility(driver, firstCameraInList));
    }

    /** Closes the menu; does nothing when no menu is open. */
    public static void dismiss(AppiumDriver driver) {
        if (PlatformLocator.isIos()) {
            // iOS: tap the dimmed area beside the menu.
            if (WaitUtils.isVisible(driver, CamerasLocators.CAMERA_SETTINGS_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
                GestureUtils.tapOutsideContextMenuIos(driver);
                Assert.assertTrue(WaitUtils.waitForInvisibility(driver, CamerasLocators.CAMERA_SETTINGS_OPTION,
                        TimeoutConstants.SHORT_TIMEOUT), "Context menu should close");
            }
        } else {
            try {
                driver.executeScript("mobile: hideKeyboard");
            } catch (Exception e) {
                // Keyboard already hidden or not present
            }
            driver.navigate().back();
        }
        logger.info("Context menu dismissed");
    }

    /** iOS: closes the keyboard with its "search" key, which is disabled while the field is empty. */
    private static void closeIosKeyboard(AppiumDriver driver) {
        if (WaitUtils.isVisible(driver, CamerasLocators.KEYBOARD_SEARCH_KEY, TimeoutConstants.SHORT_TIMEOUT)) {
            WaitUtils.click(driver, CamerasLocators.KEYBOARD_SEARCH_KEY);
        }
    }
}
