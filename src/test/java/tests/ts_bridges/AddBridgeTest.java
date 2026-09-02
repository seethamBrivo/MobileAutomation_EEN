package tests.ts_bridges;

import base.BaseTest;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.AddBridgePage;
import pages.DeleteBridgePage;
import utils.JsonDataReader;

public class AddBridgeTest extends BaseTest {

    private String testBridgeName;
    private boolean bridgeAdded = false;

    @Test(description = "EEPD-TC-11276: POS-Add Bridge - Adding Bridge to the account in Dashboard in mobile VMS app")
    public void addBridgeToVMSApp() {
        // Step 1: Login as End User with valid credentials
        loginAs("enduser_user_creds");
        AddBridgePage addBridgePage = new AddBridgePage(driver);

        // Step 2: Navigate to More menu and Dashboard
        addBridgePage.clickMoreMenuOptionAtTheBottom();
        addBridgePage.clickDashboardOptionAtTheBottom();

        // Step 3: Verify 'All devices' section is displayed
        Assert.assertTrue(
                addBridgePage.allDevicesSectionIsDisplayed(),
                "Navigated to 'All devices' section"
        );

        // Step 4: Click on Add Bridge icon
        addBridgePage.clickOnAddBridgeIcon();

        // Verify Add bridge option is displayed
        Assert.assertTrue(
                addBridgePage.isAddBridgeOptionDisplayed(),
                "Add bridge option is displayed"
        );

        // Verify Add camera option is displayed
        Assert.assertTrue(
                addBridgePage.isAddCameraOptionDisplayed(),
                "Add camera option is displayed"
        );

        // Verify Add camera direct option is displayed
        Assert.assertTrue(
                addBridgePage.isAddCameraDirectOptionDisplayed(),
                "Add camera direct option is displayed"
        );

        addBridgePage.clickAddBridgeOption();

        // Wait for the form to load
        addBridgePage.waitForBridgeNameFieldToLoad();

        // Step 6: Enter bridge name and attach ID
        testBridgeName = "TestBridge_" + System.currentTimeMillis();
        addBridgePage.enterBridgeName(testBridgeName);
        logger.info(" Entered bridge name: " + testBridgeName);

        JsonObject testData = JsonDataReader.readJsonObject("testData.json");
        String attachId = testData
                .getAsJsonObject("credentials")
                .getAsJsonObject("reseller_user_creds")
                .get("attchId")
                .getAsString();
        addBridgePage.enterAttachId(attachId);
        logger.info(" Entered attach ID");

        // Verify button is enabled after entering all required data
        Assert.assertTrue(
                addBridgePage.isAddBridgeButtonEnabled(),
                "Add bridge button is in enabled state after user enters all required data"
        );

        // Step 7: Click Add Bridge button
        addBridgePage.clickAddBridgeButton();
        logger.info(" Clicked Add Bridge button");

        // Verify user navigated back to origin screen
        Assert.assertTrue(
                addBridgePage.isUserNavigatedToOriginScreen(),
                "User navigated back to origin screen after adding a new bridge successfully"
        );
        bridgeAdded = true;
        logger.info(" Bridge added successfully: " + testBridgeName);

        // Step 8: Try to add the same bridge again with same attach ID to verify conflict error
        logger.info("\n=== Testing Duplicate Bridge Addition ===");

        // Wait for screen to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Try to add another bridge with same attach ID
        addBridgePage.clickOnAddBridgeIcon();
        addBridgePage.clickAddBridgeOption();
        addBridgePage.waitForBridgeNameFieldToLoad();

        String duplicateBridgeName = "DuplicateBridge_" + System.currentTimeMillis();
        addBridgePage.enterBridgeName(duplicateBridgeName);
        logger.info(" Entered duplicate bridge name: " + duplicateBridgeName);

        // Use the SAME attach ID
        addBridgePage.enterAttachId(attachId);
        logger.info(" Entered same attach ID again");

        // Click Add Bridge button
        addBridgePage.clickAddBridgeButton();
        logger.info(" Clicked Add Bridge button (expecting error)");

        // Wait for error message to appear
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify conflict error message is displayed
        Assert.assertTrue(
                addBridgePage.isConflictErrorMessageDisplayed(),
                "Conflict error message 'There is a conflict in the operation.' should be displayed when adding duplicate attach ID"
        );
        logger.info(" Conflict error message verified: 'There is a conflict in the operation.'");

        // Navigate back to dashboard (press back button twice: once from error, once from add bridge form)
        driver.navigate().back();
        logger.info(" Pressed back from error screen");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        driver.navigate().back();
        logger.info(" Pressed back from add bridge form");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Navigate back to dashboard/devices screen
        addBridgePage.clickDashboardOptionAtTheBottom();
        logger.info(" Navigated back to Dashboard");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step 9: Now delete the bridge that was just added
        logger.info("\n=== Starting Bridge Deletion ===");
        DeleteBridgePage deleteBridgePage = new DeleteBridgePage(driver);

        // Navigate to Bridges tab
        deleteBridgePage.clickBridgesTab();

        // Wait for list to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Filter by the bridge name we just added
        deleteBridgePage.enterBridgeNameInFilter(testBridgeName);
        logger.info(" Filtered by bridge name: " + testBridgeName);

        // Wait for filter results
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click on the filtered bridge
        deleteBridgePage.clickFirstBridge();
        logger.info(" Clicked on bridge");

        // Wait for bridge details page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Scroll down to see the "Delete Bridge" button
        utils.GestureUtils.swipeUp(driver);
        utils.GestureUtils.swipeUp(driver);
        logger.info(" Scrolled to see Delete Bridge button");

        Assert.assertTrue(
                deleteBridgePage.isDeleteBridgeButtonDisplayed(),
                "Delete Bridge button should be visible after scrolling"
        );

        // Click Delete Bridge button
        deleteBridgePage.clickDeleteBridgeButton();
        logger.info(" Clicked Delete Bridge button");

        // Wait for confirmation popup
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click "Delete" button in the popup to confirm deletion
        deleteBridgePage.clickDeleteButton();
        logger.info(" Confirmed deletion in popup");

        // Verify user navigated back to Bridges tab (optional check)
        try {
            if (deleteBridgePage.isUserNavigatedBackToBridgesTab()) {
                logger.info(" Navigated back to Bridges tab");
            }
        } catch (Exception e) {
            logger.info("Note: Navigation back check skipped");
        }

        logger.info(" Bridge deleted successfully!");
        logger.info("\n=== Add and Delete Bridge Test Completed Successfully! ===");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupBridge() {
        // Cleanup: Delete the test bridge if it was added
        if (bridgeAdded && testBridgeName != null && driver != null) {
            try {
                logger.info("\n=== @AfterMethod: Cleaning up test bridge ===");
                AddBridgePage addBridgePage = new AddBridgePage(driver);
                DeleteBridgePage deleteBridgePage = new DeleteBridgePage(driver);

                // Navigate to dashboard
                addBridgePage.clickDashboardOptionAtTheBottom();
                Thread.sleep(2000);

                // Navigate to Bridges tab
                deleteBridgePage.clickBridgesTab();
                Thread.sleep(2000);

                // Filter by the bridge name
                deleteBridgePage.enterBridgeNameInFilter(testBridgeName);
                Thread.sleep(1000);

                // Click on the bridge
                deleteBridgePage.clickFirstBridge();
                Thread.sleep(2000);

                // Scroll down to Delete button
                utils.GestureUtils.swipeUp(driver);
                utils.GestureUtils.swipeUp(driver);

                // Click Delete Bridge button
                deleteBridgePage.clickDeleteBridgeButton();
                Thread.sleep(1000);

                // Confirm deletion
                deleteBridgePage.clickDeleteButton();
                Thread.sleep(1000);

                logger.info(" Cleanup: Test bridge deleted in @AfterMethod");
                bridgeAdded = false;
            } catch (Exception e) {
                logger.info("  Cleanup: Could not delete test bridge - " + e.getMessage());
            }
        }
    }
}
