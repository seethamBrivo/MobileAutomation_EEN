package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.WaitUtils;

public class LayoutsPage {

    private static final Logger logger = LogManager.getLogger(LayoutsPage.class);
    private final AppiumDriver driver;

    private final By allCamerasTab = AppiumBy.accessibilityId("cell_at_0_layout_tab");
    private final By firstLayoutTab = AppiumBy.accessibilityId("cell_at_1_layout_tab");
    private final By threeDotsMenuButton = AppiumBy.id("com.een.vms.android:id/img_more");
    private final By addCamerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Add cameras']");
    private final By newLayoutOption = AppiumBy.xpath("//android.widget.TextView[@text='New layout']");
    private final By editLayoutOption = AppiumBy.xpath("//android.widget.TextView[@text='Edit layout']");
    private final By layoutSettingsOption = AppiumBy.xpath("//android.widget.TextView[@text='Layout settings']");
    private final By mapViewOption = AppiumBy.xpath("//android.widget.TextView[@text='Map view']");
    private final By zoomInOption = AppiumBy.xpath("//android.widget.TextView[@text='Zoom in']");
    private final By zoomOutOption = AppiumBy.xpath("//android.widget.TextView[@text='Zoom out']");
    private final By layoutNameLabel = AppiumBy.accessibilityId("row_name_header");
    private final By layoutNameEditField = AppiumBy.accessibilityId("row_name_dj0");
    private final By aspectRatio16_9 = AppiumBy.xpath("//android.widget.TextView[@text='16:9']");
    private final By aspectRatio4_3 = AppiumBy.xpath("//android.widget.TextView[@text='4:3']");
    private final By showCameraTitleBarsToggle = AppiumBy.accessibilityId("row_show_cameras_title_item_switch");
    private final By addCamerasIcon = AppiumBy.accessibilityId("row_add_cameras_end_icon");
    private final By selectCamera = AppiumBy.accessibilityId("cell_at_0_add_camera_check_box");
    private final By saveButton = AppiumBy.xpath("//android.widget.TextView[@text='Save']");
    private final By addButton = AppiumBy.xpath("//android.widget.TextView[@text='Add']");
    private final By successMessage = AppiumBy.id("com.een.vms.android:id/snackbar_text");
    private final By filterIcon = AppiumBy.id("com.een.vms.android:id/img_search");
    private final By layoutsTab = AppiumBy.xpath("//android.widget.TextView[@text='LAYOUTS']");
    private final By searchBar = AppiumBy.id("com.een.vms.android:id/search_bar");
    private final By deleteButton = AppiumBy.accessibilityId("delete_header");
    private final By confirmButton = AppiumBy.accessibilityId("negative_button");
    private final By cameraPreviewCards = AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']");
    private final By cameraPreviewImages = AppiumBy.xpath("//android.widget.ImageView[@resource-id='com.een.vms.android:id/camera_preview']");
    private final By cameraTimestamps = AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']//android.widget.TextView[contains(@text, ':') and string-length(@text) > 3 and string-length(@text) < 25 and (contains(@text, '0') or contains(@text, '1') or contains(@text, '2') or contains(@text, '3') or contains(@text, '4') or contains(@text, '5') or contains(@text, '6') or contains(@text, '7') or contains(@text, '8') or contains(@text, '9'))]");

