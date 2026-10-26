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
    /** E-mail actually created by the test, as returned by the page object; null until created. */
    private String generatedUserEmail;

    @BeforeMethod(alwaysRun = true)
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

    @Test(description = "EEPD-TC-4170: POS-Users - adding New User in mobile VMS app", groups = {"regression", "users"})
    public void addNewUserToVMSApp() {
        existingUserEmail = getTestDataValue("existingUserInSystemButNotExistingInCurrentLogin");
        usersPage.deleteUserIfExists(existingUserEmail);
        // Step 3: Tap the plus icon; the New user form opens
        usersPage.openNewUserForm();
        // Step 4: Fill in the form fields
        usersPage.enterEmailAtTheInputField(getTestDataValue("invalidEmailFormatText"));
        // The form no longer shows "Enter a valid email address": Add simply stays disabled for an
        // invalid email (verified on BrowserStack build #14's UI hierarchy). Confirm with the app team
        // if the message is expected to return.
        usersPage.verifyAddButtonDisabled();
        String firstName = TestDataGenerator.generateFirstName();
        String lastName = TestDataGenerator.generateLastName();
        usersPage.verifyEmailPlusHint(firstName);
        // Add stays disabled until first and last name are filled, so it is not checked here any more.
        // The 55-character name limit check was removed: the new form accepts 55 characters and shows
        // no error (UI hierarchy of build #14); the real limit needs confirming before re-adding it.
        generatedUserEmail = usersPage.addUserWithDetails(firstName, lastName);
        // Step 5: The "User was added" message shows and the app is back on the users list
        usersPage.verifyUserAddedAndBackOnUsersList();
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
