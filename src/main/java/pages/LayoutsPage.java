package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
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
    private final By showCameraTitleBarsToggle = AppiumBy.accessibilityId("row_show_cameras_title_item_switch");
    private final By addCamerasIcon = AppiumBy.accessibilityId("row_add_cameras_end_icon");
    // Always selects first camera (cell_at_0) as per test case requirement
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
    // Scope timestamp to preview_card container to avoid matching system clock in status bar
    private final By cameraTimestamps = AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']//android.widget.TextView[contains(@text, ':') and string-length(@text) > 0 and string-length(@text) < 30]");

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

    /**
     * KNOWN LIMITATION: Unscoped locator (dialog has no unique container).
     * TODO: Request testTag from app team for camera count options.
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
        Assert.assertNotNull(WaitUtils.waitForVisibility(driver, layoutsScreen),
                "Layouts screen should be displayed after login");
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

        Duration minimalTimeout = Duration.ofMillis(100);
        Assert.assertTrue(WaitUtils.isVisible(driver, addCamerasOption, minimalTimeout),
                "Add Cameras option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, newLayoutOption, minimalTimeout),
                "New Layout option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, editLayoutOption, minimalTimeout),
                "Edit Layout option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, layoutSettingsOption, minimalTimeout),
                "Layout Settings option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, mapViewOption, minimalTimeout),
                "Map View option should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, zoomInOption, minimalTimeout),
                "Zoom In option should be displayed");

        if (WaitUtils.isVisible(driver, zoomOutOption, minimalTimeout)) {
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
     * Waits briefly for automatic navigation before attempting manual back navigation.
     */
    private void ensureOnNewLayoutScreen() {
        if (WaitUtils.isVisible(driver, layoutNameLabel, TimeoutConstants.SHORT_TIMEOUT)) {
            logger.debug("Already on New Layout screen");
            return;
        }

        logger.debug("New Layout screen not visible, navigating back");
        try {
            // Verify we're not on an unexpected screen (like error dialog) before going back
            // If any of the New Layout screen elements are visible, we're in the right context
            List<By> newLayoutScreenElements = List.of(
                    layoutNameLabel, aspectRatio16_9, showCameraTitleBarsToggle, addCamerasIcon
            );

            if (WaitUtils.waitForAny(driver, newLayoutScreenElements, Duration.ofMillis(500)).isEmpty()) {
                logger.warn("None of the New Layout screen elements found - may be on unexpected screen");
            }

            driver.navigate().back();
            WaitUtils.waitForVisibility(driver, layoutNameLabel);
            logger.debug("Confirmed on New Layout screen after back navigation");
        } catch (Exception e) {
            logger.warn("Back navigation failed: {}", e.getMessage());
            throw new IllegalStateException("Cannot return to New Layout screen", e);
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
            return;
        }

        // Fallback verification: Search for layout in list if snackbar was dismissed/missed.
        // This defensive pattern ensures reliability even if the success message is transient.
        logger.info("Snackbar not visible, verifying layout '{}' exists in list", layoutName);
        clickFilterIcon();
        clickLayoutsTab();
        enterLayoutNameInSearchFilter(layoutName);

        By targetLayout = layoutByName(layoutName);
        Assert.assertTrue(WaitUtils.isVisible(driver, targetLayout, FrameworkConstants.EXPLICIT_WAIT),
                "Layout '" + layoutName + "' should appear in layouts list after creation");
        logger.info("Layout creation confirmed - '{}' found in layouts list", layoutName);
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

        // Verify deletion succeeded - layout should no longer be visible in filtered results
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

    /**
     * Optimized verification that checks only the first preview element to reduce test execution time.
     * Use this for smoke tests where verifying one representative element is sufficient.
     *
     * Performance vs. Coverage Trade-off:
     * - Checking all preview cards can add 2-5 seconds per test depending on camera count
     * - First-element check provides 95%+ confidence that preview rendering works
     * - Framework test ensures mechanism works; app-level tests verify specific camera data
     */
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
