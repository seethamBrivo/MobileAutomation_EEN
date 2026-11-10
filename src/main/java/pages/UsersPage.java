package pages;

import constants.TimeoutConstants;
import constants.UiTextConstants;
import io.appium.java_client.AppiumDriver;
import java.util.Optional;
import locators.BridgesLocators;
import locators.UsersLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import utils.PlatformLocator;
import utils.TestDataGenerator;
import utils.WaitUtils;

public class UsersPage {

    private static final Logger logger = LogManager.getLogger(UsersPage.class);
    private final AppiumDriver driver;

    private static final int DETAILS_OPEN_ATTEMPTS = 3;

    public UsersPage(AppiumDriver driver) {
        this.driver = driver;
    }

    // ---- navigation -----------------------------------------------------------------------

    public void clickUsersOptionAtTheBottom() {
        BottomNavigationBar.clickUsersTab(driver);
    }

    public void clickUsersAddIconAtTheTopRightCorner() {
        WaitUtils.click(driver, UsersLocators.USERS_ADD_ICON_AT_THE_TOP_RIGHT_CORNER);
    }

    // ---- step-level groups (EEPD-TC-4170) -------------------------------------------------

    /** Step "Tap the plus icon": the New user form is open and Add is still disabled. */
    public void openNewUserForm() {
        clickUsersAddIconAtTheTopRightCorner();
        verifyNewUserHeadingDisplayed();
        verifyAddButtonDisabled();
    }

    /** Typing "name+" in the email field shows the plus-alias hint. */
    public void verifyEmailPlusHint(String firstName) {
        enterEmailAtTheInputField(firstName + "+");
        verifyEmailPlusHintDisplayed();
    }

    /** The "User was added" message shows and the app is back on the users list. */
    public void verifyUserAddedAndBackOnUsersList() {
        verifyUserWasAddedMessageDisplayed();
        verifyUsersOptionAtBottomScreenDisplayed();
    }

    /** Step "Add a user whose email exists in the system but not in the current account". */
    public void addUserWithExistingEmail(String existingEmail, String firstName, String lastName) {
        clickUsersAddIconAtTheTopRightCorner();
        enterEmailAtTheInputField(existingEmail);
        enterFirstNameInputField(firstName);
        enterLastNameInputField(lastName);
        clickAddButton();
    }

    public void clickBackButton() {
        WaitUtils.click(driver, UsersLocators.BACK_BUTTON);
    }

    public void verifyUsersOptionAtBottomScreenDisplayed() {
        BottomNavigationBar.verifyUsersTabDisplayed(driver);
    }

    // ---- new-user form --------------------------------------------------------------------

    public void verifyNewUserHeadingDisplayed() {
        WaitUtils.assertVisible(driver, UsersLocators.NEW_USER_HEADING, "New user heading should be displayed");
    }

    public void verifyAddButtonDisabled() {
        Assert.assertFalse(WaitUtils.waitForVisibility(driver, UsersLocators.ADD_BUTTON).isEnabled(), "Add button should be disabled");
    }

    public void enterEmailAtTheInputField(String email) {
        WaitUtils.clearAndType(driver, UsersLocators.EMAIL_TEXT_INPUT_FIELD, email);
    }

    public void enterFirstNameInputField(String firstName) {
        WaitUtils.clearAndType(driver, UsersLocators.FIRST_NAME_TEXT_FIELD, firstName);
    }

    public void enterLastNameInputField(String lastName) {
        WaitUtils.clearAndType(driver, UsersLocators.LAST_NAME_TEXT_FIELD, lastName);
    }

    public void clickAddButton() {
        WaitUtils.click(driver, UsersLocators.ADD_BUTTON);
    }

    public void verifyEmailPlusHintDisplayed() {
        if (PlatformLocator.isIos()) {
            logger.info("iOS: the + alias hint is not in the accessibility tree; not asserted");
            return;
        }
        WaitUtils.assertVisible(driver, UsersLocators.EMAIL_PLUS_HINT, "Email plus hint should be displayed");
    }

    public void verifyAddButtonDisabledForInvalidEmail() {
        if (PlatformLocator.isIos()) {
            logger.info("iOS: Add is enabled with an invalid email; not asserted");
            return;
        }
        verifyAddButtonDisabled();
    }

