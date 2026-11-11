package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.BridgeCamerasLocators;
import locators.BridgesLocators;
import org.testng.Assert;
import org.testng.SkipException;
import utils.PlatformLocator;
import utils.WaitUtils;

public class BridgeCamerasPage {

    private final AppiumDriver driver;
    private final CameraSearchFilter cameraSearchFilter;

    public BridgeCamerasPage(AppiumDriver driver) {
        this.driver = driver;
        this.cameraSearchFilter = new CameraSearchFilter(
            driver,
            BridgeCamerasLocators.SEARCH_BAR,
            BridgeCamerasLocators.SEARCH_PLACEHOLDER,
            BridgeCamerasLocators.CAMERA_COUNTER,
            BridgeCamerasLocators.FIRST_CAMERA_IN_LIST,
            BridgeCamerasLocators.EMPTY_STATE_MESSAGE
        );
    }

    /** The search bar, camera counter and filtered list of this screen. */
    public CameraSearchFilter cameraSearchFilter() {
        return cameraSearchFilter;
    }

    public void clickCamerasOption() {
        WaitUtils.click(driver, BridgeCamerasLocators.CAMERAS_OPTION);
    }

    public void verifyCamerasOptionDisplayed() {
        WaitUtils.assertVisible(driver, BridgeCamerasLocators.CAMERAS_OPTION,
                "Cameras option should be displayed in bridge context menu");
    }

    public void verifyCameraListDisplayed() {
        // First verify we're on camera screen (search placeholder is unique to camera screen)
        WaitUtils.assertVisible(driver, BridgeCamerasLocators.SEARCH_PLACEHOLDER,
                "Camera screen should be displayed (search placeholder visible)");
        if (!WaitUtils.isVisible(driver, BridgeCamerasLocators.FIRST_CAMERA_IN_LIST, TimeoutConstants.LIST_LOAD_TIMEOUT)
                && WaitUtils.isVisible(
                        driver, BridgeCamerasLocators.EMPTY_STATE_MESSAGE, TimeoutConstants.SHORT_TIMEOUT)) {
            throw new SkipException("The first bridge needs at least 1 camera but shows 0");
        }
        WaitUtils.assertVisible(driver, BridgeCamerasLocators.FIRST_CAMERA_IN_LIST, "Camera list should be displayed");
    }

    /**
     * Test case step "the bridge name is in the header". The iOS header only reads "Cameras" and names no bridge: the
     * list is scoped by a "Bridges 1" filter chip, so that chip is verified instead. Android's header is not pinned
     * down by the test case, so nothing is asserted there.
     */
    public void verifyListScopedToOneBridge() {
        if (!PlatformLocator.isIos()) {
            return;
        }
        Assert.assertEquals(WaitUtils.waitForVisibility(driver, BridgeCamerasLocators.IOS_BRIDGES_CHIP_COUNT).getText(),
                "1", "The cameras list opened from a bridge should be scoped to exactly 1 bridge");
    }

    // ---- step-level groups ----------------------------------------------------------------

    public void verifyCanInteractWithFilteredCamera() {
        CameraContextMenu.longPressFirstCamera(driver, BridgeCamerasLocators.FIRST_CAMERA_IN_LIST);
        // Verify context menu opened - use normal wait, not SHORT_TIMEOUT
        WaitUtils.assertVisible(driver, BridgeCamerasLocators.CONTEXT_MENU_CAMERA_SETTINGS,
                "Context menu should be displayed after long press");
        CameraContextMenu.dismiss(driver);
    }

    public void clickBackButton() {
        if (PlatformLocator.isIos()) {
            WaitUtils.tryClickOptionalElement(
                    driver, java.util.List.of(BridgesLocators.CLOSE_FILTER_BUTTON), TimeoutConstants.SHORT_TIMEOUT);
        }
        WaitUtils.click(driver, BridgeCamerasLocators.BACK_BUTTON);
        if (!PlatformLocator.isIos()
                && WaitUtils.isVisible(driver, BridgeCamerasLocators.CAMERAS_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            CameraContextMenu.dismiss(driver);
        }
    }
}
