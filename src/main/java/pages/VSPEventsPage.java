package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import utils.ConfigReader;
import utils.PlatformLocator;
import utils.WaitUtils;

public class VSPEventsPage {

    private static final Logger logger = LogManager.getLogger(VSPEventsPage.class);
    private final AppiumDriver driver;

    // Stable: ProfileMain back button at fixed position [2] in profile screen
    private final By backButton = PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ProfileMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View"),
        AppiumBy.accessibilityId("backButton")
    );
    private final By vspSection = PlatformLocator.byText("VSP");
    private final By eventsOption = PlatformLocator.byText("Events");
    private final By vspEventsHeading = PlatformLocator.byText("VSP Events");
    // Scoped to VspMain screen - timestamp pattern specific to event list (Today, HH:MM format)
    private final By timestampWithTime = buildTimestampLocator();
    // Stable: First event card at position [4][1] in VspMain event list
    private final By firstEventInList = PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]/android.view.View"),
        AppiumBy.accessibilityId("firstEventCard")
    );
    // No events messages - displayed when VSP event list is empty
    private final By noEventsMessage = PlatformLocator.byText("No license plate events yet");
    private final By zeroEventsMessage = PlatformLocator.byText("0 license plate events");

    private By buildTimestampLocator() {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return AppiumBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText' AND label CONTAINS 'Today,' AND label CONTAINS ':'");
        }
        return AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]//android.widget.TextView[contains(@text, 'Today,') and contains(@text, ':')]");
    }

    public VSPEventsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateBackFromProfile() {
        WaitUtils.click(driver, backButton);
    }

    public void verifyVSPSectionVisible() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, vspSection).isDisplayed(),
                "VSP section should be visible");
    }

    public void clickEvents() {
        WaitUtils.click(driver, eventsOption);
    }

    public void verifyVSPEventsScreenDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, vspEventsHeading).isDisplayed(),
                "VSP Events screen heading should be displayed");
    }

    public String captureEventTimestamp() {
        boolean eventsExist = WaitUtils.isVisible(driver, firstEventInList, TimeoutConstants.FILTER_RESULT_TIMEOUT);

        if (!eventsExist) {
            boolean noEventsDisplayed = WaitUtils.isVisible(driver, noEventsMessage, TimeoutConstants.SHORT_TIMEOUT) ||
                                       WaitUtils.isVisible(driver, zeroEventsMessage, TimeoutConstants.SHORT_TIMEOUT);

            if (noEventsDisplayed) {
                logger.warn("No VSP events available - returning empty timestamp");
                return "";
            } else {
                Assert.fail("VSP Events screen loaded but neither events nor 'no events' message found");
                return ""; // Unreachable, but keeps compiler happy
            }
        }

        String timestampText = WaitUtils.waitForVisibility(driver, timestampWithTime).getText();
        logger.info("Captured VSP Event timestamp: {}", timestampText);
        return timestampText;
    }

    public void verifyTimestampChangedAfterTimezoneChange(String initialTimestamp) {
        String currentTimestamp = captureEventTimestamp();

        if (initialTimestamp.isEmpty() || currentTimestamp.isEmpty()) {
            throw new SkipException("SKIP: No VSP events available - cannot verify timezone change in events list. " +
                                   "Timezone setting change is verified in profile, but event timestamp change cannot be confirmed.");
        }

        Assert.assertNotEquals(currentTimestamp, initialTimestamp,
                "VSP Event timestamp should change after timezone change. Before: " + initialTimestamp + ", After: " + currentTimestamp);
        logger.info("Verified VSP Event timestamp changed from '{}' to '{}'", initialTimestamp, currentTimestamp);
    }
}
