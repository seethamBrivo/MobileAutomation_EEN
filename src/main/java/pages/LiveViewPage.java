package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.LiveViewLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.DeviceUtils;
import utils.GestureUtils;
import utils.WaitUtils;

public class LiveViewPage {

    private final AppiumDriver driver;
    private final Logger logger = LogManager.getLogger(LiveViewPage.class);

    public LiveViewPage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
    }

    public void navigateToLiveView(String cameraName) {
        logger.info("Navigating to live view of camera: {}", cameraName);
        clickSearchFilter();
        enterCameraSearch(cameraName);
        clickCameraCard(cameraName);
        verifyLiveViewDisplayed();
        logger.info("Successfully navigated to live view");
    }

    public void clickSearchFilter() {
        WaitUtils.click(driver, LiveViewLocators.SEARCH_FILTER_ICON);
    }

    public void enterCameraSearch(String cameraName) {
        WebElement searchBar = WaitUtils.waitForVisibility(driver, LiveViewLocators.SEARCH_BAR);
        searchBar.clear();
        searchBar.sendKeys(cameraName);
    }

    public void clickCameraCard(String cameraName) {
        WaitUtils.click(driver, LiveViewLocators.cameraCardByName(cameraName));
    }

    public void verifyLiveViewDisplayed() {
        boolean isDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.MECHANICAL_ZOOM_BUTTON,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(isDisplayed, "Live view screen should be displayed (mechanical zoom button visible)");
    }

    public void verifyMechanicalZoomButtonInBothOrientationsAndClick() {
        boolean portraitDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.MECHANICAL_ZOOM_BUTTON,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(portraitDisplayed, "Mechanical zoom button should be displayed at bottom in Portrait mode");

        DeviceUtils.rotateToLandscape(driver);

        boolean landscapeDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.MECHANICAL_ZOOM_BUTTON,
                TimeoutConstants.LIST_LOAD_TIMEOUT
        );
        Assert.assertTrue(landscapeDisplayed, "Mechanical zoom button should be displayed on right side in Landscape mode");

        DeviceUtils.rotateToPortrait(driver);

        WaitUtils.click(driver, LiveViewLocators.MECHANICAL_ZOOM_BUTTON);
    }

    public void verifyMechanicalZoomMenuDisplayed() {
        boolean zoomInDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_IN_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomInDisplayed, "Zoom In option should be displayed");

        boolean zoomOutDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_OUT_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomOutDisplayed, "Zoom out option should be displayed");

        boolean autoFocusDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.AUTO_FOCUS_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(autoFocusDisplayed, "Auto focus option should be displayed");
    }

    public void clickAutoFocusWithVerification() {
        WaitUtils.click(driver, LiveViewLocators.AUTO_FOCUS_ICON);

        boolean autoFocusStillVisible = WaitUtils.isVisible(
                driver,
                LiveViewLocators.AUTO_FOCUS_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(autoFocusStillVisible,
                "Auto focus control should remain visible and active after clicking");

        boolean zoomInVisible = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_IN_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomInVisible,
                "Zoom in control should remain visible after auto focus operation");
    }

    public void clickZoomInWithVerification() {
        WaitUtils.click(driver, LiveViewLocators.ZOOM_IN_ICON);

        boolean zoomInStillVisible = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_IN_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomInStillVisible,
                "Zoom in control should remain visible and active after clicking");

        boolean zoomOutVisible = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_OUT_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomOutVisible,
                "Zoom out control should remain visible after zoom in operation");
    }

    public void clickZoomOutWithVerification() {
        WaitUtils.click(driver, LiveViewLocators.ZOOM_OUT_ICON);

        boolean zoomOutStillVisible = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_OUT_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomOutStillVisible,
                "Zoom out control should remain visible and active after clicking");

        boolean zoomInVisible = WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_IN_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(zoomInVisible,
                "Zoom in control should remain visible after zoom out operation");
    }

    public void tapMechanicalZoomIcon() {
        WaitUtils.click(driver, LiveViewLocators.MECHANICAL_ZOOM_BUTTON);
    }

    public void verifyMechanicalZoomMenuClosed() {
        boolean menuClosed = !WaitUtils.isVisible(
                driver,
                LiveViewLocators.ZOOM_IN_ICON,
                TimeoutConstants.SHORT_TIMEOUT
        );
        Assert.assertTrue(menuClosed,
                "Mechanical zoom menu (Zoom In/out/Auto focus) should be closed");
    }

    public void tapScreenToHideMechanicalZoom() {
        GestureUtils.tapScreenCenter(driver);
    }

    public void verifyMechanicalZoomHidden() {
        boolean isHidden = !WaitUtils.isVisible(
                driver,
                LiveViewLocators.MECHANICAL_ZOOM_BUTTON,
                TimeoutConstants.SHORT_TIMEOUT
        );

        Assert.assertTrue(isHidden,
                "Mechanical zoom should not be visible in the live section");
    }

    public void tapScreenToUnhideMechanicalZoom() {
        GestureUtils.tapScreenCenter(driver);
    }

    public void verifyMechanicalZoomUnhidden() {
        boolean isDisplayed = WaitUtils.isVisible(
                driver,
                LiveViewLocators.MECHANICAL_ZOOM_BUTTON,
                TimeoutConstants.SHORT_TIMEOUT
        );

        Assert.assertTrue(isDisplayed,
                "Mechanical zoom should be displayed in the live screen");
    }

    public void verifyMechanicalZoomMenuClickAutoFocusAndClose() {
        verifyMechanicalZoomMenuDisplayed();
        clickAutoFocusWithVerification();
        tapMechanicalZoomIcon();
        verifyMechanicalZoomMenuClosed();
    }

    public void verifyMechanicalZoomMenuClickZoomInOutAndClose() {
        verifyMechanicalZoomMenuDisplayed();
        clickZoomInWithVerification();
        clickZoomOutWithVerification();
        tapMechanicalZoomIcon();
        verifyMechanicalZoomMenuClosed();
    }

    public void verifyMechanicalZoomHideUnhideBehavior() {
        tapScreenToHideMechanicalZoom();
        verifyMechanicalZoomHidden();
        tapScreenToUnhideMechanicalZoom();
        verifyMechanicalZoomUnhidden();
    }
}
