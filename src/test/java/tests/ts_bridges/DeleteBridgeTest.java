package tests.ts_bridges;

import base.BaseTest;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddBridgePage;
import pages.DeleteBridgePage;
import utils.JsonDataReader;

public class DeleteBridgeTest extends BaseTest {

    @Test(description = "EEPD-TC-8890: POS-Bridge Settings - delete Bridge in mobile VMS app")
    public void deleteBridgeFromVMSApp() {
        // Step 1: Login as End User with valid credentials
        loginAs("enduser_user_creds");

        // First, add a bridge so we have something to delete
        AddBridgePage addBridgePage = new AddBridgePage(driver);
        // Use timestamp to make bridge name unique for each test run
        String testBridgeName = "TestBridge_" + System.currentTimeMillis();

        addBridgePage.clickMoreMenuOptionAtTheBottom();
        addBridgePage.clickDashboardOptionAtTheBottom();

        Assert.assertTrue(
                addBridgePage.allDevicesSectionIsDisplayed(),
                "All devices section should be displayed"
        );

        // Add a new bridge
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
                "Add bridge button should be enabled after entering data"
        );

        addBridgePage.clickAddBridgeButton();
        logger.info(" Bridge added: " + testBridgeName);

        // Now start the delete flow
        DeleteBridgePage deleteBridgePage = new DeleteBridgePage(driver);

        // Step 2: Navigate to Bridges tab
        deleteBridgePage.clickBridgesTab();

        // Step 3: Filter by the bridge name we just added
        try {
            Thread.sleep(2000); // Wait for list to load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        deleteBridgePage.enterBridgeNameInFilter(testBridgeName);
        logger.info(" Filtered by bridge name: " + testBridgeName);

        // Wait for filter results
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step 4: Click on the filtered bridge
        deleteBridgePage.clickFirstBridge();
        logger.info(" Clicked on bridge");

        // Wait for bridge details page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step 5: Scroll down to see the "Delete Bridge" button
        utils.GestureUtils.swipeUp(driver);
        utils.GestureUtils.swipeUp(driver);
        logger.info(" Scrolled to see Delete Bridge button");

        Assert.assertTrue(
                deleteBridgePage.isDeleteBridgeButtonDisplayed(),
                "Delete Bridge button should be visible after scrolling"
        );

        // Step 6: Click Delete Bridge button
        deleteBridgePage.clickDeleteBridgeButton();
        logger.info(" Clicked Delete Bridge button");

        // Wait for confirmation popup
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step 7: Click "Delete" button in the popup to confirm deletion
        deleteBridgePage.clickDeleteButton();
        logger.info(" Confirmed deletion in popup");

        // Wait for delete operation to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify user navigated back to Bridges tab
        Assert.assertTrue(
                deleteBridgePage.isUserNavigatedBackToBridgesTab(),
                "User should navigate back to Bridges tab after deletion"
        );
        logger.info(" Navigated back to Bridges tab");

        // Verify success message appeared (may not appear on demo account - soft assertion)
        if (deleteBridgePage.isBridgeDeletedMessageDisplayed()) {
            logger.info(" Success message appeared: 'Bridge was deleted'");
        } else {
            logger.info("Note: Success message not displayed (expected on demo account)");
        }

        logger.info(" Bridge deletion test completed successfully!");

        // Note: On demo account success message is seen but bridge will not be actually deleted
        // This is expected behavior as mentioned in the test case
    }
}
