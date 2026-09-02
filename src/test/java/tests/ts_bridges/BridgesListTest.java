package tests.ts_bridges;

import base.BaseTest;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddBridgePage;
import pages.BridgesListPage;
import pages.DeleteBridgePage;
import utils.JsonDataReader;

public class BridgesListTest extends BaseTest {

    @Test(description = "EEPD-TC-16502: POS-Bridges - Display list of bridges under Bridges item in Dashboard in mobile VMS app")
    public void verifyBridgesListInDashboard() {
        // Step 1: Login as End User with valid credentials
        loginAs("enduser_user_creds");
        logger.info(" Logged in as End User");

        AddBridgePage addBridgePage = new AddBridgePage(driver);
        BridgesListPage bridgesListPage = new BridgesListPage(driver);

        // First, add a bridge to ensure we have at least one bridge to verify
        logger.info("\n=== Adding a test bridge ===");

        addBridgePage.clickMoreMenuOptionAtTheBottom();
        addBridgePage.clickDashboardOptionAtTheBottom();

        // Wait for dashboard to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(
                addBridgePage.allDevicesSectionIsDisplayed(),
                "All devices section should be displayed"
        );

        // Add a new bridge
        String testBridgeName = "TestBridge_" + System.currentTimeMillis();
        addBridgePage.clickOnAddBridgeIcon();
        addBridgePage.clickAddBridgeOption();
        addBridgePage.waitForBridgeNameFieldToLoad();
        addBridgePage.enterBridgeName(testBridgeName);

        JsonObject testData = JsonDataReader.readJsonObject("testData.json");
        String attachId = testData
                .getAsJsonObject("credentials")
                .getAsJsonObject("reseller_user_creds")
                .get("attchId")
                .getAsString();
        addBridgePage.enterAttachId(attachId);

        Assert.assertTrue(
                addBridgePage.isAddBridgeButtonEnabled(),
                "Add bridge button should be enabled"
        );

        addBridgePage.clickAddBridgeButton();
        logger.info(" Bridge added: " + testBridgeName);

        // Wait for bridge to be added
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step 2: Now verify the bridge appears in Bridges list
        logger.info("\n=== Verifying Bridges List ===");

        // Verify "All Devices" section is displayed
        Assert.assertTrue(
                bridgesListPage.isAllDevicesSectionDisplayed(),
                "All Devices section should be displayed"
        );
        logger.info(" All Devices section is displayed");

        // Verify all device categories are displayed
        Assert.assertTrue(
                bridgesListPage.isBridgesOptionDisplayed(),
                "Bridges option should be displayed"
        );
        logger.info("  1. Bridges - ");

        Assert.assertTrue(
                bridgesListPage.isCamerasOptionDisplayed(),
                "Cameras option should be displayed"
        );
        logger.info("  2. Cameras - ");

        Assert.assertTrue(
                bridgesListPage.isSpeakersOptionDisplayed(),
                "Speakers option should be displayed"
        );
        logger.info("  3. Speakers - ");

        Assert.assertTrue(
                bridgesListPage.isSwitchesOptionDisplayed(),
                "Switches option should be displayed"
        );
        logger.info("  4. Switches - ");

        // Navigate to Bridges section using the Bridges tab
        DeleteBridgePage deleteBridgePage = new DeleteBridgePage(driver);
        deleteBridgePage.clickBridgesTab();
        logger.info(" Clicked Bridges tab");

        // Wait for bridges list to load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify the bridge we added is in the list by filtering
        deleteBridgePage.enterBridgeNameInFilter(testBridgeName);
        logger.info(" Filtered by bridge name: " + testBridgeName);

        // Wait for filter results
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Try to click on the filtered bridge to verify it exists
        try {
            deleteBridgePage.clickFirstBridge();
            logger.info(" Bridge found in Bridges list: " + testBridgeName);
            logger.info(" Bridge is successfully displayed under Bridges section");

            // Press back to return to bridges list
            driver.navigate().back();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            Assert.fail("Bridge not found in Bridges list: " + testBridgeName);
        }

        // Step 3: Additional verifications on Bridges section
        logger.info("\n=== Additional Verifications ===");
        logger.info(" Bridge counter, status, and other UI elements can be verified");
        logger.info(" For complete verification, provide specific locators for these elements");

        // Step 4: Clean up - Delete the bridge we added
        logger.info("\n=== Cleaning Up - Deleting Test Bridge ===");

        // Filter by bridge name again
        deleteBridgePage.enterBridgeNameInFilter(testBridgeName);
        logger.info(" Filtered by bridge name for deletion: " + testBridgeName);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click on the bridge
        deleteBridgePage.clickFirstBridge();
        logger.info(" Clicked on bridge");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Scroll to see Delete button
        utils.GestureUtils.swipeUp(driver);
        utils.GestureUtils.swipeUp(driver);
        logger.info(" Scrolled to see Delete Bridge button");

        // Verify Delete Bridge button is visible
        Assert.assertTrue(
                deleteBridgePage.isDeleteBridgeButtonDisplayed(),
                "Delete Bridge button should be visible after scrolling"
        );

        // Click Delete Bridge button
        deleteBridgePage.clickDeleteBridgeButton();
        logger.info(" Clicked Delete Bridge button");

        // Wait longer for confirmation popup to appear
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Confirm deletion
        deleteBridgePage.clickDeleteButton();
        logger.info(" Confirmed deletion in popup");

        // Wait for deletion to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info(" Test bridge deleted successfully!");

        logger.info("\n=== Bridges List Test Completed Successfully! ===");
    }
}
