package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.TestDataGenerator;
import utils.WaitUtils;

public class UsersPage {

    private static final Logger logger = LogManager.getLogger(UsersPage.class);
    private final AppiumDriver driver;

    private final By usersOptionAtTheBottomOfTheScreen = AppiumBy.xpath("//android.widget.TextView[@text='Users']");
    private final By usersAddIconAtTheTopRightCorner = AppiumBy.id("com.een.vms.android:id/img_custom");
    private final By newUserHeading = AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/header' and @text='New user']");
    private final By emailTextInputField = AppiumBy.accessibilityId("email_text_field");
    private final By enterValidEmailAddressErrorMessage = AppiumBy.xpath("//android.widget.TextView[@resource-id='com.een.vms.android:id/error_desc' and @text='Enter a valid email address']");
    private final By addButton = AppiumBy.id("com.een.vms.android:id/btn_text");
    private final By firstNameTextField = AppiumBy.accessibilityId("first_name_text_field");
    private final By lastNameTextField = AppiumBy.accessibilityId("last_name_text_field");
    private final By reachedMaxCharactersErrorMessage = AppiumBy.id("com.een.vms.android:id/error_desc");
    private final By emailPlusHint = AppiumBy.id("com.een.vms.android:id/emailPlusHint");
    // TODO: Replace with proper resource-id or content-desc when mobile team adds snackbar identifiers
    private final By snackbarText = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[3]");
    private final By usersSearchFilterIcon = AppiumBy.id("com.een.vms.android:id/img_search");
    private final By filterForUsersSearchbar = AppiumBy.id("com.een.vms.android:id/search_bar");
    private final By deleteUserOption = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='delete_header_content']");
    private final By confirmDeleteUserOption = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']");
    private final By backButton = AppiumBy.id("com.een.vms.android:id/img_back");

    private static final int DETAILS_OPEN_ATTEMPTS = 3;
    private static final String END_ICON_DESC = "_user_details_container_end_icon";

    public UsersPage(AppiumDriver driver) {
        this.driver = driver;
    }

    /** The list row showing exactly this e-mail. */
    private static By userRowText(String email) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + email + "']");
    }

    /**
     * The end (details) icon inside the same row as {@code email}: the nearest ancestor of the
     * e-mail label that also contains an end icon is the row itself.
     */
    private static By userRowEndIcon(String email) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + email + "']"
                + "/ancestor::*[.//android.widget.ImageView[@content-desc='" + END_ICON_DESC + "']][1]"
                + "//android.widget.ImageView[@content-desc='" + END_ICON_DESC + "']");
    }

    // ---- navigation -----------------------------------------------------------------------

    public void clickUsersOptionAtTheBottom() {
        WaitUtils.click(driver, usersOptionAtTheBottomOfTheScreen);
    }

    public void clickUsersAddIconAtTheTopRightCorner() {
        WaitUtils.click(driver, usersAddIconAtTheTopRightCorner);
    }

    public void clickBackButton() {
        WaitUtils.click(driver, backButton);
    }

    public boolean isAddIconDisplayed() {
        return WaitUtils.isVisible(driver, usersAddIconAtTheTopRightCorner, TimeoutConstants.SHORT_TIMEOUT);
    }

    public void verifyUsersOptionAtBottomScreenDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, usersOptionAtTheBottomOfTheScreen).isDisplayed(),
                "Users option should be displayed at the bottom of the screen");
    }

    // ---- new-user form --------------------------------------------------------------------

    public void verifyNewUserHeadingDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, newUserHeading).isDisplayed(), "New user heading should be displayed");
    }

    public void verifyAddButtonDisabled() {
        Assert.assertFalse(WaitUtils.waitForVisibility(driver, addButton).isEnabled(), "Add button should be disabled");
    }

    public void verifyAddButtonEnabled() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, addButton).isEnabled(), "Add button should be enabled");
    }

    public void enterEmailAtTheInputField(String email) {
        WaitUtils.clearAndType(driver, emailTextInputField, email);
    }

    public void enterFirstNameInputField(String firstName) {
        WaitUtils.clearAndType(driver, firstNameTextField, firstName);
    }

    public void enterLastNameInputField(String lastName) {
        WaitUtils.clearAndType(driver, lastNameTextField, lastName);
    }

    public void clickAddButton() {
        WaitUtils.click(driver, addButton);
    }

    public void verifyValidEmailErrorMessageDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, enterValidEmailAddressErrorMessage).isDisplayed(),
                "Valid email error message should be displayed");
    }

    public void verifyEmailPlusHintDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, emailPlusHint).isDisplayed(), "Email plus hint should be displayed");
    }

    public void verifyMaxCharactersErrorMessageDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, reachedMaxCharactersErrorMessage).isDisplayed(),
                "Max characters error message should be displayed");
    }

    public void verifyMaxCharactersValidation() {
        enterFirstNameInputField("f".repeat(55));
        verifyMaxCharactersErrorMessageDisplayed();
        enterLastNameInputField("l".repeat(55));
    }

    /** The "user was added" snackbar - verify it appears (no text validation). */
    public void verifyUserWasAddedMessageDisplayed() {
        WebElement snackbar = WaitUtils.waitForVisibility(driver, snackbarText);
        Assert.assertTrue(snackbar.isDisplayed(), "Snackbar should be displayed after adding user");
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
        WaitUtils.click(driver, usersSearchFilterIcon);
    }

    public void filterForUsersSearchbar(String email) {
        WaitUtils.clearAndType(driver, filterForUsersSearchbar, email);
    }

    /** From the users list: search for {@code email} and report whether its row is shown. */
    public boolean searchForUser(String email) {
        clickUsersSearchFilterIcon();
        filterForUsersSearchbar(email);
        return WaitUtils.isVisible(driver, userRowText(email), TimeoutConstants.FILTER_RESULT_TIMEOUT);
    }

    /** Opens the row for exactly {@code email} and deletes it. Fails if that row is not listed. */
    public void searchAndDeleteUser(String email) {
        Assert.assertTrue(searchForUser(email), "User should be listed before deletion: " + email);
        deleteListedUser(email);
        returnToUsersList();
    }

    /** Navigates back from search screen to the main users list. */
    private void returnToUsersList() {
        clickBackButton();
        WaitUtils.waitForVisibility(driver, usersAddIconAtTheTopRightCorner);
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

    private void deleteListedUser(String email) {
        openUserDetailsWithDelete(email);
        WaitUtils.click(driver, deleteUserOption);
        WaitUtils.waitForVisibility(driver, confirmDeleteUserOption);
        WaitUtils.click(driver, confirmDeleteUserOption);
        WaitUtils.waitForVisibility(driver, filterForUsersSearchbar);
    }

    /**
     * Opens the details screen of the listed row until its delete option shows. The details of a
     * just-created user can stay on loading skeletons indefinitely (BrowserStack build #9), so a
     * screen that does not finish loading is closed and the row re-opened instead of waiting longer.
     */
    private void openUserDetailsWithDelete(String email) {
        for (int attempt = 1; attempt <= DETAILS_OPEN_ATTEMPTS; attempt++) {
            WaitUtils.click(driver, userRowEndIcon(email));
            if (WaitUtils.isVisible(driver, deleteUserOption, TimeoutConstants.DETAILS_LOAD_TIMEOUT)) {
                return;
            }
            logger.warn("Details of {} did not finish loading (attempt {}/{})", email, attempt, DETAILS_OPEN_ATTEMPTS);
            if (attempt < DETAILS_OPEN_ATTEMPTS) {
                driver.navigate().back();
                if (!WaitUtils.isVisible(driver, userRowEndIcon(email), TimeoutConstants.SHORT_TIMEOUT)) {
                    filterForUsersSearchbar(email);
                    WaitUtils.waitForVisibility(driver, userRowEndIcon(email), TimeoutConstants.FILTER_RESULT_TIMEOUT);
                }
            }
        }
        Assert.fail("Delete option never appeared on the details screen of " + email);
    }
}
