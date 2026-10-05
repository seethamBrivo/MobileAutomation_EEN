package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.WaitUtils;

/**
 * Dashboard > devices screen: the All devices / Bridges tabs, the add-bridge form and the
 * bridge list. Bridge settings (the screen a bridge row opens) live in {@link BridgeSettingsPage}.
 */
public class DevicesPage {

    private static final Logger logger = LogManager.getLogger(DevicesPage.class);
    private final AppiumDriver driver;
    private final BridgeSettingsPage settings;

    // Bottom navigation and tabs
    private final By moreMenuOption = AppiumBy.xpath("//android.widget.TextView[@text='More']");
    private final By dashboardOption = AppiumBy.xpath("//android.widget.TextView[@text='Dashboard']");
    private final By allDevicesPage = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View");
    private final By allDevicesSection = AppiumBy.xpath("//android.widget.TextView[@text='All devices']");
    private final By bridgesTab = AppiumBy.xpath("//android.widget.TextView[@text='Bridges']");
    private final By camerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    private final By speakersOption = AppiumBy.xpath("//android.widget.TextView[@text='Speakers']");
    private final By switchesOption = AppiumBy.xpath("//android.widget.TextView[@text='Switches']");

    // Add-bridge flow.
    // Positional Compose paths: no testTag exists on these nodes yet. Tracked for the mobile team.
    private final By addBridgeIcon = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.view.View[2]/android.view.View");
    private final By addBridgeOption = AppiumBy.xpath("//android.widget.TextView[@text='Add bridge']");
    private final By addCameraOption = AppiumBy.xpath("//android.widget.TextView[@text='Add camera']");
    private final By addCameraDirectOption = AppiumBy.xpath("//android.widget.TextView[@text='Add Camera Direct']");
    private final By bridgeNameField = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.widget.EditText");
    private final By attachIdField = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");
    private final By addBridgeButton = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.Button");
    private final By conflictErrorMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'There is a conflict in the operation')]");

    // Bridge list
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes
    private final By bridgesBackButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[6]/android.view.View[1]/android.view.View");
    private final By firstBridgeInList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]");
    private final By firstBridgeNameText = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.widget.TextView[1]");
    private final By bridgeThreeDotsButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View/android.view.View[7]");
    // The nearest clickable ancestor of the first "Online" label is that bridge's row.
    // (An unbounded ancestor:: step returns the outermost container first, which is the whole list.)
    private final By firstOnlineBridge = AppiumBy.xpath("(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[@clickable='true'][1]");
    private final By filterSearchField = AppiumBy.xpath("//android.widget.EditText");
    private final By noMatchingBridgesMessage = AppiumBy.xpath("//android.widget.TextView[@text='No matching bridges']");
    private final By restartBridgeContextOption = AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']");

    // Bridge settings > delete
    private final By deleteBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Delete bridge']");
    // TODO: Replace with testTag when mobile team adds proper dialog identifiers
    // Delete confirmation popup button (Compose UI positional path)
    private final By deleteConfirmationDialog = AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View");
    // TODO: Replace with testTag when mobile team adds proper dialog identifiers
    private final By deleteButton = AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");

    public DevicesPage(AppiumDriver driver) {
        this.driver = driver;
        this.settings = new BridgeSettingsPage(driver);
    }

    private static By contextMenuOption(String text) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + text + "']");
    }

    // ---- navigation -----------------------------------------------------------------------

    public void clickMoreMenuOptionAtTheBottom() {
        WaitUtils.click(driver, moreMenuOption);
    }

    public void clickDashboardOptionAtTheBottom() {
        WaitUtils.click(driver, dashboardOption);
    }

    /** More > Dashboard. Works from any screen that shows the bottom navigation. */
    public void navigateToDashboard() {
        clickMoreMenuOptionAtTheBottom();
        clickDashboardOptionAtTheBottom();
    }

    public void clickBridgesTab() {
        WaitUtils.click(driver, bridgesTab);
    }

    /**
     * Refresh the Bridges list by navigating back and switching to All Devices tab, then back to Bridges.
     * Use this after operations that modify bridge data (rename, delete) to ensure the list shows current state.
     */
    public void refreshBridgesList() {
        // Only navigate back if back button is visible (not already on main devices screen)
        if (WaitUtils.isVisible(driver, bridgesBackButton, TimeoutConstants.BACK_BUTTON_CHECK_TIMEOUT)) {
            WaitUtils.click(driver, bridgesBackButton);
        }
        WaitUtils.waitForVisibility(driver, allDevicesPage);
        WaitUtils.click(driver, bridgesTab);
        WaitUtils.waitForVisibility(driver, firstBridgeInList);
    }

    /**
     * For cleanup code that does not know which screen the test died on: back out of nested
     * screens/dialogs until the bottom navigation is visible, then More > Dashboard.
     */
    public void recoverToDashboard() {
        for (int i = 0; i < 3 && !WaitUtils.isPresent(driver, moreMenuOption); i++) {
            driver.navigate().back();
        }
        navigateToDashboard();
    }

    public void verifyAllDevicesSectionIsDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed(),
                "All devices section should be displayed");
    }

    public void verifyBridgesOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, bridgesTab).isDisplayed(), "Bridges option should be displayed");
    }

    public void verifyCamerasOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, camerasOption).isDisplayed(), "Cameras option should be displayed");
    }

    public void verifySpeakersOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, speakersOption).isDisplayed(), "Speakers option should be displayed");
    }

    public void verifySwitchesOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, switchesOption).isDisplayed(), "Switches option should be displayed");
    }

    public void verifyUserNavigatedToOriginScreen() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed(),
                "User should be navigated back to the All devices screen");
    }

    public void verifyUserNavigatedBackToBridgesTab() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, bridgesTab).isDisplayed(),
                "User should be navigated back to Bridges tab");
    }

    // ---- add bridge -----------------------------------------------------------------------

    public void clickOnAddBridgeIcon() {
        WaitUtils.click(driver, addBridgeIcon);
    }

    public void verifyAddDeviceOptionsDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addBridgeOption).isDisplayed(), "Add bridge option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addCameraOption).isDisplayed(), "Add camera option should be displayed");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addCameraDirectOption).isDisplayed(), "Add Camera Direct option should be displayed");
    }

    public void clickAddBridgeOption() {
        WaitUtils.click(driver, addBridgeOption);
    }

    public void waitForBridgeNameFieldToLoad() {
        WaitUtils.waitForVisibility(driver, bridgeNameField);
    }

    public void enterBridgeName(String bridgeName) {
        WaitUtils.clearAndType(driver, bridgeNameField, bridgeName);
    }

    public void enterAttachId(String attachId) {
        WaitUtils.clearAndType(driver, attachIdField, attachId);
    }

    public void verifyAddBridgeButtonEnabled() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled(), "Add bridge button should be enabled");
    }

    public void clickAddBridgeButton() {
        WaitUtils.click(driver, addBridgeButton);
    }

    public void verifyConflictErrorMessageDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, conflictErrorMessage).isDisplayed(),
                "Conflict error message should be displayed for a duplicate attach ID");
    }

    /** Add-bridge icon > Add bridge > fill form > Add. Leaves the app on whatever screen follows. */
    public void addBridgeWorkflow(String bridgeName, String attachId) {
        clickOnAddBridgeIcon();
        clickAddBridgeOption();
        waitForBridgeNameFieldToLoad();
        enterBridgeName(bridgeName);
        enterAttachId(attachId);
        verifyAddBridgeButtonEnabled();
        clickAddBridgeButton();
    }

    // ---- bridge list / filter -------------------------------------------------------------

    /**
     * Types into the filter field and reports whether a bridge row shows up within
     * {@link TimeoutConstants#FILTER_RESULT_TIMEOUT}. Non-asserting on purpose: cleanup code
     * uses it to ask "is there anything to delete?".
     */
    public boolean enterBridgeNameInFilter(String bridgeName) {
        WaitUtils.clearAndType(driver, filterSearchField, bridgeName);
        return WaitUtils.isVisible(driver, firstBridgeInList, TimeoutConstants.FILTER_RESULT_TIMEOUT);
    }

    public void verifyBridgeFoundInFilter(String bridgeName) {
        WaitUtils.waitForVisibility(driver, firstBridgeInList);
        Assert.assertTrue(enterBridgeNameInFilter(bridgeName), "Bridge should be found in filter: " + bridgeName);
    }

    public void verifyNoMatchingBridgesMessageDisplayed(String bridgeName) {
        WaitUtils.clearAndType(driver, filterSearchField, bridgeName);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, noMatchingBridgesMessage).isDisplayed(),
                "No matching bridges message should be displayed after deleting bridge: " + bridgeName);
    }

    public void verifyBridgeListDisplayed() {
        WaitUtils.waitForVisibility(driver, firstBridgeInList);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, firstBridgeInList).isDisplayed(), "Bridge list should be displayed");
    }

    public String getFirstBridgeName() {
        return WaitUtils.waitForVisibility(driver, firstBridgeNameText).getText();
    }

    public void verifyBridgeNameMatches(String expectedName) {
        WaitUtils.waitForVisibility(driver, firstBridgeNameText);
        Assert.assertEquals(getFirstBridgeName(), expectedName, "Bridge name should match");
    }

    public void clickFirstBridge() {
        WaitUtils.click(driver, firstBridgeInList);
    }

    public void clickFirstOnlineBridge() {
        WaitUtils.click(driver, firstOnlineBridge);
    }

    public void clickBridgeThreeDotsButton() {
        WaitUtils.click(driver, bridgeThreeDotsButton);
    }

    /** Long-press the first bridge row; fall back to its overflow button if the long-press fails. */
    public void openFirstBridgeContextMenu() {
        try {
            WebElement row = WaitUtils.waitForVisibility(driver, firstBridgeInList);
            GestureUtils.longPress(driver, row);
        } catch (Exception e) {
            logger.info("Long press did not open the context menu ({}); trying the overflow button", e.getMessage());
            clickBridgeThreeDotsButton();
        }
    }

    public void verifyContextMenuOptionDisplayed(String optionText) {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, contextMenuOption(optionText)).isDisplayed(),
                "Context menu option should be displayed: " + optionText);
    }

    /** "Restart bridge" is role-dependent, so callers decide what its presence means. */
    public boolean isRestartBridgeContextOptionDisplayed() {
        return WaitUtils.isVisible(driver, restartBridgeContextOption, TimeoutConstants.SHORT_TIMEOUT);
    }

    // ---- delete bridge --------------------------------------------------------------------

    public void clickDeleteBridgeButton() {
        WaitUtils.waitForClickability(driver, deleteBridgeButton).click();
    }

    public boolean isDeleteConfirmationDialogDisplayed() {
        return WaitUtils.isVisible(driver, deleteConfirmationDialog, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void clickDeleteButton() {
        WaitUtils.click(driver, deleteButton);
    }

    /**
     * From the Bridges tab: filter by name, open the row, scroll to Delete bridge, confirm.
     * Asserts at each step, so use it inside tests only. Cleanup uses {@link #tryDeleteBridge}.
     */
    public void deleteBridgeWorkflow(String bridgeName) {
        verifyBridgeFoundInFilter(bridgeName);
        WaitUtils.waitForVisibility(driver, firstBridgeInList).click();
        settings.waitForDeleteBridgeSettingsPage();
        Assert.assertTrue(GestureUtils.scrollUntilPresent(driver, deleteBridgeButton),
                "Delete bridge button should be reachable on the bridge settings screen");
        clickDeleteBridgeButton();
        Assert.assertTrue(isDeleteConfirmationDialogDisplayed(), "Delete confirmation dialog should be displayed");
        clickDeleteButton();
    }

    /**
     * Best-effort delete for cleanup paths. Never asserts and never throws for "not found";
     * only deletes when the filtered row's name matches {@code expectedName} exactly
     * ({@code exactMatch}) or starts with it (prefix mode), so it cannot remove an unrelated
     * bridge on the shared account.
     *
     * @return true if a bridge was deleted
     */
    public boolean tryDeleteBridge(String expectedName, boolean exactMatch) {
        if (!enterBridgeNameInFilter(expectedName)) {
            return false;
        }
        String displayed;
        try {
            displayed = getFirstBridgeName();
        } catch (TimeoutException e) {
            return false;
        }
        boolean matches = exactMatch ? displayed.equals(expectedName) : displayed.startsWith(expectedName);
        if (!matches) {
            logger.warn("Not deleting '{}': it does not match '{}'", displayed, expectedName);
            return false;
        }
        clickFirstBridge();
        if (!GestureUtils.scrollUntilPresent(driver, deleteBridgeButton)) {
            logger.warn("Delete bridge button not reachable for '{}'", displayed);
            return false;
        }
        clickDeleteBridgeButton();
        if (!isDeleteConfirmationDialogDisplayed()) {
            logger.warn("Delete confirmation dialog did not appear for '{}'", displayed);
            return false;
        }
        clickDeleteButton();
        logger.info("Deleted bridge '{}'", displayed);
        return true;
    }
}
