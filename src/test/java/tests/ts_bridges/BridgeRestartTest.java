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

    @BeforeMethod(alwaysRun = true)
    public void loginAndVerifyDashboard() {
        resellerDashboardPage = loginAsResellerAndVerifyDashboard();
    }

    @Test(description = "EEPD-TC-8898: POS-Bridge Settings - Restart the Bridge in mobile VMS app from Dashboard section",
          groups = {"regression", "bridges"})
    public void restartBridgeFromDashboard() {
        BridgeSettingsPage settings = new BridgeSettingsPage(driver);
        DevicesPage devicesPage = new DevicesPage(driver);
        // Step 2: Click an account that has a bridge to re-login into it
        resellerDashboardPage.openFirstAccountWithBridges();
        // Steps 3 and 4: Go to Dashboard and tap the Bridges option
        devicesPage.openBridgesList();
        // Step 5: Choose a bridge from the list
        devicesPage.clickFirstBridge();
        // Step 6: Click "Restart Bridge" at the bottom of the settings screen
        settings.restartBridgeAndVerifyMessage();
    }
}
