package base;

import io.appium.java_client.AppiumDriver;
import com.google.gson.JsonObject;
import pages.ResellerDashboardPage;
import pages.LoginPage;
import utils.JsonDataReader;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

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

        // Handle notification permission popup (platform-aware)
        handleNotificationPermission();
    }

    private void handleNotificationPermission() {
        String platform = utils.ConfigReader.getPlatform();

        if ("android".equalsIgnoreCase(platform)) {
            handleAndroidPermission();
        } else if ("ios".equalsIgnoreCase(platform)) {
            handleIOSPermission();
        }
    }

    private void handleAndroidPermission() {
        try {
            // Try multiple locator strategies for Android permission dialog
            // Uses fast 200ms timeout to avoid wasting time when popup doesn't exist
            java.util.List<org.openqa.selenium.By> strategies = java.util.Arrays.asList(
                io.appium.java_client.AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button"),
                io.appium.java_client.AppiumBy.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
                io.appium.java_client.AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"Allow\").className(\"android.widget.Button\")"
                )
            );

            boolean handled = utils.WaitUtils.tryClickOptionalElement(driver, strategies);
            if (!handled) {
                logger.info("No notification permission popup found or already handled");
            }
        } catch (Exception e) {
            logger.warn("Error handling notification permission: {}", e.getMessage());
        }
    }

    private void handleIOSPermission() {
        try {
            // Try multiple locator strategies for iOS permission alert
            // Uses fast 200ms timeout to avoid wasting time when popup doesn't exist
            java.util.List<org.openqa.selenium.By> strategies = java.util.Arrays.asList(
                io.appium.java_client.AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' AND name == 'Allow'"
                ),
                io.appium.java_client.AppiumBy.accessibilityId("Allow")
            );

            boolean handled = utils.WaitUtils.tryClickOptionalElement(driver, strategies);
            if (!handled) {
                logger.info("No iOS notification permission popup found or already handled");
            }
        } catch (Exception e) {
            logger.warn("Error handling iOS notification permission: {}", e.getMessage());
        }
    }


    protected ResellerDashboardPage loginAs(String accountType) {

        JsonObject testData =
                JsonDataReader.readJsonObject("testData.json");

        JsonObject credentials = testData
                .getAsJsonObject("credentials")
                .getAsJsonObject(accountType);

        String username = credentials
                .get("username")
                .getAsString();

        String password = credentials
                .get("password")
                .getAsString();

        logger.info("Logging in using account type: {}", accountType);

        LoginPage loginPage = new LoginPage(driver);

        return loginPage.loginAs(username, password);
    }

    protected String getTestDataValue(com.google.gson.JsonObject testData, String key) {
        return testData
            .getAsJsonObject("credentials")
            .getAsJsonObject("reseller_user_creds")
            .get(key)
            .getAsString();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        if (driver != null) {
            // WORKAROUND: Force app restart by closing and reopening
            // This ensures next test starts from login screen
            try {
                // Option 1: Try logout (if working)
                ResellerDashboardPage dashboardPage = new ResellerDashboardPage(driver);
                dashboardPage.logout();
                logger.info("Successfully logged out after test");
            } catch (Exception e) {
                logger.warn("Logout method failed, using force restart: {}", e.getMessage());

                // Option 2: Force stop app (Android 15 workaround)
                Process process = null;
                try {
                    String appPackage = utils.ConfigReader.getProperty("appPackage");
                    String udid = utils.ConfigReader.getProperty("udid");

                    // Guard against null appPackage
                    if (appPackage == null || appPackage.isBlank()) {
                        logger.warn("appPackage is not configured, skipping force stop");
                    } else {
                        // Use ProcessBuilder to prevent command injection
                    ProcessBuilder processBuilder;
                    if (udid != null && !udid.trim().isBlank()) {
                        processBuilder = new ProcessBuilder("adb", "-s", udid.trim(), "shell", "am", "force-stop", appPackage);
                    } else {
                        processBuilder = new ProcessBuilder("adb", "shell", "am", "force-stop", appPackage);
                    }

                    process = processBuilder.start();

                    // Close streams to prevent resource leak
                    try (java.io.InputStream inputStream = process.getInputStream();
                         java.io.InputStream errorStream = process.getErrorStream();
                         java.io.OutputStream outputStream = process.getOutputStream()) {

                        boolean completed = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);

                        if (!completed) {
                            process.destroyForcibly();
                            logger.warn("Force stop command timed out after 10s, process terminated");
                        } else {
                            int exitCode = process.exitValue();
                            if (exitCode == 0) {
                                logger.info("App force stopped successfully - will restart fresh for next test");
                            } else {
                                logger.warn("Force stop command returned exit code: {}", exitCode);
                            }
                        }
                    }
                    }
                } catch (InterruptedException ex) {
                    if (process != null) {
                        process.destroyForcibly();
                    }
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    logger.warn("Force stop interrupted and process terminated: {}", ex.getMessage());
                } catch (Exception ex) {
                    if (process != null) {
                        process.destroyForcibly();
                    }
                    logger.warn("Could not force stop app: {}", ex.getMessage());
                }
            }

            // Only update BrowserStack status if executing on BrowserStack
            if (isBrowserStackExecution()) {
                updateBrowserStackStatus(testResult);
            }
        }

        DriverManager.quitDriver();
        logger.info("Test teardown completed for {}", getClass().getSimpleName());
    }

    private boolean isBrowserStackExecution() {
        String provider = utils.ConfigReader.getProperty("executionProvider");
        return "browserstack".equalsIgnoreCase(provider);
    }

    private void updateBrowserStackStatus(ITestResult testResult) {
        try {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            if (testResult.getStatus() == ITestResult.SUCCESS) {
                jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"Test passed successfully\"}}");
            } else if (testResult.getStatus() == ITestResult.FAILURE) {
                String reason = testResult.getThrowable() != null ? testResult.getThrowable().getMessage() : "Test failed";
                if (reason != null) {
                    reason = reason.replaceAll("[\"\\\r\n]", " ");
                }
                jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + reason + "\"}}");
            }
        } catch (Exception e) {
            logger.warn("Could not update BrowserStack session status: {}", e.getMessage());
        }
    }
}