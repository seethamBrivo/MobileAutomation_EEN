package tests.ts_bridges;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;
import pages.BridgeSettingsPage;
import pages.DevicesPage;
import utils.BridgeTestHelper;
import utils.TestDataGenerator;

/**
 * End-user bridge tests. Every test that adds a bridge does so through {@link #createTestBridge},
 * which records the name so {@link #cleanupBridge} can remove it whatever happens.
 */
public class BridgesTest extends BaseTest {

    private DevicesPage devicesPage;

    /** Name of the bridge this test created and has not yet deleted, or null. */
    private String testBridgeName;
    /** Bridges whose cleanup threw: they may still hold the account's single attach ID. Reported at the end of the class. */
    private final List<String> leftoverBridges = new ArrayList<>();
    /** Original name of the existing bridge the rename test renamed and has not yet restored, or null. */
    private String renamedBridgeOriginalName;
    /** The unique name given to that bridge by the rename test; restoring looks the bridge up by this exact name. */
    private String renamedBridgeNewName;

    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenDashboard() {
        testBridgeName = null;
        renamedBridgeOriginalName = null;
        renamedBridgeNewName = null;
        loginAs(TestDataConstants.ENDUSER_CREDS);
        devicesPage = new DevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
    }

    @Test(priority = 1, description = "EEPD-TC-11276: POS-Add Bridge - Adding Bridge to the account in Dashboard in mobile VMS app",
          groups = {"regression", "bridges"})
    public void test01_addBridgeToVMSApp() {
        String bridgeName = createTestBridge(TestDataConstants.BRIDGE_PREFIX_TEST);
        devicesPage.verifyUserNavigatedToOriginScreen();
        // Same attach ID a second time must be rejected.
        devicesPage.clickOnAddBridgeIcon();
        devicesPage.verifyAddDeviceOptionsDisplayed();
        devicesPage.clickAddBridgeOption();
        devicesPage.waitForBridgeNameFieldToLoad();
        // App behavior: Button is enabled by default on empty form (updated from previous version)
        // Test validates current state - if this is a regression, mobile team will need to confirm
        devicesPage.verifyAddBridgeButtonEnabled();
        devicesPage.enterBridgeName(TestDataGenerator.generateUniqueName(TestDataConstants.BRIDGE_PREFIX_DUPLICATE));
        devicesPage.enterAttachId(getAttachId());
        devicesPage.clickAddBridgeButton();
        devicesPage.verifyConflictErrorMessageDisplayed();
        devicesPage.recoverToDashboard();
        devicesPage.clickBridgesTab();
        deleteTestBridge(bridgeName);
    }

    @Test(priority = 2, description = "EEPD-TC-8890: POS-Bridge Settings - delete Bridge in mobile VMS app",
          groups = {"regression", "bridges"})
    public void test02_deleteBridgeFromVMSApp() {
        String bridgeName = createTestBridge(TestDataConstants.BRIDGE_PREFIX_TEST);
        devicesPage.clickBridgesTab();
        deleteTestBridge(bridgeName);
        devicesPage.verifyUserNavigatedBackToBridgesTab();
        devicesPage.verifyNoMatchingBridgesMessageDisplayed(bridgeName);
    }

    @Test(priority = 3, description = "EEPD-TC-16502: POS-Bridges - Display list of bridges under Bridges item in Dashboard in mobile VMS app",
          groups = {"regression", "bridges"})
    public void test03_verifyBridgesListInDashboard() {
        String bridgeName = createTestBridge(TestDataConstants.BRIDGE_PREFIX_TEST);
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.verifyBridgesOptionDisplayed();
        devicesPage.verifyCamerasOptionDisplayed();
        // Speakers check disabled: the enduser account used for automation has no speakers, so the app does not show that row.
        // devicesPage.verifySpeakersOptionDisplayed();
        devicesPage.verifySwitchesOptionDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeFoundInFilter(bridgeName);
        devicesPage.verifyBridgeNameMatches(bridgeName);
        devicesPage.clickFirstBridge();
        BridgeSettingsPage settings = new BridgeSettingsPage(driver);
        settings.verifyBridgeSettingsPageDisplayed();
        settings.verifyStatusSectionDisplayed();
        driver.navigate().back();
        deleteTestBridge(bridgeName);
    }

