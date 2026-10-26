package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import locators.LayoutsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.WaitUtils;

public class LayoutsPage {

    private static final Logger logger = LogManager.getLogger(LayoutsPage.class);
    private final AppiumDriver driver;

    public LayoutsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyLayoutsScreenDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, LayoutsLocators.THREE_DOTS_MENU_BUTTON),
                "Three dots menu button should be visible on layouts screen");
    }

    // ---- step-level groups (EEPD-TC-9005) -------------------------------------------------

    /** Steps "Click the 3 dots button" and "Click New Layout": the New layout form is open. */
    public void openNewLayoutForm() {
        clickThreeDotsMenuButton();
        verifyMenuPopupDisplayed();
        verifyMenuOptions();
        clickNewLayoutOption();
        verifyNewLayoutScreenDisplayed();
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
        clickAddCamerasIcon();
        selectFirstCamera();
        clickSaveButton();
    }

    public void clickAllCamerasTab() {
        WaitUtils.click(driver, LayoutsLocators.ALL_CAMERAS_TAB);
    }

    public void clickFirstLayoutTab() {
        WaitUtils.click(driver, LayoutsLocators.FIRST_LAYOUT_TAB);
    }

    public void clickThreeDotsMenuButton() {
        WaitUtils.click(driver, LayoutsLocators.THREE_DOTS_MENU_BUTTON);
    }

    public void verifyMenuPopupDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, LayoutsLocators.NEW_LAYOUT_OPTION),
                "Three dots menu popup should be displayed");
    }

    public void verifyMenuOptions() {
        List<By> menuOptions = List.of(
                LayoutsLocators.ADD_CAMERAS_OPTION, LayoutsLocators.NEW_LAYOUT_OPTION, LayoutsLocators.EDIT_LAYOUT_OPTION,
                LayoutsLocators.LAYOUT_SETTINGS_OPTION, LayoutsLocators.MAP_VIEW_OPTION, LayoutsLocators.ZOOM_IN_OPTION
        );

        Assert.assertTrue(WaitUtils.waitForAny(driver, menuOptions, FrameworkConstants.EXPLICIT_WAIT).isPresent(),
                "At least one menu option should be displayed");

        Assert.assertTrue(WaitUtils.isVisible(driver, LayoutsLocators.ADD_CAMERAS_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Add Cameras option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, LayoutsLocators.NEW_LAYOUT_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "New Layout option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, LayoutsLocators.EDIT_LAYOUT_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Edit Layout option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, LayoutsLocators.LAYOUT_SETTINGS_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Layout Settings option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, LayoutsLocators.MAP_VIEW_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Map View option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, LayoutsLocators.ZOOM_IN_OPTION, TimeoutConstants.SHORT_TIMEOUT),
                "Zoom In option should be displayed");

        if (WaitUtils.isVisible(driver, LayoutsLocators.ZOOM_OUT_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Zoom Out option is displayed");
        } else {
            logger.info("Zoom Out option not displayed - already in the zoom out state");
        }
    }

    public void clickNewLayoutOption() {
        WaitUtils.click(driver, LayoutsLocators.NEW_LAYOUT_OPTION);
    }

    public void verifyNewLayoutScreenDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, LayoutsLocators.LAYOUT_NAME_LABEL),
                "New Layout screen should be displayed");
    }

    public void setLayoutName(String layoutName) {
        WaitUtils.click(driver, LayoutsLocators.LAYOUT_NAME_LABEL);
        WebElement field = WaitUtils.waitForAny(driver, LayoutsLocators.LAYOUT_NAME_EDIT_FIELDS, FrameworkConstants.EXPLICIT_WAIT)
                .orElseThrow(() -> new NoSuchElementException("Layout name field not found on the New layout screen"));
        field.clear();
        field.sendKeys(layoutName);
    }

    public void selectAspectRatio(String ratio) {
        if (!"16:9".equals(ratio) && !"4:3".equals(ratio)) {
            throw new IllegalArgumentException("Aspect ratio must be '16:9' or '4:3', got: " + ratio);
        }
        By aspectRatioLocator = ratio.equals("16:9") ? LayoutsLocators.ASPECT_RATIO_16_9 : LayoutsLocators.ASPECT_RATIO_4_3;
        WaitUtils.click(driver, aspectRatioLocator);
        ensureOnNewLayoutScreen();
    }

    public void selectMaxCamerasPerRow(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Max cameras per row must be between 1 and 6");
        }
        WaitUtils.click(driver, LayoutsLocators.camerasPerRowOption(count));
        ensureOnNewLayoutScreen();
    }

    private void ensureOnNewLayoutScreen() {
        if (WaitUtils.isVisible(driver, LayoutsLocators.LAYOUT_NAME_LABEL, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.debug("Already on New Layout screen");
            return;
        }
        logger.debug("New Layout screen not visible, navigating back to close dialog");
        try {
            driver.navigate().back();
            WaitUtils.waitForVisibility(driver, LayoutsLocators.LAYOUT_NAME_LABEL, FrameworkConstants.EXPLICIT_WAIT);
            logger.debug("Confirmed on New Layout screen after back navigation");
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IllegalStateException("Cannot return to New Layout screen after navigate().back()", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to return to New Layout screen: " + e.getMessage(), e);
        }
    }

    public void toggleShowCameraTitleBars(boolean enable) {
        WebElement toggle = WaitUtils.waitForVisibility(driver, LayoutsLocators.SHOW_CAMERA_TITLE_BARS_TOGGLE);
        boolean isEnabled = toggle.isSelected();

        if (enable != isEnabled) {
            WaitUtils.click(driver, LayoutsLocators.SHOW_CAMERA_TITLE_BARS_TOGGLE);
        }
        ensureOnNewLayoutScreen();
    }

    public void clickAddCamerasIcon() {
        WaitUtils.click(driver, LayoutsLocators.ADD_CAMERAS_ICON);
    }

    public void selectFirstCamera() {
        WaitUtils.click(driver, LayoutsLocators.SELECT_CAMERA);
    }

    public void clickSaveButton() {
        WaitUtils.click(driver, LayoutsLocators.SAVE_BUTTON);
    }

    public void clickAddButton() {
        WaitUtils.click(driver, LayoutsLocators.ADD_BUTTON);
    }

    public void verifyLayoutCreatedSuccessfully(String layoutName) {
        if (WaitUtils.isVisible(driver, LayoutsLocators.SUCCESS_MESSAGE, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Layout creation confirmed via snackbar message");
            ensureOnLayoutsListScreen();
            return;
        }

        logger.info("Snackbar not visible, verifying layout '{}' exists in list", layoutName);
        clickFilterIcon();
        clickLayoutsTab();
        enterLayoutNameInSearchFilter(layoutName);

        By targetLayout = LayoutsLocators.layoutByName(layoutName);
        Assert.assertTrue(WaitUtils.isVisible(driver, targetLayout, FrameworkConstants.EXPLICIT_WAIT),
                "Layout '" + layoutName + "' should appear in layouts list after creation");
        logger.info("Layout creation confirmed - '{}' found in layouts list", layoutName);

        ensureOnLayoutsListScreen();
    }

    private void ensureOnLayoutsListScreen() {
        int maxAttempts = 3;
        for (int i = 0; i < maxAttempts; i++) {
            if (WaitUtils.isVisible(driver, LayoutsLocators.THREE_DOTS_MENU_BUTTON, TimeoutConstants.SHORT_TIMEOUT)) {
                logger.debug("On layouts list screen (three dots menu visible)");
                return;
            }
            logger.debug("Not on layouts list screen, navigating back (attempt {}/{})", i + 1, maxAttempts);
            driver.navigate().back();
        }
        throw new IllegalStateException(
                "Failed to navigate to layouts list screen after " + maxAttempts +
                " back navigation attempts - three dots menu button not found");
    }

    public void clickFilterIcon() {
        WaitUtils.click(driver, LayoutsLocators.FILTER_ICON);
    }

    public void clickLayoutsTab() {
        WaitUtils.click(driver, LayoutsLocators.LAYOUTS_TAB);
    }

    public void enterLayoutNameInSearchFilter(String layoutName) {
        WaitUtils.clearAndType(driver, LayoutsLocators.SEARCH_BAR, layoutName);
    }

    public void clickLayoutSettingsOption() {
        WaitUtils.click(driver, LayoutsLocators.LAYOUT_SETTINGS_OPTION);
    }

    public void clickDeleteLayoutButton() {
        WaitUtils.click(driver, LayoutsLocators.DELETE_BUTTON);
    }

    public void confirmDeleteLayout() {
        WaitUtils.click(driver, LayoutsLocators.CONFIRM_BUTTON);
    }

    public void deleteLayout(String layoutName) {
        clickFilterIcon();
        clickLayoutsTab();
        enterLayoutNameInSearchFilter(layoutName);
        By targetLayout = LayoutsLocators.layoutByName(layoutName);
        WaitUtils.waitForVisibility(driver, targetLayout);
        WaitUtils.click(driver, targetLayout);
        clickThreeDotsMenuButton();
        clickLayoutSettingsOption();
        clickDeleteLayoutButton();
        confirmDeleteLayout();

        boolean isDeleted = !WaitUtils.isVisible(driver, targetLayout, TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(isDeleted,
                "Layout '" + layoutName + "' should be deleted but is still visible");
        logger.info("Verified layout '{}' was successfully deleted", layoutName);
    }

    /** Steps "Stay in Layouts" and "Wait for the camera previews": previews loaded with image and timestamp. */
    public void verifyCameraPreviewsLoaded() {
        waitForCameraPreviewsToLoad();
        verifyFirstCameraPreviewElements();
    }

    public void waitForCameraPreviewsToLoad() {
        WaitUtils.waitForVisibility(driver, LayoutsLocators.CAMERA_PREVIEW_CARDS);
    }

    public void verifyFirstCameraPreviewElements() {
        List<WebElement> previewCards = WaitUtils.findElements(driver, LayoutsLocators.CAMERA_PREVIEW_CARDS);
        List<WebElement> previewImages = WaitUtils.findElements(driver, LayoutsLocators.CAMERA_PREVIEW_IMAGES);
        List<WebElement> timestamps = WaitUtils.findElements(driver, LayoutsLocators.CAMERA_TIMESTAMPS);

        Assert.assertFalse(previewCards.isEmpty(),
                "At least one camera preview card should be displayed");
        Assert.assertFalse(previewImages.isEmpty(),
                "Camera preview image elements should be present");
        Assert.assertFalse(timestamps.isEmpty(),
                "Camera timestamp elements should be present");

        WebElement firstVisibleImage = previewImages.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No visible camera preview image found"));
        Assert.assertTrue(firstVisibleImage.isDisplayed(),
                "Camera preview image element should be visible");

        WebElement firstVisibleTimestamp = timestamps.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No visible timestamp found"));
        String timestampText = firstVisibleTimestamp.getText();
        boolean isValidFormat = timestampText.matches(".*\\d{1,2}:\\d{2}\\s*[AP]M.*") ||
                                timestampText.matches(".*\\d{2}:\\d{2}.*");
        Assert.assertTrue(isValidFormat,
                "Timestamp should match time format (HH:MM AM/PM or HH:MM), got: " + timestampText);
    }

}
