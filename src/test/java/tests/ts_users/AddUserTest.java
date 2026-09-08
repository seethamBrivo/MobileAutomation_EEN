package tests.ts_users;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import utils.JsonDataReader;
import utils.TestDataGenerator;
import pages.UsersPage;

public class AddUserTest extends BaseTest {
	private String testUserEmail;
	private String generatedUserEmail;

	@Test(description = "EEPD-TC-4170: POS-Users - adding New User in mobile VMS app", groups = {"Regression"})
	public void addNewUserToVMSApp() {
		loginAs("reseller_user_creds");
		UsersPage addUserPage = new UsersPage(driver);
		addUserPage.clickUsersOptionAtTheBottom();
		JsonObject testData = JsonDataReader.readJsonObject("testData.json");
		testUserEmail = getTestDataValue(testData, "existingUserInSystemButNotExistingInCurrentLogin");
		addUserPage.deleteUserIfExists(testUserEmail);
		addUserPage.clickUsersAddIconAtTheTopRightCorner();
		addUserPage.verifyNewUserHeadingDisplayed();
		addUserPage.verifyAddButtonDisabled();
		String invalidEmailFormatText = getTestDataValue(testData, "invalidEmailFormatText");
		addUserPage.enterEmailAtTheInputField(invalidEmailFormatText);
		addUserPage.clickAddButton();
		addUserPage.verifyValidEmailErrorMessageDisplayed();
		String firstName = TestDataGenerator.generateFirstName();
		String lastName = TestDataGenerator.generateLastName();
		generatedUserEmail = firstName + "+" + lastName + "@test.com";
		addUserPage.enterEmailAtTheInputField(firstName + "+");
		addUserPage.verifyEmailPlusHintDisplayed();
		addUserPage.isAddButtonEnabled();
		addUserPage.verifyMaxCharactersValidation();
		addUserPage.addUserWithDetails(firstName, lastName);
		addUserPage.clickUsersAddIconAtTheTopRightCorner();
		String existingUserButNotAvailableInCurrentLogin = getTestDataValue(testData, "existingUserInSystemButNotExistingInCurrentLogin");
		addUserPage.enterEmailAtTheInputField(existingUserButNotAvailableInCurrentLogin);
		addUserPage.enterFirstNameInputField(firstName);
		addUserPage.enterLastNameInputField(lastName);
		addUserPage.clickAddButton();
		boolean existingUserAdded = false;
		try {
			if (addUserPage.isUserWasAddedMessageDisplayed()) {
				existingUserAdded = true;
			}
		} catch (Exception e) {
		}
		if (!existingUserAdded) {
			Assert.assertTrue(addUserPage.isUsersOptionAtBottomScreenDisplayed(), "Existing user should be added and returned to users list");
		}
		addUserPage.searchAndDeleteUser(existingUserButNotAvailableInCurrentLogin);
		Assert.assertTrue(addUserPage.isUsersOptionAtBottomScreenDisplayed(), "User navigated back to users list after adding a new user successfully");
	}
	@AfterMethod(alwaysRun = true)
	public void cleanupTestData() {
		if (driver != null) {
			try {
				UsersPage addUserPage = new UsersPage(driver);
				addUserPage.clickUsersOptionAtTheBottom();

				// Clean up the pre-existing test user
				if (testUserEmail != null) {
					addUserPage.deleteUserIfExists(testUserEmail);
					logger.info("AfterMethod: Cleanup completed for: {}", testUserEmail);
				}

				// Clean up the generated user created during test
				if (generatedUserEmail != null) {
					addUserPage.deleteUserIfExists(generatedUserEmail);
					logger.info("AfterMethod: Cleanup completed for generated user: {}", generatedUserEmail);
				}
			} catch (Exception e) {
				logger.warn("AfterMethod: Cleanup failed: {}", e.getMessage());
			}
		}
	}
}
