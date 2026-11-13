package pages;

import constants.DashboardTab;
import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.BridgesLocators;
import locators.CommonLocators;
import locators.MyProfileLocators;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

/**
 * Dashboard > devices screen: the All devices / Bridges tabs, the add-bridge form and the
 * bridge list. Bridge settings (the screen a bridge row opens) live in {@link BridgeSettingsPage}.
 */
public class DevicesPage {

    private static final Logger logger = LogManager.getLogger(DevicesPage.class);

    /** The sessions in which the widget tip has been looked for already (see {@link #closeWidgetTipIfShown}). */
    private static final java.util.Set<String> SESSIONS_CHECKED_FOR_WIDGET_TIP =
            java.util.concurrent.ConcurrentHashMap.newKeySet();
    private final AppiumDriver driver;
    private final BridgeSettingsPage settings;

    // Add-bridge flow.
    private static final Duration BRIDGE_ROW_SETTLE = Duration.ofSeconds(15);
    private static final int ADD_MENU_TAP_ATTEMPTS = 3;
    private static final Duration ADD_MENU_APPEAR_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration ADD_FORM_CLOSE_TIMEOUT = Duration.ofSeconds(20);
    private static final Duration ADD_RETRY_PAUSE = Duration.ofSeconds(10);
    private static final Duration ADD_LANDING_TIMEOUT = Duration.ofSeconds(40);

    public DevicesPage(AppiumDriver driver) {
        this.driver = driver;
        this.settings = new BridgeSettingsPage(driver);
    }

    // ---- navigation -----------------------------------------------------------------------

    public void clickDashboardOptionAtTheBottom() {
        WaitUtils.click(driver, BridgesLocators.DASHBOARD_OPTION);
    }

    /** More > Dashboard. Works from any screen that shows the bottom navigation. */
    public void navigateToDashboard() {
        BottomNavigationBar.clickMoreTab(driver);
        clickDashboardOptionAtTheBottom();
        closeWidgetTipIfShown();
    }

    /**
     * iOS: the app offers a home-screen widget in a tip at the bottom of All devices, which can cover rows. It is shown
     * when All devices first opens in a session, so it is looked for once per session; a later visit would otherwise
     * wait for it every time.
     */
    private void closeWidgetTipIfShown() {
        if (PlatformLocator.isIos() && SESSIONS_CHECKED_FOR_WIDGET_TIP.add(driver.getSessionId().toString())
                && WaitUtils.tryClickOptionalElement(driver, List.of(BridgesLocators.WIDGET_TIP_CLOSE_BUTTON),
                        TimeoutConstants.SHORT_TIMEOUT)) {
            logger.info("Closed the widget tip on the All devices screen");
        }
    }

    /** Navigate to My Profile via bottom navigation (More > My Profile). */
    public MyProfilePage navigateToMyProfile() {
        BottomNavigationBar.clickMoreTab(driver);
        // Wait for More screen to fully render before returning - prevents timeout when clicking profile section
        // The profile section is positional (TODO: replace with testTag when mobile team adds proper identifiers),
        // so we wait for it to appear to ensure the ScrollView children are in the hierarchy
        WaitUtils.waitForVisibility(driver, MyProfileLocators.PROFILE_SECTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        return new MyProfilePage(driver);
    }

    /** Steps "Go to Dashboard" and "Tap Bridge option": the All devices section, then the Bridges tab. */
    public void openBridgesList() {
        navigateToDashboard();
        verifyAllDevicesSectionIsDisplayed();
        clickBridgesTab();
    }

    public void clickBridgesTab() {
        // The dashboard shows skeleton rows ("0 devices") until the device list loads, which took longer
        // than the default 20s in BrowserStack build #22. Wait for it, then reload the dashboard once.
        if (!WaitUtils.isVisible(driver, BridgesLocators.BRIDGES_TAB, TimeoutConstants.LIST_LOAD_TIMEOUT)) {
            logger.warn("Bridges tile not shown after {}; reloading the dashboard", TimeoutConstants.LIST_LOAD_TIMEOUT);
            navigateToDashboard();
        }
        WaitUtils.click(driver, BridgesLocators.BRIDGES_TAB);
    }

    /**
     * Refresh the Bridges list by navigating back and switching to All Devices tab, then back to Bridges.
     * Use this after operations that modify bridge data (rename, delete) to ensure the list shows current state.
     */
    public void refreshBridgesList() {
        // Only navigate back if back button is visible (not already on main devices screen)
        if (PlatformLocator.isIos()) {
            // iOS: go back until All devices shows; one back can land on the list, or only close the filter or keyboard.
            for (int step = 0; step < 3 && !WaitUtils.isVisible(driver, BridgesLocators.ALL_DEVICES_PAGE,
                    TimeoutConstants.BACK_BUTTON_CHECK_TIMEOUT); step++) {
                goBack();
            }
        } else if (WaitUtils.isVisible(driver, BridgesLocators.BRIDGES_BACK_BUTTON,
                TimeoutConstants.BACK_BUTTON_CHECK_TIMEOUT)) {
            WaitUtils.click(driver, BridgesLocators.BRIDGES_BACK_BUTTON);
        }
        WaitUtils.waitForVisibility(driver, BridgesLocators.ALL_DEVICES_PAGE);
        WaitUtils.click(driver, BridgesLocators.BRIDGES_TAB);
        WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST);
    }

