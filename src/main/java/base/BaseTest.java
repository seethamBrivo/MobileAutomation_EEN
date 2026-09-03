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

        // Handle notification permission popup (BrowserStack)
        handleNotificationPermission();
    }

    private void handleNotificationPermission() {
        // Temporarily reduce implicit wait for faster permission check
        java.time.Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        // Guard against null timeout - use default if not set
        if (originalTimeout == null) {
            originalTimeout = java.time.Duration.ofSeconds(10);
        }
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(2));

        try {
            // Try different locators for "Allow" button with reduced implicit wait
            boolean handled = false;

            // Try 1: Using resource-id (most common for Android)
            if (!handled) {
                try {
                    driver.findElement(io.appium.java_client.AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button")).click();
                    handled = true;
                } catch (Exception e) {
                    // Silently continue to next strategy
                }
            }

            // Try 2: Using alternative resource-id
            if (!handled) {
                try {
                    driver.findElement(io.appium.java_client.AppiumBy.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button")).click();
                    handled = true;
                } catch (Exception e) {
                    // Silently continue
                }
            }

            // Try 3: Using UiAutomator selector with text (faster than XPath)
            if (!handled) {
                try {
                    driver.findElement(io.appium.java_client.AppiumBy.androidUIAutomator(
                        "new UiSelector().text(\"Allow\").className(\"android.widget.Button\")"
                    )).click();
                    handled = true;
                } catch (Exception e) {
                    // No more strategies
                }
            }

            if (!handled) {
                logger.info("No notification permission popup found or already handled");
            }

        } catch (Exception e) {
            logger.warn("Error handling notification permission: {}", e.getMessage());
        } finally {
            // Restore original implicit wait timeout
            driver.manage().timeouts().implicitlyWait(originalTimeout);
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
                try {
                    String appPackage = utils.ConfigReader.getProperty("appPackage");
                    String udid = utils.ConfigReader.getProperty("udid");

                    // Guard against null appPackage
                    if (appPackage == null || appPackage.isBlank()) {
                        logger.warn("appPackage is not configured, skipping force stop");
                        return;
                    }

                    // Use ProcessBuilder to prevent command injection
                    ProcessBuilder processBuilder;
                    if (udid != null && !udid.trim().isBlank()) {
                        processBuilder = new ProcessBuilder("adb", "-s", udid.trim(), "shell", "am", "force-stop", appPackage);
                    } else {
                        processBuilder = new ProcessBuilder("adb", "shell", "am", "force-stop", appPackage);
                    }

                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        logger.info("App force stopped successfully - will restart fresh for next test");
                    } else {
                        logger.warn("Force stop command returned exit code: {}", exitCode);
                    }
                } catch (Exception ex) {
                    logger.warn("Could not force stop app: {}", ex.getMessage());
                }
            }

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

        DriverManager.quitDriver();
        logger.info("Test teardown completed for {}", getClass().getSimpleName());
    }
}