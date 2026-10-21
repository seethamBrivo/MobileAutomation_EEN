package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
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

    // Generic: the name editor is the only EditText on its screen. No testTag available yet.
    private final By bridgeNameEditField = AppiumBy.xpath("//android.widget.EditText");

    private final By restartBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");
    private final By restartConfirmationDialog = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'restart') or contains(@text, 'reboot')]");
    private final By confirmRestartButton = AppiumBy.xpath("//android.widget.Button[@content-desc='positive_button'] | //android.widget.TextView[@text='RESTART']");
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the post-restart confirmation snackbar carries no content-desc/testTag yet.
    private final By bridgeRestartedMessage = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View");

    public BridgeSettingsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ---- status ---------------------------------------------------------------------------

    public void verifyBridgeSettingsPageDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, bridgeSettingsPage).isDisplayed(), "Bridge settings page should be displayed");
    }

    public void verifyStatusSectionDisplayed() {
        // The settings screen re-renders right after it opens, which can invalidate the element between finding it
        // and asking whether it is displayed; the lookup is repeated instead of failing on that.
        Assert.assertTrue(WaitUtils.retryOnStale(() -> WaitUtils.waitForVisibility(driver, statusSection).isDisplayed()),
                "Status section should be displayed");
    }

    /** True when this settings screen shows a name field holding exactly {@code name}; does not wait long. */
    public boolean hasNameField(String name) {
        return WaitUtils.isVisible(driver,
                AppiumBy.xpath("//android.widget.EditText[@text=" + xpathLiteral(name) + "]"), TimeoutConstants.SHORT_TIMEOUT);
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

    /**
     * Taps the name field located by its own text: the child view of {@code EditText[@text=<name>]}. Used on the
     * settings screen to reach the name field of the bridge called {@code name}, in place of tapping the "Name" row.
     */
    public void clickNameField(String name) {
        By field = AppiumBy.xpath("//android.widget.EditText[@text=" + xpathLiteral(name) + "]/android.view.View");
        WebElement element = WaitUtils.waitForClickability(driver, field);
        // A real touch at the centre of the field, like a finger tap. Done with a standard W3C pointer action: the
        // "mobile: clickGesture" command is not available on BrowserStack's Appium.
        Point centre = GestureUtils.getElementCenter(element);
        PointerInput finger =
                new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), centre.x, centre.y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    public String getCurrentBridgeNameFromEditField() {
        return WaitUtils.waitForVisibility(driver, bridgeNameEditField).getText();
    }

    /**
     * Replaces the bridge name in the name field (which the caller has already opened by tapping it). The field is
     * read back afterwards, so a name that never reached it fails here with a clear message instead of later at a
     * check on the settings screen.
     */
    public void enterNewBridgeName(String newName) {
        WaitUtils.waitForVisibility(driver, bridgeNameEditField).click(); // focus first
        WaitUtils.clearAndType(driver, bridgeNameEditField, newName);
        String seen = WaitUtils.pollText(driver, bridgeNameEditField, newName, Duration.ofSeconds(5));
        Assert.assertEquals(seen, newName, "The new bridge name should be in the field before it is confirmed");
    }

    /** XPath 1.0 string literal for {@code value}, which may contain quotes. */
    private static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + String.join("',\"'\",'", value.split("'", -1)) + "')";
    }

    public void clickDoneOnKeyboard() {
        driver.executeScript("mobile: performEditorAction", Map.of("action", "done"));
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
