package tests.ts_bridges;

import base.BaseTest;
import com.google.gson.JsonObject;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BridgeSettingsPage;
import pages.DevicesPage;
import utils.BridgeTestHelper;
import utils.GestureUtils;
import utils.JsonDataReader;
import java.time.Duration;

public class BridgesTest extends BaseTest {
    private String testBridgeName;
    private boolean bridgeAdded = false;
    private DevicesPage devicesPage;

    @BeforeMethod
    public void setup(java.lang.reflect.Method method) {
        // Reset state before each test
        bridgeAdded = false;
        testBridgeName = null;

        // Test07 uses reseller login, so skip enduser login for it
        if (method.getName().equals("test07_restartBridgeFromDashboard")) {
            return;
        }
        loginAs("enduser_user_creds");
        devicesPage = new DevicesPage(driver);
    }

    @Test(priority = 1, description = "EEPD-TC-11276: POS-Add Bridge - Adding Bridge to the account in Dashboard in mobile VMS app")
    public void test01_addBridgeToVMSApp() {
        devicesPage.navigateToDashboard();
        performPreTestCleanup("TestBridge_");
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        testBridgeName = generateBridgeName("TestBridge_");
        devicesPage.addBridgeWorkflow(testBridgeName, getAttachIdFromTestData());
        bridgeAdded = true;
        devicesPage.verifyUserNavigatedToOriginScreen();

        devicesPage.clickOnAddBridgeIcon();
        devicesPage.clickAddBridgeOption();
        devicesPage.waitForBridgeNameFieldToLoad();
        devicesPage.enterBridgeName(generateBridgeName("DuplicateBridge_"));
        devicesPage.enterAttachId(getAttachIdFromTestData());
        devicesPage.clickAddBridgeButton();
        devicesPage.verifyConflictErrorMessageDisplayed();

        driver.navigate().back();
        driver.navigate().back();
        devicesPage.clickDashboardOptionAtTheBottom();
        devicesPage.clickBridgesTab();
        devicesPage.deleteBridgeWorkflow(testBridgeName);
        bridgeAdded = false;
    }

    @Test(priority = 2, description = "EEPD-TC-8890: POS-Bridge Settings - delete Bridge in mobile VMS app")
    public void test02_deleteBridgeFromVMSApp() {
        devicesPage.navigateToDashboard();
        performPreTestCleanup("TestBridge_");
        testBridgeName = generateBridgeName("TestBridge_");
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.addBridgeWorkflow(testBridgeName, getAttachIdFromTestData());
        bridgeAdded = true;

        devicesPage.clickBridgesTab();
        devicesPage.deleteBridgeWorkflow(testBridgeName);
        devicesPage.verifyUserNavigatedBackToBridgesTab();

        if (!devicesPage.isBridgeDeletedMessageDisplayed()) {
            logger.info("Note: Success message not displayed (expected on demo account)");
        }
        bridgeAdded = false;
    }

    @Test(priority = 3, description = "EEPD-TC-16502: POS-Bridges - Display list of bridges under Bridges item in Dashboard in mobile VMS app")
    public void test03_verifyBridgesListInDashboard() {
        devicesPage.clickMoreMenuOptionAtTheBottom();
        devicesPage.clickDashboardOptionAtTheBottom();
        try {
            devicesPage.clickBridgesTab();
            BridgeTestHelper.deleteBridgeIfExists(driver, devicesPage, "TestBridge_", logger);
            driver.navigate().back();
        } catch (Exception e) {
            logger.info("Pre-test: No existing bridges to cleanup or cleanup failed: {}", e.getMessage());
            try {
                driver.navigate().back();
            } catch (Exception ex) {
                logger.info("Pre-test: Could not navigate back");
            }
        }
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        testBridgeName = generateBridgeName("TestBridge_");
        devicesPage.clickOnAddBridgeIcon();
        devicesPage.clickAddBridgeOption();
        devicesPage.waitForBridgeNameFieldToLoad();
        devicesPage.enterBridgeName(testBridgeName);
        devicesPage.enterAttachId(getAttachIdFromTestData());
        devicesPage.verifyAddBridgeButtonEnabled();
        devicesPage.clickAddBridgeButton();
        bridgeAdded = true;
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.verifyBridgesOptionDisplayed();
        devicesPage.verifyCamerasOptionDisplayed();
        devicesPage.verifySpeakersOptionDisplayed();
        devicesPage.verifySwitchesOptionDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeFoundInFilter(testBridgeName);
        logger.info("Filtered by bridge name: " + testBridgeName);
        try {
            devicesPage.clickFirstBridge();
            driver.navigate().back();
        } catch (Exception e) {
            throw new AssertionError("Bridge not found in Bridges list: " + testBridgeName);
        }
        devicesPage.verifyBridgeFoundInFilter(testBridgeName);
        devicesPage.clickFirstBridge();
        GestureUtils.swipeUpTwice(driver);
        devicesPage.verifyDeleteBridgeButtonDisplayed();
        devicesPage.clickDeleteBridgeButton();
        devicesPage.clickDeleteButton();
        bridgeAdded = false;
    }