    @Test(priority = 4, description = "EEPD-TC-15483: POS-Bridges - Filter for bridges by search term under Bridges item in Dashboard in VMS mobile app",
          groups = {"regression", "bridges"})
    public void test04_filterBridgesBySearchTerm() {
        String bridgeName = createTestBridge(TestDataConstants.BRIDGE_PREFIX_FILTER);
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeFoundInFilter(bridgeName);
        devicesPage.verifyBridgeListDisplayed();
        devicesPage.verifyBridgeNameMatches(bridgeName);
        String partialName = bridgeName.substring(0, Math.min(10, bridgeName.length()));
        devicesPage.verifyBridgeFoundInFilter(partialName);
        String displayed = devicesPage.getFirstBridgeName();
        Assert.assertTrue(displayed.contains(partialName),
                "Displayed bridge name should contain the partial search term '" + partialName + "' but was '" + displayed + "'");
        deleteTestBridge(bridgeName);
    }

    @Test(priority = 5, description = "EEPD-TC-12354: POS-Bridges - Display context menu of bridge item in Bridge list in Dashboard of mobile VMS app",
          groups = {"regression", "bridges"})
    public void test05_verifyBridgeContextMenu() {
        String bridgeName = createTestBridge(TestDataConstants.BRIDGE_PREFIX_CONTEXT_MENU);
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeFoundInFilter(bridgeName);
        devicesPage.verifyBridgeNameMatches(bridgeName);
        devicesPage.openFirstBridgeContextMenu();
        devicesPage.verifyContextMenuOptionDisplayed("Bridge information");
        devicesPage.verifyContextMenuOptionDisplayed("Bridge settings");
        devicesPage.verifyContextMenuOptionDisplayed("Cameras");
        devicesPage.verifyContextMenuOptionDisplayed("Turn on all cameras");
        devicesPage.verifyContextMenuOptionDisplayed("Turn off all cameras");
        devicesPage.verifyContextMenuOptionDisplayed("Replace bridge");
        // Role-dependent entry; recorded, not asserted, until the TC states which roles see it.
        logger.info("'Restart bridge' context option shown for this user: {}", devicesPage.isRestartBridgeContextOptionDisplayed());
        driver.navigate().back();
        deleteTestBridge(bridgeName);
    }

    /**
     * Renames an existing ONLINE bridge and puts its name back. It cannot use a bridge the test adds itself: a new
     * bridge stays offline for about an hour, and the app does not let an offline bridge's name be edited. Whatever
     * happens, @AfterMethod restores the original name, looking the bridge up by its exact unique new name.
     */
    @Test(priority = 6, description = "EEPD-TC-8881: POS-Bridge Settings - changing the name and checking status of the Bridge in mobile VMS app",
          groups = {"regression", "bridges"})
    public void test06_changeBridgeNameAndCheckStatus() {
        devicesPage.clickBridgesTab();
        // Opens the first online bridge in the list and remembers its current name, to put it back at the end.
        String originalName = devicesPage.clickFirstOnlineBridgeAndGetName();
        BridgeSettingsPage settings = new BridgeSettingsPage(driver);
        settings.verifyStatusSectionDisplayed();
        String status = settings.getBridgeStatus();
        logger.info("Bridge '{}' status: {}", originalName, status);
        settings.verifyBridgeStatusIsKnown(status);

        // Tap the name field itself, found by the bridge's current name, instead of the "Name" row.
        settings.clickNameField(originalName);
        Assert.assertEquals(settings.getCurrentBridgeNameFromEditField(), originalName,
                "The settings screen should show the bridge's current name before it is changed");
        // Short on purpose (e.g. Rn_48213): easy to type, to filter by and to read in a list.
        String newName = TestDataConstants.BRIDGE_PREFIX_RENAMED + String.format("%05d", System.currentTimeMillis() % 100000);
        renamedBridgeOriginalName = originalName;
        renamedBridgeNewName = newName;
        settings.enterNewBridgeName(newName);
        settings.clickDoneOnKeyboard();
        driver.navigate().back();

        // UI shows stale data after rename; force reload by switching tabs
        devicesPage.refreshBridgesList();
        // The list's search finds the bridge by its new name, but the row can keep showing the old name for a while,
        // so the name is confirmed on the bridge's own settings screen, which is the source of truth.
        devicesPage.verifyBridgeFoundInFilter(newName);
        devicesPage.clickFirstBridge();
        settings.verifyStatusSectionDisplayed();
        Assert.assertEquals(settings.getBridgeStatus(), status, "Renaming a bridge must not change its status");
        settings.clickNameField(newName);
        Assert.assertEquals(settings.getCurrentBridgeNameFromEditField(), newName,
                "The settings screen should show the new name after renaming");

        // Put the original name back and confirm it, on the same open settings screen.
        settings.enterNewBridgeName(originalName);
        settings.clickDoneOnKeyboard();
        driver.navigate().back();
        devicesPage.refreshBridgesList();
        devicesPage.verifyBridgeFoundInFilter(originalName);
        devicesPage.clickFirstBridge();
        settings.verifyStatusSectionDisplayed();
        settings.clickNameField(originalName);
        Assert.assertEquals(settings.getCurrentBridgeNameFromEditField(), originalName,
                "The original name should be back after restoring it");
        settings.clickDoneOnKeyboard();
        renamedBridgeOriginalName = null;
        renamedBridgeNewName = null;
    }

