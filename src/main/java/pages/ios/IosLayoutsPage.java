package pages.ios;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import locators.ios.IosLayoutsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.WaitUtils;

/** iOS counterpart of {@code pages.LayoutsPage}: same steps, driven through the iOS screens. */
public class IosLayoutsPage {

    private static final Logger logger = LogManager.getLogger(IosLayoutsPage.class);
    private final AppiumDriver driver;

    public IosLayoutsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyLayoutsScreenDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, IosLayoutsLocators.THREE_DOTS_MENU_BUTTON),
                "Three dots menu button should be visible on layouts screen");
    }

    // ---- step-level groups (EEPD-TC-9005) -------------------------------------------------

    /** Steps "Click the 3 dots button" and "Click New Layout": the New layout form is open. */
    public void openNewLayoutForm() {
        clickThreeDotsMenuButton();
        verifyMenuOptions();
        WaitUtils.click(driver, IosLayoutsLocators.NEW_LAYOUT_OPTION);
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, IosLayoutsLocators.LAYOUT_NAME_LABEL),
                "New Layout screen should be displayed");
    }

    /** Steps "Set name", "Aspect ratio", "Max cameras per row" and "Show Camera Title Bars". */
    public void setLayoutDetails(String layoutName, String aspectRatio, int maxCamerasPerRow, boolean showCameraTitleBars) {
        setLayoutName(layoutName);
        selectAspectRatio(aspectRatio);
        selectMaxCamerasPerRow(maxCamerasPerRow);
        toggleShowCameraTitleBars(showCameraTitleBars);
    }

    /** Steps "Add cameras" and "Save": adds the first camera and saves the selection. */
    public void addFirstCameraAndSave() {
        WaitUtils.click(driver, IosLayoutsLocators.ADD_CAMERAS_ROW);
        WaitUtils.click(driver, IosLayoutsLocators.SELECT_CAMERA);
        WaitUtils.click(driver, IosLayoutsLocators.SAVE_BUTTON);
    }

    public void clickAllCamerasTab() {
        WaitUtils.click(driver, IosLayoutsLocators.ALL_CAMERAS_TAB);
    }

    public void clickFirstLayoutTab() {
        WaitUtils.click(driver, IosLayoutsLocators.FIRST_LAYOUT_TAB);
    }

    public void clickThreeDotsMenuButton() {
        WaitUtils.click(driver, IosLayoutsLocators.THREE_DOTS_MENU_BUTTON);
    }

    public void verifyMenuOptions() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, IosLayoutsLocators.NEW_LAYOUT_OPTION),
                "Three dots menu popup should be displayed");
        assertMenuOption("Add cameras", IosLayoutsLocators.ADD_CAMERAS_OPTION);
        assertMenuOption("New layout", IosLayoutsLocators.NEW_LAYOUT_OPTION);
        assertMenuOption("Edit layout", IosLayoutsLocators.EDIT_LAYOUT_OPTION);
        assertMenuOption("Layout settings", IosLayoutsLocators.LAYOUT_SETTINGS_OPTION);
        assertMenuOption("Map view", IosLayoutsLocators.MAP_VIEW_OPTION);
        assertMenuOption("Zoom in", IosLayoutsLocators.ZOOM_IN_OPTION);

        if (WaitUtils.isVisible(driver, IosLayoutsLocators.ZOOM_OUT_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Zoom Out option is displayed");
        } else {
            logger.info("Zoom Out option not displayed - already in the zoom out state");
        }
    }

    private void assertMenuOption(String name, By locator) {
        Assert.assertTrue(WaitUtils.isVisible(driver, locator, TimeoutConstants.SHORT_TIMEOUT),
                name + " option should be displayed");
    }

    public void setLayoutName(String layoutName) {
        WaitUtils.clearAndType(driver, IosLayoutsLocators.LAYOUT_NAME_FIELD, layoutName);
    }

    public void selectAspectRatio(String ratio) {
        if (!"16:9".equals(ratio) && !"4:3".equals(ratio)) {
            throw new IllegalArgumentException("Aspect ratio must be '16:9' or '4:3', got: " + ratio);
        }
        By option = ratio.equals("16:9") ? IosLayoutsLocators.ASPECT_RATIO_16_9 : IosLayoutsLocators.ASPECT_RATIO_4_3;
        WaitUtils.click(driver, IosLayoutsLocators.ASPECT_RATIO_ROW);
        WaitUtils.click(driver, option);
        ensureOnNewLayoutScreen();
    }

    public void selectMaxCamerasPerRow(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Max cameras per row must be between 1 and 6");
        }
        WaitUtils.click(driver, IosLayoutsLocators.CAMERAS_PER_ROW_ROW);
        WaitUtils.click(driver, IosLayoutsLocators.camerasPerRowOption(count));
        ensureOnNewLayoutScreen();
    }

    /** Picking a value returns to the form on its own; this only guards against a picker that stayed open. */
    private void ensureOnNewLayoutScreen() {
        if (WaitUtils.isVisible(driver, IosLayoutsLocators.LAYOUT_NAME_LABEL, TimeoutConstants.SHORT_TIMEOUT)) {
            return;
        }
        logger.debug("New Layout screen not visible, going back from the picker");
        WaitUtils.click(driver, IosLayoutsLocators.BACK_BUTTON);
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, IosLayoutsLocators.LAYOUT_NAME_LABEL,
                FrameworkConstants.EXPLICIT_WAIT), "Cannot return to the New Layout screen");
    }

    public void toggleShowCameraTitleBars(boolean enable) {
        WebElement toggle = WaitUtils.waitForVisibility(driver, IosLayoutsLocators.SHOW_CAMERA_TITLE_BARS_SWITCH);
        boolean isEnabled = "1".equals(toggle.getAttribute("value"));
        if (enable != isEnabled) {
            toggle.click();
        }
        ensureOnNewLayoutScreen();
    }

    public void clickAddButton() {
        WaitUtils.click(driver, IosLayoutsLocators.ADD_BUTTON);
    }

    /**
     * iOS shows no snackbar. The app returns to the layouts screen with the new layout among the tabs; which tab
     * is selected afterwards varies, so the check is that the new layout exists. If its tab is not on screen it is
     * looked up through the filter, like the Android fallback.
     */
    public void verifyLayoutCreatedSuccessfully(String layoutName) {
        By tab = IosLayoutsLocators.layoutTabByName(layoutName);
        if (WaitUtils.isVisible(driver, tab, TimeoutConstants.DETAILS_LOAD_TIMEOUT)) {
            logger.info("Layout creation confirmed - tab '{}' is listed", layoutName);
        } else {
            logger.info("Tab not on screen, verifying layout '{}' exists through the filter", layoutName);
            openLayoutFromSearch(layoutName);
            Assert.assertTrue(WaitUtils.isVisible(driver, tab, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                    "Layout '" + layoutName + "' should appear in layouts list after creation");
            logger.info("Layout creation confirmed - '{}' found through the filter", layoutName);
        }
        ensureOnLayoutsListScreen();
    }

    private void ensureOnLayoutsListScreen() {
        int maxAttempts = 3;
        for (int i = 0; i < maxAttempts; i++) {
            if (WaitUtils.isVisible(driver, IosLayoutsLocators.THREE_DOTS_MENU_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
                return;
            }
            logger.debug("Not on layouts list screen, going back (attempt {}/{})", i + 1, maxAttempts);
            WaitUtils.click(driver, IosLayoutsLocators.BACK_BUTTON);
        }
        throw new IllegalStateException(
                "Failed to reach the layouts list screen after " + maxAttempts + " back taps");
    }

    /** Opens the layout from the filter results, so the layouts screen shows its tab. */
    public void openLayoutFromSearch(String layoutName) {
        WaitUtils.click(driver, IosLayoutsLocators.FILTER_ICON);
        WaitUtils.click(driver, IosLayoutsLocators.LAYOUTS_SEGMENT);
        WaitUtils.clearAndType(driver, IosLayoutsLocators.SEARCH_FIELD, layoutName);
        By result = IosLayoutsLocators.layoutInSearchResults(layoutName);
        WaitUtils.waitForVisibility(driver, result);
        WaitUtils.click(driver, result);
    }

    public void deleteLayout(String layoutName) {
        openLayoutFromSearch(layoutName);
        clickThreeDotsMenuButton();
        WaitUtils.click(driver, IosLayoutsLocators.LAYOUT_SETTINGS_OPTION);
        WaitUtils.click(driver, IosLayoutsLocators.DELETE_LAYOUT_ROW);
        WaitUtils.click(driver, IosLayoutsLocators.DELETE_ALERT_CONFIRM);

        // The search results are gone once the layout is deleted, so its tab is checked instead.
        boolean isDeleted = !WaitUtils.isVisible(driver, IosLayoutsLocators.layoutTabByName(layoutName),
                TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(isDeleted, "Layout '" + layoutName + "' should be deleted but is still visible");
        logger.info("Verified layout '{}' was successfully deleted", layoutName);
    }

    /** Steps "Stay in Layouts" and "Wait for the camera previews": previews loaded with image and timestamp. */
    public void verifyCameraPreviewsLoaded() {
        WaitUtils.waitForVisibility(driver, IosLayoutsLocators.CAMERA_PREVIEW_CARDS);
        // The time label is drawn over the video, so it appears after the stream starts.
        WaitUtils.waitForVisibility(driver, IosLayoutsLocators.CAMERA_TIMESTAMPS);
        verifyFirstCameraPreviewElements();
    }

    public void verifyFirstCameraPreviewElements() {
        List<WebElement> previewCards = WaitUtils.findElements(driver, IosLayoutsLocators.CAMERA_PREVIEW_CARDS);
        List<WebElement> previewImages = WaitUtils.findElements(driver, IosLayoutsLocators.CAMERA_PREVIEW_IMAGES);
        List<WebElement> timestamps = WaitUtils.findElements(driver, IosLayoutsLocators.CAMERA_TIMESTAMPS);

        Assert.assertFalse(previewCards.isEmpty(), "At least one camera preview card should be displayed");
        Assert.assertFalse(previewImages.isEmpty(), "Camera preview image elements should be present");
        Assert.assertFalse(timestamps.isEmpty(), "Camera timestamp elements should be present");

        WebElement firstVisibleImage = previewImages.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No visible camera preview image found"));
        Assert.assertTrue(firstVisibleImage.isDisplayed(), "Camera preview image element should be visible");

        WebElement firstVisibleTimestamp = timestamps.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No visible timestamp found"));
        String timestampText = firstVisibleTimestamp.getText();
        Assert.assertTrue(timestampText.matches(".*\\d{2}:\\d{2}.*"),
                "Timestamp should match time format (HH:MM or HH:MM:SS), got: " + timestampText);
    }
}
