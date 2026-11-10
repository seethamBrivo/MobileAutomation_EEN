package tests.ts_users;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.AfterMethod;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.UsersPage;
import utils.TestDataGenerator;

public class AddUserTest extends BaseTest {

    private UsersPage usersPage;
    private String existingUserEmail;
    private String generatedUserEmail;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToUsers() {
        existingUserEmail = null;
        generatedUserEmail = null;
        usersPage = null;
        loginAs(TestDataConstants.RESELLER_CREDS);
        usersPage = new UsersPage(driver);
        usersPage.clickUsersOptionAtTheBottom();
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
    }

    @Test(description = "EEPD-TC-4170: POS-Users - adding New User in mobile VMS app",
          groups = {"regression", "users", "ios"})
    public void addNewUserToVMSApp() {
        existingUserEmail = getTestDataValue("existingUserInSystemButNotExistingInCurrentLogin");
        usersPage.deleteUserIfExists(existingUserEmail);
        // Step 3: Tap the plus icon; the New user form opens
        usersPage.openNewUserForm();
        // Step 4: Fill in the form fields
        usersPage.enterEmailAtTheInputField(getTestDataValue("invalidEmailFormatText"));
        usersPage.verifyAddButtonDisabledForInvalidEmail();
        String firstName = TestDataGenerator.generateFirstName();
        String lastName = TestDataGenerator.generateLastName();
        usersPage.verifyEmailPlusHint(firstName);
        generatedUserEmail = usersPage.addUserWithDetails(firstName, lastName);
        // Step 5: The "User was added" message shows and the app is back on the users list
        usersPage.verifyUserAddedAndBackOnUsersList();
        usersPage.verifyUserListed(generatedUserEmail);
        // Step 6: Add a user whose email exists in the system but not in the current account
        usersPage.addUserWithExistingEmail(existingUserEmail, firstName, lastName);
        usersPage.verifyUserAddedAndBackOnUsersList();
        usersPage.searchAndDeleteUser(existingUserEmail);
        existingUserEmail = null;
        usersPage.verifyUsersOptionAtBottomScreenDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTestData(ITestResult testResult) {
        try {
            if (driver != null && usersPage != null) {
                usersPage.clickUsersOptionAtTheBottom();
                if (existingUserEmail != null) {
                    usersPage.deleteUserIfExists(existingUserEmail);
                }
                if (generatedUserEmail != null) {
                    usersPage.deleteUserIfExists(generatedUserEmail);
                }
            }
        } catch (Throwable t) {
            logger.warn("@AfterMethod: user cleanup failed: {}", t.toString());
        } finally {
            super.tearDown(testResult);
        }
    }
}
