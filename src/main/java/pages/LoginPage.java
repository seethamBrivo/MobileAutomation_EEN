package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import utils.ConfigReader;
import utils.WaitUtils;

public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private static final int LOGIN_ATTEMPTS = 3;

    private final AppiumDriver driver;
    private final By signInButton = AppiumBy.accessibilityId("sign_in_btn_button");
    private final By emailInputField = AppiumBy.className("android.widget.EditText");
    private final By nextButton = AppiumBy.xpath("//android.widget.Button[@text=\"Next\"]");
    private final By passwordField = AppiumBy.xpath("//android.widget.EditText[@resource-id=\"authentication--input__password\"]");
    private final By signInButtonToLogin = AppiumBy.xpath("//android.widget.Button[@resource-id=\"next\"]");
    private final By noThanksOptionInBiometricsAuthenticationPopUp = AppiumBy.id("com.een.vms.android:id/positive_button");

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
        // Uses 5-second timeout since popup appears after login completes (async operation)
        // NOTE: negative_button is intentionally excluded - it's used as confirm delete in UsersPage
        java.util.List<org.openqa.selenium.By> strategies = java.util.Arrays.asList(
            noThanksOptionInBiometricsAuthenticationPopUp,
            AppiumBy.xpath("//android.widget.Button[contains(@text, 'NO THANKS') or contains(@text, 'No Thanks') or contains(@text, 'No thanks')]"),
            AppiumBy.id("android:id/button2"),
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
        for (int attempt = 1; attempt <= LOGIN_ATTEMPTS; attempt++) {
            try {
                tapSignIn();
                enterEmail(username);
                tapNextButton();
                enterPassword(password);
                tapSignInButtonToLogin();
                break;
            } catch (TimeoutException e) {
                if (attempt == LOGIN_ATTEMPTS) {
                    throw e;
                }
                logger.warn("Login screen did not load (attempt {}/{}), restarting the app: {}",
                        attempt, LOGIN_ATTEMPTS, e.getMessage().lines().findFirst().orElse(""));
                restartApp();
            }
        }
        tapNoThanksOptionInBiometricsAuthenticationPopup();
        return new ResellerDashboardPage(driver);
    }

    /**
     * The sign-in page intermittently never renders its input fields (BrowserStack CI builds
     * #10/#11). Restarting the app returns to the sign-in entry point; app data is kept so
     * permissions granted earlier in the session stay in place.
     */
    private void restartApp() {
        boolean android = "android".equalsIgnoreCase(ConfigReader.getPlatform());
        String key = android ? "appId" : "bundleId";
        String appId = ConfigReader.getProperty(android ? "appPackage" : "bundleId");
        if (appId.isBlank()) {
            logger.warn("App id not configured; retrying login without restarting the app");
            return;
        }
        driver.executeScript("mobile: terminateApp", java.util.Map.of(key, appId));
        driver.executeScript("mobile: activateApp", java.util.Map.of(key, appId));
    }
}
