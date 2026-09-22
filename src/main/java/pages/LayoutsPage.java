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
    private final By layoutNameLabel = AppiumBy.xpath("//android.widget.TextView[@content-desc=\"row_name_header\"]");
    private final By layoutNameEditField = AppiumBy.xpath("//android.widget.EditText[@content-desc=\"row_name_dj0\"]");
    private final By aspectRatio16_9 = AppiumBy.xpath("//android.widget.TextView[@text='16:9']");
    private final By aspectRatio4_3 = AppiumBy.xpath("//android.widget.TextView[@text='4:3']");
    private final By showCameraTitleBarsToggle = AppiumBy.xpath("//android.widget.Switch");
    private final By addCamerasIcon = AppiumBy.xpath("//android.widget.ImageView[@content-desc=\"row_add_cameras_end_icon\"]");
    // Always selects first camera (cell_at_0) as per test case requirement
    private final By selectCamera = AppiumBy.xpath("//android.widget.CheckBox[@content-desc=\"cell_at_0_add_camera_check_box\"]");
    private final By saveButton = AppiumBy.xpath("//android.widget.TextView[@text='Save']");
    private final By addButton = AppiumBy.xpath("//android.widget.TextView[@text='Add']");
    private final By successMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Layout successfully created')]");
    private final By filterIcon = AppiumBy.id("com.een.vms.android:id/img_search");
    private final By layoutsTab = AppiumBy.xpath("//android.widget.TextView[@text='LAYOUTS']");
    private final By searchBar = AppiumBy.id("com.een.vms.android:id/search_bar");
    private final By filteredLayout = AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']//androidx.cardview.widget.CardView[1]");

    private By layoutByName(String layoutName) {
        return AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.een.vms.android:id/recycler']"
                + "//androidx.cardview.widget.CardView[descendant::*[contains(@text, '" + layoutName + "')]]");
    }
    private final By deleteButton = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='delete_header_content']");
    private final By confirmButton = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']");
    private final By cameraPreviewCards = AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='com.een.vms.android:id/preview_card']");
    private final By cameraPreviewImages = AppiumBy.xpath("//android.widget.ImageView[@resource-id='com.een.vms.android:id/camera_preview']");
    private final By cameraTimestamps = AppiumBy.xpath("//android.widget.TextView[contains(@text, ':')]");

    private By camerasPerRowOption(int count) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + count + "']");
    }

    public LayoutsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyLayoutsScreenDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, layoutsScreen).isDisplayed(),
                "Layouts screen should be displayed after login");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, threeDotsMenuButton).isDisplayed(),
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
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, newLayoutOption).isDisplayed(),
                "Three dots menu popup should be displayed");
    }

    public void verifyMenuOptions() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addCamerasOption).isDisplayed(),
                "Add Cameras option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, newLayoutOption).isDisplayed(),
                "New Layout option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, editLayoutOption).isDisplayed(),
                "Edit Layout option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, layoutSettingsOption).isDisplayed(),
                "Layout Settings option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, mapViewOption).isDisplayed(),
                "Map View option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, zoomInOption).isDisplayed(),
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
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, layoutNameLabel).isDisplayed(),
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

        // Check if click already returned us to New Layout screen
        if (!WaitUtils.isVisible(driver, layoutNameLabel, TimeoutConstants.SHORT_TIMEOUT)) {
            driver.navigate().back();
        }
        WaitUtils.waitForVisibility(driver, layoutNameLabel);
    }

    public void selectMaxCamerasPerRow(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Max cameras per row must be between 1 and 6");
        }
        WaitUtils.click(driver, camerasPerRowOption(count));

        // Check if click already returned us to New Layout screen
        if (!WaitUtils.isVisible(driver, layoutNameLabel, TimeoutConstants.SHORT_TIMEOUT)) {
            driver.navigate().back();
        }
        WaitUtils.waitForVisibility(driver, layoutNameLabel);
    }

    public void toggleShowCameraTitleBars(boolean enable) {
        WebElement toggle = WaitUtils.waitForVisibility(driver, showCameraTitleBarsToggle);
        String checkedAttr = toggle.getAttribute("checked");
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
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, successMessage).isDisplayed(),
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
        // Wait for the specific layout to appear in filtered results
        WaitUtils.waitForVisibility(driver, layoutByName(layoutName));
        clickFilteredLayout();
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
    public void verifyAllCameraPreviewElementsFast() {
        waitForCameraPreviewsToLoad();

        // Wait for preview images and timestamps to load, not just the card containers
        WaitUtils.waitForVisibility(driver, cameraPreviewImages);
        WaitUtils.waitForVisibility(driver, cameraTimestamps);

        List<WebElement> previewCards = driver.findElements(cameraPreviewCards);
        List<WebElement> previewImages = driver.findElements(cameraPreviewImages);
        List<WebElement> timestamps = driver.findElements(cameraTimestamps);

        Assert.assertTrue(!previewCards.isEmpty(),
                "At least one camera preview card should be displayed");
        Assert.assertTrue(!previewImages.isEmpty(),
                "Camera preview images should be loaded");
        Assert.assertTrue(previewImages.get(0).isDisplayed(),
                "Camera preview image should be visible");
        org.openqa.selenium.Dimension size = previewImages.get(0).getSize();
        Assert.assertTrue(size.getWidth() > 0 && size.getHeight() > 0,
                "Camera preview image should have valid dimensions");
        Assert.assertTrue(!timestamps.isEmpty(),
                "Camera timestamps should be displayed");
        String timestampText = timestamps.get(0).getText();
        Assert.assertTrue(timestampText.contains(":"),
                "Timestamp should contain ':' character");
    }

}
