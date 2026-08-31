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
    private final By userNavigatedToOriginScreen= AppiumBy.xpath("//android.widget.TextView[@text=\\\"All devices\\\"]");
   

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
    public void enterBridgeName(String bridgeName) {
        WaitUtils.waitForVisibility(driver, bridgeNameField).sendKeys(bridgeName);
    }
    public boolean isAddBridgeButtonDisabled() {
	  	 return !WaitUtils.waitForVisibility(driver, addBridgeButton).isEnabled();
	    }
    public void enterAttachId(String attachId) {
        WaitUtils.waitForVisibility(driver, attachIdField).sendKeys(attachId);
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
  
  
    
//    public void clickUsersAddIconAtTheTopRightCorner() {
//      	 WaitUtils.waitForVisibility(driver, usersAddIconAtTheTopRightCorner).click();
//      }
//    public  boolean isNewUserHeadingIsDisplayed() {
//      	 return WaitUtils.waitForVisibility(driver, newUserHeading).isDisplayed();
//      }
//    public  boolean addButtonIsInDisabledState() {
//    	return !WaitUtils.waitForVisibility(driver, addButton).isEnabled();
//      }
//    public void enterEmailAtTheInputField(String email) {
//     	  WaitUtils.waitForVisibility(driver, emailTextInputField).sendKeys(email);
//     }
//    public void clickAddButton() {
//      	 WaitUtils.waitForVisibility(driver, addButton).click();
//      }
//    public  boolean enterValidEmailAddressErrorMessage() {
//     	 return WaitUtils.waitForVisibility(driver, enterValidEmailAddressErrorMessage).isDisplayed();
//     }
//    
//    public  boolean isEmailEnteringWithPlusDisplaysHintDisplayed() {
//    	 return WaitUtils.waitForVisibility(driver, emailPlusHint).isDisplayed();
//    }
//    public  boolean isAddButtonEnabled() {
//   	 return WaitUtils.waitForVisibility(driver, addButton).isEnabled();
//   }
//    public void enterFirstNameInputField(String firstName) {
//   	  WaitUtils.waitForVisibility(driver, firstNameTextField).sendKeys(firstName);
//   }
//    public void enterLastNameInputField(String lastName) {
//     	  WaitUtils.waitForVisibility(driver, lastNameTextField).sendKeys(lastName);
//     }
//    public boolean isMaxCharactersReachedErrorMessageDisplayed() {
//    	 return WaitUtils.waitForVisibility(driver, reachedMaxCharactersErrorMessage).isDisplayed();
//    }
//    public boolean isUserWasAddedMessageDisplayed() {
//   	 return WaitUtils.waitForVisibility(driver, snackbarTextUserwasAdded).isDisplayed();
//   }
//    public boolean isUsersOptionAtBottonScreenDisplayed() {
//      	 return WaitUtils.waitForVisibility(driver, usersOptionAtTheBottomOfTheScreen).isDisplayed();
//      }
//    public void clickUsersSearchFilterIcon() {
//      	 WaitUtils.waitForVisibility(driver, usersSearchFilterIcon).click();
//      }
//    public void filterForUsersSearchbar(String email) {
//     	  WaitUtils.waitForVisibility(driver, filterForUsersSearchbar).sendKeys(email);
//     }
//    public void selectTheUser() {
//   	  WaitUtils.waitForVisibility(driver, selectTheUser).click();
//   }
//    public void clickDeleteUserOption() {
//     	  WaitUtils.waitForVisibility(driver, deleteUserOption).click();
//     }
//    public void clickConfirmDeleteOption() {
//     	  WaitUtils.waitForVisibility(driver, confirmDeleteUserOption).click();
//     }
}