    private By layoutByName(String layoutName) {
        String xpathValue = buildXPathStringLiteral(layoutName);
        return AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']"
                + "//androidx.cardview.widget.CardView[descendant::*[contains(@text, " + xpathValue + ")]]");
    }

    private String buildXPathStringLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder result = new StringBuilder("concat(");
        String[] parts = value.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(", \"'\", ");
            }
            result.append("'").append(parts[i]).append("'");
        }
        result.append(")");
        return result.toString();
    }

    /**
     * Returns locator for camera count option in max cameras per row selector.
     * NOTE: This locator is intentionally unscoped as the selection dialog has no unique
     * container or identifiers. App team has been requested to add testTag for scoping.
     * Current implementation validated with input range check (1-6) and post-selection
     * screen verification via ensureOnNewLayoutScreen().
     * TODO: Replace with scoped locator once app team provides testTag
     */
    private By camerasPerRowOption(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Invalid camera count for locator: " + count + " (must be 1-6)");
        }
        return AppiumBy.xpath("//android.widget.TextView[@text='" + count + "']");
    }

    public LayoutsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyLayoutsScreenDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, threeDotsMenuButton),
                "Three dots menu button should be visible on layouts screen");
    }

    public void clickAllCamerasTab() {
        WaitUtils.click(driver, allCamerasTab);
    }

    public void clickFirstLayoutTab() {
        WaitUtils.click(driver, firstLayoutTab);
    }

    public void clickThreeDotsMenuButton() {
        WaitUtils.click(driver, threeDotsMenuButton);
    }

    public void verifyMenuPopupDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, newLayoutOption),
                "Three dots menu popup should be displayed");
    }

    public void verifyMenuOptions() {
        List<By> menuOptions = List.of(
                addCamerasOption, newLayoutOption, editLayoutOption,
                layoutSettingsOption, mapViewOption, zoomInOption
        );

        Assert.assertTrue(WaitUtils.waitForAny(driver, menuOptions, FrameworkConstants.EXPLICIT_WAIT).isPresent(),
                "At least one menu option should be displayed");

        Assert.assertTrue(WaitUtils.isVisible(driver, addCamerasOption, TimeoutConstants.SHORT_TIMEOUT),
                "Add Cameras option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, newLayoutOption, TimeoutConstants.SHORT_TIMEOUT),
                "New Layout option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, editLayoutOption, TimeoutConstants.SHORT_TIMEOUT),
                "Edit Layout option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, layoutSettingsOption, TimeoutConstants.SHORT_TIMEOUT),
                "Layout Settings option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, mapViewOption, TimeoutConstants.SHORT_TIMEOUT),
                "Map View option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, zoomInOption, TimeoutConstants.SHORT_TIMEOUT),
                "Zoom In option should be displayed");

        if (WaitUtils.isVisible(driver, zoomOutOption, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Zoom Out option is displayed");
        } else {
            logger.info("Zoom Out option not displayed - already in the zoom out state");
        }
    }

    public void clickNewLayoutOption() {
        WaitUtils.click(driver, newLayoutOption);
    }

    public void verifyNewLayoutScreenDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, layoutNameLabel),
                "New Layout screen should be displayed");
    }

    public void setLayoutName(String layoutName) {
        WaitUtils.click(driver, layoutNameLabel);
        WaitUtils.clearAndType(driver, layoutNameEditField, layoutName);
    }

    public void selectAspectRatio(String ratio) {
        if (!"16:9".equals(ratio) && !"4:3".equals(ratio)) {
            throw new IllegalArgumentException("Aspect ratio must be '16:9' or '4:3', got: " + ratio);
        }
        By aspectRatioLocator = ratio.equals("16:9") ? aspectRatio16_9 : aspectRatio4_3;
        WaitUtils.click(driver, aspectRatioLocator);
        ensureOnNewLayoutScreen();
    }

    public void selectMaxCamerasPerRow(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Max cameras per row must be between 1 and 6");
        }
        WaitUtils.click(driver, camerasPerRowOption(count));
        ensureOnNewLayoutScreen();
    }

    private void ensureOnNewLayoutScreen() {
        if (WaitUtils.isVisible(driver, layoutNameLabel, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.debug("Already on New Layout screen");
            return;
        }

        logger.debug("New Layout screen not visible, navigating back");
        try {
            driver.navigate().back();
            WaitUtils.waitForVisibility(driver, layoutNameLabel, FrameworkConstants.EXPLICIT_WAIT);
            logger.debug("Confirmed on New Layout screen after back navigation");
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IllegalStateException("Cannot return to New Layout screen after navigate().back()", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to return to New Layout screen: " + e.getMessage(), e);
        }
    }

    public void toggleShowCameraTitleBars(boolean enable) {
        WebElement toggle = WaitUtils.waitForVisibility(driver, showCameraTitleBarsToggle);
        boolean isEnabled = toggle.isSelected();

        if (enable != isEnabled) {
            WaitUtils.click(driver, showCameraTitleBarsToggle);
        }
        ensureOnNewLayoutScreen();
    }

    public void clickAddCamerasIcon() {
        WaitUtils.click(driver, addCamerasIcon);
    }

    public void selectFirstCamera() {
        WaitUtils.click(driver, selectCamera);
    }

    public void clickSaveButton() {
        WaitUtils.click(driver, saveButton);
    }

    public void clickAddButton() {
        WaitUtils.click(driver, addButton);
    }

    public void verifyLayoutCreatedSuccessfully(String layoutName) {
        if (WaitUtils.isVisible(driver, successMessage, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Layout creation confirmed via snackbar message");
            ensureOnLayoutsListScreen();
            return;
        }

        logger.info("Snackbar not visible, verifying layout '{}' exists in list", layoutName);
        clickFilterIcon();
        clickLayoutsTab();
        enterLayoutNameInSearchFilter(layoutName);

        By targetLayout = layoutByName(layoutName);
        Assert.assertTrue(WaitUtils.isVisible(driver, targetLayout, FrameworkConstants.EXPLICIT_WAIT),
                "Layout '" + layoutName + "' should appear in layouts list after creation");
        logger.info("Layout creation confirmed - '{}' found in layouts list", layoutName);

        ensureOnLayoutsListScreen();
    }

    private void ensureOnLayoutsListScreen() {
        int maxAttempts = 3;
        for (int i = 0; i < maxAttempts; i++) {
            if (WaitUtils.isVisible(driver, threeDotsMenuButton, TimeoutConstants.SHORT_TIMEOUT)) {
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
        WaitUtils.click(driver, filterIcon);
    }

    public void clickLayoutsTab() {
        WaitUtils.click(driver, layoutsTab);
    }

    public void enterLayoutNameInSearchFilter(String layoutName) {
        WaitUtils.clearAndType(driver, searchBar, layoutName);
    }

    public void clickLayoutSettingsOption() {
        WaitUtils.click(driver, layoutSettingsOption);
    }

    public void clickDeleteLayoutButton() {
        WaitUtils.click(driver, deleteButton);
    }

    public void confirmDeleteLayout() {
        WaitUtils.click(driver, confirmButton);
    }

    public void deleteLayout(String layoutName) {
        clickFilterIcon();
        clickLayoutsTab();
        enterLayoutNameInSearchFilter(layoutName);
        By targetLayout = layoutByName(layoutName);
        WaitUtils.waitForVisibility(driver, targetLayout);
        WaitUtils.click(driver, targetLayout);
        clickThreeDotsMenuButton();
        clickLayoutSettingsOption();
        clickDeleteLayoutButton();
        confirmDeleteLayout();

        boolean isDeleted = !WaitUtils.isVisible(driver, targetLayout, TimeoutConstants.SHORT_TIMEOUT);
        if (isDeleted) {
            logger.info("Verified layout '{}' was successfully deleted", layoutName);
        } else {
            logger.warn("Layout '{}' still visible after deletion - may have failed", layoutName);
        }
    }

    public void waitForCameraPreviewsToLoad() {
        WaitUtils.waitForVisibility(driver, cameraPreviewCards);
    }

    public void verifyFirstCameraPreviewElements() {
        List<WebElement> previewCards = WaitUtils.findElements(driver, cameraPreviewCards);
        List<WebElement> previewImages = WaitUtils.findElements(driver, cameraPreviewImages);
        List<WebElement> timestamps = WaitUtils.findElements(driver, cameraTimestamps);

        Assert.assertFalse(previewCards.isEmpty(),
                "At least one camera preview card should be displayed");
        Assert.assertFalse(previewImages.isEmpty(),
                "Camera preview image elements should be present");
        Assert.assertFalse(timestamps.isEmpty(),
                "Camera timestamp elements should be present");

        WebElement firstImage = previewImages.get(0);
        Assert.assertTrue(firstImage.isDisplayed(),
                "Camera preview image element should be visible");

        WebElement firstTimestamp = timestamps.get(0);
        String timestampText = firstTimestamp.getText();
        boolean isValidFormat = timestampText.matches(".*\\d{1,2}:\\d{2}\\s*[AP]M.*") ||
                                timestampText.matches(".*\\d{2}:\\d{2}.*");
        Assert.assertTrue(isValidFormat,
                "Timestamp should match time format (HH:MM AM/PM or HH:MM), got: " + timestampText);
    }

}
