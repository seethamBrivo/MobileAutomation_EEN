package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.MyProfileLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

public class MyProfilePage {

    private static final Logger logger = LogManager.getLogger(MyProfilePage.class);
    private final AppiumDriver driver;

    public MyProfilePage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void clickProfileSection() {
        WaitUtils.click(driver, MyProfileLocators.PROFILE_SECTION);
        WaitUtils.waitForVisibility(driver, MyProfileLocators.PROFILE_DETAILS_HEADING, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        logger.info("Profile section expanded");
    }

    /** Steps "Go to the TIME section and click the Time zone item". */
    public void openTimezonePicker() {
        clickProfileSection();
        scrollToTimezoneOption();
        clickTimezoneOption();
    }

    public void scrollToTimezoneOption() {
        // Wait for profile screen to render before swiping to avoid exhausting scroll budget
        if (!WaitUtils.isVisible(driver, MyProfileLocators.TIMEZONE_OPTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT)) {
            // iOS keeps the rows below the screen in the page, so it scrolls until the row is really visible.
            boolean found = PlatformLocator.isIos()
                    ? GestureUtils.scrollUntilVisible(driver, MyProfileLocators.TIMEZONE_OPTION)
                    : GestureUtils.scrollUntilPresent(driver, MyProfileLocators.TIMEZONE_OPTION);
            Assert.assertTrue(found, "Could not find timezone option even after scrolling");
        }
    }

    public void clickTimezoneOption() {
        WaitUtils.click(driver, MyProfileLocators.TIMEZONE_OPTION);
    }

    public String getCurrentTimezone() {
        String timezone = WaitUtils.waitForVisibility(driver, MyProfileLocators.CURRENT_TIMEZONE_VALUE).getText();
        logger.info("Current timezone: {}", timezone);
        return timezone;
    }

    public void verifyTimezoneChanged(String expectedTimezone) {
        String actualTimezone = getCurrentTimezone();
        String normalizedActual = actualTimezone.toLowerCase();
        String normalizedExpected = expectedTimezone.toLowerCase();

        // Handle timezone aliases (Calcutta/Kolkata vary across Android versions)
        boolean matches = normalizedActual.contains(normalizedExpected) ||
                         (normalizedExpected.contains("calcutta") && normalizedActual.contains("kolkata")) ||
                         (normalizedExpected.contains("kolkata") && normalizedActual.contains("calcutta"));

        Assert.assertTrue(matches,
                "Timezone should contain '" + expectedTimezone + "' (or its alias) but was: " + actualTimezone);
    }

    public void scrollToTimeFormatOption() {
        // SAFETY FIX: Removed blind swipeUp when visible - it was unnecessary and cost 3s per probe
        if (!WaitUtils.isVisible(driver, MyProfileLocators.TIME_FORMAT_OPTION, TimeoutConstants.SHORT_TIMEOUT)) {
            boolean found = GestureUtils.scrollUntilVisible(driver, MyProfileLocators.TIME_FORMAT_OPTION);
            Assert.assertTrue(found, "Could not find time format option even after scrolling");
        }
    }

    public void clickTimeFormatOption() {
        WaitUtils.click(driver, MyProfileLocators.TIME_FORMAT_OPTION);
    }

    public String getCurrentTimeFormat() {
        String timeFormat = WaitUtils.getTextWithRetry(driver, MyProfileLocators.CURRENT_TIME_FORMAT_VALUE, TimeoutConstants.SHORT_TIMEOUT);
        logger.info("Current time format: {}", timeFormat);
        return timeFormat;
    }

    public void selectTimeFormat(String format) {
        WaitUtils.click(driver, MyProfileLocators.timeFormatOption(format));
        logger.info("Selected time format: {}", format);
    }

    public void verifyTimeFormatChanged(String expectedFormat) {
        String actualFormat = getCurrentTimeFormat();
        Assert.assertEquals(actualFormat, expectedFormat,
                "Time format should be '" + expectedFormat + "' but was: " + actualFormat);
    }

    public void verifyMyProfileScreenDisplayed() {
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.PROFILE_SECTION, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                "My Profile screen should be displayed");
    }

