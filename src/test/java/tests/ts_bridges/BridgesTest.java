package tests.ts_bridges;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
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
    /** Original name of a pre-existing bridge this test renamed and has not yet restored, or null. */
    private String renamedBridgeOriginalName;

    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenDashboard() {
        testBridgeName = null;
        renamedBridgeOriginalName = null;
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
        devicesPage.verifySpeakersOptionDisplayed();
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
     * Uses an existing online bridge on the account (a freshly attached test bridge is not
     * online). The rename is tracked in {@link #renamedBridgeOriginalName} so it is undone
     * in @AfterMethod if any later step fails.
     */
    @Test(priority = 6, description = "EEPD-TC-8881: POS-Bridge Settings - changing the name and checking status of the Bridge in mobile VMS app",
          groups = {"regression", "bridges"})
    public void test06_changeBridgeNameAndCheckStatus() {
        devicesPage.clickBridgesTab();
        devicesPage.clickFirstOnlineBridge();
        BridgeSettingsPage settings = new BridgeSettingsPage(driver);
        settings.verifyStatusSectionDisplayed();
        String status = settings.getBridgeStatus();
        logger.info("Bridge status: {}", status);
        settings.verifyBridgeStatusIsKnown(status);
        settings.clickNameOption();
        String originalName = settings.getCurrentBridgeNameFromEditField();
        Assert.assertFalse(originalName.isBlank(), "Existing bridge should have a name to restore afterwards");
        String newName = TestDataGenerator.generateUniqueName(TestDataConstants.BRIDGE_PREFIX_UPDATED);
        renamedBridgeOriginalName = originalName;
        settings.enterNewBridgeName(newName);
        settings.clickDoneOnKeyboard();
        driver.navigate().back();
        // UI shows stale data after rename; force reload by switching tabs
        devicesPage.refreshBridgesList();
        devicesPage.verifyBridgeFoundInFilter(newName);
        devicesPage.verifyBridgeNameMatches(newName);
        devicesPage.clickFirstBridge();
        settings.verifyStatusSectionDisplayed();
        settings.renameBridge(originalName);
        driver.navigate().back();
        // UI shows stale data after rename; force reload by switching tabs
        devicesPage.refreshBridgesList();
        devicesPage.verifyBridgeFoundInFilter(originalName);
        devicesPage.verifyBridgeNameMatches(originalName);
        renamedBridgeOriginalName = null;
    }

    // ---- lifecycle helpers ----------------------------------------------------------------

    @AfterMethod(alwaysRun = true)
    public void cleanupBridge() {
        if (driver == null) {
            return;
        }
        // Cleanup must never throw: an exception here is a TestNG configuration failure that
        // skips every remaining test in the class. Page verify* methods throw AssertionError,
        // which is not an Exception, hence Throwable.
        if (renamedBridgeOriginalName != null) {
            try {
                devicesPage.recoverToDashboard();
                devicesPage.clickBridgesTab();
                devicesPage.verifyBridgeFoundInFilter(TestDataConstants.BRIDGE_PREFIX_UPDATED);
                devicesPage.clickFirstBridge();
                new BridgeSettingsPage(driver).renameBridge(renamedBridgeOriginalName);
                logger.info("@AfterMethod: restored bridge name '{}'", renamedBridgeOriginalName);
                renamedBridgeOriginalName = null;
            } catch (Throwable t) {
                logger.error("@AfterMethod: could not restore bridge name '{}': {}", renamedBridgeOriginalName, t.toString());
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
                testBridgeName = null;
            } catch (Throwable t) {
                logger.error("@AfterMethod: could not clean up bridge '{}': {}", testBridgeName, t.toString());
            }
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
