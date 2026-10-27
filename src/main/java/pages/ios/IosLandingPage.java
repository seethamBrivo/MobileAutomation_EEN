package pages.ios;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.ios.IosLandingLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

/** The Layouts screen shown after sign-in, and the bottom tab bar around it (iOS). */
public class IosLandingPage {

    private static final Logger logger = LogManager.getLogger(IosLandingPage.class);
    private final AppiumDriver driver;

    public IosLandingPage(AppiumDriver driver) {
        this.driver = driver;
    }

    /** The app sometimes shows a feedback survey over the screen; it blocks every tap behind it. */
    public void dismissSurveyIfShown() {
        if (WaitUtils.isVisible(driver, IosLandingLocators.SURVEY_TITLE, TimeoutConstants.SHORT_TIMEOUT)) {
            WaitUtils.click(driver, IosLandingLocators.SURVEY_CLOSE_BUTTON);
            logger.info("Feedback survey closed");
        }
    }

    public void verifyLandingScreenDisplayed() {
        dismissSurveyIfShown();
        WaitUtils.waitForVisibility(driver, IosLandingLocators.LAYOUTS_TITLE, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        assertShown("Layouts title", IosLandingLocators.LAYOUTS_TITLE);
        assertShown("Notifications button", IosLandingLocators.NOTIFICATIONS_BUTTON);
        assertShown("Filter button", IosLandingLocators.FILTER_BUTTON);
        assertShown("More options button", IosLandingLocators.MORE_OPTIONS_BUTTON);
        logger.info("Landing screen top bar verified");
    }

    public void verifyTabBar() {
        assertShown("Layouts tab", IosLandingLocators.TAB_LAYOUTS);
        assertShown("Tags tab", IosLandingLocators.TAB_TAGS);
        assertShown("Search tab", IosLandingLocators.TAB_SEARCH);
        assertShown("More tab", IosLandingLocators.TAB_MORE);
        logger.info("Bottom tab bar verified");
    }

    public void verifyAllCamerasListed() {
        assertShown("'All cameras' chip selected", IosLandingLocators.ALL_CAMERAS_CHIP_SELECTED);
        WaitUtils.waitForVisibility(driver, IosLandingLocators.FIRST_CAMERA_TILE_NAME, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        String name = driver.findElement(IosLandingLocators.FIRST_CAMERA_TILE_NAME).getText();
        Assert.assertFalse(name.isBlank(), "First camera tile should show a camera name");
        logger.info("First camera tile: {}", name);
    }

    public void openMoreTab() {
        dismissSurveyIfShown();
        WaitUtils.click(driver, IosLandingLocators.TAB_MORE);
        Assert.assertTrue(WaitUtils.waitForInvisibility(driver, IosLandingLocators.LAYOUTS_TITLE, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                "Layouts title should disappear after opening the More tab");
    }

    public void openLayoutsTab() {
        dismissSurveyIfShown();
        WaitUtils.click(driver, IosLandingLocators.TAB_LAYOUTS);
        WaitUtils.waitForVisibility(driver, IosLandingLocators.LAYOUTS_TITLE, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
    }

    private void assertShown(String what, By locator) {
        Assert.assertTrue(WaitUtils.isVisible(driver, locator, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                what + " should be displayed after sign-in");
    }
}
