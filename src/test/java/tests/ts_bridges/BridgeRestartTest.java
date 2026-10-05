package tests.ts_bridges;

import base.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BridgeSettingsPage;
import pages.DevicesPage;
import pages.ResellerDashboardPage;

/** Reseller-side bridge test, kept apart from {@link BridgesTest} because it logs in with a different account. */
public class BridgeRestartTest extends BaseTest {

    private ResellerDashboardPage resellerDashboardPage;

    @BeforeMethod
    public void loginAndVerifyDashboard() {
        resellerDashboardPage = loginAsResellerAndVerifyDashboard();
    }

    @Test(description = "EEPD-TC-8898: POS-Bridge Settings - Restart the Bridge in mobile VMS app from Dashboard section")
    public void restartBridgeFromDashboard() {
        BridgeSettingsPage settings = new BridgeSettingsPage(driver);
        DevicesPage devicesPage = new DevicesPage(driver);
        resellerDashboardPage.clickFirstAccountWithBridges();
        resellerDashboardPage.verifyLayoutsScreenDisplayed();
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.clickFirstBridge();
        settings.clickRestartBridgeButton();
        settings.verifyRestartBridgeConfirmationDisplayed();
        settings.confirmRestartBridge();
        // If the demo account never shows this confirmation, the TC step should be marked
        // not-automated rather than passed; a silent pass is what this replaces.
        settings.verifyBridgeRestartedMessageDisplayed();
    }
}
