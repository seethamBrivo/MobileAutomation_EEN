package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import utils.ConfigReader;
import utils.PlatformLocator;
import utils.WaitUtils;

public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private static final int LOGIN_ATTEMPTS = 3;
    /** Launcher activity; the CI android.properties leaves appActivity unset on purpose (Android 15). */
    private static final String DEFAULT_APP_ACTIVITY = "com.een.core.MainActivity";

    private final AppiumDriver driver;
    private final By signInButton = PlatformLocator.byAccessibilityId("sign_in_btn_button");
    private final By emailInputField = PlatformLocator.get(
        AppiumBy.className("android.widget.EditText"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeTextField[1]")
    );
    private final By nextButton = PlatformLocator.byButton("Next");
    private final By passwordField = PlatformLocator.get(
        AppiumBy.xpath("//android.widget.EditText[@resource-id=\"authentication--input__password\"]"),
        AppiumBy.accessibilityId("authentication--input__password")
    );
    private final By signInButtonToLogin = PlatformLocator.get(
        AppiumBy.xpath("//android.widget.Button[@resource-id=\"next\"]"),
        AppiumBy.accessibilityId("next")
    );
    private final By noThanksOptionInBiometricsAuthenticationPopUp = PlatformLocator.get(
        AppiumBy.id("com.een.vms.android:id/positive_button"),
        AppiumBy.accessibilityId("positive_button")
    );

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
        java.util.List<org.openqa.selenium.By> strategies = buildBiometricsPopupStrategies();
        boolean clicked = WaitUtils.tryClickOptionalElement(driver, strategies, TimeoutConstants.POPUP_TIMEOUT);
        if (!clicked) {
            logger.info("Biometrics popup did not appear or was already dismissed");
        } else {
            logger.info("Biometrics popup successfully dismissed");
        }
        return this;
    }

    private java.util.List<org.openqa.selenium.By> buildBiometricsPopupStrategies() {
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            return java.util.Arrays.asList(
                noThanksOptionInBiometricsAuthenticationPopUp,
                AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND (label CONTAINS[c] 'no thanks' OR name CONTAINS[c] 'no thanks')"),
                AppiumBy.accessibilityId("No Thanks"),
                AppiumBy.accessibilityId("Not Now")
            );
        }
        return java.util.Arrays.asList(
            noThanksOptionInBiometricsAuthenticationPopUp,
            AppiumBy.xpath("//android.widget.Button[contains(@text, 'NO THANKS') or contains(@text, 'No Thanks') or contains(@text, 'No thanks')]"),
            AppiumBy.id("android:id/button2"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"No\").className(\"android.widget.Button\")")
        );
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
     * #10/#11). Relaunches the app so the sign-in entry point is shown again; app data is kept so
     * permissions granted earlier in the session stay in place. BrowserStack's Appium rejects
     * {@code mobile: terminateApp}/{@code activateApp}, so the supported {@code mobile: startActivity}
     * with {@code stop=true} (force-stop, then launch) is used. Never throws: if the relaunch is
     * refused the caller simply retries on the current screen.
     */
    private void restartApp() {
        if (!"android".equalsIgnoreCase(ConfigReader.getPlatform())) {
            logger.warn("App restart is only implemented for Android; retrying login in place");
            return;
        }
        String appPackage = ConfigReader.getProperty("appPackage");
        if (appPackage.isBlank()) {
            logger.warn("appPackage not configured; retrying login without restarting the app");
            return;
        }
        String activity = ConfigReader.getProperty("appActivity");
        if (activity.isBlank()) {
            activity = DEFAULT_APP_ACTIVITY;
        }
        try {
            driver.executeScript("mobile: startActivity",
                    java.util.Map.of("intent", appPackage + "/" + activity, "stop", true));
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.warn("Could not relaunch the app ({}); retrying login in place",
                    e.getMessage().lines().findFirst().orElse(""));
        }
    }
}
