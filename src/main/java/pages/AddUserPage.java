package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.WaitUtils;


public class AddUserPage {

    private final AppiumDriver driver;
    private final By usersOptionAtTheBottomOfTheScreen= AppiumBy.xpath("//android.widget.TextView[@text=\"Users\"]");
    private final By usersAddIconAtTheTopRightCorner= AppiumBy.id("com.een.vms.android:id/img_custom");
    private final By newUserHeading= AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/header\" and @text=\"New user\"]");
    private final By emailTextInputField= AppiumBy.accessibilityId("email_text_field");
    private final By enterValidEmailAddressErrorMessage= AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/error_desc\" and @text=\"Enter a valid email address\"]");
    private final By addButton= AppiumBy.id("com.een.vms.android:id/btn_text");
    private final By firstNameTextField= AppiumBy.accessibilityId("first_name_text_field");
    private final By lastNameTextField= AppiumBy.accessibilityId("last_name_text_field");
    private final By reachedMaxCharactersErrorMessage= AppiumBy.id("com.een.vms.android:id/error_desc");
    private final By emailPlusHint=AppiumBy.id("com.een.vms.android:id/emailPlusHint");
    private final By snackbarTextUserwasAdded=AppiumBy.xpath("//android.widget.TextView[@resource-id=\"com.een.vms.android:id/snackbar_text\"]");
    private final By usersSearchFilterIcon=AppiumBy.id("com.een.vms.android:id/img_search");
    private final By filterForUsersSearchbar=AppiumBy.id("com.een.vms.android:id/search_bar");
    private final By selectTheUser=AppiumBy.xpath("(//android.widget.ImageView[@content-desc=\"_user_details_container_end_icon\"])[1]");
    private final By deleteUserOption=AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"delete_header_content\"]");
    private final By confirmDeleteUserOption=AppiumBy.xpath("//android.widget.Button[@content-desc=\"negative_button\"]");
    
    
    public AddUserPage(AppiumDriver driver) {
        this.driver = driver;
    }
    public void clickUsersOptionAtTheBottom() {
   	 WaitUtils.waitForVisibility(driver, usersOptionAtTheBottomOfTheScreen).click();
   }
    public void clickUsersAddIconAtTheTopRightCorner() {
      	 WaitUtils.waitForVisibility(driver, usersAddIconAtTheTopRightCorner).click();
      }
    public  boolean isNewUserHeadingIsDisplayed() {
      	 return WaitUtils.waitForVisibility(driver, newUserHeading).isDisplayed();
      }
    public  boolean addButtonIsInDisabledState() {
    	return !WaitUtils.waitForVisibility(driver, addButton).isEnabled();
      }
    public void enterEmailAtTheInputField(String email) {
        var emailField = WaitUtils.waitForVisibility(driver, emailTextInputField);
        emailField.clear();
        emailField.sendKeys(email);
     }
    public void clickAddButton() {
      	 WaitUtils.waitForVisibility(driver, addButton).click();
      }
    public  boolean enterValidEmailAddressErrorMessage() {
     	 return WaitUtils.waitForVisibility(driver, enterValidEmailAddressErrorMessage).isDisplayed();
     }
    
    public  boolean isEmailEnteringWithPlusDisplaysHintDisplayed() {
    	 return WaitUtils.waitForVisibility(driver, emailPlusHint).isDisplayed();
    }
    public  boolean isAddButtonEnabled() {
   	 return WaitUtils.waitForVisibility(driver, addButton).isEnabled();
   }
    public void enterFirstNameInputField(String firstName) {
        var firstNameField = WaitUtils.waitForVisibility(driver, firstNameTextField);
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
   }
    public void enterLastNameInputField(String lastName) {
        var lastNameField = WaitUtils.waitForVisibility(driver, lastNameTextField);
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
     }
    public boolean isMaxCharactersReachedErrorMessageDisplayed() {
    	 return WaitUtils.waitForVisibility(driver, reachedMaxCharactersErrorMessage).isDisplayed();
    }
    public boolean isUserWasAddedMessageDisplayed() {
   	 return WaitUtils.waitForVisibility(driver, snackbarTextUserwasAdded).isDisplayed();
   }
    public boolean isUsersOptionAtBottonScreenDisplayed() {
      	 return WaitUtils.waitForVisibility(driver, usersOptionAtTheBottomOfTheScreen).isDisplayed();
      }
    public void clickUsersSearchFilterIcon() {
      	 WaitUtils.waitForVisibility(driver, usersSearchFilterIcon).click();
      }
    public void filterForUsersSearchbar(String email) {
     	  WaitUtils.waitForVisibility(driver, filterForUsersSearchbar).sendKeys(email);
     }
    public void selectTheUser() {
   	  WaitUtils.waitForVisibility(driver, selectTheUser).click();
   }
    public void waitForUserDetailsToLoad() {
        // Wait for user details screen to load after selecting user
        try {
            Thread.sleep(2000); // Give time for screen transition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void clickDeleteUserOption() {
     	  WaitUtils.waitForVisibility(driver, deleteUserOption).click();
     }
    public void clickConfirmDeleteOption() {
     	  WaitUtils.waitForVisibility(driver, confirmDeleteUserOption).click();
     }
}