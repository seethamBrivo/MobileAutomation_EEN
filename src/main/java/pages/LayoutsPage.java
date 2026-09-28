package pages;

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

    private final By layoutsScreen = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView");
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
    private final By showCameraTitleBarsToggle = AppiumBy.xpath("//android.widget.Switch[@content-desc='row_show_cameras_title_item_switch']");
    private final By addCamerasIcon = AppiumBy.accessibilityId("row_add_cameras_end_icon");
    // Always selects first camera (cell_at_0) as per test case requirement
    private final By selectCamera = AppiumBy.accessibilityId("cell_at_0_add_camera_check_box");
    private final By saveButton = AppiumBy.xpath("//android.widget.TextView[@text='Save']");
    private final By addButton = AppiumBy.xpath("//android.widget.TextView[@text='Add']");
    private final By successMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Layout successfully created')]");
    private final By filterIcon = AppiumBy.id("com.een.vms.android:id/img_search");
    private final By layoutsTab = AppiumBy.xpath("//android.widget.TextView[@text='LAYOUTS']");
    private final By searchBar = AppiumBy.id("com.een.vms.android:id/search_bar");
    private final By filteredLayout = AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']//androidx.cardview.widget.CardView[1]");
    private final By deleteButton = AppiumBy.accessibilityId("delete_header");
    private final By confirmButton = AppiumBy.accessibilityId("negative_button");
    private final By cameraPreviewCards = AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']");
    private final By cameraPreviewImages = AppiumBy.xpath("//android.widget.ImageView[@resource-id='com.een.vms.android:id/camera_preview']");
    private final By cameraTimestamps = AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']//android.widget.TextView[contains(@text, ':')]");

    private By layoutByName(String layoutName) {
        // XPath 1.0 doesn't support entity escaping - use concat() for quotes
        String xpathValue = buildXPathStringLiteral(layoutName);
        return AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']"
                + "//androidx.cardview.widget.CardView[descendant::*[contains(@text, " + xpathValue + ")]]");
    }

    /**
     * Builds an XPath string literal that properly handles quotes using concat().
     * XPath 1.0 has no escape mechanism, so we break on quotes and concatenate.
     */
    private String buildXPathStringLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        // Contains both - use concat
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
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, layoutsScreen),
                "Layouts screen should be displayed after login");
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, threeDotsMenuButton),
                "Three dots menu button should be visible on layouts screen");
    }

    public void clickAllCamerasTab() {
        WebElement tab = WaitUtils.waitForVisibility(driver, allCamerasTab);
        tab.click();
    }

    public void clickFirstLayoutTab() {
        WebElement tab = WaitUtils.waitForVisibility(driver, firstLayoutTab);
        tab.click();
    }

    public void clickThreeDotsMenuButton() {
        WaitUtils.click(driver, threeDotsMenuButton);
    }

    public void verifyMenuPopupDisplayed() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, newLayoutOption),
                "Three dots menu popup should be displayed");
    }

    public void verifyMenuOptions() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, addCamerasOption),
                "Add Cameras option should be displayed");
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, newLayoutOption),
                "New Layout option should be displayed");
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, editLayoutOption),
                "Edit Layout option should be displayed");
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, layoutSettingsOption),
                "Layout Settings option should be displayed");
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, mapViewOption),
                "Map View option should be displayed");
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, zoomInOption),
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

    /**
     * Ensures we're back on the New Layout screen after selecting an option.
     * Some selections auto-navigate back, others require explicit back navigation.
     */
    private void ensureOnNewLayoutScreen() {
        // Quick instant check first - avoid polling if already on screen
        if (WaitUtils.isPresent(driver, layoutNameLabel)) {
            logger.debug("Already on New Layout screen");
            return;
        }

        // Not immediately present - wait briefly then navigate back if needed
        if (!WaitUtils.isVisible(driver, layoutNameLabel, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.debug("New Layout screen not visible, navigating back");
            try {
                driver.navigate().back();
            } catch (Exception e) {
                logger.warn("Back navigation failed: {}", e.getMessage());
                throw new IllegalStateException("Cannot return to New Layout screen", e);
            }
            WaitUtils.waitForVisibility(driver, layoutNameLabel);
        }
        logger.debug("Confirmed on New Layout screen");
    }

    public void toggleShowCameraTitleBars(boolean enable) {
        WebElement toggle = WaitUtils.waitForVisibility(driver, showCameraTitleBarsToggle);
        String checkedAttr = toggle.getAttribute("checked");
        if (checkedAttr == null) {
            throw new IllegalStateException("Toggle 'checked' attribute is null - cannot determine current state");
        }
        boolean isEnabled = Boolean.parseBoolean(checkedAttr);

        if (enable != isEnabled) {
            WaitUtils.click(driver, showCameraTitleBarsToggle);
        }
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

    public void verifyLayoutCreatedSuccessfully() {
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, successMessage),
                "Success message should be displayed");
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

    public void clickFilteredLayout() {
        WaitUtils.click(driver, filteredLayout);
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
        // Wait for and click the specific layout by name
        By targetLayout = layoutByName(layoutName);
        WaitUtils.waitForVisibility(driver, targetLayout);
        WaitUtils.click(driver, targetLayout);
        clickThreeDotsMenuButton();
        clickLayoutSettingsOption();
        clickDeleteLayoutButton();
        confirmDeleteLayout();
    }

    public void waitForCameraPreviewsToLoad() {
        WaitUtils.waitForVisibility(driver, cameraPreviewCards);
    }

    /**
     * Optimized verification that checks only the first preview element to reduce test execution time.
     * Use this for smoke tests where verifying one representative element is sufficient.
     */
    public void verifyFirstCameraPreviewElements() {
        // Wait only for cards (fast - ~1-2s, doesn't wait for image data from network)
        WaitUtils.waitForVisibility(driver, cameraPreviewCards);

        // Fetch elements - cards exist, so images and timestamps should be present in DOM
        List<WebElement> previewCards = driver.findElements(cameraPreviewCards);
        List<WebElement> previewImages = driver.findElements(cameraPreviewImages);
        List<WebElement> timestamps = driver.findElements(cameraTimestamps);

        // Verify elements exist
        Assert.assertFalse(previewCards.isEmpty(),
                "At least one camera preview card should be displayed");
        Assert.assertFalse(previewImages.isEmpty(),
                "Camera preview image elements should be present");
        Assert.assertFalse(timestamps.isEmpty(),
                "Camera timestamp elements should be present");

        // Verify first image element structure
        WebElement firstImage = previewImages.get(0);
        Assert.assertTrue(firstImage.isDisplayed(),
                "Camera preview image element should be visible");

        // Verify timestamp format
        WebElement firstTimestamp = timestamps.get(0);
        String timestampText = firstTimestamp.getText();
        Assert.assertTrue(timestampText.matches(".*\\d{1,2}:\\d{2}.*"),
                "Timestamp should match time format (HH:MM), got: " + timestampText);
    }

}