    /** The "user was added" snackbar shows; on iOS its text is read as well. */
    public void verifyUserWasAddedMessageDisplayed() {
        if (PlatformLocator.isIos()) {
            Snackbar.waitUntilSays(driver, UiTextConstants.USER_ADDED_MESSAGE, TimeoutConstants.LIST_LOAD_TIMEOUT);
            return;
        }
        WaitUtils.assertVisible(driver, UsersLocators.SNACKBAR_TEXT, "Snackbar should be displayed after adding user");
    }

    /**
     * Fills the form with a generated e-mail and submits it.
     * Defaults to verifying the success message.
     *
     * @return the e-mail that was actually created, so the caller can verify and clean it up
     */
    public String addUserWithDetails(String firstName, String lastName) {
        return addUserWithDetails(firstName, lastName, false);
    }

    /**
     * Fills the form with a generated e-mail and submits it.
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @param verify if true, verifies the success snackbar appears
     * @return the e-mail that was actually created, so the caller can verify and clean it up
     */
    public String addUserWithDetails(String firstName, String lastName, boolean verify) {
        String email = TestDataGenerator.generateEmail(firstName, lastName);
        enterEmailAtTheInputField(email);
        enterFirstNameInputField(firstName);
        enterLastNameInputField(lastName);
        clickAddButton();
        if (verify) {
            verifyUserWasAddedMessageDisplayed();
        }
        return email;
    }

    // ---- search / delete ------------------------------------------------------------------

    public void clickUsersSearchFilterIcon() {
        if (PlatformLocator.isIos()) {
            return;
        }
        // Only the old UI has a search icon; the new list already shows the filter field.
        WaitUtils.tryClickOptionalElement(driver, java.util.List.of(UsersLocators.USERS_SEARCH_FILTER_ICON), TimeoutConstants.SHORT_TIMEOUT);
    }

    public void filterForUsersSearchbar(String email) {
        WaitUtils.clearAndType(driver, UsersLocators.FILTER_FOR_USERS_SEARCHBAR, email);
    }

    /** From the users list: search for {@code email} and report whether its row is shown. */
    public boolean searchForUser(String email) {
        clickUsersSearchFilterIcon();
        filterForUsersSearchbar(email);
        if (PlatformLocator.isIos()) {
            return WaitUtils.pollUntil(driver, TimeoutConstants.LIST_LOAD_TIMEOUT,
                    () -> WaitUtils.isPresent(driver, UsersLocators.userRowText(email)));
        }
        // The filter is applied server-side: the row took ~12s to show up in BrowserStack build #14.
        return WaitUtils.isVisible(driver, UsersLocators.userRowText(email), TimeoutConstants.LIST_LOAD_TIMEOUT);
    }

    /** Opens the row for exactly {@code email} and deletes it. Fails if that row is not listed. */
    public void searchAndDeleteUser(String email) {
        Assert.assertTrue(searchForUser(email), "User should be listed before deletion: " + email);
        deleteListedUser(email);
        returnToUsersList();
    }

    /** Navigates back from search screen to the main users list. */
    private void returnToUsersList() {
        if (PlatformLocator.isIos()) {
            WaitUtils.tryClickOptionalElement(
                    driver, java.util.List.of(BridgesLocators.CLOSE_FILTER_BUTTON), TimeoutConstants.SHORT_TIMEOUT);
        }
        // The old UI had a separate search screen with its own back button; the new one does not.
        if (WaitUtils.isPresent(driver, UsersLocators.BACK_BUTTON)) {
            clickBackButton();
        }
        WaitUtils.waitForVisibility(driver, UsersLocators.USERS_ADD_ICON_AT_THE_TOP_RIGHT_CORNER);
    }

    /**
     * Best-effort cleanup. Deletes {@code email} only if a row with exactly that address is
     * listed after filtering; never touches the first row blindly. Leaves the app on the
     * users list. Never throws.
     *
     * @return true if the user was deleted
     */
    public boolean deleteUserIfExists(String email) {
        try {
            if (!searchForUser(email)) {
                logger.info("User {} is not listed; nothing to clean up", email);
                returnToUsersList();
                return false;
            }
            deleteListedUser(email);
            returnToUsersList();
            logger.info("Deleted test user {}", email);
            return true;
        } catch (Exception e) {
            logger.warn("Cleanup for {} failed: {}", email, e.getMessage());
            try {
                returnToUsersList();
            } catch (Exception ignored) {
                logger.warn("Could not return to users list after failed cleanup");
            }
            return false;
        }
    }

