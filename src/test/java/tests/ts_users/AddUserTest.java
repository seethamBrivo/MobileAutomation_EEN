package tests.ts_users;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UsersPage;
import utils.TestDataGenerator;

public class AddUserTest extends BaseTest {

    private UsersPage usersPage;
    private String existingUserEmail;
    /** E-mail actually created by the test, as returned by the page object; null until created. */
    private String generatedUserEmail;

    @BeforeMethod
    public void loginAndNavigateToUsers() {
        existingUserEmail = null;
        generatedUserEmail = null;
        // Nulled before loginAs(): if it throws, usersPage must not be left pointing at a
        // previous test's (already-quit) session for cleanupTestData()'s null guard to catch.
        usersPage = null;
        loginAs(TestDataConstants.RESELLER_CREDS);
        usersPage = new UsersPage(driver);
        usersPage.clickUsersOptionAtTheBottom();
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
    }

    @Test(description = "EEPD-TC-4170: POS-Users - adding New User in mobile VMS app", groups = {"Regression"})
    public void addNewUserToVMSApp() {
        existingUserEmail = getTestDataValue("existingUserInSystemButNotExistingInCurrentLogin");
        usersPage.deleteUserIfExists(existingUserEmail);
        usersPage.clickUsersAddIconAtTheTopRightCorner();
        usersPage.verifyNewUserHeadingDisplayed();
        usersPage.verifyAddButtonDisabled();
        usersPage.enterEmailAtTheInputField(getTestDataValue("invalidEmailFormatText"));
        usersPage.clickAddButton();
        usersPage.verifyValidEmailErrorMessageDisplayed();
        String firstName = TestDataGenerator.generateFirstName();
        String lastName = TestDataGenerator.generateLastName();
        usersPage.enterEmailAtTheInputField(firstName + "+");
        usersPage.verifyEmailPlusHintDisplayed();
        usersPage.verifyAddButtonEnabled();
        usersPage.verifyMaxCharactersValidation();
        generatedUserEmail = usersPage.addUserWithDetails(firstName, lastName);
        usersPage.verifyUserWasAddedMessageDisplayed();
        // Wait for app to return to users list before adding next user
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
        usersPage.clickUsersAddIconAtTheTopRightCorner();
        usersPage.enterEmailAtTheInputField(existingUserEmail);
        usersPage.enterFirstNameInputField(firstName);
        usersPage.enterLastNameInputField(lastName);
        usersPage.clickAddButton();
        usersPage.verifyUserWasAddedMessageDisplayed();
        // Ensure we're back on the users list before searching
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
        usersPage.searchAndDeleteUser(existingUserEmail);
        existingUserEmail = null;
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTestData() {
        if (driver == null || usersPage == null) {
            return;
        }
        try {
            usersPage.clickUsersOptionAtTheBottom();
            if (existingUserEmail != null) {
                usersPage.deleteUserIfExists(existingUserEmail);
            }
            if (generatedUserEmail != null) {
                usersPage.deleteUserIfExists(generatedUserEmail);
            }
        } catch (Throwable t) {
            logger.warn("@AfterMethod: user cleanup failed: {}", t.toString());
        }
    }
}
