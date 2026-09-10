package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.util.Map;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/** The screen a bridge row opens: status, name editing, restart and delete. */
public class BridgeSettingsPage {

    public static final String STATUS_ONLINE = "Online";
    public static final String STATUS_OFFLINE = "Offline";
    public static final String STATUS_UNKNOWN = "Unknown";

    private final AppiumDriver driver;

    private final By bridgeSettingsPage = AppiumBy.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout");
    private final By statusSection = AppiumBy.xpath("//android.widget.TextView[@text='Status']");
    private final By onlineStatus = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Online')]");
    private final By offlineStatus = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Offline')]");

    private final By bridgeNameOption = AppiumBy.xpath("//android.widget.TextView[@text='Name']");
    // Generic: the name editor is the only EditText on its screen. No testTag available yet.
    private final By bridgeNameEditField = AppiumBy.xpath("//android.widget.EditText");

    private final By restartBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");
    private final By restartConfirmationDialog = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restart') or contains(@text, 'reboot')]");
    private final By confirmRestartButton = AppiumBy.xpath("//android.widget.Button[@content-desc='positive_button'] | //android.widget.TextView[@text='RESTART']");
    private final By bridgeRestartedMessage = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View");

    public BridgeSettingsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ---- status ---------------------------------------------------------------------------

    public void verifyBridgeSettingsPageDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, bridgeSettingsPage).isDisplayed(), "Bridge settings page should be displayed");
    }

    public void verifyStatusSectionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, statusSection).isDisplayed(), "Status section should be displayed");
    }

    /** {@link #STATUS_ONLINE}, {@link #STATUS_OFFLINE} or {@link #STATUS_UNKNOWN}. */
    public String getBridgeStatus() {
        if (WaitUtils.isVisible(driver, onlineStatus, TimeoutConstants.SHORT_TIMEOUT)) {
            return STATUS_ONLINE;
        }
        if (WaitUtils.isVisible(driver, offlineStatus, TimeoutConstants.SHORT_TIMEOUT)) {
            return STATUS_OFFLINE;
        }
        return STATUS_UNKNOWN;
    }

    public void verifyBridgeStatusIsKnown(String status) {
        Assert.assertNotEquals(status, STATUS_UNKNOWN,
                "Bridge status should be shown as Online or Offline on the settings screen");
    }

    // ---- rename ---------------------------------------------------------------------------

    public void clickNameOption() {
        WaitUtils.click(driver, bridgeNameOption);
    }

    public String getCurrentBridgeNameFromEditField() {
        return WaitUtils.waitForVisibility(driver, bridgeNameEditField).getText();
    }

    public void enterNewBridgeName(String newName) {
        WaitUtils.waitForVisibility(driver, bridgeNameEditField).click(); // focus first
        WaitUtils.clearAndType(driver, bridgeNameEditField, newName);
    }

    public void clickDoneOnKeyboard() {
        driver.executeScript("mobile: performEditorAction", Map.of("action", "done"));
    }

    /** Name option > type > Done. Caller decides how to leave the screen. */
    public void renameBridge(String newName) {
        clickNameOption();
        enterNewBridgeName(newName);
        clickDoneOnKeyboard();
    }

    // ---- restart --------------------------------------------------------------------------

    /** Scrolls the settings screen until Restart bridge is reachable, then taps it. */
    public void clickRestartBridgeButton() {
        Assert.assertTrue(GestureUtils.scrollUntilPresent(driver, restartBridgeButton),
                "Restart bridge button should be reachable on the bridge settings screen");
        WaitUtils.click(driver, restartBridgeButton);
    }

    public void verifyRestartBridgeConfirmationDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, restartConfirmationDialog).isDisplayed(),
                "Restart bridge confirmation should be displayed");
    }

    public void confirmRestartBridge() {
        WaitUtils.click(driver, confirmRestartButton);
    }

    public void verifyBridgeRestartedMessageDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, bridgeRestartedMessage).isDisplayed(),
                "Bridge restarted confirmation message should be displayed after confirming the restart");
    }
    public void waitForDeleteBridgeSettingsPage() {
        WaitUtils.waitForVisibility(driver, bridgeSettingsPage);
    }
}
