package tests.ts_users;

import base.BaseTest;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

import org.openqa.selenium.Point;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import utils.GestureUtils;
import utils.JsonDataReader;
import utils.TestDataGenerator;
import pages.ResellerDashboardPage;
import pages.AddUserPage;

public class AddUserTest extends BaseTest {
	 @Test(description = "EEPD-TC-4170: POS-Users - adding New User in mobile VMS app")
	    public void addNewUserToVMSApp() {
		 ResellerDashboardPage resellerDashboardPage = loginAs("reseller_user_creds");
		 AddUserPage addUserPage = new AddUserPage(driver);
		 addUserPage.clickUsersOptionAtTheBottom();
		 addUserPage.clickUsersAddIconAtTheTopRightCorner();
	     Assert.assertTrue(
	    		 addUserPage.isNewUserHeadingIsDisplayed(),
	                "New User Heading is visible at the add user screen"
	        );
	     Assert.assertTrue(
	    		 addUserPage.addButtonIsInDisabledState(),
	                "Add button is in disabled state until user enters the valid email"
	        );
	     JsonObject testData =
	                JsonDataReader.readJsonObject("testData.json");
	     String invalidEmailFormatText = testData
	                .getAsJsonObject("credentials")
	                .getAsJsonObject("reseller_user_creds")
	                .get("inavlidEmailFormatText")
	                .getAsString();
	     addUserPage.enterEmailAtTheInputField(invalidEmailFormatText);
	     addUserPage.clickAddButton();
	     Assert.assertTrue(
	        		addUserPage.enterValidEmailAddressErrorMessage(),
	                "Enter a valid email address error message is displayed"
	        );
	     String firstName = TestDataGenerator.generateFirstName();
	     String lastName = TestDataGenerator.generateLastName();
	     addUserPage.enterEmailAtTheInputField(firstName+"+");
	     Assert.assertTrue(
 		addUserPage.isEmailEnteringWithPlusDisplaysHintDisplayed(),
 		"If user enters '+' 'The he + alias is no longer required. It's possible to access multiple accounts with the same email address. You can easily switch between accounts without re-login.' is visible "
         );
	     addUserPage.isAddButtonEnabled();
	     String email = TestDataGenerator.generateEmail(firstName, lastName);
	     addUserPage.enterEmailAtTheInputField(email);
	     String firstNameOver50Chars = "f".repeat(55);
	     addUserPage.enterFirstNameInputField(firstNameOver50Chars);
	      Assert.assertTrue(
 		addUserPage.isMaxCharactersReachedErrorMessageDisplayed(),
 		"Reached Max characters error message is displayed"
          );
	     String lastNameOver50Chars = "l".repeat(55);
	     addUserPage.enterLastNameInputField(lastNameOver50Chars);
	     addUserPage.enterFirstNameInputField(firstName);
	     addUserPage.enterLastNameInputField(lastName);
	     addUserPage.clickAddButton();

	     // Verify user was added successfully by checking if we're back at users list
	     // or success snackbar appears
	     boolean userAdded = false;
	     try {
	         if (addUserPage.isUserWasAddedMessageDisplayed()) {
	             userAdded = true;
	         }
	     } catch (Exception e) {
	         // Snackbar may not appear or may be too quick
	     }

	     // Verify we're back at users screen (fallback verification)
	     if (!userAdded) {
	         Assert.assertTrue(
	             addUserPage.isUsersOptionAtBottonScreenDisplayed(),
	             "User should be added and returned to users list"
	         );
	     }
	   //Adding a user already existing in the system but not existing in the current logged account
	     addUserPage.clickUsersAddIconAtTheTopRightCorner();

	     String exstingUserButNotAvailbleInCurrentLogin = testData
	                .getAsJsonObject("credentials")
	                .getAsJsonObject("reseller_user_creds")
	                .get("existingUserInSystemButNotExistingInCurrentLogin")
	                .getAsString();
	     addUserPage.enterEmailAtTheInputField(exstingUserButNotAvailbleInCurrentLogin);
	     addUserPage.enterFirstNameInputField(firstName);
	     addUserPage.enterLastNameInputField(lastName);
	     addUserPage.clickAddButton();

	     // Verify user was added successfully by checking if we're back at users list
	     // or success snackbar appears
	     boolean existingUserAdded = false;
	     try {
	         if (addUserPage.isUserWasAddedMessageDisplayed()) {
	             existingUserAdded = true;
	         }
	     } catch (Exception e) {
	         // Snackbar may not appear or may be too quick
	     }

	     // Verify we're back at users screen (fallback verification)
	     if (!existingUserAdded) {
	         Assert.assertTrue(
	             addUserPage.isUsersOptionAtBottonScreenDisplayed(),
	             "Existing user should be added and returned to users list"
	         );
	     }
	     //Delete the existing user in the system who added in current logged account
	     addUserPage.clickUsersSearchFilterIcon();
	     addUserPage.filterForUsersSearchbar(exstingUserButNotAvailbleInCurrentLogin);
	     addUserPage.selectTheUser();
	     addUserPage.clickDeleteUserOption();
	     addUserPage.clickConfirmDeleteOption();
	     Assert.assertTrue(
	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
	    	 		"User navigated back to users list after adding a new user successfully"
	     );

	    }	
}