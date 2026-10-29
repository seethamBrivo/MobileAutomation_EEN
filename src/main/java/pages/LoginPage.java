package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import locators.LoginLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import utils.ConfigReader;
import utils.PlatformLocator;
import utils.WaitUtils;

public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private static final int LOGIN_ATTEMPTS = 3;
    private static final int TYPE_ATTEMPTS = 3;
    private static final String EMAIL_PLACEHOLDER = "Email";
    private static final Duration TYPE_SETTLE = Duration.ofSeconds(4);
    /** Launcher activity; the CI android.properties leaves appActivity unset on purpose (Android 15). */
    private static final String DEFAULT_APP_ACTIVITY = "com.een.core.MainActivity";

    private final AppiumDriver driver;

    /** What the app shows right after it starts. */
    public enum StartScreen { SIGN_IN, SIGNED_IN, UNKNOWN }

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public LoginPage tapSignIn() {
        WaitUtils.waitForClickability(driver, LoginLocators.SIGN_IN_BUTTON).click();
        return this;
    }

    public LoginPage enterEmail(String username) {
        typeAndVerify(LoginLocators.EMAIL_INPUT_FIELD, username);
        return this;
    }


    /**
     * Types into a web sign-in field and makes sure all of it arrived. In the Chrome sign-in page the typing can
     * still be running when the command returns, and tapping Next / Sign in at that moment submits a cut-off
     * value (seen: a password field holding only its first few characters, so the login was rejected). If the
     * field does not hold the full text shortly after typing, it is cleared and typed again. Never logs the text.
     */
    private void typeAndVerify(By locator, String text) {
        int lastSeenLength = -1;
        for (int attempt = 1; attempt <= TYPE_ATTEMPTS; attempt++) {
            WebElement field = WaitUtils.waitForVisibility(driver, locator);
            if (attempt > 1) {
                field.clear();
            }
            try {
                if (PlatformLocator.isIos()) {
                    field.click(); // a web input on iOS ignores keys until it has focus
                }
                field.sendKeys(text);
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                continue; // the page re-rendered while typing; look the field up again
            }
            long deadline = System.nanoTime() + TYPE_SETTLE.toNanos();
            while (System.nanoTime() < deadline) {
                try {
                    lastSeenLength = countTypedCharacters(field);
                    if (lastSeenLength >= text.length()) {
                        return;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    break; // the page re-rendered; type again into the fresh element
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            logger.warn("Only part of the text reached the field (attempt {}/{}); typing it again", attempt, TYPE_ATTEMPTS);
        }
        // Not fatal: a field can report fewer characters than were typed even when it holds all of them (masked or
        // trimmed text, a trailing space or line break in the stored credential), and failing here stopped every login
        // of such an account. If the text really was cut off, the sign-in is rejected and the landing-screen wait
        // reports that. Lengths only: the text may be a password.
        logger.warn("The field reports {} of {} typed characters after {} attempts; continuing with the sign-in",
                lastSeenLength, text.length(), TYPE_ATTEMPTS);
    }

    /**
     * Length of what the field holds, on both platforms: Android web inputs report their text via getText, iOS web
     * inputs only through the "value" attribute. An empty iOS input reports its placeholder ("Email") as its value.
     */
    private static int countTypedCharacters(WebElement field) {
        String typed = field.getText();
        if (typed == null || typed.isEmpty()) {
            typed = readValueAttributeIfSupported(field);
        }
        return typed == null || typed.equals(EMAIL_PLACEHOLDER) ? 0 : typed.length();
    }

    /** The "value" attribute exists on iOS only; Android answers with an "unknown attribute" error, which means empty. */
    private static String readValueAttributeIfSupported(WebElement field) {
        try {
            return field.getAttribute("value");
        } catch (WebDriverException e) {
            return null;
        }
    }

    public LoginPage tapNextButton() {
        WaitUtils.waitForVisibility(driver, LoginLocators.NEXT_BUTTON).click();
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeAndVerify(LoginLocators.PASSWORD_FIELD, password);
        return this;
    }

    public LoginPage tapSignInButtonToLogin() {
        WaitUtils.waitForVisibility(driver, LoginLocators.SIGN_IN_BUTTON_TO_LOGIN).click();
        return this;
    }

    /**
     * Dismisses the biometrics prompt if one shows up after sign-in. On BrowserStack it never did
     * (0 of 36 sessions), so waiting a fixed few seconds for it cost about 6 s per login for nothing.
     * Instead this waits for whichever comes first, the prompt or the signed-in screen behind it, and
     * returns as soon as the signed-in screen is up.
     * NOTE: negative_button is intentionally excluded - it's used as confirm delete in UsersPage
     */
    private boolean dismissBiometricsPromptIfShown() {
        if (PlatformLocator.isIos()) {
            // iOS shows no biometrics prompt or security-code step; wait for the signed-in screen.
            return WaitUtils.isVisible(driver, LoginLocators.SIGNED_IN_MARKER, TimeoutConstants.SIGN_IN_LANDING_TIMEOUT);
        }
        List<By> prompt = List.of(
                LoginLocators.NO_THANKS_OPTION_IN_BIOMETRICS_AUTHENTICATION_POP_UP,
                LoginLocators.NO_THANKS_BUTTON_BY_TEXT,
                LoginLocators.BIOMETRICS_NEGATIVE_BUTTON);
        List<By> candidates = new ArrayList<>();
        candidates.add(LoginLocators.SIGNED_IN_MARKER);
        candidates.add(LoginLocators.VERIFICATION_PROMPT);
        candidates.addAll(prompt);

        Optional<By> first = WaitUtils.waitForFirstVisible(driver, candidates, TimeoutConstants.SIGN_IN_LANDING_TIMEOUT);
        if (first.isEmpty()) {
            logger.warn("Neither the biometrics prompt nor the signed-in screen appeared within {}",
                    TimeoutConstants.SIGN_IN_LANDING_TIMEOUT);
            return false;
        }
        By promptButton = first.get();
        if (promptButton == LoginLocators.VERIFICATION_PROMPT) {
            throw new AccountVerificationRequiredException("Sign-in stopped at the identity provider's 'Account verification' "
                    + "(security code) step. A test cannot complete it; use an account without verification "
                    + "or whitelist this device.");
        }
        if (promptButton == LoginLocators.SIGNED_IN_MARKER) {
            // Signed-in screen is up. Look once, without waiting, in case the prompt is drawn over it.
            promptButton = prompt.stream().filter(b -> WaitUtils.isPresent(driver, b)).findFirst().orElse(null);
        }
        if (promptButton == null) {
            logger.info("No biometrics prompt; signed-in screen is showing");
        } else {
            WaitUtils.click(driver, promptButton);
            logger.info("Biometrics popup successfully dismissed");
        }
        return true;
    }

    /**
     * Waits for the sign-in screen or a signed-in screen, whichever the app shows. Used after the app
     * starts or is relaunched, to learn whether a previous login survived.
     */
    public StartScreen waitForStartScreen(Duration timeout) {
        Optional<By> first = WaitUtils.waitForFirstVisible(driver, List.of(LoginLocators.SIGN_IN_BUTTON, LoginLocators.SIGNED_IN_MARKER), timeout);
        if (first.isEmpty()) {
            return StartScreen.UNKNOWN;
        }
        return first.get() == LoginLocators.SIGN_IN_BUTTON ? StartScreen.SIGN_IN : StartScreen.SIGNED_IN;
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
        if (!dismissBiometricsPromptIfShown()) {
            // Failing here, rather than a step later, keeps a login that never completed from being reused
            // by the next test and gives the failure a message that names the real problem.
            throw new TimeoutException("Signed-in screen did not appear within " + TimeoutConstants.SIGN_IN_LANDING_TIMEOUT
                    + " after tapping Sign in; check the credentials for this account");
        }
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
    public void restartApp() {
        if (PlatformLocator.isIos()) {
            restartIosApp();
            return;
        }
        if (!"android".equalsIgnoreCase(ConfigReader.getPlatform())) {
            logger.warn("App restart is only implemented for Android and iOS; retrying login in place");
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

    /** iOS: terminate and activate the app by bundle id; app data is kept. Never throws. */
    private void restartIosApp() {
        String bundleId = ConfigReader.getProperty("bundleId");
        if (bundleId.isBlank()) {
            logger.warn("bundleId not configured; retrying login without restarting the app");
            return;
        }
        try {
            driver.executeScript("mobile: terminateApp", java.util.Map.of("bundleId", bundleId));
            driver.executeScript("mobile: activateApp", java.util.Map.of("bundleId", bundleId));
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.warn("Could not relaunch the app ({}); retrying login in place",
                    e.getMessage().lines().findFirst().orElse(""));
        }
    }
}
