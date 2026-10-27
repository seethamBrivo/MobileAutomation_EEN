package pages.ios;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import locators.ios.IosDevicesLocators;
import locators.ios.IosLoginLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import utils.ConfigReader;
import utils.WaitUtils;

/** iOS counterpart of {@code pages.LoginPage}: the same sign-in steps against the iOS screens. */
public class IosLoginPage {

    private static final Logger logger = LogManager.getLogger(IosLoginPage.class);
    private static final int LOGIN_ATTEMPTS = 3;
    private static final int TYPE_ATTEMPTS = 3;
    private static final Duration TYPE_SETTLE = Duration.ofSeconds(4);
    /** An empty web input reports its placeholder as its value. */
    private static final String EMAIL_PLACEHOLDER = "Email";

    private final AppiumDriver driver;

    /** What the app shows right after it starts. */
    public enum StartScreen { SIGN_IN, SIGNED_IN, UNKNOWN }

    public IosLoginPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public IosLoginPage tapSignIn() {
        WaitUtils.waitForClickability(driver, IosLoginLocators.SIGN_IN_BUTTON).click();
        return this;
    }

    public IosLoginPage enterEmail(String username) {
        typeAndVerify(IosLoginLocators.EMAIL_INPUT_FIELD, username);
        return this;
    }

    public IosLoginPage tapNextButton() {
        WaitUtils.waitForVisibility(driver, IosLoginLocators.NEXT_BUTTON).click();
        return this;
    }

    public IosLoginPage enterPassword(String password) {
        typeAndVerify(IosLoginLocators.PASSWORD_FIELD, password);
        return this;
    }

    public IosLoginPage tapSignInButtonToLogin() {
        WaitUtils.waitForVisibility(driver, IosLoginLocators.SIGN_IN_BUTTON_TO_LOGIN).click();
        return this;
    }

    /**
     * Types into a web sign-in field and makes sure all of it arrived. A web input on iOS ignores keys until it has
     * focus, so it is tapped first, and it reports its text only through the "value" attribute. If the field does not
     * hold the full text shortly after typing, it is cleared and typed again. Never logs the text.
     */
    private void typeAndVerify(By locator, String text) {
        int lastSeenLength = -1;
        for (int attempt = 1; attempt <= TYPE_ATTEMPTS; attempt++) {
            WebElement field = WaitUtils.waitForVisibility(driver, locator);
            if (attempt > 1) {
                field.clear();
            }
            try {
                field.click();
                field.sendKeys(text);
            } catch (StaleElementReferenceException e) {
                continue; // the page re-rendered while typing; look the field up again
            }
            long deadline = System.nanoTime() + TYPE_SETTLE.toNanos();
            while (System.nanoTime() < deadline) {
                try {
                    lastSeenLength = typedLength(field);
                    if (lastSeenLength >= text.length()) {
                        return;
                    }
                } catch (StaleElementReferenceException e) {
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
        // Not fatal: if the text really was cut off the sign-in is rejected and the landing-screen wait reports that.
        // Lengths only: the text may be a password.
        logger.warn("The field reports {} of {} typed characters after {} attempts; continuing with the sign-in",
                lastSeenLength, text.length(), TYPE_ATTEMPTS);
    }

    private static int typedLength(WebElement field) {
        String value = field.getAttribute("value");
        return value == null || value.equals(EMAIL_PLACEHOLDER) ? 0 : value.length();
    }

    /**
     * Waits for the sign-in screen or a signed-in screen, whichever the app shows. Used after the app is
     * relaunched, to learn whether the previous login survived.
     */
    public StartScreen waitForStartScreen(Duration timeout) {
        Optional<By> first = WaitUtils.waitForFirstVisible(driver,
                List.of(IosLoginLocators.SIGN_IN_BUTTON, IosDevicesLocators.MORE_TAB), timeout);
        if (first.isEmpty()) {
            return StartScreen.UNKNOWN;
        }
        return first.get() == IosLoginLocators.SIGN_IN_BUTTON ? StartScreen.SIGN_IN : StartScreen.SIGNED_IN;
    }

    /** Signs in and waits for the signed-in screen (the More tab). */
    public void loginAs(String username, String password) {
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
        if (!WaitUtils.isVisible(driver, IosDevicesLocators.MORE_TAB, TimeoutConstants.SIGN_IN_LANDING_TIMEOUT)) {
            // Failing here, rather than a step later, keeps a login that never completed from being reused.
            throw new TimeoutException("Signed-in screen did not appear within " + TimeoutConstants.SIGN_IN_LANDING_TIMEOUT
                    + " after tapping Sign in; check the credentials for this account");
        }
        logger.info("Signed-in screen is showing");
    }

    /** Relaunches the app so the sign-in entry point is shown again. Never throws. */
    public void restartApp() {
        String bundleId = ConfigReader.getProperty("bundleId");
        if (bundleId.isBlank()) {
            logger.warn("bundleId not configured; retrying login without restarting the app");
            return;
        }
        try {
            driver.executeScript("mobile: terminateApp", Map.of("bundleId", bundleId));
            driver.executeScript("mobile: activateApp", Map.of("bundleId", bundleId));
        } catch (org.openqa.selenium.WebDriverException e) {
            logger.warn("Could not relaunch the app ({}); retrying login in place",
                    e.getMessage().lines().findFirst().orElse(""));
        }
    }
}