    /**
     * One step back. Android uses the system back; iOS has no system back, so it taps the screen's back button, or
     * Close while the filter field has focus (the back button is gone then).
     */
    public void goBack() {
        if (!PlatformLocator.isIos()) {
            driver.navigate().back();
            return;
        }
        Optional<By> screenButton = WaitUtils.waitForFirstVisible(driver,
                List.of(BridgesLocators.BRIDGES_BACK_BUTTON, BridgesLocators.CLOSE_FILTER_BUTTON),
                TimeoutConstants.BACK_BUTTON_CHECK_TIMEOUT);
        if (screenButton.isPresent()) {
            WaitUtils.click(driver, screenButton.get());
            return;
        }
        // Neither button is on screen: only a bridge context menu is ours to close. Anything else (an alert, a slow
        // render) is left alone, because this runs in cleanup and must not tap blindly or throw.
        if (WaitUtils.isPresent(driver, BridgesLocators.contextMenuOption("Bridge settings"))) {
            closeContextMenu();
        } else {
            logger.warn("goBack: no back button, close button or context menu on screen; nothing was tapped");
        }
    }

    /** Closes an open context menu: Android with the system back, iOS by tapping outside it. */
    public void closeContextMenu() {
        if (PlatformLocator.isIos()) {
            GestureUtils.tapOutsideContextMenuIos(driver);
        } else {
            driver.navigate().back();
        }
    }

    /**
     * For cleanup code that does not know which screen the test died on: back out of nested
     * screens/dialogs until the bottom navigation is visible, then More > Dashboard.
     */
    public void recoverToDashboard() {
        for (int i = 0; i < 3 && !BottomNavigationBar.isShowing(driver); i++) {
            goBack();
        }
        navigateToDashboard();
    }

    public void verifyAllDevicesSectionIsDisplayed() {
        WaitUtils.assertVisible(driver, BridgesLocators.ALL_DEVICES_SECTION, "All devices section should be displayed");
    }

    /** Asserts every given tile is shown on the All devices screen, naming the missing one in the failure. */
    public void verifyDashboardTabsDisplayed(DashboardTab... tabs) {
        for (DashboardTab tab : tabs) {
            WaitUtils.assertVisible(driver, tabLocator(tab), tab.getDisplayName() + " option should be displayed");
        }
    }

    private static By tabLocator(DashboardTab tab) {
        return switch (tab) {
            case BRIDGES -> BridgesLocators.BRIDGES_TAB;
            case CAMERAS -> BridgesLocators.CAMERAS_OPTION;
            case SPEAKERS -> BridgesLocators.SPEAKERS_OPTION;
            case SWITCHES -> BridgesLocators.SWITCHES_OPTION;
        };
    }