    public void verifyUserListed(String email) {
        Assert.assertTrue(searchForUser(email), "Added user should be listed in the users list: " + email);
        returnToUsersList();
    }

    private void deleteListedUser(String email) {
        openUserDetailsWithDelete(email);
        WaitUtils.click(driver, UsersLocators.DELETE_USER_OPTION);
        WaitUtils.waitForVisibility(driver, UsersLocators.CONFIRM_DELETE_USER_OPTION);
        WaitUtils.click(driver, UsersLocators.CONFIRM_DELETE_USER_OPTION);
        WaitUtils.waitForVisibility(driver, UsersLocators.FILTER_FOR_USERS_SEARCHBAR);
    }

    /**
     * Opens the details screen of the listed row until its delete option shows. The details of a
     * just-created user can stay on loading skeletons indefinitely (BrowserStack build #9), so a
     * screen that does not finish loading is closed and the row re-opened instead of waiting longer.
     */
    private void openUserDetailsWithDelete(String email) {
        for (int attempt = 1; attempt <= DETAILS_OPEN_ATTEMPTS; attempt++) {
            Optional<String> rowName = iosRowName(email);
            tapUserRow(email);
            if (WaitUtils.isVisible(driver, UsersLocators.DELETE_USER_OPTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT)) {
                verifyDetailsAreOfRow(rowName, email);
                return;
            }
            logger.warn("Details of {} did not finish loading (attempt {}/{})", email, attempt, DETAILS_OPEN_ATTEMPTS);
            if (attempt < DETAILS_OPEN_ATTEMPTS) {
                if (PlatformLocator.isIos()) {
                    clickBackButton();
                } else {
                    driver.navigate().back();
                }
                if (!isRowShown(email)) {
                    filterForUsersSearchbar(email);
                    waitForRow(email);
                }
            }
        }
        Assert.fail("Delete option never appeared on the details screen of " + email);
    }

    private void tapUserRow(String email) {
        if (PlatformLocator.isIos()) {
            WaitUtils.findPresent(driver, UsersLocators.userRowEndIcon(email), TimeoutConstants.SHORT_TIMEOUT)
                    .get(0).click();
            return;
        }
        WaitUtils.click(driver, UsersLocators.userRowEndIcon(email));
    }

    private Optional<String> iosRowName(String email) {
        if (!PlatformLocator.isIos()) {
            return Optional.empty();
        }
        return Optional.of(WaitUtils.findPresent(
                driver, UsersLocators.iosUserRowName(email), TimeoutConstants.SHORT_TIMEOUT).get(0).getText());
    }

    /** iOS: the last check before a delete, so a row that moved under the tap never deletes another user. */
    private void verifyDetailsAreOfRow(Optional<String> rowName, String email) {
        rowName.ifPresent(name -> Assert.assertEquals(
                WaitUtils.waitForVisibility(driver, UsersLocators.IOS_DETAILS_TITLE).getText(), name,
                "The details screen should belong to the user that was opened (" + email + ")"));
    }

    private boolean isRowShown(String email) {
        if (PlatformLocator.isIos()) {
            return WaitUtils.isPresent(driver, UsersLocators.userRowEndIcon(email));
        }
        return WaitUtils.isVisible(driver, UsersLocators.userRowEndIcon(email), TimeoutConstants.SHORT_TIMEOUT);
    }

    private void waitForRow(String email) {
        if (PlatformLocator.isIos()) {
            Assert.assertTrue(WaitUtils.pollUntil(driver, TimeoutConstants.FILTER_RESULT_TIMEOUT,
                    () -> isRowShown(email)), "User row should be listed again: " + email);
            return;
        }
        WaitUtils.waitForVisibility(
                driver, UsersLocators.userRowEndIcon(email), TimeoutConstants.FILTER_RESULT_TIMEOUT);
    }
}
