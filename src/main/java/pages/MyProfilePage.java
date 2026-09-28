package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

public class MyProfilePage {

    private static final Logger logger = LogManager.getLogger(MyProfilePage.class);
    private final AppiumDriver driver;

    // Stable: Profile section is always first [1] child in ScrollView on My Profile screen
    private final By profileSection = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.view.View");
    private final By timezoneOption = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']");
    private final By currentTimezoneValue = AppiumBy.xpath("//android.widget.TextView[@text='Time zone']/following-sibling::android.widget.TextView[1]");

    public MyProfilePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickProfileSection() {
        WaitUtils.click(driver, profileSection);
    }

    public void scrollToTimeSection() {
        if (!WaitUtils.isVisible(driver, timezoneOption, TimeoutConstants.SHORT_TIMEOUT)) {
            GestureUtils.swipeUp(driver);
        }
    }

    public void clickTimezoneOption() {
        WaitUtils.click(driver, timezoneOption);
    }

    public String getCurrentTimezone() {
        String timezone = WaitUtils.waitForVisibility(driver, currentTimezoneValue).getText();
        logger.info("Current timezone: {}", timezone);
        return timezone;
    }

    public void verifyTimezoneChanged(String expectedTimezone) {
        String actualTimezone = getCurrentTimezone();
        Assert.assertTrue(actualTimezone.contains(expectedTimezone),
                "Timezone should be changed to " + expectedTimezone + " but was: " + actualTimezone);
    }
}
