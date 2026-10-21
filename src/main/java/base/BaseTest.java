package base;

import constants.TestDataConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.AccountVerificationRequiredException;
import pages.LoginPage;
import pages.ResellerDashboardPage;
import utils.BrowserStackStatus;
import utils.ConfigReader;
import utils.JsonDataReader;
import utils.WaitUtils;

/**
 * Session and login lifecycle, selected by {@link SessionScope} ({@code -DsessionScope=class|method}).
 *
 * <p><b>class</b> (default): one Appium session per test class. The first test signs in; every later test
 * calls {@link #loginAs} again, which keeps the login, relaunches the app to reach the landing screen
 * and returns. A different account is signed in after a logout; an untrustworthy app state is wiped
 * with {@code clearApp}. A dead Appium session is replaced. BrowserStack status is reported once per
 * session, and each test is marked on the session timeline.
 *
 * <p><b>method</b>: the original behaviour, a new session plus UI login and logout around every test.
 */
public abstract class BaseTest {

    protected AppiumDriver driver;
    protected final Logger logger = LogManager.getLogger(getClass());

    // Per-class state in instance fields (reporter, blockedAccounts, lastTornDown, driver): safe only because TestNG
    // runs all methods of one class instance on one thread (parallel="classes" or a serial suite), never one class
    // on two threads. The per-thread state (driver, AuthSession) lives in ThreadLocals.
    private final BrowserStackReporter reporter = new BrowserStackReporter();
    /** Accounts whose sign-in is blocked (e.g. a security-code step), so later tests fail at once instead of retrying it. */
    private final Map<String, String> blockedAccounts = new LinkedHashMap<>();
    /** Subclass cleanup hooks call {@link #tearDown} themselves, so it can run twice for one test. */
    private ITestResult lastTornDown;

    // ---- session lifecycle ---------------------------------------------------------------

