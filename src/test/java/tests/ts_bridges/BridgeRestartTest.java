package tests.ts_bridges;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.Test;
import pages.BridgeSettingsPage;
import pages.DevicesPage;
import pages.ResellerDashboardPage;

/**
 * Reseller-side bridge test. Kept apart from {@link BridgesTest} because it logs in with a
 * different account; sharing a @BeforeMethod would need a per-method special case.
 */
public class BridgeRestartTest extends BaseTest {

    @Test(description = "EEPD-TC-8898: POS-Bridge Settings - Restart the Bridge in mobile VMS app from Dashboard section")
    public void restartBridgeFromDashboard() {
        ResellerDashboardPage resellerDashboardPage = loginAs(TestDataConstants.RESELLER_CREDS);
        resellerDashboardPage.verifyDashboardPageDisplayed();
        resellerDashboardPage.clickFirstAccountWithBridges();
        resellerDashboardPage.verifyLayoutsScreenDisplayed();

        DevicesPage devicesPage = new DevicesPage(driver);
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.clickFirstBridge();

        BridgeSettingsPage settings = new BridgeSettingsPage(driver);
        settings.clickRestartBridgeButton();
        settings.verifyRestartBridgeConfirmationDisplayed();
        settings.confirmRestartBridge();
        // If the demo account never shows this confirmation, the TC step should be marked
        // not-automated rather than passed; a silent pass is what this replaces.
        settings.verifyBridgeRestartedMessageDisplayed();
    }
}
