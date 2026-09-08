package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

public class DevicesPage {
    private final AppiumDriver driver;
    private final By moreMenuOption = AppiumBy.xpath("//android.widget.TextView[@text='More']");
    private final By dashboardOption = AppiumBy.xpath("//android.widget.TextView[@text='Dashboard']");
    private final By allDevicesSection = AppiumBy.xpath("//android.widget.TextView[@text='All devices']");
    private final By bridgesTab = AppiumBy.xpath("//android.widget.TextView[@text='Bridges']");
    private final By camerasOption = AppiumBy.xpath("//android.widget.TextView[@text='Cameras']");
    private final By speakersOption = AppiumBy.xpath("//android.widget.TextView[@text='Speakers']");
    private final By switchesOption = AppiumBy.xpath("//android.widget.TextView[@text='Switches']");
    private final By addBridgeIcon = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.view.View[2]/android.view.View");
    private final By addBridgeOption = AppiumBy.xpath("//android.widget.TextView[@text='Add bridge']");
    private final By addCameraOption = AppiumBy.xpath("//android.widget.TextView[@text='Add camera']");
    private final By addCameraDirectOption = AppiumBy.xpath("//android.widget.TextView[@text='Add Camera Direct']");
    private final By bridgeNameField = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.widget.EditText");
    private final By attachIdField = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");
    private final By addBridgeButton = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.Button");
    private final By conflictErrorMessage = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'There is a conflict in the operation')]");
    private final By backNavigationIcon = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.view.View[1]/android.view.View");
    private final By firstBridgeInList = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]");
    private final By firstBridgeNameText = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.widget.TextView[1]");
    private final By bridgeThreeDotsIcon = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[1]//android.view.View[contains(@content-desc, 'icon')]");
    private final By bridgeThreeDotsButton = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='DevicesMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View/android.view.View[7]");
    private final By firstOnlineBridge = AppiumBy.xpath("(//android.widget.TextView[@text='Online'])[1]/ancestor::android.view.View[contains(@index, '0') or contains(@index, '1')]");
    private final By filterSearchField = AppiumBy.xpath("//android.widget.EditText");
    private final By deleteBridgeButton = AppiumBy.xpath("//android.widget.TextView[@text='Delete bridge']");
    private final By deleteConfirmationDialog = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Are you sure you want to remove this bridge')]");
    private final By deleteButton = AppiumBy.xpath("//android.widget.TextView[@text='DELETE']");
    private final By cancelButton = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']");
    private final By bridgeDeletedMessage = AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/snackbar_text' and @text='Bridge was deleted']");
    private final By greenDotStatus = AppiumBy.xpath("//android.view.View[@content-desc='online_status']");
    private final By redDotStatus = AppiumBy.xpath("//android.view.View[@content-desc='offline_status']");
    private final By yellowDotStatus = AppiumBy.xpath("//android.view.View[@content-desc='connectivity_issue_status']");

    public DevicesPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickMoreMenuOptionAtTheBottom() {
        WaitUtils.waitForVisibility(driver, moreMenuOption).click();
    }
    public void clickDashboardOptionAtTheBottom() {
        WaitUtils.waitForVisibility(driver, dashboardOption).click();
    }
    public boolean isAllDevicesSectionDisplayed() {
        return WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed();
    }
    public void verifyAllDevicesSectionIsDisplayed() {
        Assert.assertTrue(
            WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed(),
            "All devices section should be displayed"
        );
    }
    public void clickBridgesTab() {
        WaitUtils.waitForVisibility(driver, bridgesTab).click();
    }
    public boolean isBridgesOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgesTab).isDisplayed();
    }
    public boolean isCamerasOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, camerasOption).isDisplayed();
    }
    public boolean isSpeakersOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, speakersOption).isDisplayed();
    }
    public boolean isSwitchesOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, switchesOption).isDisplayed();
    }
    public void clickOnAddBridgeIcon() {
        WaitUtils.waitForVisibility(driver, addBridgeIcon).click();
    }
    public boolean isAddBridgeOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, addBridgeOption).isDisplayed();
    }
    public boolean isAddCameraOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, addCameraOption).isDisplayed();
    }
    public boolean isAddCameraDirectOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, addCameraDirectOption).isDisplayed();
    }
    public void clickAddBridgeOption() {
        WaitUtils.waitForClickability(driver, addBridgeOption).click();
    }
    public void waitForBridgeNameFieldToLoad() {
        WaitUtils.waitForVisibility(driver, bridgeNameField);
    }
    public void enterBridgeName(String bridgeName) {
        if (bridgeName == null) {
            throw new IllegalArgumentException("Bridge name cannot be null");
        }
        var bridgeNameInput = WaitUtils.waitForVisibility(driver, bridgeNameField);
        if (bridgeNameInput.isEnabled()) {
            bridgeNameInput.clear();
        }
        bridgeNameInput.sendKeys(bridgeName);
    }
    public void enterAttachId(String attachId) {
        if (attachId == null) {
            throw new IllegalArgumentException("Attach ID cannot be null");
        }
        var attachIdInput = WaitUtils.waitForVisibility(driver, attachIdField);
        if (attachIdInput.isEnabled()) {
            attachIdInput.clear();
        }
        attachIdInput.sendKeys(attachId);
    }
    public boolean isAddBridgeButtonEnabled() {
        return WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled();
    }
    public boolean isAddBridgeButtonDisabled() {
        return !WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled();
    }
    public void clickAddBridgeButton() {
        WaitUtils.waitForVisibility(driver, addBridgeButton).click();
    }
    public boolean isUserNavigatedToOriginScreen() {
        return WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed();
    }
    public boolean isConflictErrorMessageDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, conflictErrorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public void clickBackNavigationIcon() {
        WaitUtils.waitForVisibility(driver, backNavigationIcon).click();
    }
    public boolean enterBridgeNameInFilter(String bridgeName) {
        if (bridgeName == null) {
            throw new IllegalArgumentException("Bridge name cannot be null");
        }
        try {
            var filterField = WaitUtils.waitForVisibility(driver, filterSearchField);
            filterField.clear();
            filterField.sendKeys(bridgeName);
            WaitUtils.waitForClickability(driver, firstBridgeInList);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        } catch (org.openqa.selenium.InvalidElementStateException |
                 org.openqa.selenium.StaleElementReferenceException e) {
            return false;
        }
    }
    public boolean isBridgeListDisplayed() {
        return WaitUtils.waitForVisibility(driver, firstBridgeInList).isDisplayed();
    }
    public void waitForFirstBridgeToBeClickable() {
        WaitUtils.waitForClickability(driver, firstBridgeInList);
    }
    public void clickFirstBridge() {
        WaitUtils.waitForClickability(driver, firstBridgeInList).click();
    }
    public void clickFirstOnlineBridge() {
        WaitUtils.waitForVisibility(driver, firstOnlineBridge).click();
    }
    public void clickFirstBridgeThreeDotsIcon() {
        WaitUtils.waitForVisibility(driver, bridgeThreeDotsIcon).click();
    }
    public void clickBridgeThreeDotsButton() {
        WaitUtils.waitForVisibility(driver, bridgeThreeDotsButton).click();
    }
    public boolean isBridgeThreeDotsIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeThreeDotsIcon).isDisplayed();
    }
    public String getFirstBridgeName() {
        return WaitUtils.waitForVisibility(driver, firstBridgeNameText).getText();
    }
    public void clickDeleteBridgeButton() {
        WaitUtils.waitForVisibility(driver, deleteBridgeButton).click();
    }
    public boolean isDeleteBridgeButtonDisplayed() {
        return WaitUtils.waitForVisibility(driver, deleteBridgeButton).isDisplayed();
    }
    public boolean isDeleteConfirmationDialogDisplayed() {
        return WaitUtils.waitForVisibility(driver, deleteConfirmationDialog).isDisplayed();
    }
    public void clickDeleteButton() {
        WaitUtils.waitForVisibility(driver, deleteButton).click();
    }
    public void clickCancelButton() {
        WaitUtils.waitForVisibility(driver, cancelButton).click();
    }
    public boolean isBridgeDeletedMessageDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, bridgeDeletedMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isUserNavigatedBackToBridgesTab() {
        return WaitUtils.waitForVisibility(driver, bridgesTab).isDisplayed();
    }
    public boolean areStatusIndicatorsDisplayed() {
        java.time.Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        if (originalTimeout == null) {
            originalTimeout = java.time.Duration.ofSeconds(10);
        }
        try {
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(2));
            return !driver.findElements(greenDotStatus).isEmpty() ||
                   !driver.findElements(redDotStatus).isEmpty() ||
                   !driver.findElements(yellowDotStatus).isEmpty();
        } finally {
            driver.manage().timeouts().implicitlyWait(originalTimeout);
        }
    }
    public void verifyBridgesOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, bridgesTab).isDisplayed(), "Bridges option should be displayed");
    }
    public void verifyCamerasOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, camerasOption).isDisplayed(), "Cameras option should be displayed");
    }
    public void verifySpeakersOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, speakersOption).isDisplayed(), "Speakers option should be displayed");
    }
    public void verifySwitchesOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, switchesOption).isDisplayed(), "Switches option should be displayed");
    }
    public void verifyAddBridgeOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addBridgeOption).isDisplayed(), "Add bridge option should be displayed");
    }
    public void verifyAddCameraOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addCameraOption).isDisplayed(), "Add camera option should be displayed");
    }
    public void verifyAddCameraDirectOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addCameraDirectOption).isDisplayed(), "Add Camera Direct option should be displayed");
    }
    public void verifyAddBridgeButtonEnabled() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled(), "Add bridge button should be enabled");
    }
    public void verifyConflictErrorMessageDisplayed() {
        Assert.assertTrue(isConflictErrorMessageDisplayed(), "Conflict error message should be displayed");
    }
    public void verifyBridgeDeletedMessageDisplayed() {
        Assert.assertTrue(isBridgeDeletedMessageDisplayed(), "Bridge deleted message should be displayed");
    }
    public void verifyUserNavigatedToOriginScreen() {
        Assert.assertTrue(isUserNavigatedToOriginScreen(), "User should be navigated back to origin screen");
    }
    public void verifyUserNavigatedBackToBridgesTab() {
        Assert.assertTrue(isUserNavigatedBackToBridgesTab(), "User should be navigated back to Bridges tab");
    }
    public void verifyDeleteBridgeButtonDisplayed() {
        Assert.assertTrue(isDeleteBridgeButtonDisplayed(), "Delete Bridge button should be displayed");
    }
    public void verifyBridgeListDisplayed() {
        Assert.assertTrue(isBridgeListDisplayed(), "Bridge list should be displayed");
    }
    public void verifyBridgeFoundInFilter(String bridgeName) {
        Assert.assertTrue(enterBridgeNameInFilter(bridgeName), "Bridge should be found in filter: " + bridgeName);
    }
    public void verifyBridgeNameMatches(String expectedName) {
        String actualName = getFirstBridgeName();
        Assert.assertEquals(actualName, expectedName, "Bridge name should match");
    }

    // ==================== Workflow Methods ====================

    /**
     * Navigate to Dashboard from any screen
     */
    public void navigateToDashboard() {
        clickMoreMenuOptionAtTheBottom();
        clickDashboardOptionAtTheBottom();
    }

    /**
     * Complete workflow: Add a bridge with name and attach ID
     * @param bridgeName The name for the bridge
     * @param attachId The attach ID for the bridge
     */
    public void addBridgeWorkflow(String bridgeName, String attachId) {
        clickOnAddBridgeIcon();
        clickAddBridgeOption();
        waitForBridgeNameFieldToLoad();
        enterBridgeName(bridgeName);
        enterAttachId(attachId);
        verifyAddBridgeButtonEnabled();
        clickAddBridgeButton();
    }

    /**
     * Complete workflow: Delete a bridge by filtering and removing it
     * @param bridgeName The name of the bridge to delete
     */
    public void deleteBridgeWorkflow(String bridgeName) {
        verifyBridgeFoundInFilter(bridgeName);
        clickFirstBridge();
        utils.GestureUtils.swipeUpTwice(driver);
        verifyDeleteBridgeButtonDisplayed();
        clickDeleteBridgeButton();
        clickDeleteButton();
    }
}
