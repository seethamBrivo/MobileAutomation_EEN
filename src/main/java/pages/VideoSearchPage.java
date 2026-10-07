package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

public class VideoSearchPage {

    private static final Logger logger = LogManager.getLogger(VideoSearchPage.class);
    private final AppiumDriver driver;

    // Stable: Back button at fixed position [6] in VspMain screen
    private final By backButtonFromVSPEvents = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"VspMain\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View/android.view.View");
    private final By searchOption = AppiumBy.xpath("//android.widget.TextView[@text='Search']");
    private final By dateField = AppiumBy.id("com.een.vms.android:id/date");

    public VideoSearchPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateBackFromVSPEvents() {
        WaitUtils.click(driver, backButtonFromVSPEvents);
    }

    public void clickSearch() {
        WaitUtils.click(driver, searchOption);
    }

    public void verifyDateFieldDisplayed() {
        String dateText = WaitUtils.waitForVisibility(driver, dateField).getText();
        logger.info("Video search date/time displayed: {}", dateText);

        Assert.assertFalse(dateText.isEmpty(),
                "Date field should contain date/time information");

        Assert.assertTrue(dateText.matches(".*\\d+.*"),
                "Date field should contain numeric date/time values, but was: " + dateText);

        Assert.assertTrue(dateText.matches(".*[\\d/:,\\s-]+.*"),
                "Date field should be in a recognizable date/time format, but was: " + dateText);
    }
}