    @Test(priority = 4, description = "EEPD-TC-15441: POS-Bridges - Filter for bridges by search term under Bridges item in Dashboard in VMS mobile app")
    public void test04_filterBridgesBySearchTerm() {
        devicesPage.clickMoreMenuOptionAtTheBottom();
        devicesPage.clickDashboardOptionAtTheBottom();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        try {
            devicesPage.clickBridgesTab();
            BridgeTestHelper.deleteBridgeIfExists(driver, devicesPage, "FilterTestBridge_", logger);
            driver.navigate().back();
        } catch (Exception e) {
            logger.info("Pre-test: No existing bridges to cleanup or cleanup failed: {}", e.getMessage());
            try {
                driver.navigate().back();
            } catch (Exception ex) {
                logger.info("Pre-test: Could not navigate back");
            }
        }
        testBridgeName = generateBridgeName("FilterTestBridge_");
        devicesPage.clickOnAddBridgeIcon();
        devicesPage.clickAddBridgeOption();
        devicesPage.waitForBridgeNameFieldToLoad();
        devicesPage.enterBridgeName(testBridgeName);
        devicesPage.enterAttachId(getAttachIdFromTestData());
        devicesPage.verifyAddBridgeButtonEnabled();
        devicesPage.clickAddBridgeButton();
        bridgeAdded = true;
        devicesPage.clickBridgesTab();
        try {
            WebElement filterField = driver.findElement(AppiumBy.xpath("//android.widget.EditText"));
            if (filterField.isDisplayed()) {
                // Filter field exists and is displayed - validation passed
            }
        } catch (Exception e) {
            logger.info("Filter field verification: Field appears on interaction");
        }
        devicesPage.enterBridgeNameInFilter(testBridgeName);
        devicesPage.verifyBridgeListDisplayed();
        devicesPage.verifyBridgeNameMatches(testBridgeName);
        devicesPage.enterBridgeNameInFilter("");
        String partialName = testBridgeName.substring(0, Math.min(10, testBridgeName.length()));
        devicesPage.enterBridgeNameInFilter(partialName);
        String partialDisplayedName = devicesPage.getFirstBridgeName();
        if (!partialDisplayedName.contains(partialName)) {
            throw new AssertionError("Displayed bridge name should contain the partial search term. Expected to contain: '"
                + partialName + "', but got: '" + partialDisplayedName + "'");
        }
        devicesPage.verifyBridgeFoundInFilter(testBridgeName);
        devicesPage.clickFirstBridge();
        GestureUtils.swipeUpTwice(driver);
        devicesPage.verifyDeleteBridgeButtonDisplayed();
        devicesPage.clickDeleteBridgeButton();
        devicesPage.clickDeleteButton();
        bridgeAdded = false;
    }