    @BeforeClass(alwaysRun = true)
    public void startClassSession() {
        if (SessionScope.current() == SessionScope.CLASS) {
            try {
                openSession(getClass().getSimpleName(), null, new Object[0]);
            } catch (org.openqa.selenium.WebDriverException e) {
                // The first launch right after installing the app can time out ("MainActivity never
                // started"). A failed @BeforeClass makes TestNG skip every test in the class, so try once
                // more before giving up.
                logger.warn("First session attempt failed ({}); trying once more",
                        String.valueOf(e.getMessage()).lines().findFirst().orElse(""));
                DriverManager.quitDriver();
                openSession(getClass().getSimpleName(), null, new Object[0]);
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, ITestResult testResult) {
        String className = method.getDeclaringClass().getSimpleName();
        if (SessionScope.current() == SessionScope.METHOD) {
            openSession(className, method.getName(), testResult.getParameters());
        } else {
            ensureSessionAlive();
            reporter.testStarted(driver, method.getName());
        }
        logger.info("Test setup completed for {}.{}", className, method.getName());
    }

    private void openSession(String className, String methodName, Object[] parameters) {
        DriverManager.initializeDriver(className, methodName, parameters);
        driver = DriverManager.getDriver();
        AuthSession.reset();
        handleNotificationPermission();
    }

    /** A crashed device session or an expired idle timeout must not take the rest of the class down. */
    private void ensureSessionAlive() {
        if (driver != null && DriverManager.isSessionAlive()) {
            return;
        }
        logger.warn("Appium session is not usable; starting a new one for {}", getClass().getSimpleName());
        DriverManager.quitDriver();
        openSession(getClass().getSimpleName(), null, new Object[0]);
    }

    // ---- login / test data ---------------------------------------------------------------

    /**
     * Makes sure the app is signed in as {@code accountType} and showing its landing screen.
     * In method scope that is a plain UI login. In class scope an existing login is reused.
     */
    protected ResellerDashboardPage loginAs(String accountType) {
        if (SessionScope.current() == SessionScope.METHOD) {
            return signIn(accountType);
        }
        return ensureLoggedInAs(accountType);
    }

    private ResellerDashboardPage ensureLoggedInAs(String accountType) {
        String blocked = blockedAccounts.get(accountType);
        if (blocked != null) {
            throw new IllegalStateException("Sign-in for " + accountType + " already failed in this class: " + blocked);
        }
        LoginPage loginPage = new LoginPage(driver);
        if (AuthSession.isSignedInAs(accountType)) {
            // Relaunch without wiping data: back on the landing screen, login (hopefully) intact.
            loginPage.restartApp();
            switch (loginPage.waitForStartScreen(TimeoutConstants.SIGN_IN_LANDING_TIMEOUT)) {
                case SIGNED_IN -> {
                    logger.info("Reusing the existing login for {}", accountType);
                    return new ResellerDashboardPage(driver);
                }
                case SIGN_IN -> {
                    logger.warn("Login did not survive the app relaunch; signing in again as {}", accountType);
                    AuthSession.reset();
                }
                case UNKNOWN -> {
                    logger.warn("App did not reach a known screen after relaunch; wiping app data");
                    AuthSession.markDirty();
                    leaveCurrentAccount();
                }
            }
        } else if (AuthSession.account() != null || AuthSession.isDirty()) {
            logger.info("Switching account from {} to {}", AuthSession.account(), accountType);
            leaveCurrentAccount();
        } else {
            LoginPage.StartScreen start = loginPage.waitForStartScreen(TimeoutConstants.SIGN_IN_LANDING_TIMEOUT);
            if (start == LoginPage.StartScreen.SIGNED_IN) {
                // First login of this session, but the app is already signed in as someone unknown
                // (data kept from an earlier local run).
                logger.info("App starts signed in as an unknown account; signing out first");
                leaveCurrentAccount();
            } else if (start == LoginPage.StartScreen.UNKNOWN) {
                // Neither sign-in nor a signed-in screen: a previous attempt left the app somewhere else
                // (e.g. an unfinished web login). Start it again.
                logger.warn("App is on an unknown screen; relaunching before signing in");
                loginPage.restartApp();
            }
        }
        return signInReplacingDeadSession(accountType);
    }

    /**
     * A BrowserStack session can stop answering in the middle of the sign-in (seen: an empty reply from the hub). When
     * the session is really dead, sign in once more on a new one instead of failing, which would skip the whole class.
     */
    private ResellerDashboardPage signInReplacingDeadSession(String accountType) {
        try {
            return signIn(accountType);
        } catch (org.openqa.selenium.WebDriverException e) {
            if (DriverManager.isSessionAlive()) {
                throw e;
            }
            logger.warn("Appium session died during sign-in ({}); starting a new one and signing in again",
                    firstLine(e.getMessage()));
            DriverManager.quitDriver();
            openSession(getClass().getSimpleName(), null, new Object[0]);
            return signIn(accountType);
        }
    }

    private ResellerDashboardPage signIn(String accountType) {
        // A half-finished login must not count as signed in.
        AuthSession.reset();
        String username = JsonDataReader.getCredentialValue(accountType, "username");
        String password = JsonDataReader.getCredentialValue(accountType, "password");
        logger.info("Logging in using account type: {}", accountType);
        ResellerDashboardPage landing;
        try {
            landing = new LoginPage(driver).loginAs(username, password);
        } catch (AccountVerificationRequiredException e) {
            // Not a flaky screen: the account itself cannot sign in. Do not repeat it for every test.
            blockedAccounts.put(accountType, e.getMessage());
            throw e;
        }
        AuthSession.signedInAs(accountType);
        return landing;
    }

    /** Signs out through the UI; if that is not possible (or the state is dirty) wipes the app data. */
    private void leaveCurrentAccount() {
        if (!AuthSession.isDirty()) {
            try {
                new ResellerDashboardPage(driver).logout();
                logger.info("Logged out");
                AuthSession.reset();
                return;
            } catch (AssertionError | Exception e) {
                logger.warn("Logout failed ({}), clearing app state through the driver", e.toString());
            }
        }
        clearAppState(true);
        AuthSession.reset();
    }

    /** Shared by every test class whose @BeforeMethod logs in as reseller and lands on the dashboard. */
    protected ResellerDashboardPage loginAsResellerAndVerifyDashboard() {
        ResellerDashboardPage resellerDashboardPage = loginAs(TestDataConstants.RESELLER_CREDS);
        resellerDashboardPage.verifyDashboardPageDisplayed();
        return resellerDashboardPage;
    }

    /** A value from the reseller credentials block of testData.json. */
    protected String getTestDataValue(String key) {
        return JsonDataReader.getCredentialValue(TestDataConstants.RESELLER_CREDS, key);
    }

    // ---- optional system popups ----------------------------------------------------------

    private void handleNotificationPermission() {
        String platform = ConfigReader.getPlatform();
        List<By> strategies;
        if ("android".equalsIgnoreCase(platform)) {
            strategies = List.of(
                    AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button"),
                    AppiumBy.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
                    AppiumBy.androidUIAutomator("new UiSelector().text(\"Allow\").className(\"android.widget.Button\")"));
        } else if ("ios".equalsIgnoreCase(platform)) {
            strategies = List.of(
                    AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Allow'"),
                    AppiumBy.accessibilityId("Allow"));
        } else {
            return;
        }
        try {
            // One shared budget across all strategies: the popup either shows up within
            // POPUP_TIMEOUT or it does not exist on this session.
            boolean handled = WaitUtils.tryClickOptionalElement(driver, strategies, TimeoutConstants.POPUP_TIMEOUT);
            logger.info(handled ? "Notification permission popup dismissed"
                                : "No notification permission popup within {}", TimeoutConstants.POPUP_TIMEOUT);
        } catch (Exception e) {
            logger.warn("Error handling notification permission: {}", e.getMessage());
        }
    }

    // ---- teardown ------------------------------------------------------------------------

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        if (driver == null) {
            return;
        }
        if (SessionScope.current() == SessionScope.CLASS) {
            if (testResult != lastTornDown) {
                lastTornDown = testResult;
                reporter.record(driver, testResult);
            }
            return;
        }
        try {
            resetAppStateForNextTest();
        } finally {
            // Update BrowserStack status before quitting driver - ensures status is reported even if cleanup fails
            if (driver != null && isBrowserStackExecution()) {
                updateBrowserStackStatus(testResult);
            }
            DriverManager.quitDriver();
            driver = null;
            logger.info("Test teardown completed for {}", getClass().getSimpleName());
        }
    }

    @AfterClass(alwaysRun = true)
    public void endClassSession() {
        if (SessionScope.current() != SessionScope.CLASS || driver == null) {
            return;
        }
        try {
            reporter.finishSession(driver);
        } finally {
            DriverManager.quitDriver();
            driver = null;
            AuthSession.reset();
            reporter.reset();
            blockedAccounts.clear();
            logger.info("Class session ended for {}", getClass().getSimpleName());
        }
    }

    private static String firstLine(String text) {
        return BrowserStackReporter.firstLine(text);
    }

    /**
     * Method scope only. Sessions run with noReset=true, so whatever this leaves behind is what the
     * next test starts from. Try a normal logout first; if the test died somewhere logout cannot
     * navigate from, clear the app through the driver. That works on BrowserStack as well
     * as locally (a local adb call would not). Catches AssertionError alongside Exception,
     * not the broader Throwable: this runs from @AfterMethod and must not let a failure here
     * skip the rest of the class, but it also must not swallow a true JVM Error (OOM,
     * StackOverflowError) during teardown the way a bare Throwable catch would.
     */
    private void resetAppStateForNextTest() {
        try {
            new ResellerDashboardPage(driver).logout();
            logger.info("Logged out after test");
            return;
        } catch (AssertionError | Exception e) {
            logger.warn("Logout failed ({}), clearing app state through the driver", e.toString());
        }
        clearAppState(false);
    }

    /**
     * Wipes the app's data. With {@code relaunch} the app is started again afterwards, so the session can
     * carry on (the runtime-permission popup comes back after a wipe and is dismissed again).
     */
    private void clearAppState(boolean relaunch) {
        try {
            String platform = ConfigReader.getPlatform();
            if ("android".equalsIgnoreCase(platform)) {
                String appPackage = ConfigReader.getProperty("appPackage");
                if (appPackage.isBlank()) {
                    logger.warn("appPackage not configured; cannot clear app state. Next test may start logged in.");
                    return;
                }
                driver.executeScript("mobile: clearApp", Map.of("appId", appPackage));
                logger.info("Cleared app data for {}", appPackage);
                if (relaunch) {
                    new LoginPage(driver).restartApp();
                    handleNotificationPermission();
                }
            } else {
                String bundleId = ConfigReader.getProperty("bundleId");
                if (!bundleId.isBlank()) {
                    driver.executeScript("mobile: terminateApp", Map.of("bundleId", bundleId));
                }
            }
        } catch (AssertionError | Exception e) {
            logger.warn("Could not clear app state: {}", e.toString());
        }
    }

    private boolean isBrowserStackExecution() {
        return BrowserStackReporter.isBrowserStackExecution();
    }

    private void updateBrowserStackStatus(ITestResult testResult) {
        if (testResult.getStatus() == ITestResult.SUCCESS) {
            BrowserStackStatus.setStatus(driver, true, "Test passed successfully");
        } else if (testResult.getStatus() == ITestResult.FAILURE || testResult.wasRetried()) {
            // A failed attempt that is about to be retried is reported by TestNG as SKIP.
            String reason = testResult.getThrowable() != null ? testResult.getThrowable().getMessage() : null;
            BrowserStackStatus.setStatus(driver, false, reason == null ? "Test failed" : reason);
        }
    }
}
