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

	     // Wait for user to be added
	     try {
	         Thread.sleep(3000);
	     } catch (InterruptedException e) {
	         Thread.currentThread().interrupt();
	     }

	     // Navigate back to users list
	     driver.navigate().back();

	     try {
	         Thread.sleep(1000);
	     } catch (InterruptedException e) {
	         Thread.currentThread().interrupt();
	     }

	     Assert.assertTrue(
	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
	    	 		"User navigated back to users list after adding a new user successfully"
	    	          );
	   //Adding a user already existing in the system but not existing in the current logged account
	     addUserPage.clickUsersAddIconAtTheTopRightCorner();

	     // Wait for Add User screen to load
	     try {
	         Thread.sleep(2000);
	     } catch (InterruptedException e) {
	         Thread.currentThread().interrupt();
	     }

	     String exstingUserButNotAvailbleInCurrentLogin = testData
	                .getAsJsonObject("credentials")
	                .getAsJsonObject("reseller_user_creds")
	                .get("existingUserInSystemButNotExistingInCurrentLogin")
	                .getAsString();
	     addUserPage.enterEmailAtTheInputField(exstingUserButNotAvailbleInCurrentLogin);
	     addUserPage.enterFirstNameInputField(firstName);
	     addUserPage.enterLastNameInputField(lastName);
	     addUserPage.clickAddButton();

	     // Wait for user to be added
	     try {
	         Thread.sleep(3000);
	     } catch (InterruptedException e) {
	         Thread.currentThread().interrupt();
	     }

	     // Navigate back to users list
	     driver.navigate().back();

	     try {
	         Thread.sleep(1000);
	     } catch (InterruptedException e) {
	         Thread.currentThread().interrupt();
	     }

	     Assert.assertTrue(
	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
	    	 		"User navigated back to users list after adding a new user successfully"
	    	          );
	     //Delete the existing user in the system who added in current logged account
	     addUserPage.clickUsersSearchFilterIcon();
	     addUserPage.filterForUsersSearchbar(exstingUserButNotAvailbleInCurrentLogin);
	     addUserPage.selectTheUser();
	     addUserPage.waitForUserDetailsToLoad();
	     addUserPage.clickDeleteUserOption();
	     addUserPage.clickConfirmDeleteOption();
	     Assert.assertTrue(
	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
	    	 		"User navigated back to users list after adding a new user successfully"
	     );

	    }	
}