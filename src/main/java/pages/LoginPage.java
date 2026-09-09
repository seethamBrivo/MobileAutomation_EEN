package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import constants.TimeoutConstants;
import utils.WaitUtils;
public class LoginPage {
    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private final AppiumDriver driver;
    private final By signInButton = AppiumBy.accessibilityId("sign_in_btn_button");
    private final By emailInputField = AppiumBy.className("android.widget.EditText");
    private final By nextButton = AppiumBy.xpath("//android.widget.Button[@text=\"Next\"]");
    private final By passwordField = AppiumBy.xpath("//android.widget.EditText[@resource-id=\"authentication--input__password\"]");
    private final By signInButtonToLogin = AppiumBy.xpath("//android.widget.Button[@resource-id=\"next\"]");
    private final By noThanksOptionInBiometricsAuthenticationPopUp= AppiumBy.id("com.een.vms.android:id/positive_button");

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public LoginPage tapSignIn() {
        WaitUtils.waitForClickability(driver, signInButton).click();
		return this;
    }
    public LoginPage enterEmail(String username) {
        WaitUtils.waitForVisibility(driver, emailInputField).sendKeys(username);
        return this;
    }
    public LoginPage tapNextButton() {
        WaitUtils.waitForVisibility(driver, nextButton).click();
        return this;
    }
    public LoginPage enterPassword(String password) {
        WaitUtils.waitForVisibility(driver, passwordField).sendKeys(password);
        return this;
    }
    public LoginPage tapSignInButtonToLogin() {
        WaitUtils.waitForVisibility(driver, signInButtonToLogin).click();
        return this;
    }
    public LoginPage tapNoThanksOptionInBiometricsAuthenticationPopup() {
        // Try multiple locator strategies for biometric authentication popup
        // Uses 5-second timeout since popup appears after login completes (async operation)
        // NOTE: negative_button is intentionally excluded - it's used as confirm delete in UsersPage
        java.util.List<org.openqa.selenium.By> strategies = java.util.Arrays.asList(
            // Strategy 1: Resource ID
            noThanksOptionInBiometricsAuthenticationPopUp,
            // Strategy 2: Text-based (case-insensitive variations)
            AppiumBy.xpath("//android.widget.Button[contains(@text, 'NO THANKS') or contains(@text, 'No Thanks') or contains(@text, 'No thanks')]"),
            // Strategy 3: Alternative resource IDs
            AppiumBy.id("android:id/button2"),
            // Strategy 4: UiAutomator selector
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"No\").className(\"android.widget.Button\")")
        );

        boolean clicked = WaitUtils.tryClickOptionalElement(driver, strategies, TimeoutConstants.POPUP_TIMEOUT);
        if (!clicked) {
            logger.info("Biometrics popup did not appear or was already dismissed");
        } else {
            logger.info("Biometrics popup successfully dismissed");
        }
        return this;
    }
    public ResellerDashboardPage loginAs(String username, String password) {
        tapSignIn();
        enterEmail(username);
        tapNextButton();
        enterPassword(password);
        tapSignInButtonToLogin();
        tapNoThanksOptionInBiometricsAuthenticationPopup();
        return new ResellerDashboardPage(driver);
    }
}
