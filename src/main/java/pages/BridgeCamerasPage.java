package pages;

import io.appium.java_client.AppiumDriver;
import locators.BridgeCamerasLocators;
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
        WaitUtils.assertVisible(driver, BridgeCamerasLocators.FIRST_CAMERA_IN_LIST, "Camera list should be displayed");
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
        WaitUtils.click(driver, BridgeCamerasLocators.BACK_BUTTON);
    }
}
