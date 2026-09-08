package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import utils.WaitUtils;

public class UsersPage {
    private static final Logger logger = LoggerFactory.getLogger(UsersPage.class);
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
    private final By backButton=AppiumBy.xpath("//android.widget.ImageView[@resource-id=\"com.een.vms.android:id/img_back\"]");

    public UsersPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickUsersOptionAtTheBottom() {
   	 WaitUtils.waitForVisibility(driver, usersOptionAtTheBottomOfTheScreen).click();
   }
    public void clickUsersAddIconAtTheTopRightCorner() {
      	 WaitUtils.waitForVisibility(driver, usersAddIconAtTheTopRightCorner).click();
      }
    public  boolean isNewUserHeadingDisplayed() {
      	 return WaitUtils.waitForVisibility(driver, newUserHeading).isDisplayed();
      }
    public  boolean addButtonIsInDisabledState() {
    	return !WaitUtils.waitForVisibility(driver, addButton).isEnabled();
      }
    public void enterEmailAtTheInputField(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        var emailField = WaitUtils.waitForVisibility(driver, emailTextInputField);
        if (emailField.isEnabled()) {
            emailField.clear();
        }
        emailField.sendKeys(email);
     }
    public void clickAddButton() {
      	 WaitUtils.waitForVisibility(driver, addButton).click();
      }
    public  boolean isValidEmailErrorMessageDisplayed() {
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
        if (firstNameField.isEnabled()) {
            firstNameField.clear();
        }
        firstNameField.sendKeys(firstName);
   }
    public void enterLastNameInputField(String lastName) {
        var lastNameField = WaitUtils.waitForVisibility(driver, lastNameTextField);
        if (lastNameField.isEnabled()) {
            lastNameField.clear();
        }
        lastNameField.sendKeys(lastName);
     }
    public boolean isMaxCharactersReachedErrorMessageDisplayed() {
    	 return WaitUtils.waitForVisibility(driver, reachedMaxCharactersErrorMessage).isDisplayed();
    }
    public boolean isUserWasAddedMessageDisplayed() {
   	 return WaitUtils.waitForVisibility(driver, snackbarTextUserwasAdded).isDisplayed();
   }
    public boolean isUsersOptionAtBottomScreenDisplayed() {
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
    public void clickDeleteUserOption() {
     	  WaitUtils.waitForVisibility(driver, deleteUserOption).click();
     }
    public void clickConfirmDeleteOption() {
     	  WaitUtils.waitForVisibility(driver, confirmDeleteUserOption).click();
     }
    public boolean isSelectTheUserDisplayed() {
        try {
            return driver.findElement(selectTheUser).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public void clickBackButton() {
        WaitUtils.waitForVisibility(driver, backButton).click();
    }
    public boolean isAddIconDisplayed() {
        try {
            return driver.findElement(usersAddIconAtTheTopRightCorner).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public void verifyNewUserHeadingDisplayed() {
        Assert.assertTrue(isNewUserHeadingDisplayed(), "New user heading should be displayed");
    }
    public void verifyAddButtonDisabled() {
        Assert.assertTrue(addButtonIsInDisabledState(), "Add button should be disabled");
    }
    public void verifyAddButtonEnabled() {
        Assert.assertTrue(isAddButtonEnabled(), "Add button should be enabled");
    }
    public void verifyValidEmailErrorMessageDisplayed() {
        Assert.assertTrue(isValidEmailErrorMessageDisplayed(), "Valid email error message should be displayed");
    }
    public void verifyEmailPlusHintDisplayed() {
        Assert.assertTrue(isEmailEnteringWithPlusDisplaysHintDisplayed(), "Email plus hint should be displayed");
    }
    public void verifyMaxCharactersErrorMessageDisplayed() {
        Assert.assertTrue(isMaxCharactersReachedErrorMessageDisplayed(), "Max characters error message should be displayed");
    }
    public void verifyUserWasAddedMessageDisplayed() {
        Assert.assertTrue(isUserWasAddedMessageDisplayed(), "User was added message should be displayed");
    }

    public void navigateToAddUserScreen() {
        clickUsersOptionAtTheBottom();
        clickUsersAddIconAtTheTopRightCorner();
    }

    public void searchAndDeleteUser(String email) {
        clickUsersSearchFilterIcon();
        filterForUsersSearchbar(email);
        selectTheUser();
        clickDeleteUserOption();
        clickConfirmDeleteOption();
    }

    public void deleteUserIfExists(String email) {
        try {
            clickUsersSearchFilterIcon();
            filterForUsersSearchbar(email);
            if (isSelectTheUserDisplayed()) {
                selectTheUser();
                clickDeleteUserOption();
                clickConfirmDeleteOption();
                logger.info("Cleaned up existing test user: {}", email);
                if (!isAddIconDisplayed()) {
                    logger.info("Add icon not visible after deletion, clicking back");
                    clickBackButton();
                } else {
                    logger.info("Add icon visible, already on correct screen");
                }
            } else {
                logger.info("User {} does not exist, navigating back", email);
                clickBackButton();
            }
        } catch (Exception e) {
            logger.info("Error during cleanup for {}: {}", email, e.getMessage());
            try {
                clickBackButton();
            } catch (Exception ex) {
            }
        }
    }

    public void verifyMaxCharactersValidation() {
        enterFirstNameInputField("f".repeat(55));
        verifyMaxCharactersErrorMessageDisplayed();
        enterLastNameInputField("l".repeat(55));
    }

    public void addUserWithDetails(String firstName, String lastName) {
        String email = utils.TestDataGenerator.generateEmail(firstName, lastName);
        enterEmailAtTheInputField(email);
        enterFirstNameInputField(firstName);
        enterLastNameInputField(lastName);
        clickAddButton();
        verifyUserWasAddedMessageDisplayed();
    }
}
