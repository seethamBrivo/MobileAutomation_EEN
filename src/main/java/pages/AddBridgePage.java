package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.WaitUtils;


public class AddBridgePage {

    private final AppiumDriver driver;
    private final By moreMenuOption= AppiumBy.xpath("//android.widget.TextView[@text=\"More\"]");
    private final By dashboardOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Dashboard\"]");
    private final By allDevicesSection= AppiumBy.xpath("//android.widget.TextView[@text=\"All devices\"]");
    private final By addBridgeIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DevicesMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.view.View[2]/android.view.View");
    private final By addBridgeOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Add bridge\"]");
    private final By addCameraOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Add camera\"]");
    private final By addCameraDirectOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Add camera\"]");
    private final By bridgeNameField= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[1]/android.widget.EditText");
    private final By attachIdField= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");
    private final By addBridgeButton= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.Button");
    private final By userNavigatedToOriginScreen= AppiumBy.xpath("//android.widget.TextView[@text='All devices']");

    // Error message for duplicate bridge
    private final By conflictErrorMessage= AppiumBy.xpath("//android.widget.TextView[contains(@text, 'There is a conflict in the operation')]");
   

    public AddBridgePage(AppiumDriver driver) {
        this.driver = driver;
    }
    public void clickMoreMenuOptionAtTheBottom() {
   	 WaitUtils.waitForVisibility(driver, moreMenuOption).click();
   }
    public void clickDashboardOptionAtTheBottom() {
      	 WaitUtils.waitForVisibility(driver, dashboardOption).click();
      }
   public  boolean allDevicesSectionIsDisplayed() {
  	 return WaitUtils.waitForVisibility(driver, allDevicesSection).isDisplayed();
    }
   public void clickOnAddBridgeIcon() {
   	 WaitUtils.waitForVisibility(driver, addBridgeIcon).click();
   }
   public  boolean isAddBridgeOptionDisplayed() {
	  	 return WaitUtils.waitForVisibility(driver, addBridgeOption).isDisplayed();
	    }
   public  boolean isAddCameraOptionDisplayed() {
	  	 return WaitUtils.waitForVisibility(driver, addCameraOption).isDisplayed();
	    }
   public  boolean isAddCameraDirectOptionDisplayed() {
	  	 return WaitUtils.waitForVisibility(driver, addCameraDirectOption).isDisplayed();
	    }
   public void clickAddBridgeOption() {
	   	 WaitUtils.waitForVisibility(driver, addBridgeOption).click();
	   }
    public void waitForBridgeNameFieldToLoad() {
        WaitUtils.waitForVisibility(driver, bridgeNameField);
    }
    public void enterBridgeName(String bridgeName) {
        if (bridgeName == null) {
            throw new IllegalArgumentException("Bridge name cannot be null");
        }
        var bridgeNameInput = WaitUtils.waitForVisibility(driver, bridgeNameField);
        bridgeNameInput.clear();
        bridgeNameInput.sendKeys(bridgeName);
    }
    public boolean isAddBridgeButtonDisabled() {
	  	 return !WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled();
	    }
    public void enterAttachId(String attachId) {
        if (attachId == null) {
            throw new IllegalArgumentException("Attach ID cannot be null");
        }
        var attachIdInput = WaitUtils.waitForVisibility(driver, attachIdField);
        attachIdInput.clear();
        attachIdInput.sendKeys(attachId);
    }
    public boolean isAddBridgeButtonEnabled() {
	  	 return WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled();
	    }
    public void clickAddBridgeButton() {
        WaitUtils.waitForVisibility(driver, addBridgeButton).click();
    }
    public boolean isUserNavigatedToOriginScreen() {
      	 return WaitUtils.waitForVisibility(driver, userNavigatedToOriginScreen).isDisplayed();
        }

    public boolean isConflictErrorMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, conflictErrorMessage).isDisplayed();
    }


}