    public void verifyUserNavigatedToOriginScreen() {
        WaitUtils.assertVisible(driver, BridgesLocators.ALL_DEVICES_SECTION,
                "User should be navigated back to the All devices screen");
    }

    public void verifyUserNavigatedBackToBridgesTab() {
        WaitUtils.assertVisible(driver, BridgesLocators.BRIDGES_TAB, "User should be navigated back to Bridges tab");
    }

    // ---- add bridge -----------------------------------------------------------------------

    /**
     * Opens the "add device" menu. The "+" icon exists before the device list has finished loading, and a tap
     * that lands during that reload is swallowed (the menu never opens). So the tap is repeated, a few seconds
     * apart, until the menu shows, instead of waiting out the full element timeout for a menu that is not coming.
     * If it still does not open, the next step fails with its usual message.
     */
    public void clickOnAddBridgeIcon() {
        if (!WaitUtils.isVisible(driver, BridgesLocators.ALL_DEVICES_SECTION, ADD_MENU_APPEAR_TIMEOUT)) {
            // Still on the Bridges list (or another screen): its header has no "+" for this locator, and a
            // positional guess would hit the filter icon. Go back to the All devices screen first.
            recoverToDashboard();
        }
        for (int attempt = 1; attempt <= ADD_MENU_TAP_ATTEMPTS; attempt++) {
            if (attempt > 1 && WaitUtils.isPresent(driver, BridgesLocators.ADD_BRIDGE_OPTION)) {
                return; // the menu opened just after the last check; tapping "+" again would close it
            }
            WaitUtils.click(driver, BridgesLocators.ADD_BRIDGE_ICON);
            if (WaitUtils.isVisible(driver, BridgesLocators.ADD_BRIDGE_OPTION, ADD_MENU_APPEAR_TIMEOUT)) {
                return;
            }
        }
        Assert.fail("The add-device menu did not open after " + ADD_MENU_TAP_ATTEMPTS + " taps on the '+' icon");
    }

    public void verifyAddDeviceOptionsDisplayed() {
        WaitUtils.assertVisible(driver, BridgesLocators.ADD_BRIDGE_OPTION, "Add bridge option should be displayed");
        WaitUtils.assertVisible(driver, BridgesLocators.ADD_CAMERA_OPTION, "Add camera option should be displayed");
        WaitUtils.assertVisible(driver, BridgesLocators.ADD_CAMERA_DIRECT_OPTION,
                "Add Camera Direct option should be displayed");
    }

    public void clickAddBridgeOption() {
        WaitUtils.click(driver, BridgesLocators.ADD_BRIDGE_OPTION);
    }

    public void waitForBridgeNameFieldToLoad() {
        WaitUtils.waitForVisibility(driver, BridgesLocators.BRIDGE_NAME_FIELD);
    }

    public void enterBridgeName(String bridgeName) {
        WaitUtils.clearAndType(driver, BridgesLocators.BRIDGE_NAME_FIELD, bridgeName);
    }

    public void enterAttachId(String attachId) {
        WaitUtils.clearAndType(driver, BridgesLocators.ATTACH_ID_FIELD, attachId);
    }

