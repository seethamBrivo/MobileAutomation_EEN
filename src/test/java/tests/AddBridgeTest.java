package tests;

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
import pages.AddBridgePage;
import pages.AddUserPage;

public class AddBridgeTest extends BaseTest {
	 @Test(description = "EEPD-TC-11276: POS-Add Bridge - Adding Bridge to the account in Dashboard in mobile VMS app")
	    public void addBridgeToVMSApp() {
		 ResellerDashboardPage resellerDashboardPage = loginAs("enduser_user_creds");
		 AddBridgePage addBridgePage = new AddBridgePage(driver);
		 addBridgePage.clickMoreMenuOptionAtTheBottom();
		 addBridgePage.clickDashboardOptionAtTheBottom();
	     Assert.assertTrue(
	    		 addBridgePage.allDevicesSectionIsDisplayed(),
            "Navigated to 'All devices' section"
	    		 );
	     addBridgePage.clickOnAddBridgeIcon();
	     Assert.assertTrue(
	    		 addBridgePage.isAddBridgeOptionDisplayed(),
            "Add bridge option is displayed"
	    		 );
	     Assert.assertTrue(
	    		 addBridgePage.isAddCameraOptionDisplayed(),
            "Add camera option is displayed"
	    		 );
	     Assert.assertTrue(
	    		 addBridgePage.isAddCameraDirectOptionDisplayed(),
            "Add camera direct option is displayed"
	    		 );
		addBridgePage.clickAddBridgeOption();
		// Check button is disabled initially (before entering any data)
		Assert.assertTrue(
				addBridgePage.isAddBridgeButtonDisabled(),
				"Add bridge button is in disabled state initially"
				);
		addBridgePage.enterBridgeName("TestBridge");

		JsonObject testData =
                JsonDataReader.readJsonObject("testData.json");
		String attachId = testData
				.getAsJsonObject("credentials")
				.getAsJsonObject("reseller_user_creds")
				.get("attchId")
				.getAsString();
		addBridgePage.enterAttachId(attachId);

		// Check button is enabled after entering all required data
		Assert.assertTrue(
				addBridgePage.isAddBridgeButtonEnabled(),
				"Add bridge button is in enabled state after user enters all required data"
				);

		addBridgePage.clickAddBridgeButton();
        Assert.assertTrue(
				addBridgePage.isUserNavigatedToOriginScreen(),
				"User navigated back to origin screen after adding a new bridge successfully"
		);
		
		
	     
//		 addUserPage.clickUsersOptionAtTheBottom();
//		 addUserPage.clickUsersAddIconAtTheTopRightCorner();
//	     Assert.assertTrue(
//	    		 addUserPage.isNewUserHeadingIsDisplayed(),
//	                "New User Heading is visible at the add user screen"
//	        );
//	     Assert.assertTrue(
//	    		 addUserPage.addButtonIsInDisabledState(),
//	                "Add button is in disabled state until user enters the valid email"
//	        );
//	     JsonObject testData =
//	                JsonDataReader.readJsonObject("testData.json");
//	     String invalidEmailFormatText = testData
//	                .getAsJsonObject("credentials")
//	                .getAsJsonObject("reseller_user_creds")
//	                .get("inavlidEmailFormatText")
//	                .getAsString();
//	     addUserPage.enterEmailAtTheInputField(invalidEmailFormatText);
//	     addUserPage.clickAddButton();
//	     Assert.assertTrue(
//	        		addUserPage.enterValidEmailAddressErrorMessage(),
//	                "Enter a valid email address error message is displayed"
//	        );
//	     String firstName = TestDataGenerator.generateFirstName();
//	     String lastName = TestDataGenerator.generateLastName();
//	     addUserPage.enterEmailAtTheInputField(firstName+"+");
//	     Assert.assertTrue(
// 		addUserPage.isEmailEnteringWithPlusDisplaysHintDisplayed(),
// 		"If user enters '+' 'The he + alias is no longer required. It's possible to access multiple accounts with the same email address. You can easily switch between accounts without re-login.' is visible "
//         );
//	     addUserPage.isAddButtonEnabled();
//	     String email = TestDataGenerator.generateEmail(firstName, lastName);
//	     addUserPage.enterEmailAtTheInputField(email);
//	     String firstNameOver50Chars = "f".repeat(55);
//	     addUserPage.enterFirstNameInputField(firstNameOver50Chars);
//	      Assert.assertTrue(
// 		addUserPage.isMaxCharactersReachedErrorMessageDisplayed(),
// 		"Reached Max characters error message is displayed"
//          );
//	     String lastNameOver50Chars = "l".repeat(55);
//	     addUserPage.enterLastNameInputField(lastNameOver50Chars);
//	     addUserPage.enterFirstNameInputField(firstName);
//	     addUserPage.enterLastNameInputField(lastName);
//	     addUserPage.clickAddButton();
//	     Assert.assertTrue(
//	    	 		addUserPage.isUserWasAddedMessageDisplayed(),
//	    	 		"User was added message is displayed"
//	    	          );
//	     Assert.assertTrue(
//	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
//	    	 		"User navigated back to users list after adding a new user successfully"
//	    	          );
//	   //Adding a user already existing in the system but not existing in the current logged account 
//	     addUserPage.clickUsersAddIconAtTheTopRightCorner();
//	     String exstingUserButNotAvailbleInCurrentLogin = testData
//	                .getAsJsonObject("credentials")
//	                .getAsJsonObject("reseller_user_creds")
//	                .get("existingUserInSystemButNotExistingInCurrentLogin")
//	                .getAsString();
//	     addUserPage.enterEmailAtTheInputField(exstingUserButNotAvailbleInCurrentLogin);
//	     addUserPage.enterFirstNameInputField(firstName);
//	     addUserPage.enterLastNameInputField(lastName);
//	     addUserPage.clickAddButton();
//	     Assert.assertTrue(
//	    	 		addUserPage.isUserWasAddedMessageDisplayed(),
//	    	 		"User was added message is displayed"
//	    	          );
//	     Assert.assertTrue(
//	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
//	    	 		"User navigated back to users list after adding a new user successfully"
//	    	          );
//	     //Delete the existing user in the system who added in current logged account
//	     addUserPage.clickUsersSearchFilterIcon();
//	     addUserPage.filterForUsersSearchbar(exstingUserButNotAvailbleInCurrentLogin);
//	     addUserPage.selectTheUser();
//	     addUserPage.clickDeleteUserOption();
//	     addUserPage.clickConfirmDeleteOption();
//	     Assert.assertTrue(
//	    	 		addUserPage.isUsersOptionAtBottonScreenDisplayed(),
//	    	 		"User navigated back to users list after adding a new user successfully"
//	     );

	    }	
}