    public void pullToRefresh() {
        GestureUtils.swipeDown(driver);
    }

    public void verifyLoadingIndicatorAppears() {
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.LOADING_INDICATOR, TimeoutConstants.SHORT_TIMEOUT),
                "Loading indicator should appear after pull-to-refresh");
    }

    public void verifyDataRefreshed() {
        boolean loadingDisappeared = WaitUtils.waitForInvisibility(driver, MyProfileLocators.LOADING_INDICATOR, TimeoutConstants.DETAILS_LOAD_TIMEOUT);
        Assert.assertTrue(loadingDisappeared, "Loading indicator should disappear after refresh completes");
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.PROFILE_DETAILS_HEADING, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                "Profile details should be visible after refresh");
    }

    public void clickProfileDetailsOption() {
        WaitUtils.click(driver, MyProfileLocators.PROFILE_DETAILS_OPTION);
    }

    public void clickLoginEmailOption() {
        WaitUtils.click(driver, MyProfileLocators.LOGIN_EMAIL_OPTION);
    }

    public void enterVerifyPassword(String password) {
        WaitUtils.waitForVisibility(driver, MyProfileLocators.VERIFY_PASSWORD_INPUT).sendKeys(password);
    }

    public void enterNewEmail(String email) {
        WaitUtils.waitForVisibility(driver, MyProfileLocators.NEW_EMAIL_INPUT).sendKeys(email);
    }

    public void clickNextButton() {
        WaitUtils.click(driver, MyProfileLocators.NEXT_BUTTON);
    }

    public boolean isNextButtonEnabled() {
        return WaitUtils.waitForVisibility(driver, MyProfileLocators.NEXT_BUTTON_CLICKABLE).isEnabled();
    }

    public void verifyNextButtonDisabled() {
        Assert.assertFalse(isNextButtonEnabled(),
                "Next button should be disabled when fields are empty");
    }

    public void verifyPasswordErrorMessage() {
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.PASSWORD_ERROR_MESSAGE, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                "Password error message should be visible");
    }

    public void verifyEmailValidationError() {
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.EMAIL_VALIDATION_ERROR_MESSAGE, TimeoutConstants.SHORT_TIMEOUT),
                "Email validation error message should be visible");
    }

    public void verifyNextButtonEnabled() {
        Assert.assertTrue(isNextButtonEnabled(),
                "Next button should be enabled after entering required fields");
    }

    public void enterAuthenticationCode(String code) {
        WebElement authCodeField = WaitUtils.waitForVisibility(driver, MyProfileLocators.AUTH_CODE_INPUT);
        authCodeField.click();
        authCodeField.sendKeys(code);
    }

    public void clickConfirmButton() {
        WaitUtils.click(driver, MyProfileLocators.CONFIRM_BUTTON);
    }

    public void verifyAuthCodeErrorMessage() {
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.AUTH_CODE_ERROR_MESSAGE, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                "Authentication code error message should be visible");
    }

    public void scrollDownInProfile() {
        GestureUtils.swipeUp(driver);
        GestureUtils.swipeUp(driver);
        GestureUtils.swipeUp(driver);
    }

    public void clickSupportPin() {
        WaitUtils.click(driver, MyProfileLocators.SUPPORT_PIN_OPTION);
    }

    public void clickCallSupport() {
        WaitUtils.click(driver, MyProfileLocators.CALL_SUPPORT_OPTION);
    }

    public void verifyPhonePermissionDialogDisplayed() {
        Assert.assertTrue(WaitUtils.isVisible(driver, MyProfileLocators.PHONE_PERMISSION_ICON, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                "Phone permission dialog should be displayed after clicking Call Support");
    }
}