    public void verifyAddBridgeButtonEnabled() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, BridgesLocators.ADD_BRIDGE_BUTTON).isEnabled(), "Add bridge button should be enabled");
    }

    public void verifyAddBridgeButtonDisabled() {
        Assert.assertFalse(WaitUtils.waitForVisibility(driver, BridgesLocators.ADD_BRIDGE_BUTTON).isEnabled(),
                "Add bridge button should be disabled");
    }

    public void clickAddBridgeButton() {
        WaitUtils.click(driver, BridgesLocators.ADD_BRIDGE_BUTTON);
    }

    public void verifyConflictErrorMessageDisplayed() {
        WaitUtils.assertVisible(driver, BridgesLocators.CONFLICT_ERROR_MESSAGE,
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
        AddResult result = awaitAddResult();
        if (result == AddResult.STILL_OPEN) {
            // The server can still be freeing the attach ID of a bridge deleted a moment ago, which answers with a
            // short-lived "conflict" message and leaves the form open. The fields keep their values: wait, tap Add once more.
            logger.warn("The add-bridge form is still open after tapping Add; waiting {} and trying once more", ADD_RETRY_PAUSE);
            WaitUtils.pauseFor(ADD_RETRY_PAUSE);
            clickAddBridgeButton();
            result = awaitAddResult();
        }
        if (result == AddResult.UNKNOWN) {
            // Neither the form nor the dashboard: the app is still busy (or somewhere unexpected). Do NOT tap Add again, the
            // bridge may already be added; give the dashboard more time to show up.
            logger.warn("Neither the add form nor the dashboard is showing; waiting up to {} for the dashboard", ADD_LANDING_TIMEOUT);
            WaitUtils.assertVisible(driver, BridgesLocators.ALL_DEVICES_SECTION, ADD_LANDING_TIMEOUT,
                    "After adding bridge '" + bridgeName + "' the app did not return to the All devices screen");
        } else {
            Assert.assertSame(result, AddResult.ACCEPTED,
                    "Adding bridge '" + bridgeName + "' did not go through: the add-bridge form stayed open after two attempts");
        }
    }

    private enum AddResult { ACCEPTED, STILL_OPEN, UNKNOWN }

    /**
     * After tapping Add: ACCEPTED once the "All devices" screen is back (a positive sign; the form has no such heading),
     * STILL_OPEN if the form's name field is on screen at the deadline, UNKNOWN if neither is. Notes a conflict message
     * if one flashes by.
     */
    private AddResult awaitAddResult() {
        long deadline = System.nanoTime() + ADD_FORM_CLOSE_TIMEOUT.toNanos();
        boolean conflictSeen = false;
        while (System.nanoTime() < deadline) {
            if (WaitUtils.isPresent(driver, BridgesLocators.ALL_DEVICES_SECTION)) {
                return AddResult.ACCEPTED;
            }
            if (!conflictSeen && WaitUtils.isPresent(driver, BridgesLocators.CONFLICT_ERROR_MESSAGE)) {
                conflictSeen = true;
                logger.warn("The app answered the add with a conflict message");
            }
            WaitUtils.pauseFor(Duration.ofMillis(300));
        }
        return WaitUtils.isPresent(driver, BridgesLocators.BRIDGE_NAME_FIELD) ? AddResult.STILL_OPEN : AddResult.UNKNOWN;
    }


    // ---- bridge list / filter -------------------------------------------------------------

    /**
     * Types into the filter field and reports whether a bridge row shows up within
     * {@link TimeoutConstants#FILTER_RESULT_TIMEOUT}. Non-asserting on purpose: cleanup code
     * uses it to ask "is there anything to delete?".
     */
    public boolean enterBridgeNameInFilter(String bridgeName) {
        typeIntoBridgeFilter(bridgeName);
        return WaitUtils.isVisible(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST, TimeoutConstants.FILTER_RESULT_TIMEOUT);
    }

    /**
     * Non-asserting, for cleanup: types the name into the filter and waits until the first row is a bridge with exactly
     * that name. A visible first row alone is not enough, because the list keeps showing the previous rows for a
     * moment after the filter changes.
     */
    public boolean filterShowsBridgeNamed(String bridgeName) {
        return enterBridgeNameInFilter(bridgeName) && bridgeName.equals(awaitFirstBridgeName(bridgeName));
    }

    public void verifyBridgeFoundInFilter(String bridgeName) {
        WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST);
        Assert.assertTrue(enterBridgeNameInFilter(bridgeName), "Bridge should be found in filter: " + bridgeName);
        verifyBridgeFilterHolds(bridgeName);
    }

    /** iOS: the filter field really holds the typed text (a missed keystroke would leave the list unfiltered). */
    private void verifyBridgeFilterHolds(String text) {
        if (!PlatformLocator.isIos()) {
            return;
        }
        String typed = WaitUtils.retryOnStale(
                () -> WaitUtils.waitForVisibility(driver, BridgesLocators.FILTER_SEARCH_FIELD).getAttribute("value"));
        Assert.assertEquals(typed, text, "The bridge filter should hold the text that was typed");
    }

    public void verifyNoMatchingBridgesMessageDisplayed(String bridgeName) {
        typeIntoBridgeFilter(bridgeName);
        verifyBridgeFilterHolds(bridgeName);
        String message = "No matching bridges message should be displayed after deleting bridge: " + bridgeName;
        closeKeyboardIfIos();
        WaitUtils.assertVisible(driver, BridgesLocators.NO_MATCHING_BRIDGES_MESSAGE, message);
    }

    /** iOS: the keyboard sits over the "No matching bridges" message and makes it report as not visible, so close it. */
    private void closeKeyboardIfIos() {
        if (!PlatformLocator.isIos()) {
            return;
        }
        WaitUtils.click(driver, BridgesLocators.KEYBOARD_SEARCH_KEY);
    }

    /** Replaces the text of the bridge filter. iOS clears it with its "Clear text" button before typing. */
    private void typeIntoBridgeFilter(String text) {
        if (!PlatformLocator.isIos()) {
            WaitUtils.clearAndType(driver, BridgesLocators.FILTER_SEARCH_FIELD, text);
            return;
        }
        if (WaitUtils.isPresent(driver, CommonLocators.CLEAR_TEXT_BUTTON)) {
            WaitUtils.click(driver, CommonLocators.CLEAR_TEXT_BUTTON);
        }
        WaitUtils.retryOnStale(() -> {
            WebElement field = WaitUtils.waitForVisibility(driver, BridgesLocators.FILTER_SEARCH_FIELD);
            field.click();
            field.sendKeys(text);
            return null;
        });
    }

    public void verifyBridgeListDisplayed() {
        WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST);
        WaitUtils.assertVisible(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST, "Bridge list should be displayed");
    }

    public String getFirstBridgeName() {
        return WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_NAME_TEXT).getText();
    }

    public void verifyBridgeNameMatches(String expectedName) {
        WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_NAME_TEXT);
        Assert.assertEquals(awaitFirstBridgeName(expectedName), expectedName, "Bridge name should match");
    }

    /**
     * Polls the first row of the (filtered) list until its name is exactly {@code expectedName}, for up to
     * {@link #BRIDGE_ROW_SETTLE}. Returns the name last seen, so the caller can assert on it. The list keeps
     * showing the previous rows for a moment after the filter or a rename, so a single read is not reliable.
     */
    private String awaitFirstBridgeName(String expectedName) {
        return WaitUtils.pollText(driver, BridgesLocators.FIRST_BRIDGE_NAME_TEXT, expectedName, BRIDGE_ROW_SETTLE);
    }

    public void clickFirstBridge() {
        // WaitUtils.click repeats the lookup if the list re-renders between finding the row and tapping it.
        WaitUtils.click(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST);
    }

    /**
     * Opens the first online bridge in the list and returns its name. Only an online bridge's name can be edited (a
     * bridge that was just added stays offline for about an hour), so the rename test works on an existing online
     * bridge and puts the name it found back afterwards.
     */
    public String clickFirstOnlineBridgeAndGetName() {
        if (PlatformLocator.isIos()) {
            // iOS: the name text of the first online bridge; tapping the row container does not open the bridge.
            return WaitUtils.retryOnStale(() -> {
                WebElement nameText = WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_ONLINE_BRIDGE_NAME_TEXT);
                String name = nameText.getText().trim();
                nameText.click();
                return name;
            });
        }
        for (WebElement row : WaitUtils.findElements(driver, BridgesLocators.ONLINE_BRIDGE_ROWS)) {
            try {
                String name = row.findElement(By.xpath(".//android.widget.TextView[1]")).getText().trim();
                row.click();
                return name;
            } catch (StaleElementReferenceException e) {
                // the list re-rendered under us: try the next row, the loop below ends with a clear failure
            }
        }
        Assert.fail("No online bridge is available for the rename test");
        return null;
    }

    public void clickBridgeThreeDotsButton() {
        WaitUtils.click(driver, BridgesLocators.BRIDGE_THREE_DOTS_BUTTON);
    }

    /** Long-press the first bridge row; fall back to its overflow button if the long-press fails. */
    public void openFirstBridgeContextMenu() {
        if (PlatformLocator.isIos()) {
            clickBridgeThreeDotsButton(); // iOS opens the menu from the 3 dots
            return;
        }
        try {
            WebElement row = WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST);
            GestureUtils.longPress(driver, row);
        } catch (Exception e) {
            logger.info("Long press did not open the context menu ({}); trying the overflow button", e.getMessage());
            clickBridgeThreeDotsButton();
        }
    }

    /** Opens the menu of the first online bridge, which has cameras, unlike an offline one that may sort first. */
    public void openFirstOnlineBridgeContextMenu() {
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, BridgesLocators.FIRST_ONLINE_BRIDGE_THREE_DOTS);
            return;
        }
        GestureUtils.longPress(driver, WaitUtils.findElements(driver, BridgesLocators.ONLINE_BRIDGE_ROWS).get(0));
    }

    public void verifyContextMenuOptionDisplayed(String optionText) {
        WaitUtils.assertVisible(driver, BridgesLocators.contextMenuOption(optionText),
                "Context menu option should be displayed: " + optionText);
    }

    /** "Restart bridge" is role-dependent, so callers decide what its presence means. */
    public boolean isRestartBridgeContextOptionDisplayed() {
        return WaitUtils.isVisible(driver, BridgesLocators.RESTART_BRIDGE_CONTEXT_OPTION, TimeoutConstants.SHORT_TIMEOUT);
    }

    // ---- delete bridge --------------------------------------------------------------------

    public void clickDeleteBridgeButton() {
        WaitUtils.waitForClickability(driver, BridgesLocators.DELETE_BRIDGE_BUTTON).click();
    }

    public boolean isDeleteConfirmationDialogDisplayed() {
        return WaitUtils.isVisible(driver, BridgesLocators.DELETE_CONFIRMATION_DIALOG, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void clickDeleteButton() {
        WaitUtils.click(driver, BridgesLocators.DELETE_BUTTON);
    }

    /**
     * From the Bridges tab: filter by name, open the row, scroll to Delete bridge, confirm.
     * Asserts at each step, so use it inside tests only. Cleanup uses {@link #tryDeleteBridge}.
     */
    public void deleteBridgeWorkflow(String bridgeName) {
        verifyBridgeFoundInFilter(bridgeName);
        // The list can still be showing the previous (unfiltered) rows for a moment after typing in the filter.
        // Clicking its first row would open, and then delete, an unrelated bridge on the shared account.
        waitForFirstBridgeNamed(bridgeName);
        WaitUtils.waitForVisibility(driver, BridgesLocators.FIRST_BRIDGE_IN_LIST).click();
        deleteBridgeFromOpenSettings();
        // The server frees the attach ID only once the bridge is really gone; returning earlier lets the next
        // add-bridge hit a "conflict" error. The filter showing no match for this name means it is gone.
        verifyUserNavigatedBackToBridgesTab();
        verifyNoMatchingBridgesMessageDisplayed(bridgeName);
    }

    /**
     * Deletes the bridge whose settings screen is open. The caller is responsible for having confirmed which bridge
     * that is (e.g. by reading its name on the settings screen).
     */
    public void deleteBridgeFromOpenSettings() {
        settings.waitForDeleteBridgeSettingsPage();
        Assert.assertTrue(GestureUtils.scrollUntilPresent(driver, BridgesLocators.DELETE_BRIDGE_BUTTON),
                "Delete bridge button should be reachable on the bridge settings screen");
        clickDeleteBridgeButton();
        Assert.assertTrue(isDeleteConfirmationDialogDisplayed(), "Delete confirmation dialog should be displayed");
        clickDeleteButton();
    }

    /**
     * Fails without touching anything unless the first row of the filtered list is exactly {@code bridgeName}, so a
     * delete can never be pointed at a bridge the test did not create.
     */
    private void waitForFirstBridgeNamed(String bridgeName) {
        String seen = awaitFirstBridgeName(bridgeName);
        if (!bridgeName.equals(seen)) {
            Assert.fail("Refusing to delete: the first bridge in the list is '" + seen + "', not '" + bridgeName + "'");
        }
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
        // The list can re-render between the check above and the click; look again right before clicking.
        try {
            String stillDisplayed = getFirstBridgeName();
            boolean stillMatches = exactMatch ? stillDisplayed.equals(expectedName) : stillDisplayed.startsWith(expectedName);
            if (!stillMatches) {
                logger.warn("Not deleting: the first row changed from '{}' to '{}'", displayed, stillDisplayed);
                return false;
            }
        } catch (TimeoutException e) {
            return false;
        }
        clickFirstBridge();
        if (!GestureUtils.scrollUntilPresent(driver, BridgesLocators.DELETE_BRIDGE_BUTTON)) {
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
        awaitBridgeGone(displayed);
        return true;
    }

    // ---- step-level workflows used by BridgesTest ------------------------------------------

    /** A second add with the same attach ID must be refused with a conflict message. */
    public void verifyDuplicateAttachIdIsRejected(String duplicateBridgeName, String attachId) {
        clickOnAddBridgeIcon();
        verifyAddDeviceOptionsDisplayed();
        clickAddBridgeOption();
        waitForBridgeNameFieldToLoad();
        if (PlatformLocator.isIos()) {
            verifyAddBridgeButtonDisabled(); // iOS: disabled until both fields are filled
        } else {
            // App behavior: the button is enabled on an empty form (changed in an earlier app version).
            verifyAddBridgeButtonEnabled();
        }
        enterBridgeName(duplicateBridgeName);
        enterAttachId(attachId);
        clickAddBridgeButton();
        verifyConflictErrorMessageDisplayed();
    }

    public void verifyDevicesOverviewOptions() {
        verifyAllDevicesSectionIsDisplayed();
        // Speakers is not checked: the enduser account used for automation has no speakers, so the app does not
        // show that row.
        verifyDashboardTabsDisplayed(DashboardTab.BRIDGES, DashboardTab.CAMERAS, DashboardTab.SWITCHES);
    }

    /** Bridges tab > filter by name > the first row is that bridge. */
    public void verifyBridgeShownInBridgesTab(String bridgeName) {
        clickBridgesTab();
        verifyBridgeFoundInFilter(bridgeName);
        verifyBridgeNameMatches(bridgeName);
    }

    public void openFirstBridgeAndVerifySettings() {
        clickFirstBridge();
        settings.verifyBridgeSettingsPageDisplayed();
        settings.verifyStatusSectionDisplayed();
    }

    public void verifyBridgeContextMenuOptions() {
        verifyContextMenuOptionDisplayed("Bridge information");
        verifyContextMenuOptionDisplayed("Bridge settings");
        verifyContextMenuOptionDisplayed("Cameras");
        verifyContextMenuOptionDisplayed("Turn on all cameras");
        verifyContextMenuOptionDisplayed("Turn off all cameras");
        verifyContextMenuOptionDisplayed("Replace bridge");
        // Role-dependent entry; recorded, not asserted, until the TC states which roles see it.
        logger.info("'Restart bridge' context option shown for this user: {}", isRestartBridgeContextOptionDisplayed());
    }

    /**
     * Cleanup version of the wait in {@link #deleteBridgeWorkflow}: the attach ID is free again only once the bridge is
     * gone, so the next add must not start before that. Never throws; a bridge that lingers is only logged.
     */
    private void awaitBridgeGone(String bridgeName) {
        try {
            typeIntoBridgeFilter(bridgeName);
            closeKeyboardIfIos();
            if (!WaitUtils.isVisible(driver, BridgesLocators.NO_MATCHING_BRIDGES_MESSAGE,
                    FrameworkConstants.EXPLICIT_WAIT)) {
                logger.warn("Bridge '{}' still shows in the list after its delete", bridgeName);
            }
        } catch (RuntimeException e) {
            logger.warn("Could not confirm that bridge '{}' is gone: {}", bridgeName, e.toString());
        }
    }
}
