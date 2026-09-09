package tests.ts_users;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.UsersPage;
import utils.TestDataGenerator;

public class AddUserTest extends BaseTest {

    private String existingUserEmail;
    /** E-mail actually created by the test, as returned by the page object; null until created. */
    private String generatedUserEmail;

    @Test(description = "EEPD-TC-4170: POS-Users - adding New User in mobile VMS app", groups = {"Regression"})
    public void addNewUserToVMSApp() {
        loginAs(TestDataConstants.RESELLER_CREDS);
        UsersPage usersPage = new UsersPage(driver);
        usersPage.clickUsersOptionAtTheBottom();

        existingUserEmail = getTestDataValue("existingUserInSystemButNotExistingInCurrentLogin");
        usersPage.deleteUserIfExists(existingUserEmail);

        // Validation on the new-user form
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

        // Brand-new user
        generatedUserEmail = usersPage.addUserWithDetails(firstName, lastName);

        // User that exists in the system but not in this account
        usersPage.clickUsersAddIconAtTheTopRightCorner();
        usersPage.enterEmailAtTheInputField(existingUserEmail);
        usersPage.enterFirstNameInputField(firstName);
        usersPage.enterLastNameInputField(lastName);
        usersPage.clickAddButton();
        usersPage.verifyUserWasAddedMessageDisplayed();

        usersPage.searchAndDeleteUser(existingUserEmail);
        existingUserEmail = null;
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTestData() {
        if (driver == null) {
            return;
        }
        try {
            UsersPage usersPage = new UsersPage(driver);
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
