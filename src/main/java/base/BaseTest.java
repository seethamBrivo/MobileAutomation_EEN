package base;

import constants.TestDataConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.ResellerDashboardPage;
import utils.ConfigReader;
import utils.JsonDataReader;
import utils.WaitUtils;

public abstract class BaseTest {

    protected AppiumDriver driver;
    protected final Logger logger = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, ITestResult testResult) {
        DriverManager.initializeDriver(
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                testResult.getParameters());
        driver = DriverManager.getDriver();
        logger.info("Test setup completed for {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        handleNotificationPermission();
    }

    // ---- login / test data ---------------------------------------------------------------

    protected ResellerDashboardPage loginAs(String accountType) {
        String username = JsonDataReader.getCredentialValue(accountType, "username");
        String password = JsonDataReader.getCredentialValue(accountType, "password");
        logger.info("Logging in using account type: {}", accountType);
        return new LoginPage(driver).loginAs(username, password);
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

    /**
     * Sessions run with noReset=true, so whatever this leaves behind is what the next test
     * starts from. Try a normal logout first; if the test died somewhere logout cannot
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
        return "browserstack".equalsIgnoreCase(ConfigReader.getProperty("executionProvider"));
    }

    private void updateBrowserStackStatus(ITestResult testResult) {
        try {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            if (testResult.getStatus() == ITestResult.SUCCESS) {
                jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"Test passed successfully\"}}");
            } else if (testResult.getStatus() == ITestResult.FAILURE) {
                String reason = testResult.getThrowable() != null ? testResult.getThrowable().getMessage() : "Test failed";
                if (reason != null) {
                    reason = reason.replaceAll("[\"\\\\\r\n]", " ");
                }
                jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + reason + "\"}}");
            }
        } catch (Exception e) {
            logger.warn("Could not update BrowserStack session status: {}", e.toString());
        }
    }
}