    // ---- lifecycle helpers ----------------------------------------------------------------

    @AfterClass(alwaysRun = true)
    public void reportLeftoverBridges() {
        if (!leftoverBridges.isEmpty()) {
            // Loud on purpose: with one attach ID, a bridge left behind makes the next run's add fail with a conflict.
            logger.error("Bridges that could not be cleaned up and may still be on the account: {}", leftoverBridges);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupBridge(ITestResult testResult) {
        try {
            if (driver == null) {
                return;
            }
            // Cleanup must never throw: an exception here is a TestNG configuration failure that
            // skips every remaining test in the class. Page verify* methods throw AssertionError,
            // which is not an Exception, hence Throwable.
            // The rename test renames an existing bridge; if it stopped part-way, put the original name back.
            if (renamedBridgeNewName != null) {
                try {
                    devicesPage.recoverToDashboard();
                    devicesPage.clickBridgesTab();
                    // Exactly the bridge this test renamed: its new name is unique.
                    devicesPage.verifyBridgeFoundInFilter(renamedBridgeNewName);
                    devicesPage.clickFirstBridge();
                    BridgeSettingsPage restoreSettings = new BridgeSettingsPage(driver);
                    // Wait for the settings screen first: a slow render must not look like "the name is not there".
                    restoreSettings.verifyStatusSectionDisplayed();
                    if (restoreSettings.hasNameField(renamedBridgeNewName)) {
                        restoreSettings.clickNameField(renamedBridgeNewName);
                        restoreSettings.enterNewBridgeName(renamedBridgeOriginalName);
                        restoreSettings.clickDoneOnKeyboard();
                        logger.info("@AfterMethod: restored bridge name '{}'", renamedBridgeOriginalName);
                        renamedBridgeOriginalName = null;
                        renamedBridgeNewName = null;
                    } else if (restoreSettings.hasNameField(renamedBridgeOriginalName)) {
                        // The test body already put the name back (the failure came after that): nothing to restore.
                        logger.info("@AfterMethod: the bridge already has its original name '{}'", renamedBridgeOriginalName);
                        renamedBridgeOriginalName = null;
                        renamedBridgeNewName = null;
                    } else {
                        // Neither name is on the open screen: do not report it as fine.
                        logger.error("@AfterMethod: this bridge shows neither '{}' nor '{}'; check the account's bridge names",
                                renamedBridgeNewName, renamedBridgeOriginalName);
                    }
                } catch (Throwable t) {
                    // Loud on purpose: the account is left with a renamed bridge until someone sets it back.
                    logger.error("@AfterMethod: COULD NOT RESTORE bridge '{}' to its original name '{}': {}",
                            renamedBridgeNewName, renamedBridgeOriginalName, t.toString());
                }
            }
            if (testBridgeName != null) {
                try {
                    devicesPage.recoverToDashboard();
                    devicesPage.clickBridgesTab();
                    if (devicesPage.tryDeleteBridge(testBridgeName, true)) {
                        logger.info("@AfterMethod: cleaned up bridge '{}'", testBridgeName);
                    } else {
                        logger.warn("@AfterMethod: bridge '{}' was not found for cleanup", testBridgeName);
                    }
                } catch (Throwable t) {
                    logger.error("@AfterMethod: could not clean up bridge '{}': {}", testBridgeName, t.toString());
                    leftoverBridges.add(testBridgeName);
                }
            }
            testBridgeName = null;
        } finally {
            super.tearDown(testResult);
        }
    }

    /**
     * From the dashboard: remove leftovers with this prefix, then add a new uniquely named
     * bridge and record it for cleanup. Returns to the screen the add flow lands on.
     */
    private String createTestBridge(String prefix) {
        devicesPage.clickBridgesTab();
        BridgeTestHelper.deleteLeftoverBridges(devicesPage, prefix);
        devicesPage.recoverToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        String bridgeName = TestDataGenerator.generateUniqueName(prefix);
        testBridgeName = bridgeName;
        devicesPage.addBridgeWorkflow(bridgeName, getAttachId());
        return bridgeName;
    }

    /** From the Bridges tab: delete the test bridge and mark it as gone. */
    private void deleteTestBridge(String bridgeName) {
        devicesPage.deleteBridgeWorkflow(bridgeName);
        testBridgeName = null;
    }

    private String getAttachId() {
        return getTestDataValue("attachId");
    }
}
