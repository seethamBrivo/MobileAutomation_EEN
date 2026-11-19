package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.BridgesLocators;
import java.time.Duration;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

/** The screen a bridge row opens: status, name editing, restart and delete. */
public class BridgeSettingsPage {

    private final AppiumDriver driver;
    public static final String STATUS_ONLINE = "Online";
    public static final String STATUS_OFFLINE = "Offline";
    public static final String STATUS_UNKNOWN = "Unknown";


    public BridgeSettingsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ---- status ---------------------------------------------------------------------------

    public void verifyBridgeSettingsPageDisplayed() {
        WaitUtils.assertVisible(driver, BridgesLocators.BRIDGE_SETTINGS_PAGE,
                "Bridge settings page should be displayed");
    }

    public void verifyStatusSectionDisplayed() {
        // The settings screen re-renders right after it opens, which can invalidate the element between finding it
        // and asking whether it is displayed; the lookup is repeated instead of failing on that.
        Assert.assertTrue(WaitUtils.retryOnStale(() -> WaitUtils.waitForVisibility(driver, BridgesLocators.STATUS_SECTION).isDisplayed()),
                "Status section should be displayed");
    }

    /** {@link #STATUS_ONLINE}, {@link #STATUS_OFFLINE} or {@link #STATUS_UNKNOWN}. */
    public String getBridgeStatus() {
        if (WaitUtils.isVisible(driver, BridgesLocators.ONLINE_STATUS, TimeoutConstants.SHORT_TIMEOUT)) {
            return STATUS_ONLINE;
        }
        if (WaitUtils.isVisible(driver, BridgesLocators.OFFLINE_STATUS, TimeoutConstants.SHORT_TIMEOUT)) {
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
     * Opens the name editor of the bridge called {@code name} from the settings screen: Android taps the "Name" row
     * (which opens a sheet with the field), iOS taps the inline field.
     */
    public void clickNameField(String name) {
        WaitUtils.click(driver, BridgesLocators.nameFieldTapTarget(name));
    }

    public String getCurrentBridgeNameFromEditField() {
        return WaitUtils.waitForVisibility(driver, BridgesLocators.BRIDGE_NAME_EDIT_FIELD).getText();
    }

    /**
     * Replaces the bridge name in the name field (which the caller has already opened by tapping it). The field is
     * read back afterwards, so a name that never reached it fails here with a clear message instead of later at a
     * check on the settings screen.
     */
    public void enterNewBridgeName(String newName) {
        WaitUtils.waitForVisibility(driver, BridgesLocators.BRIDGE_NAME_EDIT_FIELD).click(); // focus first
        WaitUtils.clearAndType(driver, BridgesLocators.BRIDGE_NAME_EDIT_FIELD, newName);
        String seen = WaitUtils.pollText(driver, BridgesLocators.BRIDGE_NAME_EDIT_FIELD, newName, Duration.ofSeconds(5));
        Assert.assertEquals(seen, newName, "The new bridge name should be in the field before it is confirmed");
    }


    /** Confirms the name: iOS uses the keyboard's Done key, Android the Save button of the name sheet. */
    public void clickDoneOnKeyboard() {
        WaitUtils.click(driver, PlatformLocator.isIos() ? BridgesLocators.KEYBOARD_DONE_KEY
                : BridgesLocators.NAME_SAVE_BUTTON);
    }

    // ---- restart --------------------------------------------------------------------------

    /** Scrolls the settings screen until Restart bridge is reachable, then taps it. */
    public void clickRestartBridgeButton() {
        Assert.assertTrue(GestureUtils.scrollUntilPresent(driver, BridgesLocators.RESTART_BRIDGE_BUTTON),
                "Restart bridge button should be reachable on the bridge settings screen");
        WaitUtils.click(driver, BridgesLocators.RESTART_BRIDGE_BUTTON);
    }

    public void verifyRestartBridgeConfirmationDisplayed() {
        WaitUtils.assertVisible(driver, BridgesLocators.RESTART_CONFIRMATION_DIALOG,
                "Restart bridge confirmation should be displayed");
    }

    public void confirmRestartBridge() {
        WaitUtils.click(driver, BridgesLocators.CONFIRM_RESTART_BUTTON);
    }

    public void verifyBridgeRestartedMessageDisplayed() {
        // The restart takes a while to be confirmed (iOS, and Android on BrowserStack, where the default 20 s ran
        // out with the settings screen still showing), so the wait is longer than the default on both platforms.
        WaitUtils.assertVisible(driver, BridgesLocators.BRIDGE_RESTARTED_MESSAGE,
                TimeoutConstants.LIST_LOAD_TIMEOUT,
                "Bridge restarted confirmation message should be displayed after confirming the restart");
    }

    /** Step "Click Restart Bridge": confirm the restart and wait for the restarted message. */
    public void restartBridgeAndVerifyMessage() {
        clickRestartBridgeButton();
        verifyRestartBridgeConfirmationDisplayed();
        confirmRestartBridge();
        // If the demo account never shows this confirmation, the TC step should be marked
        // not-automated rather than passed; a silent pass is what this replaces.
        verifyBridgeRestartedMessageDisplayed();
    }

    public void waitForDeleteBridgeSettingsPage() {
        WaitUtils.waitForVisibility(driver, BridgesLocators.BRIDGE_SETTINGS_PAGE);
    }
}