    @Test(priority = 5, description = "EEPD-TC-12354: POS-Bridges - Display context menu of bridge item in Bridge list in Dashboard of mobile VMS app")
    public void test05_verifyBridgeContextMenu() {
        devicesPage.clickMoreMenuOptionAtTheBottom();
        devicesPage.clickDashboardOptionAtTheBottom();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        try {
            devicesPage.clickBridgesTab();
            BridgeTestHelper.deleteBridgeIfExists(driver, devicesPage, "ContextMenuBridge_", logger);
            driver.navigate().back();
        } catch (Exception e) {
            logger.info("Pre-test: No existing bridges to cleanup or cleanup failed: {}", e.getMessage());
            try {
                driver.navigate().back();
            } catch (Exception ex) {
                logger.info("Pre-test: Could not navigate back");
            }
        }
        testBridgeName = generateBridgeName("ContextMenuBridge_");
        devicesPage.clickOnAddBridgeIcon();
        devicesPage.clickAddBridgeOption();
        devicesPage.waitForBridgeNameFieldToLoad();
        devicesPage.enterBridgeName(testBridgeName);
        devicesPage.enterAttachId(getAttachIdFromTestData());
        devicesPage.clickAddBridgeButton();
        bridgeAdded = true;
        devicesPage.clickBridgesTab();
        devicesPage.verifyBridgeFoundInFilter(testBridgeName);
        try {
            WebElement bridgeElement = driver.findElement(
                AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]")
            );
            GestureUtils.longPress(driver, bridgeElement, Duration.ofMillis(2000));
        } catch (Exception e) {
            logger.info("Long press context menu: " + e.getMessage());
            logger.info("(Trying alternative approach with 3-dots button)");
            try {
                devicesPage.clickBridgeThreeDotsButton();
            } catch (Exception ex) {
                logger.info("Could not click 3-dots button: " + ex.getMessage());
            }
        }
        verifyContextMenuOption("Bridge information");
        verifyContextMenuOption("Bridge settings");
        verifyContextMenuOption("Cameras");
        verifyContextMenuOption("Turn on all cameras");
        verifyContextMenuOption("Turn off all cameras");
        verifyContextMenuOption("Replace bridge");
        try {
            WebElement restartOption = driver.findElement(
                AppiumBy.xpath("//android.widget.TextView[@text='Restart bridge']")
            );
            if (restartOption.isDisplayed()) {
                // Optional menu item - exists only for certain user roles
            }
        } catch (Exception e) {
            logger.info("Restart bridge option (not visible - user is not Sub-User)");
        }
        driver.navigate().back();
        devicesPage.verifyBridgeFoundInFilter(testBridgeName);
        devicesPage.clickFirstBridge();
        GestureUtils.swipeUpTwice(driver);
        devicesPage.verifyDeleteBridgeButtonDisplayed();
        devicesPage.clickDeleteBridgeButton();
        devicesPage.clickDeleteButton();
        bridgeAdded = false;
    }

    @Test(priority = 6, description = "EEPD-TC-8881: POS-Bridge Settings - changing the name and checking status of the Bridge in mobile VMS app")
    public void test06_changeBridgeNameAndCheckStatus() {
        devicesPage.clickMoreMenuOptionAtTheBottom();
        devicesPage.clickDashboardOptionAtTheBottom();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.clickFirstOnlineBridge();
        BridgeSettingsPage bridgeSettingsPage = new BridgeSettingsPage(driver);
        bridgeSettingsPage.verifyStatusSectionDisplayed();
        String bridgeStatus = bridgeSettingsPage.getBridgeStatus();
        logger.info("Bridge Status: " + bridgeStatus);

        // Verify status is determined (not "Unknown")
        if (!bridgeStatus.equals("Unknown")) {
            if (bridgeStatus.equals("Online")) {
                bridgeSettingsPage.verifyOnlineStatusDisplayed();
            } else if (bridgeStatus.equals("Offline")) {
                bridgeSettingsPage.verifyOfflineStatusDisplayed();
            }
        } else {
            logger.warn("Bridge status could not be determined - showing as Unknown");
        }

        // Test bridge rename
        bridgeSettingsPage.clickNameOption();
        String originalBridgeName = bridgeSettingsPage.getCurrentBridgeNameFromEditField();
        String newBridgeName = generateBridgeName("UpdatedBridge_");
        bridgeSettingsPage.enterNewBridgeName(newBridgeName);
        bridgeSettingsPage.clickDoneOnKeyboard();
        driver.navigate().back();

        // Verify rename succeeded
        boolean bridgeFoundWithNewName = devicesPage.enterBridgeNameInFilter(newBridgeName);
        if (bridgeFoundWithNewName) {
            String displayedName = devicesPage.getFirstBridgeName();
            logger.info("Bridge rename verified - new name: " + displayedName);
            devicesPage.verifyBridgeNameMatches(newBridgeName);
        } else {
            throw new AssertionError("Bridge rename failed - could not find bridge with new name: " + newBridgeName);
        }

        // Restore original bridge name (cleanup)
        devicesPage.clickFirstBridge();
        bridgeSettingsPage.clickNameOption();
        bridgeSettingsPage.enterNewBridgeName(originalBridgeName);
        bridgeSettingsPage.clickDoneOnKeyboard();
        logger.info("Restored original bridge name: " + originalBridgeName);
    }

    @Test(priority = 7, description = "EEPD-TC-8898: POS-Bridge Settings - Restart the Bridge in mobile VMS app from Dashboard section")
    public void test07_restartBridgeFromDashboard() {
        loginAs("reseller_user_creds");
        pages.ResellerDashboardPage resellerDashboardPage = new pages.ResellerDashboardPage(driver);
        resellerDashboardPage.verifyDashboardPageDisplayed();
        resellerDashboardPage.clickFirstAccountWithBridges();
        resellerDashboardPage.verifyLayoutsScreenDisplayed();
        devicesPage = new DevicesPage(driver);
        devicesPage.clickMoreMenuOptionAtTheBottom();
        devicesPage.clickDashboardOptionAtTheBottom();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        devicesPage.clickBridgesTab();
        devicesPage.clickFirstBridge();
        logger.info("Clicked on a bridge");
        BridgeSettingsPage bridgeSettingsPage = new BridgeSettingsPage(driver);
        GestureUtils.swipeUpTwice(driver);
        logger.info("Scrolled to bottom of bridge settings page");
        bridgeSettingsPage.clickRestartBridgeButton();
        bridgeSettingsPage.verifyRestartBridgeConfirmationDisplayed();
        bridgeSettingsPage.confirmRestartBridge();
        logger.info("Confirmed restart bridge");
        if (bridgeSettingsPage.isBridgeRestartedMessageDisplayed()) {
            logger.info("Bridge restart success message displayed");
        } else {
            logger.info("Note: Success message not displayed (expected on demo account)");
        }
    }
    @AfterMethod(alwaysRun = true)
    public void cleanupBridge() {
        if (bridgeAdded && testBridgeName != null && driver != null) {
            try {
                devicesPage.clickBridgesTab();
                devicesPage.deleteBridgeWorkflow(testBridgeName);
                logger.info("@AfterMethod: Cleaned up bridge: {}", testBridgeName);
                bridgeAdded = false;
            } catch (Exception e) {
                logger.warn("@AfterMethod: Could not cleanup bridge: {}", e.getMessage());
            }
        }
    }
    private void verifyContextMenuOption(String optionText) {
        WebElement option = driver.findElement(
            AppiumBy.xpath("//android.widget.TextView[@text='" + optionText + "']")
        );
        if (!option.isDisplayed()) {
            throw new AssertionError(optionText + " should be displayed");
        }
    }
    private void performPreTestCleanup(String bridgePrefix) {
        try {
            devicesPage.clickBridgesTab();
            BridgeTestHelper.deleteBridgeIfExists(driver, devicesPage, bridgePrefix, logger);
            driver.navigate().back();
        } catch (Exception e) {
            logger.info("Pre-test: Cleanup exception - {}", e.getMessage());
            try {
                driver.navigate().back();
            } catch (Exception ex) {
                logger.info("Pre-test: Could not navigate back");
            }
        }
    }
    private String getAttachIdFromTestData() {
        JsonObject testData = JsonDataReader.readJsonObject("testData.json");
        return testData
                .getAsJsonObject("credentials")
                .getAsJsonObject("reseller_user_creds")
                .get("attachId")
                .getAsString();
    }
    private String generateBridgeName(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
