package tests.ts_diagnostics;

import base.BaseTest;
import constants.TestDataConstants;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.annotations.Test;
import pages.DevicesPage;
import pages.LayoutsPage;
import pages.UsersPage;

/**
 * NOT part of the regression suite (see testng-diagnostics.xml). Walks the screens whose locators
 * keep drifting and saves each screen's UI hierarchy (Appium page source) under
 * target/page-sources/, which CI uploads as an artifact. Use it to pick locators from the real
 * hierarchy instead of guessing from videos. Every step is isolated: a screen that cannot be reached
 * is recorded and the walk continues.
 */
public class HierarchyDumpTest extends BaseTest {

    private static final Path OUT = Path.of("target", "page-sources");

    private void dump(String name) {
        try {
            Files.createDirectories(OUT);
            String source = driver.getPageSource();
            Files.writeString(OUT.resolve(name + ".xml"), source, StandardCharsets.UTF_8);
            logger.info("Saved hierarchy '{}' ({} chars)", name, source.length());
        } catch (IOException | RuntimeException e) {
            logger.warn("Could not save hierarchy '{}': {}", name, e.toString());
        }
    }

    /** Runs one navigation step; whatever happens, the screen reached afterwards is dumped. */
    private void step(String dumpName, Runnable navigation) {
        try {
            navigation.run();
        } catch (Throwable t) {
            logger.warn("Step before '{}' failed: {}", dumpName, t.toString());
        }
        dump(dumpName);
    }

    @Test(description = "Hierarchies of the end-user screens used by bridges, cameras and layouts tests")
    public void endUserScreens() {
        loginAs(TestDataConstants.ENDUSER_CREDS);
        DevicesPage devices = new DevicesPage(driver);
        dump("enduser-01-after-login");
        step("enduser-02-dashboard", devices::navigateToDashboard);
        // The add-bridge icon lives on the dashboard header.
        step("enduser-03-add-menu", devices::clickOnAddBridgeIcon);
        step("enduser-04-add-bridge-form", devices::clickAddBridgeOption);
        step("enduser-05-back-to-dashboard", () -> {
            driver.navigate().back();
            devices.navigateToDashboard();
        });
        step("enduser-06-bridges-tab", devices::clickBridgesTab);
        step("enduser-07-dashboard-again", () -> {
            driver.navigate().back();
            devices.navigateToDashboard();
        });

        LayoutsPage layouts = new LayoutsPage(driver);
        step("enduser-08-layouts", () -> {
            layouts.verifyLayoutsScreenDisplayed();
            layouts.clickFirstLayoutTab();
        });
        step("enduser-09-layouts-menu", layouts::clickThreeDotsMenuButton);
        step("enduser-10-new-layout", layouts::clickNewLayoutOption);
        step("enduser-11-new-layout-name-editing", () -> layouts.setLayoutName("DiagLayout"));
    }

    @Test(description = "Hierarchies of the reseller screens used by the users and account tests")
    public void resellerScreens() {
        loginAs(TestDataConstants.RESELLER_CREDS);
        dump("reseller-01-after-login");
        UsersPage users = new UsersPage(driver);
        step("reseller-02-users", users::clickUsersOptionAtTheBottom);
        // Same data the AddUserTest creates (a pending test user), so the post-add snackbar, the user's
        // details screen and the delete dialog can be captured. The delete is never confirmed.
        String first = utils.TestDataGenerator.generateFirstName();
        String last = utils.TestDataGenerator.generateLastName();
        String[] email = new String[1];
        step("reseller-03-new-user-form", users::clickUsersAddIconAtTheTopRightCorner);
        step("reseller-04-after-add", () -> email[0] = users.addUserWithDetails(first, last));
        step("reseller-04b-after-add-1500ms", () -> pause(1_500));
        step("reseller-05-filtered", () -> users.filterForUsersSearchbar(email[0]));
        step("reseller-06-details", () -> driver.findElement(io.appium.java_client.AppiumBy.xpath(
                "//android.widget.TextView[@text='" + email[0] + "']/ancestor::android.view.View[@clickable='true'][1]")).click());
        step("reseller-06b-details-loaded", () -> pause(12_000));
        step("reseller-07-delete-dialog", () -> driver.findElement(io.appium.java_client.AppiumBy.xpath(
                "//android.widget.TextView[contains(translate(@text,'DELETE','delete'),'delete')]"
                        + "/ancestor-or-self::android.view.View[@clickable='true'][1]")).click());
        logger.info("Diagnostics created pending test user {}", email[0]);
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
